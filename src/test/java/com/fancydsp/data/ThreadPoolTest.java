package com.fancydsp.data;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {
    static class Runner implements Runnable{
        private static Random rand = new Random();
        static int i=0;
        @Override
        public void run() {
            System.out.println("thread " + i ++ +" created");
            synchronized (ThreadPoolTest.class){
            for(int j = 1;j< 100;j++){
                System.out.println("thread "+i + " loop:" + j);

                if(rand.nextInt(100) == 10) {
                   throw new RuntimeException("manual throw");
                }
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException("failed");
                }
            }
            }

        }
    }

    public static void main(String[] args) {
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,0, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>()) {
//            @Override
//            protected void afterExecute(Runnable r, Throwable t) {
//                super.afterExecute(r, t);
//                System.out.println("in after execute ");
//                if(t != null){
//                    execute(new Runner());
//                }
//            }
//        };
//        threadPoolExecutor.execute(new Runner());
        for(int i=0;i<3;i++){
            new Thread(new Runner()).start();
        }
    }
}
