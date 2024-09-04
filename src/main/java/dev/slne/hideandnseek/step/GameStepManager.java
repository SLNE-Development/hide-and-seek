package dev.slne.hideandnseek.step;

import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import dev.slne.hideandnseek.step.steps.EndingGameStep;
import dev.slne.hideandnseek.step.steps.IngameStep;
import dev.slne.hideandnseek.step.steps.LobbyStep;
import dev.slne.hideandnseek.step.steps.PreparationStep;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.time.Duration;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.World;

public class GameStepManager {

  private ObjectSet<GameStep> gameSteps;
  private boolean running = false;

  public CompletableFuture<Void> prepareGame(
      HideAndSeekGame game,
      GameData gameData
  ) {
    if (running) {
      throw new IllegalStateException("Game is already running");
    }

    gameSteps = ObjectSet.of(
        new LobbyStep(game, gameData),
        new PreparationStep(game, gameData),
        new IngameStep(game, gameData),
        new EndingGameStep(game, gameData)
    );

    final CompletableFuture<Void> future = new CompletableFuture<>();
    executeStepsSequentially(gameSteps.iterator(), GameStep::load, future);

    return future;
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


  @Builder
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @Getter
  public static class GameData {

    private final Duration lobbyTime;
    private final Duration preparationTime;
    private final Duration gameDuration;
    private final Duration shrinkTime;
    private final HideAndSeekPlayer initialSeeker;
    private final World world;
    private final int initialRadius;
  }

  @Getter
  public static class Continuation {

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
}
