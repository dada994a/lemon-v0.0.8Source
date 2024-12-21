package com.lemonclient.api.util.misc;

import java.util.Iterator;
import java.util.function.ToIntFunction;

public class CollectionUtils {
   public static <T> T maxOrNull(Iterable<T> iterable, ToIntFunction<T> block) {
      int value = Integer.MIN_VALUE;
      T maxElement = null;
      Iterator var4 = iterable.iterator();

      while(var4.hasNext()) {
         T element = var4.next();
         int newValue = block.applyAsInt(element);
         if (newValue > value) {
            value = newValue;
            maxElement = element;
         }
      }

      return maxElement;
   }
}
