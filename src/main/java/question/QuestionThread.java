package question;

/**
 * 子线程跑10次主线程跑5次，循环跑3次
 * @author: wangpeng
 * @date: 2020/1/14 20:32
 */
public class QuestionThread {
    public static void main(String[] args) {
        Bussiness bussiness = new Bussiness();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    bussiness.subMethod();
                    System.out.println("sub thread===============");
                }
            }
        }).start();
        for (int i = 0; i < 3; i++) {
            bussiness.mainMethod();
            System.out.println("main thread===============");
        }
    }
    static class Bussiness {
        private boolean subFlag = true;

        public synchronized void mainMethod() {
            while (subFlag) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName() + " : main thread running loop count " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            subFlag = true;
            notify();
        }

        public synchronized void subMethod() {
            while (!subFlag) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 10; i++) {
                System.err.println(Thread.currentThread().getName() + " : sub thread running loop count " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            subFlag = false;
            notify();
        }
    }
}
