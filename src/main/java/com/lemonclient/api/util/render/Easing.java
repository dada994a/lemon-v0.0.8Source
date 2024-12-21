package com.lemonclient.api.util.render;

public interface Easing {
   Easing LINEAR = (t, b, c, d) -> {
      return c * t / d + b;
   };
   Easing QUAD_IN = (t, b, c, d) -> {
      return c * (t /= d) * t + b;
   };
   Easing QUAD_OUT = (t, b, c, d) -> {
      return -c * (t /= d) * (t - 2.0F) + b;
   };
   Easing QUAD_IN_OUT = (t, b, c, d) -> {
      return (t /= d / 2.0F) < 1.0F ? c / 2.0F * t * t + b : -c / 2.0F * (--t * (t - 2.0F) - 1.0F) + b;
   };
   Easing CUBIC_IN = (t, b, c, d) -> {
      return c * (t /= d) * t * t + b;
   };
   Easing CUBIC_OUT = (t, b, c, d) -> {
      return c * ((t = t / d - 1.0F) * t * t + 1.0F) + b;
   };
   Easing CUBIC_IN_OUT = (t, b, c, d) -> {
      return (t /= d / 2.0F) < 1.0F ? c / 2.0F * t * t * t + b : c / 2.0F * ((t -= 2.0F) * t * t + 2.0F) + b;
   };
   Easing QUARTIC_IN = (t, b, c, d) -> {
      return c * (t /= d) * t * t * t + b;
   };
   Easing QUARTIC_OUT = (t, b, c, d) -> {
      return -c * ((t = t / d - 1.0F) * t * t * t - 1.0F) + b;
   };
   Easing QUARTIC_IN_OUT = (t, b, c, d) -> {
      return (t /= d / 2.0F) < 1.0F ? c / 2.0F * t * t * t * t + b : -c / 2.0F * ((t -= 2.0F) * t * t * t - 2.0F) + b;
   };
   Easing QUINTIC_IN = (t, b, c, d) -> {
      return c * (t /= d) * t * t * t * t + b;
   };
   Easing QUINTIC_OUT = (t, b, c, d) -> {
      return c * ((t = t / d - 1.0F) * t * t * t * t + 1.0F) + b;
   };
   Easing QUINTIC_IN_OUT = (t, b, c, d) -> {
      return (t /= d / 2.0F) < 1.0F ? c / 2.0F * t * t * t * t * t + b : c / 2.0F * ((t -= 2.0F) * t * t * t * t + 2.0F) + b;
   };
   Easing SINE_IN = (t, b, c, d) -> {
      return -c * (float)Math.cos((double)(t / d) * 1.5707963267948966D) + c + b;
   };
   Easing SINE_OUT = (t, b, c, d) -> {
      return c * (float)Math.sin((double)(t / d) * 1.5707963267948966D) + b;
   };
   Easing SINE_IN_OUT = (t, b, c, d) -> {
      return -c / 2.0F * ((float)Math.cos(3.141592653589793D * (double)t / (double)d) - 1.0F) + b;
   };
   Easing EXPO_IN = (t, b, c, d) -> {
      return t == 0.0F ? b : c * (float)Math.pow(2.0D, (double)(10.0F * (t / d - 1.0F))) + b;
   };
   Easing EXPO_OUT = (t, b, c, d) -> {
      return t == d ? b + c : c * (-((float)Math.pow(2.0D, (double)(-10.0F * t / d))) + 1.0F) + b;
   };
   Easing EXPO_IN_OUT = (t, b, c, d) -> {
      if (t == 0.0F) {
         return b;
      } else if (t == d) {
         return b + c;
      } else {
         return (t /= d / 2.0F) < 1.0F ? c / 2.0F * (float)Math.pow(2.0D, (double)(10.0F * (t - 1.0F))) + b : c / 2.0F * (-((float)Math.pow(2.0D, (double)(-10.0F * --t))) + 2.0F) + b;
      }
   };
   Easing CIRC_IN = (t, b, c, d) -> {
      return -c * ((float)Math.sqrt((double)(1.0F - (t /= d) * t)) - 1.0F) + b;
   };
   Easing CIRC_OUT = (t, b, c, d) -> {
      return c * (float)Math.sqrt((double)(1.0F - (t = t / d - 1.0F) * t)) + b;
   };
   Easing CIRC_IN_OUT = (t, b, c, d) -> {
      return (t /= d / 2.0F) < 1.0F ? -c / 2.0F * ((float)Math.sqrt((double)(1.0F - t * t)) - 1.0F) + b : c / 2.0F * ((float)Math.sqrt((double)(1.0F - (t -= 2.0F) * t)) + 1.0F) + b;
   };
   Easing.Elastic ELASTIC_IN = new Easing.ElasticIn();
   Easing.Elastic ELASTIC_OUT = new Easing.ElasticOut();
   Easing.Elastic ELASTIC_IN_OUT = new Easing.ElasticInOut();
   Easing.Back BACK_IN = new Easing.BackIn();
   Easing.Back BACK_OUT = new Easing.BackOut();
   Easing.Back BACK_IN_OUT = new Easing.BackInOut();
   Easing BOUNCE_OUT = (t, b, c, d) -> {
      if ((t /= d) < 0.36363637F) {
         return c * 7.5625F * t * t + b;
      } else if (t < 0.72727275F) {
         return c * (7.5625F * (t -= 0.54545456F) * t + 0.75F) + b;
      } else {
         return t < 0.90909094F ? c * (7.5625F * (t -= 0.8181818F) * t + 0.9375F) + b : c * (7.5625F * (t -= 0.95454544F) * t + 0.984375F) + b;
      }
   };
   Easing BOUNCE_IN = (t, b, c, d) -> {
      return c - BOUNCE_OUT.ease(d - t, 0.0F, c, d) + b;
   };
   Easing BOUNCE_IN_OUT = (t, b, c, d) -> {
      return t < d / 2.0F ? BOUNCE_IN.ease(t * 2.0F, 0.0F, c, d) * 0.5F + b : BOUNCE_OUT.ease(t * 2.0F - d, 0.0F, c, d) * 0.5F + c * 0.5F + b;
   };

