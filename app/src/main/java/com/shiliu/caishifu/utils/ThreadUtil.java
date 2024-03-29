package com.shiliu.caishifu.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadUtil {


    private static ThreadFactory threadFactory = new ThreadFactory() {
        private AtomicInteger numbs = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"caishifu-"+ numbs.getAndDecrement());
        }
    };
    private static ExecutorService executor =  Executors.newCachedThreadPool(threadFactory);

    public static ExecutorService getExecutors(){
        return executor;
    }

}
