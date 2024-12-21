package com.lemonclient.api.util.render;

public interface Easings {
   String[] easings = new String[]{"none", "cubic", "quint", "quad", "quart", "expo", "sine", "circ"};

   static double toOutEasing(String easing, double value) {
      byte var4 = -1;
      switch(easing.hashCode()) {
      case 3053847:
         if (easing.equals("circ")) {
            var4 = 6;
         }
         break;
      case 3127794:
         if (easing.equals("expo")) {
            var4 = 4;
         }
         break;
      case 3481927:
         if (easing.equals("quad")) {
            var4 = 2;
         }
         break;
      case 3530381:
         if (easing.equals("sine")) {
            var4 = 5;
         }
         break;
      case 95011658:
         if (easing.equals("cubic")) {
            var4 = 0;
         }
         break;
      case 107940287:
         if (easing.equals("quart")) {
            var4 = 3;
         }
         break;
      case 107947851:
         if (easing.equals("quint")) {
            var4 = 1;
         }
      }

      switch(var4) {
      case 0:
         return cubicOut(value);
      case 1:
         return quintOut(value);
      case 2:
         return quadOut(value);
      case 3:
         return quartOut(value);
      case 4:
         return expoOut(value);
      case 5:
         return sineOut(value);
      case 6:
         return circOut(value);
      default:
         return value;
      }
   }

   static double toInEasing(String easing, double value) {
      byte var4 = -1;
      switch(easing.hashCode()) {
      case 3053847:
         if (easing.equals("circ")) {
            var4 = 6;
         }
         break;
      case 3127794:
         if (easing.equals("expo")) {
            var4 = 4;
         }
         break;
      case 3481927:
         if (easing.equals("quad")) {
            var4 = 2;
         }
         break;
      case 3530381:
         if (easing.equals("sine")) {
            var4 = 5;
         }
         break;
      case 95011658:
         if (easing.equals("cubic")) {
            var4 = 0;
         }
         break;
      case 107940287:
         if (easing.equals("quart")) {
            var4 = 3;
         }
         break;
      case 107947851:
         if (easing.equals("quint")) {
            var4 = 1;
         }
      }

      switch(var4) {
      case 0:
         return cubicIn(value);
      case 1:
         return quintIn(value);
      case 2:
         return quadIn(value);
      case 3:
         return quartIn(value);
      case 4:
         return expoIn(value);
      case 5:
         return sineIn(value);
      case 6:
         return circIn(value);
      default:
         return value;
      }
   }

   static double inOutEasing(String easing, double value) {
      byte var4 = -1;
      switch(easing.hashCode()) {
      case 3053847:
         if (easing.equals("circ")) {
            var4 = 6;
         }
         break;
      case 3127794:
         if (easing.equals("expo")) {
            var4 = 4;
         }
         break;
      case 3481927:
         if (easing.equals("quad")) {
            var4 = 2;
         }
         break;
      case 3530381:
         if (easing.equals("sine")) {
            var4 = 5;
         }
         break;
      case 95011658:
         if (easing.equals("cubic")) {
            var4 = 0;
         }
         break;
      case 107940287:
         if (easing.equals("quart")) {
            var4 = 3;
         }
         break;
      case 107947851:
         if (easing.equals("quint")) {
            var4 = 1;
         }
      }

      switch(var4) {
      case 0:
         return cubicInOut(value);
      case 1:
         return quintInOut(value);
      case 2:
         return quadInOut(value);
      case 3:
         return quartInOut(value);
      case 4:
         return expoInOut(value);
      case 5:
         return sineInOut(value);
      case 6:
         return circInOut(value);
      default:
         return value;
      }
   }

   static double cubicIn(double value) {
      return value * value * value;
   }

   static double cubicOut(double value) {
      return 1.0D - Math.pow(1.0D - value, 3.0D);
   }

   static double cubicInOut(double value) {
      return value < 0.5D ? 4.0D * value * value * value : 1.0D - Math.pow(-2.0D * value + 2.0D, 3.0D) / 2.0D;
   }

   static double quintIn(double value) {
      return value * value * value * value * value;
   }

   static double quintOut(double value) {
      return 1.0D - Math.pow(1.0D - value, 5.0D);
   }

   static double quintInOut(double value) {
      return value < 0.5D ? 16.0D * value * value * value * value * value : 1.0D - Math.pow(-2.0D * value + 2.0D, 5.0D) / 2.0D;
   }

   static double quadIn(double value) {
      return value * value;
   }

   static double quadOut(double value) {
      return 1.0D - (1.0D - value) * (1.0D - value);
   }

   static double quadInOut(double value) {
      return value < 0.5D ? 2.0D * value * value : 1.0D - Math.pow(-2.0D * value + 2.0D, 2.0D) / 2.0D;
   }

   static double quartIn(double value) {
      return value * value * value * value;
   }

   static double quartOut(double value) {
      return 1.0D - Math.pow(1.0D - value, 4.0D);
   }

   static double quartInOut(double value) {
      return value < 0.5D ? 8.0D * value * value * value * value : 1.0D - Math.pow(-2.0D * value + 2.0D, 4.0D) / 2.0D;
   }

   static double expoIn(double value) {
      return value == 0.0D ? 0.0D : Math.pow(2.0D, 10.0D * value - 10.0D);
   }

   static double expoOut(double value) {
      return value == 1.0D ? 1.0D : 1.0D - Math.pow(2.0D, -10.0D * value);
   }

   static double expoInOut(double value) {
      return value == 0.0D ? 0.0D : (value == 1.0D ? 1.0D : (value < 0.5D ? Math.pow(2.0D, 20.0D * value - 10.0D) / 2.0D : (2.0D - Math.pow(2.0D, -20.0D * value + 10.0D)) / 2.0D));
   }

   static double sineIn(double value) {
      return 1.0D - Math.cos(value * 3.141592653589793D / 2.0D);
   }

   static double sineOut(double value) {
      return Math.sin(value * 3.141592653589793D / 2.0D);
   }

   static double sineInOut(double value) {
      return -(Math.cos(3.141592653589793D * value) - 1.0D) / 2.0D;
   }

   static double circIn(double value) {
      return 1.0D - Math.sqrt(1.0D - Math.pow(value, 2.0D));
   }

   static double circOut(double value) {
      return Math.sqrt(1.0D - Math.pow(value - 1.0D, 2.0D));
   }

   static double circInOut(double value) {
      return value < 0.5D ? (1.0D - Math.sqrt(1.0D - Math.pow(2.0D * value, 2.0D))) / 2.0D : (Math.sqrt(1.0D - Math.pow(-2.0D * value + 2.0D, 2.0D)) + 1.0D) / 2.0D;
   }
}
