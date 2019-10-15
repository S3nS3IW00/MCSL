package app.mcsl.manager.thread;

public abstract class RunnableThread implements Runnable {

    private Thread t;
    private String threadName;

    boolean cancel = false;

    public RunnableThread(String threadName) {
        this.threadName = threadName;
    }

    public void run() {
        while (!cancel) {
            onRun();
        }
    }

    public void start() {
        cancel = false;
        t = new Thread(this, threadName);
        t.start();
    }

    public abstract void onRun();

    public Thread getThread() {
        return t;
    }

    public void cancel() {
        cancel = true;
    }
}
