package ru.mail.sphere;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Verticle;
import io.vertx.core.impl.JavaVerticleFactory;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"MagicNumber", "UseOfSystemOutOrSystemErr"})
public final class Auditor extends AbstractVerticle {
  private final long number;

  private Auditor(long number) {
    this.number = number;
  }

  @Override
  public void start() {
    System.out.println("Start " + number + " auditor");

    vertx.setPeriodic(5000, timer ->
        vertx.sharedData().getAsyncMap("cookies", map ->
            map.result().entries(cookies -> {
                  // System.out.println("Audition:");
                  cookies.result().forEach((stove, cookiesNumber) ->
                      System.out.println(stove + " did " + cookiesNumber + " cookies (" + number + " auditor)")
                  );
                }
            ))
    );
  }

  public static final class Factory extends JavaVerticleFactory {
    private long auditorNumber;

    @Override
    public @NotNull String prefix() {
      return "sphere";
    }

    @SuppressWarnings("ProhibitedExceptionDeclared")
    @Override
    public @NotNull Verticle createVerticle(@NotNull String verticleName, @NotNull ClassLoader classLoader) throws Exception {
      return new Auditor(auditorNumber++);
    }
  }
}