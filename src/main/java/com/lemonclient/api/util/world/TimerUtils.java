package com.lemonclient.api.util.world;

import com.lemonclient.api.util.misc.Mapping;
import com.lemonclient.mixin.mixins.accessor.AccessorMinecraft;
import com.lemonclient.mixin.mixins.accessor.AccessorTimer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;

public class TimerUtils {
   private static int counter;
   private static final HashMap<Integer, Float> multipliers = new HashMap();

   public static void setTickLength(float speed) {
      Timer timer = ((AccessorMinecraft)Minecraft.func_71410_x()).getTimer();
      ((AccessorTimer)timer).setTickLength(speed);
   }

   public static float getTickLength() {
      Timer timer = ((AccessorMinecraft)Minecraft.func_71410_x()).getTimer();
      return ((AccessorTimer)timer).getTickLength();
   }

   public static void setSpeed(float speed) {
      Timer timer = ((AccessorMinecraft)Minecraft.func_71410_x()).getTimer();
      ((AccessorTimer)timer).setTickLength(50.0F / speed);
   }

   public static float getTimer() {
      Timer timer = ((AccessorMinecraft)Minecraft.func_71410_x()).getTimer();
      return 50.0F / ((AccessorTimer)timer).getTickLength();
   }

   public static void setTimerSpeed(float speed) {
      try {
         Field timer = Minecraft.class.getDeclaredField(Mapping.timer);
         timer.setAccessible(true);
         Field tickLength = Timer.class.getDeclaredField(Mapping.tickLength);
         tickLength.setAccessible(true);
         tickLength.setFloat(timer.get(Minecraft.func_71410_x()), 50.0F / speed);
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   private static float getMultiplier() {
      float multiplier = 1.0F;

      float f;
      for(Iterator var1 = multipliers.values().iterator(); var1.hasNext(); multiplier *= f) {
         f = (Float)var1.next();
      }

      return multiplier;
   }

   public static int push(float multiplier) {
      multipliers.put(++counter, multiplier);
      setSpeed(getMultiplier());
      return counter;
   }

   public static void pop(int counter) {
      multipliers.remove(counter);
      setSpeed(getMultiplier());
   }
}
