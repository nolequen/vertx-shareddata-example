package ru.mail.sphere;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.LockSupport;

@SuppressWarnings({"MagicNumber", "UseOfSystemOutOrSystemErr"})
public final class Stove extends AbstractVerticle {

  @Override
  public void start(@NotNull Promise<Void> startPromise) {
    final String name = config().getString("name");

    vertx.sharedData().getCounter("stoveNumber", counter -> {
      if (counter.succeeded()) {
        counter.result().incrementAndGet(number -> turnOn(name + '#' + number.result()));
        startPromise.complete();
      }
    });
  }

  private static long bakeCookies() {
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    final long secondsToBake = random.nextInt(5);
    LockSupport.parkNanos(secondsToBake * 1_000_000_000L);
    final long cookies = random.nextLong(50);
    System.out.println(cookies + " cookies are ready after " + secondsToBake + " seconds");
    return cookies;
  }

  private void turnOn(@NotNull String stove) {
    System.out.println("Turn on stove " + stove);
    vertx.setPeriodic(10000, timer ->
        vertx.<Long>executeBlocking(
            promise -> promise.complete(bakeCookies()),
            cookies -> report(stove, cookies.result())
        )
    );
  }

  private void report(@NotNull String stove, @NotNull Long cookiesNumber) {
    vertx.sharedData().getAsyncMap("cookies", map ->
        map.result().put(stove, cookiesNumber, completion ->
            System.out.println("Reported " + cookiesNumber + " cookies from " + stove)
        ));
  }
}
