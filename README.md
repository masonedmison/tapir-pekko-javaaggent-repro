## Tapir Java Agent Reproduction

- docker-compose up -d
- `sbt run`
- run
  `curl http://localhost:8080/api/v1/subitems`

```
[error] [otel.javaagent 2025-05-21 10:10:20:174 -0500] [minimal-tapir-app-pekko.actor.default-dispatcher-8
] DEBUG io.opentelemetry.javaagent.shaded.io.opentelemetry.context.ThreadLocalContextStorage -  Trying to
close scope which does not represent current context. Ignoring the call.
```
