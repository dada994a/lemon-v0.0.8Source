package com.lemonclient.api.util.chat;

import com.lemonclient.api.util.misc.Timing;

public class AnimationUtil {
   private static final float defaultSpeed = 0.125F;
   private final Timing timerUtil = new Timing();

   public static float moveTowards(float current, float end, float smoothSpeed, float minSpeed, boolean back) {
      float movement = (end - current) * smoothSpeed;
      if (movement > 0.0F) {
         movement = Math.max(minSpeed, movement);
         movement = Math.min(end - current, movement);
      } else if (movement < 0.0F) {
         movement = Math.min(-minSpeed, movement);
         movement = Math.max(end - current, movement);
      }

      return back ? movement - current : current + movement;
   }

   public static double moveTowards(double target, double current, double speed) {
      boolean larger = target > current;
      double dif = Math.max(target, current) - Math.min(target, current);
      double factor = dif * speed;
      if (factor < 0.1D) {
         factor = 0.1D;
      }

      if (larger) {
         current += factor;
      } else {
         current -= factor;
      }

      return current;
   }

   public static double expand(double target, double current, double speed) {
      if (current > target) {
         current = target;
      }

      if (current < -target) {
         current = -target;
      }

      current += speed;
      return current;
   }

   public static float calculateCompensation(float target, float current, long delta, double speed) {
      float diff = current - target;
      if (delta < 1L) {
         delta = 1L;
      }

      if (delta > 1000L) {
         delta = 16L;
      }

      double xD;
      if ((double)diff > speed) {
         xD = Math.max(speed * (double)delta / 16.0D, 0.5D);
         if ((current = (float)((double)current - xD)) < target) {
            current = target;
         }
      } else if ((double)diff < -speed) {
         xD = Math.max(speed * (double)delta / 16.0D, 0.5D);
         if ((current = (float)((double)current + xD)) > target) {
            current = target;
         }
      } else {
         current = target;
      }

      return current;
   }

   public float moveUD(float current, float end, float minSpeed) {
      return this.moveUD(current, end, 0.125F, minSpeed);
   }

   public double animate(double target, double current, double speed) {
      if (this.timerUtil.passedMs(4L)) {
         boolean larger = target > current;
         if (speed < 0.0D) {
            speed = 0.0D;
         } else if (speed > 1.0D) {
            speed = 1.0D;
         }

         double dif = Math.max(target, current) - Math.min(target, current);
         double factor = dif * speed;
         if (factor < 0.1D) {
            factor = 0.1D;
         }

         current = larger ? current + factor : current - factor;
         this.timerUtil.reset();
      }

      return current;
   }

   public double animates(double target, double current, double speed) {
      if (this.timerUtil.passedMs(1L)) {
         boolean larger = target > current;
         if (speed < 0.0D) {
            speed = 0.0D;
         } else if (speed > 1.0D) {
            speed = 1.0D;
         }

         double dif = Math.max(target, current) - Math.min(target, current);
         double factor = dif * speed;
         if (factor < 0.1D) {
            factor = 0.1D;
         }

         current = larger ? current + factor : current - factor;
         this.timerUtil.reset();
      }

      return current;
   }

   public float animate(float target, float current, float speed) {
      if (this.timerUtil.passedMs(4L)) {
         boolean larger = target > current;
         if (speed < 0.0F) {
            speed = 0.0F;
         } else if ((double)speed > 1.0D) {
            speed = 1.0F;
         }

         float dif = Math.max(target, current) - Math.min(target, current);
         float factor = dif * speed;
         if (factor < 0.01F) {
            factor = 0.01F;
         }

         current = larger ? current + factor : current - factor;
         this.timerUtil.reset();
      }

      return (double)Math.abs(current - target) < 0.2D ? target : current;
   }

   public float moveUD(float current, float end, float smoothSpeed, float minSpeed) {
      float movement = 0.0F;
      if (this.timerUtil.passedMs(4L)) {
         movement = (end - current) * smoothSpeed;
         if (movement > 0.0F) {
            movement = Math.max(minSpeed, movement);
            movement = Math.min(end - current, movement);
         } else if (movement < 0.0F) {
            movement = Math.min(-minSpeed, movement);
            movement = Math.max(end - current, movement);
         }

         this.timerUtil.reset();
      }

      return current + movement;
   }
}
