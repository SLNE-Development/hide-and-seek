package dev.slne.hideandnseek.step;

import dev.slne.hideandnseek.GameData;
import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.step.steps.EndGameStep;
import dev.slne.hideandnseek.step.steps.EndingGameStep;
import dev.slne.hideandnseek.step.steps.IngameStep;
import dev.slne.hideandnseek.step.steps.LobbyStep;
import dev.slne.hideandnseek.step.steps.PreparationStep;
import dev.slne.hideandnseek.util.Continuation;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;


public enum GameStepManager {
  INSTANCE;

  private ObjectSet<GameStep> gameSteps;
  private boolean running = false;

  public @NotNull CompletableFuture<Void> prepareGame(
      HideAndSeekGame game,
      GameData gameData
  ) {
    if (running) {
      throw new IllegalStateException("Game is already running");
    }

    this.running = true;

    gameSteps = ObjectSet.of(
        new LobbyStep(game, gameData),
        new PreparationStep(game, gameData),
        new IngameStep(game, gameData),
        new EndingGameStep(gameData),
        new EndGameStep()
    );

    final CompletableFuture<Void> future = new CompletableFuture<>();
    executeStepsSequentially(gameSteps.iterator(), GameStep::load, future);

    return future;
  }

  public CompletableFuture<Void> startGame() {
    if (!running) {
      throw new IllegalStateException("Game is not running");
    }

    final CompletableFuture<Void> future = new CompletableFuture<>();
    executeStepsSequentially(gameSteps.iterator(), GameStep::start, future);

    return future;
  }

  public CompletableFuture<Void> stopGame(HideAndSeekEndReason reason) {
    if (!running) {
      throw new IllegalStateException("Game is not running");
    }

    final CompletableFuture<Void> future = new CompletableFuture<>();
    executeStepsSequentially(
        gameSteps.iterator(),
        (gameStep, continuation) -> gameStep.end(reason, continuation),
        future
    );

    return future.thenRun(() -> running = false);
  }

  private void executeStepsSequentially(
      Iterator<GameStep> stepsIterator,
      BiConsumer<GameStep, Continuation> executor,
      CompletableFuture<Void> finalFuture
  ) {
    if (stepsIterator.hasNext()) {
      final GameStep step = stepsIterator.next();
      final Continuation continuation = new Continuation();

      executor.accept(step, continuation);

      continuation.getFuture()
          .thenRun(() -> executeStepsSequentially(stepsIterator, executor, finalFuture))
          .exceptionally(ex -> {
            finalFuture.completeExceptionally(ex);
            return null;
          });
    } else {
      finalFuture.complete(null);
    }
  }

  public CompletableFuture<Void> resetGame() {
    if (!running) {
      throw new IllegalStateException("Game is not running");
    }

    final CompletableFuture<Void> future = new CompletableFuture<>();
    executeStepsSequentially(gameSteps.iterator(), GameStep::reset, future);

    return future;
  }
}
