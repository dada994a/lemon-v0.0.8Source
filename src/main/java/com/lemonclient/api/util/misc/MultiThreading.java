package com.lemonclient.api.util.misc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreading {
   private static final AtomicInteger threadCounter = new AtomicInteger(0);
   private static final ExecutorService SERVICE = Executors.newCachedThreadPool((task) -> {
      return new Thread(task, "Lemon Thread " + threadCounter.getAndIncrement()) {
      };
   });

   public static void runAsync(Runnable task) {
      SERVICE.execute(task);
   }
}
