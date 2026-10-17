package com.proyecto.gestioncitas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Verifica la regla fundamental de la arquitectura (instruccion M1):
 * ningun modulo accede a las clases internas de otro.
 *
 * Es lo que convierte esa regla en una condicion comprobable en lugar de un
 * acuerdo de equipo. Si alguien importa un repositorio o una entidad de otro
 * modulo, esta prueba falla y la construccion del proyecto se detiene.
 *
 * Spring Modulith expone por defecto solo el paquete raiz de cada modulo. Los
 * subpaquetes que si forman parte del contrato publico se declaran con
 * @NamedInterface en su package-info.java: api para las Facades, eventos para
 * los eventos de dominio y enums para los tipos de valor compartidos.
 */
class ModularityTest {

    private final ApplicationModules modulos = ApplicationModules.of(GestionCitasApplication.class);

    @Test
    @DisplayName("ningun modulo accede a las clases internas de otro")
    void verificarAislamientoEntreModulos() {
        modulos.verify();
    }

    @Test
    @DisplayName("la estructura de modulos se puede describir")
    void imprimirEstructura() {
        // No afirma nada: deja la estructura detectada en la salida de la
        // prueba, que sirve para revisar que modulo depende de cual.
        modulos.forEach(System.out::println);
    }
}
