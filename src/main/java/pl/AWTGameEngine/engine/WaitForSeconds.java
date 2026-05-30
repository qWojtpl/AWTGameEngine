package pl.AWTGameEngine.engine;

public class WaitForSeconds {

    private static int awaitingTasks = 0;

    /**
     * Create block of code which will be executed after <code>delay</code> seconds.
     * It won't block the current thread, it will instead create another thread
     * with the wait process.
     * @param operation Code of block to be executed after delay
     * @param delay     Delay (in seconds)
     */
    public static void then(Runnable operation, double delay) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep((long) (delay * 1000));
                operation.run();
            } catch(Exception e) {
                Logger.exception("Exception caught while running operation in WaitForSeconds", e);
            }
            awaitingTasks--;
        }, "WaitForSeconds-" + System.currentTimeMillis());
        thread.start();
        awaitingTasks++;
    }

    public static int getNumberOfAwaitingTasks() {
        return awaitingTasks;
    }

}
