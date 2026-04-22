# IsNote Backend

Backend Spring Boot do IsNote, configurado com Maven.

## Requisitos

- Java 21
- Maven 3.9+
- PostgreSQL local via Docker Compose

## Subir o banco

Na raiz do monorepo:

```bash
docker compose up -d
```

## Rodar a aplicação

Dentro de `backendIsNote/`:

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
mvn spring-boot:run
```

Se precisar usar outra porta do banco:

```bash
DB_PORT=5433 mvn spring-boot:run
```

## Comandos úteis

```bash
mvn test
mvn clean package
```

## Configuração padrão

- `DB_HOST=localhost`
- `DB_PORT=5433`
- `DB_NAME=isnote`
- `DB_USER=isnote`
- `DB_PASSWORD=isnote_dev_secret`
