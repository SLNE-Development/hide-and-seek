package dev.slne.hideandnseek.step;

import dev.slne.hideandnseek.GameSettings;
import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.step.steps.EndGameStep;
import dev.slne.hideandnseek.step.steps.IngameStep;
import dev.slne.hideandnseek.step.steps.LobbyStep;
import dev.slne.hideandnseek.step.steps.PreparationStep;
import dev.slne.hideandnseek.util.Continuation;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import lombok.Getter;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public enum GameStepManager {
  INSTANCE;

  private static final ComponentLogger LOGGER = ComponentLogger.logger("HideAndSeek");
  private ObjectSet<GameStep> gameSteps;

  private boolean running = false;
  private boolean interrupt = false;

//  @Nullable
//  private CompletableFuture<Void> currentFuture;

  @Getter
  @Nullable
  private GameStep currentStep;

  public @NotNull CompletableFuture<Void> prepareGame(
      HideAndSeekGame game,
      GameSettings gameSettings
  ) {
    if (running) {
      throw new IllegalStateException("Game is already running");
    }

    this.running = true;

    gameSteps = ObjectSet.of(
        new LobbyStep(game, gameSettings),
        new PreparationStep(game, gameSettings),
        new IngameStep(game, gameSettings),
        new EndGameStep(game, gameSettings)
    );

    final CompletableFuture<Void> future = new CompletableFuture<>();
    executeStepsSequentially(gameSteps.iterator(), GameStep::load, future, false);

    return future.exceptionally(ex -> {
      running = false;
      LOGGER.error("An error occurred while preparing the game", ex);

      return null;
    });
  }

  public CompletableFuture<Void> startGame() {
    if (!running) {
      throw new IllegalStateException("Game is not running");
    }

    final CompletableFuture<Void> future = new CompletableFuture<>();
    executeStepsSequentially(gameSteps.iterator(), GameStep::start, future, false);

    return future.exceptionally(ex -> {
      running = false;
      LOGGER.error("An error occurred while starting the game", ex);

      return null;
    });
  }

  public CompletableFuture<Void> stopGame(
      HideAndSeekEndReason reason, boolean interrupt) { // TODO: 06.09.2024 21:49 - call
    if (!running) {
      throw new IllegalStateException("Game is not running");
    }

    if (interrupt) {
      System.err.println("stop Interrupting game");
      this.interrupt = true;
      if (currentStep != null) {
        currentStep.interrupt();
      }
    }

//    if (currentFuture != null) {
//      currentFuture.cancel(true);
//    }

    final CompletableFuture<Void> future = new CompletableFuture<>();
    executeStepsSequentially(
        gameSteps.iterator(),
        (gameStep, continuation) -> gameStep.end(reason, continuation),
        future,
        true
    );

    return future.thenRun(() -> running = false)
        .thenRun(() -> {
          System.err.println("Game stopped 3507387095");
        })
        .exceptionally(ex -> {
      LOGGER.error("An error occurred while stopping the game", ex);
      return null;
    });
  }

  public CompletableFuture<Void> forceStop() {
    if (!running) {
      throw new IllegalStateException("Game is not running");
    }

    interrupt = true;

//    if (currentFuture != null) {
//      currentFuture.cancel(true);
//    }

    return stopGame(HideAndSeekEndReason.FORCED_END, interrupt);
  }

  public CompletableFuture<Void> resetGame() {
    final CompletableFuture<Void> future = new CompletableFuture<>();
    executeStepsSequentially(gameSteps.iterator(), GameStep::reset, future, true);

    return future.thenRun(() -> {
      currentStep = null;
      running = false;
      interrupt = false;
    }).exceptionally(ex -> {
      LOGGER.error("An error occurred while resetting the game", ex);
      return null;
    });
  }

  private void executeStepsSequentially(
      Iterator<GameStep> stepsIterator,
      BiConsumer<GameStep, Continuation> executor,
      CompletableFuture<Void> finalFuture,
      boolean ignoreInterrupt
  ) {
    if (interrupt && !ignoreInterrupt) {
      finalFuture.complete(null);

      if (currentStep != null) {
        currentStep.interrupt();
        currentStep = null;
      }

      return;
    }

    if (stepsIterator.hasNext()) {
      final GameStep step = stepsIterator.next();
      final Continuation continuation = new Continuation();

      executor.accept(step, continuation);

//      currentFuture =
          continuation.getFuture()
          .thenRun(() -> executeStepsSequentially(stepsIterator, executor, finalFuture, ignoreInterrupt))
          .exceptionally(ex -> {
            finalFuture.completeExceptionally(ex);
            return null;
          });
      currentStep = step;
    } else {
      finalFuture.complete(null);
    }
  }
}
