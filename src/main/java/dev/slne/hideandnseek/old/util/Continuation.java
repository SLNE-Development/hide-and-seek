package dev.slne.hideandnseek.old.util;

import java.util.concurrent.CompletableFuture;
import lombok.Getter;

@Getter
public class Continuation {

  private final CompletableFuture<Void> future = new CompletableFuture<>();

  public void resume() {
    if (future.isDone()) {
      throw new IllegalStateException("Continuation already completed");
    }

    future.complete(null);
  }

  public void resumeWithException(Throwable throwable) {
    if (future.isDone()) {
      throw new IllegalStateException("Continuation already completed");
    }

    future.completeExceptionally(throwable);
  }

}
