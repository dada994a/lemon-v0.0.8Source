package com.lemonclient.api.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager.Profile;
import org.lwjgl.opengl.GL11;

public class ChamsUtil {
   private static final Minecraft mc = Minecraft.func_71410_x();

   public static void createChamsPre() {
      mc.func_175598_ae().func_178633_a(false);
      mc.func_175598_ae().func_178632_c(false);
      GlStateManager.func_179094_E();
      GlStateManager.func_179132_a(true);
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 240.0F, 240.0F);
      GL11.glEnable(32823);
      GL11.glDepthRange(0.0D, 0.01D);
      GlStateManager.func_179121_F();
   }

   public static void createChamsPost() {
      boolean shadow = mc.func_175598_ae().func_178627_a();
      mc.func_175598_ae().func_178633_a(shadow);
      GlStateManager.func_179094_E();
      GlStateManager.func_179132_a(false);
      GL11.glDisable(32823);
      GL11.glDepthRange(0.0D, 1.0D);
      GlStateManager.func_179121_F();
   }

   public static void createColorPre(GSColor color, boolean isPlayer) {
      mc.func_175598_ae().func_178633_a(false);
      mc.func_175598_ae().func_178632_c(false);
      GlStateManager.func_179094_E();
      GlStateManager.func_179132_a(true);
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 240.0F, 240.0F);
      GL11.glEnable(32823);
      GL11.glDepthRange(0.0D, 0.01D);
      GL11.glDisable(3553);
      if (!isPlayer) {
         GlStateManager.func_187408_a(Profile.TRANSPARENT_MODEL);
      }

      color.glColor();
      GlStateManager.func_179121_F();
   }

   public static void createColorPost(boolean isPlayer) {
      boolean shadow = mc.func_175598_ae().func_178627_a();
      mc.func_175598_ae().func_178633_a(shadow);
      GlStateManager.func_179094_E();
      GlStateManager.func_179132_a(false);
      if (!isPlayer) {
         GlStateManager.func_187440_b(Profile.TRANSPARENT_MODEL);
      }

      GL11.glDisable(32823);
      GL11.glDepthRange(0.0D, 1.0D);
      GL11.glEnable(3553);
      GlStateManager.func_179121_F();
   }

   public static void createWirePre(GSColor color, int lineWidth, boolean isPlayer) {
      mc.func_175598_ae().func_178633_a(false);
      mc.func_175598_ae().func_178632_c(false);
      GlStateManager.func_179094_E();
      GlStateManager.func_179132_a(true);
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 240.0F, 240.0F);
      GL11.glPolygonMode(1032, 6913);
      GL11.glEnable(10754);
      GL11.glDepthRange(0.0D, 0.01D);
      GL11.glDisable(3553);
      GL11.glDisable(2896);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      if (!isPlayer) {
         GlStateManager.func_187408_a(Profile.TRANSPARENT_MODEL);
      }

      GL11.glLineWidth((float)lineWidth);
      color.glColor();
      GlStateManager.func_179121_F();
   }

   public static void createWirePost(boolean isPlayer) {
      boolean shadow = mc.func_175598_ae().func_178627_a();
      mc.func_175598_ae().func_178633_a(shadow);
      GlStateManager.func_179094_E();
      GlStateManager.func_179132_a(false);
      if (!isPlayer) {
         GlStateManager.func_187440_b(Profile.TRANSPARENT_MODEL);
      }

      GL11.glPolygonMode(1032, 6914);
      GL11.glDisable(10754);
      GL11.glDepthRange(0.0D, 1.0D);
      GL11.glEnable(3553);
      GL11.glEnable(2896);
      GL11.glDisable(2848);
      GlStateManager.func_179121_F();
   }
}
