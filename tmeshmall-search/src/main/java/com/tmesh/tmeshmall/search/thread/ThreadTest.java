package com.tmesh.tmeshmall.search.thread;

import java.util.concurrent.*;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 * @createTime: 2024-02-18 11:16
 **/
public class ThreadTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 1.测试callable，配合FutureTask（本质也是runnable）
        FutureTask<Integer> futureTask = new FutureTask<>(new MyCallable());// 【new FutureTask<>(new MyRunnable(), result)】可以接受runnable的返回值
        new Thread(futureTask).start();
        System.out.println(futureTask.get());// 阻塞等待异步执行结束获取结果

        // 2.创建一个固定线程数的线程池，执行异步任务
        ExecutorService pool = Executors.newFixedThreadPool(10);// 使用了线程池，所以当前程序不会结束
        Future<?> future = pool.submit(new MyCallable());
        System.out.println("future: " + future.get());

        // 3.创建一个原生线程池
        new ThreadPoolExecutor(5,
                200,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());

        // 4.创建常用的4种线程池
        ExecutorService executor = Executors.newFixedThreadPool(10);// 使用了线程池，所以当前程序不会结束
        Executors.newCachedThreadPool();
        Executors.newScheduledThreadPool(10);
        Executors.newSingleThreadExecutor();

        /**
         * 线程串行化
         * 1、thenRunL：不能获取上一步的执行结果
         * 2、thenAcceptAsync：能接受上一步结果，但是无返回值
         * 3、thenApplyAsync：能接受上一步结果，有返回值
         *
         */
        // 5.测试CompletableFuture
        // 5.1.提交任务异步执行(supplyAsync)
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "测试使用", executor);
        System.out.println(future1.get());
        // 5.2.获取上一步结果并链式异步调用(thenApplyAsync)【 能获取上一步结果 + 有返回值】
        CompletableFuture<String> future2 = future1.thenApplyAsync(s -> s + " 链式调用", executor);// 参数s是上一步的返回值
        System.out.println(future2.get());
        // 5.3.获取上一步执行结果并获取异常信息(whenCompleteAsync)
        CompletableFuture<String> future3 = future2.whenCompleteAsync((result, exception) -> System.out.println("结果是：" + result + "----异常是：" + exception));
        // 5.4.获取上一步异常，如果出现异常返回默认值，不出现异常保持原值(exceptionally)
        CompletableFuture<Integer> future4 = future3.thenApplyAsync((s -> 1 / 0), executor);
        CompletableFuture<Integer> future5 = future4.exceptionally(exception -> {
            System.out.println("出现异常：" + exception);
            return 10;
        });// 出现异常，使用默认返回值
        System.out.println("默认值：" + future5.get());
        // 5.5.方法执行完成后的处理
        CompletableFuture<Integer> future6 = future3.thenApplyAsync((s -> 1 / 0), executor).handle((result, exception) -> {
            if (exception == null) {
                return result;
            }
            System.out.println("handle处理异常：" + exception);
            return 1;
        });
        System.out.println("handle处理返回结果：" + future6.get());

        // 5.6.1.二者都要完成，组合【不获取前两个任务返回值，且自己无返回值】
        CompletableFuture<Integer> future011 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务11执行");
            return 10 / 2;
        }, executor);
        CompletableFuture<String> future012 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务12执行");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return "hello";
        }, executor);
        CompletableFuture<Void> future013 = future011.runAfterBothAsync(future012, () -> {
            System.out.println("任务13执行");
        }, executor);

        // 5.6.2.二者都要完成，组合【获取前两个任务返回值，自己无返回值】
        CompletableFuture<Integer> future021 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务21执行");
            return 10 / 2;
        }, executor);
        CompletableFuture<String> future022 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务22执行");
            return "hello";
        }, executor);
        CompletableFuture<Void> future023 = future021.thenAcceptBothAsync(future022,
                (result1, result2) -> {
                    System.out.println("任务23执行");
                    System.out.println("任务21返回值：" + result1);
                    System.out.println("任务22返回值：" + result2);
                }, executor);

        // 5.6.3.二者都要完成，组合【获取前两个任务返回值，自己有返回值】
        CompletableFuture<Integer> future031 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务31执行");
            return 10 / 2;
        }, executor);
        CompletableFuture<String> future032 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务32执行");
            return "hello";
        }, executor);
        CompletableFuture<String> future033 = future031.thenCombineAsync(future032,
                (result1, result2) -> {
                    System.out.println("任务33执行");
                    System.out.println("任务31返回值：" + result1);
                    System.out.println("任务32返回值：" + result2);
                    return "任务33返回值";
                }, executor);
        System.out.println(future033.get());


        CompletableFuture<Void> allOf = CompletableFuture.allOf(future031, future032, future033);
        allOf.get();// 阻塞等待所有任务完成

        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(future031, future032, future033);
        anyOf.get();// 阻塞等待任一任务完成，返回值是执行成功的任务返回值
    }

    /**
     * 1.测试callable
     */
    public static class MyCallable implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            System.out.println("任务执行");
            return 1;

        }
    }


}

