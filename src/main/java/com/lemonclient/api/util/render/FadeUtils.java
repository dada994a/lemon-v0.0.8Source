package com.lemonclient.api.util.render;

public class FadeUtils {
   protected long start;
   protected long length;

   public FadeUtils(long ms) {
      this.length = ms;
      this.reset();
   }

   public void reset() {
      this.start = System.currentTimeMillis();
   }

   public boolean isEnd() {
      return this.getTime() >= this.length;
   }

   public FadeUtils end() {
      this.start = System.currentTimeMillis() - this.length;
      return this;
   }

   protected long getTime() {
      return System.currentTimeMillis() - this.start;
   }

   public void setLength(long length) {
      this.length = length;
   }

   public long getLength() {
      return this.length;
   }

   public double getFadeOne() {
      return this.isEnd() ? 1.0D : (double)this.getTime() / (double)this.length;
   }

   public double toDelta() {
      double value = (double)this.toDelta(this.start) / (double)this.length;
      if (value > 1.0D) {
         value = 1.0D;
      }

      if (value < 0.0D) {
         value = 0.0D;
      }

      return value;
   }

   public long toDelta(long start) {
      return System.currentTimeMillis() - start;
   }

   public double getFade(String fadeMode) {
      return getFade(fadeMode, this.getFadeOne());
   }

   public static double getFade(String fadeMode, double current) {
      byte var4 = -1;
      switch(fadeMode.hashCode()) {
      case -1013029786:
         if (fadeMode.equals("FADE_EASE_OUT_QUAD")) {
            var4 = 5;
         }
         break;
      case -837750349:
         if (fadeMode.equals("FADE_EASE_IN_QUAD")) {
            var4 = 4;
         }
         break;
      case -373408312:
         if (fadeMode.equals("FADE_IN")) {
            var4 = 0;
         }
         break;
      case 511173812:
         if (fadeMode.equals("FADE_EPS_OUT")) {
            var4 = 3;
         }
         break;
      case 986320607:
         if (fadeMode.equals("FADE_EPS_IN")) {
            var4 = 2;
         }
         break;
      case 1309250283:
         if (fadeMode.equals("FADE_OUT")) {
            var4 = 1;
         }
      }

      switch(var4) {
      case 0:
         return getFadeInDefault(current);
      case 1:
         return getFadeOutDefault(current);
      case 2:
         return getEpsEzFadeIn(current);
      case 3:
         return getEpsEzFadeOut(current);
      case 4:
         return easeInQuad(current);
      case 5:
         return easeOutQuad(current);
      default:
         return current;
      }
   }

   public static double getFadeType(String fadeType, boolean FadeIn, double current) {
      byte var5 = -1;
      switch(fadeType.hashCode()) {
      case -704144546:
         if (fadeType.equals("FADE_DEFAULT")) {
            var5 = 0;
         }
         break;
      case 509712629:
         if (fadeType.equals("FADE_EASE_QUAD")) {
            var5 = 2;
         }
         break;
      case 1309240517:
         if (fadeType.equals("FADE_EPS")) {
            var5 = 1;
         }
      }

      switch(var5) {
      case 0:
         return FadeIn ? getFadeInDefault(current) : getFadeOutDefault(current);
      case 1:
         return FadeIn ? getEpsEzFadeIn(current) : getEpsEzFadeOut(current);
      case 2:
         return FadeIn ? easeInQuad(current) : easeOutQuad(current);
      default:
         return FadeIn ? current : 1.0D - current;
      }
   }

   private static double checkOne(double one) {
      return Math.max(0.0D, Math.min(1.0D, one));
   }

   public static double getFadeInDefault(double current) {
      return Math.tanh(checkOne(current) * 3.0D);
   }

   public static double getFadeOutDefault(double current) {
      return 1.0D - getFadeInDefault(current);
   }

   public static double getEpsEzFadeIn(double current) {
      return 1.0D - getEpsEzFadeOut(current);
   }

   public static double getEpsEzFadeOut(double current) {
      return Math.cos(1.5707963267948966D * checkOne(current)) * Math.cos(2.5132741228718345D * checkOne(current));
   }

   public static double easeOutQuad(double current) {
      return 1.0D - easeInQuad(current);
   }

   public static double easeInQuad(double current) {
      return checkOne(current) * checkOne(current);
   }

   public double getEpsEzFadeInGUI() {
      return this.isEnd() ? 1.0D : Math.sin(this.getFadeOne());
   }
}
