.PHONY: up down deps seed logs health swagger test

COMPOSE      = docker compose -f infra/docker/docker-compose.yml
COMPOSE_DEPS = docker compose -f infra/docker/docker-compose.deps.yml

## Levantar todo el stack
up:
	$(COMPOSE) up -d --build

## Detener todo
down:
	$(COMPOSE) down -v

## Solo dependencias (BDs + Kafka) — backend desde el IDE
deps:
	$(COMPOSE_DEPS) up -d

## Cargar datos de prueba
seed:
	bash infra/scripts/seed.sh

## Logs en tiempo real
logs:
	$(COMPOSE) logs -f

## Verificar salud del backend
health:
	curl -s http://localhost:8080/actuator/health | python -m json.tool

## Abrir Swagger UI
swagger:
	start http://localhost:8080/swagger-ui.html

## Tests del backend
test:
	cd backend && mvn test

## Build completo con integración
verify:
	cd backend && mvn verify