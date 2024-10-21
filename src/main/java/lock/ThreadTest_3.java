package lock;

public class ThreadTest_3 {
    static int val = 0;

    static FairLock lock = new FairLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> count());
        Thread t2 = new Thread(() -> count());
        Thread t3 = new Thread(() -> count());
        Thread t4 = new Thread(() -> count());
        Thread t5 = new Thread(() -> count());
        
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();


        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
            t5.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        System.out.println(val);

    }

    static void count() {
        try {
            lock.lock();

            for (int j = 0; j < 10000; j++) {
                val++;
            }
        } finally {
            lock.unlock();
        }
    }

}
