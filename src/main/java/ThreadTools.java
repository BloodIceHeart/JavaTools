import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ThreadTools implements Callable {
    /**
     * Computes a result, or throws an exception if unable to do so.
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Object call() throws Exception {
        System.out.println(Thread.currentThread().getName() + "Tools run");
        return "back";
    }
    
    @Override
    public Object clone() {
        return new ThreadTools();
    }

    public static void main(String[] args) throws Exception {
        ThreadTools thread = (ThreadTools)new ThreadTools().clone();
        FutureTask tasks = new FutureTask(thread);
        Thread thread1 = new Thread(tasks);
        Thread thread2 = new Thread(tasks);
        System.out.println("start");
        thread1.start();
        System.out.println(tasks.get());
        thread2.start();
        System.out.println(tasks.get());
        System.out.println("end");
    }
}
