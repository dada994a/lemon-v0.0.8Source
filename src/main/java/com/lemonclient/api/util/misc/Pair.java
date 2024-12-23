package com.lemonclient.api.util.misc;

public class Pair<T, S> {
   T key;
   S value;

   public Pair(T key, S value) {
      this.key = key;
      this.value = value;
   }

   public T getKey() {
      return this.key;
   }

   public S getValue() {
      return this.value;
   }

   public void setKey(T key) {
      this.key = key;
   }

   public void setValue(S value) {
      this.value = value;
   }
}
