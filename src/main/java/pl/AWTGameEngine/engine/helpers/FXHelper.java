package pl.AWTGameEngine.engine.helpers;

import javafx.application.Platform;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class FXHelper {

    /**
     * Used to execute task on JavaFX thread, but synchronized - invoking thread
     * will wait for the result of the task.
     * @param task  Task to be executed
     * @return      Result of a task
     */
    public static <T> T synchronizedCall(Callable<T> task) {
        if(Platform.isFxApplicationThread()) {
            try {
                return task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        FutureTask<T> future = new FutureTask<>(task);
        Platform.runLater(future);

        try {
            return future.get();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
