package test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * 线程池创建的七种方式
 * author:WH
 * Date:2020/12/18 10:24
 */
public class Test01 {
    public static void main(String[] args) {

        //Executors.newFixedThreadPool(固定大小线程池)
        // fixedThreadPool();

        //Executors.newCachedThreadPool(可缓存线程池)
        // cachedThreadPool();

        //Executors.newSingleThreadExecutor(单个线程数的线程池)
        // singleThreadExecutor();

        //Executors.newScheduledThreadPool(可延迟任务线程池)
        // scheduledThreadPool();

        //Executors.newSingleThreadScheduledExecutor(单线程的可以执行延迟任务的线程池)
        SingleThreadScheduledExecutor();
        
        //Executors.newWorkStealingPool(抢占式执行的线程池)
        // workStealingPool();

        //ThreadPoolExecutor(自定义创建)
        threadPoolExecutor();

    }



    /**
     * new ThreadPoolExecutor()
     * 最原始的创建线程池的方式，它包含了 7 个参数可供设置。
     * 参数介绍：
     * corePoolSize         核心线程数，线程池中始终存活的线程数。
     * maximumPoolSize      最大线程数，线程池中允许的最大线程数，当线程池的任务队列满了之后可以创建的最大线程数。
     * keepAliveTime        最大线程数可以存活的时间，当线程中没有任务执行时，最大线程就会销毁一部分，最终保持核心线程数量的线程。
     * unit                 单位是和参数 3 存活时间配合使用的，合在一起用于设定线程的存活时间
     * workQueue            一个阻塞队列，用来存储线程池等待执行的任务，均为线程安全，它包含以下 7 种类型：
     *       ArrayBlockingQueue：一个由数组结构组成的有界阻塞队列。
     *       LinkedBlockingQueue：一个由链表结构组成的有界阻塞队列。
     *       SynchronousQueue：一个不存储元素的阻塞队列，即直接提交给线程不保持它们。
     *       PriorityBlockingQueue：一个支持优先级排序的无界阻塞队列
     *       DelayQueue：一个使用优先级队列实现的无界阻塞队列，只有在延迟期满时才能从中提取元素。
     *       LinkedTransferQueue：一个由链表结构组成的无界阻塞队列。与SynchronousQueue类似，还含有非阻塞方法。
     *       LinkedBlockingDeque：一个由链表结构组成的双向阻塞队列。
     * threadFactory        线程工厂，主要用来创建线程，默认为正常优先级、非守护线程。
     * handler              拒绝策略，拒绝处理任务时的策略，系统提供了 4 种可选：
     *      AbortPolicy：拒绝并抛出异常。
     *      CallerRunsPolicy：使用当前调用的线程来执行此任务。
     *      DiscardOldestPolicy：抛弃队列头部（最旧）的一个任务，并执行当前任务。
     *      DiscardPolicy：忽略并抛弃当前任务。
     *      默认策略为 AbortPolicy也可使用自定义拒绝策略
     */
    private static void threadPoolExecutor() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 2, 100,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(1)
                , (r, executor) -> {
                    new Thread(r).start();
                });
        //
        // for (int i = 0; i < 20; i++) {
        //     int finalI = i;
        //     threadPoolExecutor.execute(() -> {
        //         System.out.println(finalI + "执行" + Thread.currentThread().getName());
        //         stop();
        //     });
        // }
        threadPoolExecutor.execute(Test01::exec);
        threadPoolExecutor.execute(Test01::exec);
        threadPoolExecutor.execute(Test01::exec);
        threadPoolExecutor.execute(Test01::exec);
        threadPoolExecutor.execute(Test01::exec);

    }

    /**
     * Executors.newSingleThreadScheduledExecutor()
     * 创建一个单线程的可以执行延迟任务的线程池。
     */
    private static void SingleThreadScheduledExecutor() {
        ScheduledExecutorService threadPool = Executors.newSingleThreadScheduledExecutor();
        System.out.println("添加任务,时间:" + LocalDate.now());
        threadPool.schedule(() -> {
            System.out.println("任务被执行,时间:" + LocalDate.now());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
        }, 2, TimeUnit.SECONDS);
    }

    /**
     * Executors.newWorkStealingPool()
     * 创建一个抢占式执行的线程池（任务执行顺序不确定），注意此方法只有在 JDK 1.8+ 版本中才能使用。
     */
    private static void workStealingPool() {
        ExecutorService workStealingPool = Executors.newWorkStealingPool();
        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            workStealingPool.execute(() ->{
                System.out.println(finalI + "执行" + Thread.currentThread().getName());
            });

        }
        while (!workStealingPool.isTerminated()) {
            workStealingPool.shutdown();
        }
    }

    /**
     * Executors.newScheduledThreadPool(5)
     * 创建一个可以执行延迟任务的线程池。
     */
    private static void scheduledThreadPool() {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        System.out.println("添加任务，时间：" + LocalDateTime.now());
        scheduledThreadPool.schedule(() -> {
            System.out.println("执行任务，时间：" + LocalDateTime.now());
            stop();
        },10,TimeUnit.SECONDS );

        scheduledThreadPool.shutdown();
    }

    /**
     * Executors.newSingleThreadExecutor();
     * 创建单个线程数的线程池，它可以保证先进先出的执行顺序。
     */
    private static void singleThreadExecutor() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

        for (int i = 0; i < 10; i++) {
            final int index = i;
            singleThreadExecutor.execute(() -> {
                System.out.println(index);
                stop();
            });
        }
        singleThreadExecutor.shutdown();

    }

    /**
     * Executors.newCachedThreadPool()
     * 创建一个可缓存的线程池，若线程数超过处理所需，缓存一段时间后会回收，若线程数不够，则新建线程。
     */
    private static void cachedThreadPool() {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            cachedThreadPool.execute(Test01::exec);
        }
        cachedThreadPool.shutdown();
    }

    /**
     * Executors.newFixedThreadPool
     * 创建一个固定大小的线程池，可控制并发的线程数，超出的线程会在队列中等待；
     */
    private static void fixedThreadPool() {
        ExecutorService fixedExecutor = Executors.newFixedThreadPool(3);
        fixedExecutor.submit(Test01::exec);
        fixedExecutor.execute(Test01::exec);
        fixedExecutor.execute(Test01::exec);
        fixedExecutor.execute(Test01::exec);
        fixedExecutor.shutdown();
    }

    public static void stop() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void exec() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("任务被执行,线程:" + Thread.currentThread().getName());
    }
}
