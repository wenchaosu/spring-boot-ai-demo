# spring-boot-ai-demo

Minimal [Spring Boot](https://spring.io/projects/spring-boot) application using [Spring AI](https://spring.io/projects/spring-ai) with the OpenAI chat starter.

## Prerequisites

- JDK 21+
- Maven 3.9+
- An OpenAI API key ([platform.openai.com](https://platform.openai.com/))

## Configure

Set your API key (required to call the model):

```bash
export OPENAI_API_KEY="sk-..."
```

Optional: override model in `src/main/resources/application.yml` under `spring.ai.openai.chat.options.model`.

## Run

```bash
cd spring-boot-ai-demo
mvn spring-boot:run
```

## Try it

- Home: [http://localhost:8080/](http://localhost:8080/)
- Chat: [http://localhost:8080/api/chat?message=Hello](http://localhost:8080/api/chat?message=Hello)

## Build

```bash
mvn -q -DskipTests package
java -jar target/spring-boot-ai-demo-0.0.1-SNAPSHOT.jar
```

## License

Apache 2.0 (same family as Spring projects; use your own license file if you prefer.)