   float ease(float var1, float var2, float var3, float var4);

   public static class BackInOut extends Easing.Back {
      public BackInOut() {
      }

      public BackInOut(float overshoot) {
         super(overshoot);
      }

      public float ease(float time, float startTime, float change, float endTime) {
         float s = this.getOvershoot();
         return (time /= endTime / 2.0F) < 1.0F ? change / 2.0F * time * time * (((s = (float)((double)s * 1.525D)) + 1.0F) * time - s) + startTime : change / 2.0F * ((time -= 2.0F) * time * (((s = (float)((double)s * 1.525D)) + 1.0F) * time + s) + 2.0F) + startTime;
      }
   }

   public static class BackOut extends Easing.Back {
      public BackOut() {
      }

      public BackOut(float overshoot) {
         super(overshoot);
      }

      public float ease(float time, float startTime, float change, float endTime) {
         float s = this.getOvershoot();
         return change * ((time = time / endTime - 1.0F) * time * ((s + 1.0F) * time + s) + 1.0F) + startTime;
      }
   }

   public static class BackIn extends Easing.Back {
      public BackIn() {
      }

      public BackIn(float overshoot) {
         super(overshoot);
      }

      public float ease(float time, float startTime, float change, float endTime) {
         float s = this.getOvershoot();
         return change * (time /= endTime) * time * ((s + 1.0F) * time - s) + startTime;
      }
   }

   public abstract static class Back implements Easing {
      public static final float DEFAULT_OVERSHOOT = 1.70158F;
      private float overshoot;

