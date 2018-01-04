package repository;

import akka.actor.ActorSystem;
import scala.concurrent.ExecutionContext;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;

/**
 * Custom execution context, so that blocking database operations don't
 * happen on the rendering thread pool.
 *
 * @link https://www.playframework.com/documentation/latest/ThreadPools
 */
public class DatabaseExecutionContext implements ExecutionContextExecutor {
    private play.Logger.ALogger logger = play.Logger.of(getClass());
    private final ExecutionContext executionContext;

    private static final String name = "mongo-execution-context";

    @Inject
    public DatabaseExecutionContext(ActorSystem actorSystem) {

        this.executionContext = actorSystem.dispatchers().lookup(name);
        logger.warn("ec="+executionContext.toString());
    }

    @Override
    public ExecutionContext prepare() {
        return executionContext.prepare();
    }

    @Override
    public void execute(Runnable command) {
        executionContext.execute(command);
    }

    @Override
    public void reportFailure(Throwable cause) {
        executionContext.reportFailure(cause);
    }
}