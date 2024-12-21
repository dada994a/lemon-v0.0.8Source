package com.lemonclient.api.util.chat;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class SkippingCounter {
   private final AtomicInteger counter;
   private final Predicate<Integer> skip;
   private final int initial;

   public SkippingCounter(int initial, Predicate<Integer> skip) {
      this.counter = new AtomicInteger(initial);
      this.initial = initial;
      this.skip = skip;
   }

   public int get() {
      return this.counter.get();
   }

   public int next() {
      int result;
      do {
         result = this.counter.incrementAndGet();
      } while(!this.skip.test(result));

      return result;
   }

   public void reset() {
      this.counter.set(this.initial);
   }
}