      public Back() {
         this(1.70158F);
      }

      public Back(float overshoot) {
         this.overshoot = overshoot;
      }

      public float getOvershoot() {
         return this.overshoot;
      }

      public void setOvershoot(float overshoot) {
         this.overshoot = overshoot;
      }
   }

   public static class ElasticInOut extends Easing.Elastic {
      public ElasticInOut(float amplitude, float period) {
         super(amplitude, period);
      }

      public ElasticInOut() {
      }

      public float ease(float time, float startTime, float change, float endTime) {
         float a = this.getAmplitude();
         float p = this.getPeriod();
         if (time == 0.0F) {
            return startTime;
         } else if ((time /= endTime / 2.0F) == 2.0F) {
            return startTime + change;
         } else {
            if (p == 0.0F) {
               p = endTime * 0.45000002F;
            }

            float s;
            if (a < Math.abs(change)) {
               a = change;
               s = p / 4.0F;
            } else {
               s = p / 6.2831855F * (float)Math.asin((double)(change / a));
            }

            return time < 1.0F ? -0.5F * a * (float)Math.pow(2.0D, (double)(10.0F * --time)) * (float)Math.sin((double)(time * endTime - s) * 6.283185307179586D / (double)p) + startTime : a * (float)Math.pow(2.0D, (double)(-10.0F * --time)) * (float)Math.sin((double)(time * endTime - s) * 6.283185307179586D / (double)p) * 0.5F + change + startTime;
         }
      }
   }

   public static class ElasticOut extends Easing.Elastic {
      public ElasticOut(float amplitude, float period) {
         super(amplitude, period);
      }

      public ElasticOut() {
      }

      public float ease(float time, float startTime, float change, float endTime) {
         float a = this.getAmplitude();
         float p = this.getPeriod();
         if (time == 0.0F) {
            return startTime;
         } else if ((time /= endTime) == 1.0F) {
            return startTime + change;
         } else {
            if (p == 0.0F) {
               p = endTime * 0.3F;
            }

            float s;
            if (a < Math.abs(change)) {
               a = change;
               s = p / 4.0F;
            } else {
               s = p / 6.2831855F * (float)Math.asin((double)(change / a));
            }

            return a * (float)Math.pow(2.0D, (double)(-10.0F * time)) * (float)Math.sin((double)(time * endTime - s) * 6.283185307179586D / (double)p) + change + startTime;
         }
      }
   }

   public static class ElasticIn extends Easing.Elastic {
      public ElasticIn(float amplitude, float period) {
         super(amplitude, period);
      }

      public ElasticIn() {
      }

      public float ease(float time, float startTime, float change, float endTime) {
         float a = this.getAmplitude();
         float p = this.getPeriod();
         if (time == 0.0F) {
            return startTime;
         } else if ((time /= endTime) == 1.0F) {
            return startTime + change;
         } else {
            if (p == 0.0F) {
               p = endTime * 0.3F;
            }

            float s;
            if (a < Math.abs(change)) {
               a = change;
               s = p / 4.0F;
            } else {
               s = p / 6.2831855F * (float)Math.asin((double)(change / a));
            }

            return -(a * (float)Math.pow(2.0D, (double)(10.0F * --time)) * (float)Math.sin((double)(time * endTime - s) * 6.283185307179586D / (double)p)) + startTime;
         }
      }
   }

   public abstract static class Elastic implements Easing {
      private float amplitude;
      private float period;

      public Elastic(float amplitude, float period) {
         this.amplitude = amplitude;
         this.period = period;
      }

      public Elastic() {
         this(-1.0F, 0.0F);
      }

      public float getPeriod() {
         return this.period;
      }

      public void setPeriod(float period) {
         this.period = period;
      }

      public float getAmplitude() {
         return this.amplitude;
      }

      public void setAmplitude(float amplitude) {
         this.amplitude = amplitude;
      }
   }
}
