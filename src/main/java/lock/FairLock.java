package lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

public class FairLock extends AQS implements Lock {


    /**
     * 加入队列(存在并发)
     */
    private void addQueue() {
        Node node = new Node(Thread.currentThread(), Node.EXCLUSIVE);
        
        // 队列中还没有数据
        if (null == getTail()) {
            if (compareAndSetTail(getTail(), node)) {
                setHead(node);
                
                // 防止后面解锁时出错
                node.prev = node.next = node;

                System.out.println(Thread.currentThread().getName() + " : 第一个,加入队列成功");

                LockSupport.park();
            }
        } else {
            // 队列中有数据
            Node t = getTail();
            
            node.prev = t;
            
            if (compareAndSetTail(t, node)) {
                t.next = node;

                System.out.println(Thread.currentThread().getName() + " : 不是第一个,加入队列成功");
                
                LockSupport.park();
            }
        }
        
        // 唤醒后抢锁
        lock();
    }
    @Override
    public void lock() {
        int state = getState();
        
        if (0 == state) {
            if (compareAndSetState(0 , 1)) {
                setExclusiveOwnerThread(Thread.currentThread());

                System.out.println(Thread.currentThread().getName() + ": 抢占锁成功");
                
                return ;
            }
        }

        System.out.println(Thread.currentThread().getName() + ": 抢占锁失败");
        addQueue();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        System.out.println("=======================");
        // 清除锁
        setState(0);
        
        // 取出队列第一个元素
        Node h = getHead();
        
        if (null != h.next) {
            setHead(h.next);
            
            // 唤醒线程
            LockSupport.unpark(h.thread);
        } else {
            // 如果是最后一个
            LockSupport.unpark(h.thread);
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
