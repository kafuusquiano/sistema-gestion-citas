package com.proyecto.gestioncitas.auth.service.impl;

import com.proyecto.gestioncitas.auth.domain.dto.request.LoginRequest;
import com.proyecto.gestioncitas.auth.domain.dto.request.RegisterRequest;
import com.proyecto.gestioncitas.auth.domain.dto.response.AuthResponse;
import com.proyecto.gestioncitas.auth.domain.entity.RefreshToken;
import com.proyecto.gestioncitas.auth.domain.entity.Usuario;
import com.proyecto.gestioncitas.auth.domain.enums.Rol;
import com.proyecto.gestioncitas.auth.event.producers.AuthEventProducer;
import com.proyecto.gestioncitas.auth.exception.AuthException;
import com.proyecto.gestioncitas.auth.exception.CredencialesInvalidasException;
import com.proyecto.gestioncitas.auth.mapper.UsuarioMapper;
import com.proyecto.gestioncitas.auth.repository.RefreshTokenRepository;
import com.proyecto.gestioncitas.auth.repository.UsuarioRepository;
import com.proyecto.gestioncitas.auth.service.AuthService;
import com.proyecto.gestioncitas.auth.service.TokenBlacklistService;
import com.proyecto.gestioncitas.auth.service.UsuarioService;
import com.proyecto.gestioncitas.common.exception.ErrorCode;
import com.proyecto.gestioncitas.config.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    /** Intentos fallidos antes de bloquear la cuenta. */
    private static final int MAXIMO_INTENTOS = 5;

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioService usuarioService;
    private final TokenBlacklistService listaNegra;
    private final JwtTokenProvider jwt;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper mapper;
    private final AuthEventProducer eventos;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
            RefreshTokenRepository refreshTokenRepository,
            UsuarioService usuarioService,
            TokenBlacklistService listaNegra,
            JwtTokenProvider jwt,
            PasswordEncoder passwordEncoder,
            UsuarioMapper mapper,
            AuthEventProducer eventos) {
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.usuarioService = usuarioService;
        this.listaNegra = listaNegra;
        this.jwt = jwt;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.eventos = eventos;
    }

    @Override
    public AuthResponse registrar(RegisterRequest peticion) {
        // El registro publico nunca crea un MEDICO ni un ADMIN: a los medicos
        // los da de alta un administrador, que verifica su CMP.
        Usuario usuario = usuarioService.crearCuenta(
                peticion.email(), peticion.dni(), peticion.password(), Rol.PACIENTE);

        // El modulo paciente escucha este evento y crea la ficha en esta misma
        // transaccion. El correo de bienvenida lo manda notificacion aparte.
        eventos.usuarioRegistrado(usuario,
                peticion.nombres(),
                peticion.apellidoPaterno(),
                peticion.apellidoMaterno(),
                peticion.fechaNacimiento(),
                peticion.sexo(),
                peticion.telefono());

        log.info("Usuario {} registrado como PACIENTE", usuario.getId());
        return emitirTokens(usuario);
    }

    /**
     * noRollbackFor no es opcional aqui.
     *
     * Al fallar la contrasena se incrementa intentos_fallidos y acto seguido
     * se lanza la excepcion. Como es una RuntimeException, Spring revertiria
     * la transaccion y descartaria el incremento: el contador se quedaria
     * siempre en cero y el bloqueo tras MAXIMO_INTENTOS no se activaria nunca.
     *
     * En este metodo lo unico que se escribe es ese contador y, al alcanzar el
     * limite, el cambio de estado con el cierre de sesiones. Todo eso debe
     * persistir aunque la peticion termine en 401.
     */
    @Override
    @Transactional(noRollbackFor = CredencialesInvalidasException.class)
    public AuthResponse iniciarSesion(LoginRequest peticion) {
        String correo = peticion.email().trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByEmail(correo)
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(peticion.password(), usuario.getPasswordHash())) {
            boolean quedoBloqueado = usuario.registrarIntentoFallido(MAXIMO_INTENTOS);
            if (quedoBloqueado) {
                refreshTokenRepository.revocarTodosDelUsuario(usuario.getId(), Instant.now());
                log.warn("Cuenta {} bloqueada tras {} intentos fallidos",
                        usuario.getId(), MAXIMO_INTENTOS);
            }
            throw new CredencialesInvalidasException();
        }

        // El estado se revisa despues de la contrasena. Al reves, un atacante
        // sabria que la cuenta existe con solo ver un mensaje distinto.
        if (!usuario.puedeIniciarSesion()) {
            throw new AuthException(ErrorCode.USUARIO_BLOQUEADO);
        }

        usuario.registrarAccesoExitoso();
        log.info("Inicio de sesion correcto del usuario {}", usuario.getId());
        return emitirTokens(usuario);
    }

    @Override
    public AuthResponse renovar(String refreshToken) {
        Claims claims = jwt.leerClaims(refreshToken);

        if (!jwt.esRefreshToken(claims)) {
            throw new AuthException(ErrorCode.TOKEN_INVALIDO,
                    "Se esperaba un refresh token");
        }

        String jti = jwt.obtenerJti(claims);
        RefreshToken guardado = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new AuthException(ErrorCode.TOKEN_REVOCADO));

        if (!guardado.esUtilizable()) {
            throw new AuthException(ErrorCode.TOKEN_REVOCADO);
        }

        Usuario usuario = guardado.getUsuario();
        if (!usuario.puedeIniciarSesion()) {
            throw new AuthException(ErrorCode.USUARIO_BLOQUEADO);
        }

        // Rotacion: el refresh usado se revoca y se emite uno nuevo. Si alguien
        // reutiliza un refresh ya canjeado, lo encontrara revocado.
        guardado.revocar();

        log.debug("Sesion renovada para el usuario {}", usuario.getId());
        return emitirTokens(usuario);
    }

    @Override
    public void cerrarSesion(String refreshToken, String accessToken) {
        // Revocar el refresh token impide renovar la sesion.
        try {
            Claims claims = jwt.leerClaims(refreshToken);
            refreshTokenRepository.findByJti(jwt.obtenerJti(claims))
                    .filter(rt -> !rt.isRevocado())
                    .ifPresent(RefreshToken::revocar);
        } catch (AuthException e) {
            // Cerrar sesion con un token ya vencido no es un error: el
            // resultado que el usuario queria ya se cumplio.
            log.debug("Cierre de sesion con un refresh token no valido");
        }

        // Y la lista negra invalida el access token que el cliente ya tiene,
        // que de otro modo seguiria sirviendo hasta caducar. El filtro
        // consulta esa lista en cada peticion.
        if (accessToken != null && !accessToken.isBlank()) {
            Claims claims = jwt.leerClaimsONull(accessToken);
            if (claims != null) {
                listaNegra.revocar(jwt.obtenerJti(claims), jwt.milisegundosRestantes(claims));
            }
        }
    }

    /** Emite el par de tokens y persiste el refresh para poder revocarlo. */
    private AuthResponse emitirTokens(Usuario usuario) {
        String accessToken = jwt.generarAccessToken(usuario.getId(), usuario.getRol().name());

        String jti = jwt.generarJti();
        String refreshToken = jwt.generarRefreshToken(usuario.getId(), jti);

        refreshTokenRepository.save(new RefreshToken(usuario, jti,
                Instant.now().plusMillis(jwt.getRefreshMs())));

        return AuthResponse.de(accessToken, refreshToken, jwt.getExpirationMs(),
                mapper.aResponse(usuario));
    }
}
