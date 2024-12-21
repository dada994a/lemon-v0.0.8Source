package com.lemonclient.api.util.verify;

public class NoStackTraceThrowable extends RuntimeException {
   public NoStackTraceThrowable(String msg) {
      super(msg);
      this.setStackTrace(new StackTraceElement[0]);
   }

   public String toString() {
      return "Oh no!";
   }

   public synchronized Throwable fillInStackTrace() {
      return this;
   }
}
