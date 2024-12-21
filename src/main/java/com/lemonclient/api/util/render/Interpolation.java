package com.lemonclient.api.util.render;

import net.minecraft.client.Minecraft;

public class Interpolation {
   public static Minecraft mc = Minecraft.func_71410_x();

   public static double getRenderPosX() {
      return mc.func_175598_ae().field_78725_b;
   }

   public static double getRenderPosY() {
      return mc.func_175598_ae().field_78726_c;
   }

   public static double getRenderPosZ() {
      return mc.func_175598_ae().field_78723_d;
   }
}
