## Tapir Java Agent Reproduction

- docker-compose up -d
- runMain
- run
  `curl http://localhost:8080/api/v1/subitems`
```
[error] === PekkoRouteHolder Debug ===
[error] holder.stack: []
[error] holder.stack.getClass(): class java.util.ArrayDeque
[error] holder.route: null
[error] Thread: minimal-tapir-app-pekko.actor.default-dispatcher-8
[error] [otel.javaagent 2025-02-14 10:48:11:268 -0600] [minimal-tapir-app-pekko.actor.default-
dispatcher-8] DEBUG io.opentelemetry.javaagent.bootstrap.ExceptionLogger - Failed to handle ex
ception in instrumentation for org.apache.pekko.http.scaladsl.server.RouteConcatenation$RouteW
ithConcatenation
[error] java.lang.NullPointerException
[error]         at java.base/java.util.ArrayDeque.addFirst(ArrayDeque.java:286)
[error]         at java.base/java.util.ArrayDeque.push(ArrayDeque.java:579)
[error]         at io.opentelemetry.javaagent.instrumentation.pekkohttp.v1_0.server.route.Pekk
oRouteHolder.save(PekkoRouteHolder.java:65)
[error]         at org.apache.pekko.http.scaladsl.server.RouteConcatenation$RouteWithConcatena
tion.$anonfun$$tilde$1(RouteConcatenation.scala:55)
[error]         at org.apache.pekko.http.scaladsl.server.RouteConcatenation$RouteWithConcatena
tion.$anonfun$$tilde$2(RouteConcatenation.scala:58)
[error]         at org.apache.pekko.http.scaladsl.util.FastFuture$.strictTransform$1(FastFutur
e.scala:49)
[error]         at org.apache.pekko.http.scaladsl.util.FastFuture$.$anonfun$transformWith$3(Fa
stFuture.scala:59)
[error]         at scala.concurrent.impl.Promise$Transformation.run(Promise.scala:484)
[error]         at org.apache.pekko.dispatch.BatchingExecutor$AbstractBatch.processBatch(Batch
ingExecutor.scala:73)
[error]         at org.apache.pekko.dispatch.BatchingExecutor$BlockableBatch.$anonfun$run$1(Ba
tchingExecutor.scala:110)
[error]         at scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
[error]         at scala.concurrent.BlockContext$.withBlockContext(BlockContext.scala:94)
[error]         at org.apache.pekko.dispatch.BatchingExecutor$BlockableBatch.run(BatchingExecu
tor.scala:110)
[error]         at org.apache.pekko.dispatch.TaskInvocation.run(AbstractDispatcher.scala:59)
[error]         at org.apache.pekko.dispatch.ForkJoinExecutorConfigurator$PekkoForkJoinTask.ex
ec(ForkJoinExecutorConfigurator.scala:61)
[error]         at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
[error]         at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoin
Pool.java:1182)
[error]         at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
[error]         at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:162
2)
[error]         at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThrea
d.java:165)
[error] [otel.javaagent 2025-02-14 10:48:11:269 -0600] [minimal-tapir-app-pekko.actor.default-
dispatcher-8] DEBUG io.opentelemetry.javaagent.bootstrap.ExceptionLogger - Failed to handle ex
ception in instrumentation for org.apache.pekko.http.scaladsl.server.RouteConcatenation$RouteW
ithConcatenation
[error] java.util.NoSuchElementException
[error]         at java.base/java.util.ArrayDeque.removeFirst(ArrayDeque.java:362)
[error]         at java.base/java.util.ArrayDeque.pop(ArrayDeque.java:593)
[error]         at io.opentelemetry.javaagent.instrumentation.pekkohttp.v1_0.server.route.Pekk
oRouteHolder.restore(PekkoRouteHolder.java:81)
[error]         at org.apache.pekko.http.scaladsl.server.RouteConcatenation$RouteWithConcatena
tion.$anonfun$$tilde$1(RouteConcatenation.scala:55)
[error]         at org.apache.pekko.http.scaladsl.server.RouteConcatenation$RouteWithConcatena
tion.$anonfun$$tilde$2(RouteConcatenation.scala:58)
[error]         at org.apache.pekko.http.scaladsl.util.FastFuture$.strictTransform$1(FastFutur
e.scala:49)
[error]         at org.apache.pekko.http.scaladsl.util.FastFuture$.$anonfun$transformWith$3(Fa
stFuture.scala:59)
[error]         at scala.concurrent.impl.Promise$Transformation.run(Promise.scala:484)
[error]         at org.apache.pekko.dispatch.BatchingExecutor$AbstractBatch.processBatch(Batch
ingExecutor.scala:73)
[error]         at org.apache.pekko.dispatch.BatchingExecutor$BlockableBatch.$anonfun$run$1(Ba
tchingExecutor.scala:110)
[error]         at scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
[error]         at scala.concurrent.BlockContext$.withBlockContext(BlockContext.scala:94)
[error]         at org.apache.pekko.dispatch.BatchingExecutor$BlockableBatch.run(BatchingExecu
tor.scala:110)
[error]         at org.apache.pekko.dispatch.TaskInvocation.run(AbstractDispatcher.scala:59)
[error]         at org.apache.pekko.dispatch.ForkJoinExecutorConfigurator$PekkoForkJoinTask.ex
ec(ForkJoinExecutorConfigurator.scala:61)
[error]         at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
[error]         at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoin
Pool.java:1182)
[error]         at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
[error]         at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:162
2)
[error]         at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThrea
d.java:165)
```

