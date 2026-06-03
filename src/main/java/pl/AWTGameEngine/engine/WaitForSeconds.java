package pl.AWTGameEngine.engine;

public class WaitForSeconds {

    private final double delay;
    private static int awaitingTasks = 0;

    public WaitForSeconds(double delay) {
        this.delay = delay;
    }

    /**
     * Create block of code which will be executed after <code>delay</code> seconds.
     * It won't block the current thread, it will instead create another thread
     * with the wait process.
     * @param operation Code of block to be executed after delay
     */
    public void then(Runnable operation) {
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

    /** This method blocks the current thread and waits for <code>delay</code> seconds. */
    public void here() {
        try {
            Thread.sleep((long) (delay * 1000));
        } catch(Exception e) {
            Logger.exception("Exception caught while running operation in WaitForSeconds", e);
        }
    }

    public static int getNumberOfAwaitingTasks() {
        return awaitingTasks;
    }

}
