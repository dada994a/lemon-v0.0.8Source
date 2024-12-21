package com.lemonclient.api.util.render;

import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.util.font.FontUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.lemonclient.client.module.modules.render.Nametags;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

public class RenderUtil {
   private static final Minecraft mc = Minecraft.func_71410_x();

   public static void drawLine(double posx, double posy, double posz, double posx2, double posy2, double posz2, GSColor color) {
      drawLine(posx, posy, posz, posx2, posy2, posz2, color, 1.0F);
   }

   public static void drawRectOutline(double x, double y, double width, double height, Color color) {
      drawGradientRectOutline(x, y, width, height, RenderUtil.GradientDirection.Normal, color, color);
   }

   public static void drawGradientRectOutline(double x, double y, double width, double height, RenderUtil.GradientDirection direction, Color startColor, Color endColor) {
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glShadeModel(7425);
      Color[] result = checkColorDirection(direction, startColor, endColor);
      GL11.glBegin(2);
      GL11.glColor4f((float)result[2].getRed() / 255.0F, (float)result[2].getGreen() / 255.0F, (float)result[2].getBlue() / 255.0F, (float)result[2].getAlpha() / 255.0F);
      GL11.glVertex2d(x + width, y);
      GL11.glColor4f((float)result[3].getRed() / 255.0F, (float)result[3].getGreen() / 255.0F, (float)result[3].getBlue() / 255.0F, (float)result[3].getAlpha() / 255.0F);
      GL11.glVertex2d(x, y);
      GL11.glColor4f((float)result[0].getRed() / 255.0F, (float)result[0].getGreen() / 255.0F, (float)result[0].getBlue() / 255.0F, (float)result[0].getAlpha() / 255.0F);
      GL11.glVertex2d(x, y + height);
      GL11.glColor4f((float)result[1].getRed() / 255.0F, (float)result[1].getGreen() / 255.0F, (float)result[1].getBlue() / 255.0F, (float)result[1].getAlpha() / 255.0F);
      GL11.glVertex2d(x + width, y + height);
      GL11.glEnd();
      GL11.glDisable(3042);
      GL11.glEnable(3553);
   }

   public static void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GlStateManager.func_179120_a(770, 771, 1, 0);
      GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
      GL11.glBegin(6);
      GL11.glVertex2d(x1, y1);
      GL11.glVertex2d(x2, y2);
      GL11.glVertex2d(x3, y3);
      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
   }

   public static void drawRect(double x, double y, double width, double height, Color color) {
      drawGradientRect(x, y, width, height, RenderUtil.GradientDirection.Normal, color, color);
   }

   public static void setColor(Color color) {
      GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
   }

   private static Color[] checkColorDirection(RenderUtil.GradientDirection direction, Color start, Color end) {
      Color[] dir = new Color[4];
      int a;
      if (direction == RenderUtil.GradientDirection.Normal) {
         for(a = 0; a < dir.length; ++a) {
            dir[a] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
         }
      } else if (direction == RenderUtil.GradientDirection.DownToUp) {
         dir[0] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
         dir[1] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
         dir[2] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
         dir[3] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
      } else if (direction == RenderUtil.GradientDirection.UpToDown) {
         dir[0] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
         dir[1] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
         dir[2] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
         dir[3] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
      } else if (direction == RenderUtil.GradientDirection.RightToLeft) {
         dir[0] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
         dir[1] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
         dir[2] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
         dir[3] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
      } else if (direction == RenderUtil.GradientDirection.LeftToRight) {
         dir[0] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
         dir[1] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
         dir[2] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
         dir[3] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
      } else {
         for(a = 0; a < dir.length; ++a) {
            dir[a] = new Color(255, 255, 255);
         }
      }

      return dir;
   }

   public static void drawGradientRect(double x, double y, double width, double height, RenderUtil.GradientDirection direction, Color startColor, Color endColor) {
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glShadeModel(7425);
      Color[] result = checkColorDirection(direction, startColor, endColor);
      GL11.glBegin(7);
      setColor(result[0]);
      GL11.glVertex2d(x + width, y);
      setColor(result[1]);
      GL11.glVertex2d(x, y);
      setColor(result[2]);
      GL11.glVertex2d(x, y + height);
      setColor(result[3]);
      GL11.glVertex2d(x + width, y + height);
      GL11.glEnd();
      GL11.glDisable(3042);
      GL11.glEnable(3553);
   }

   public static void drawRect(float x1, float y1, float x2, float y2, int color) {
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(2848);
      GL11.glPushMatrix();
      color(color);
      GL11.glBegin(7);
      GL11.glVertex2d((double)x2, (double)y1);
      GL11.glVertex2d((double)x1, (double)y1);
      GL11.glVertex2d((double)x1, (double)y2);
      GL11.glVertex2d((double)x2, (double)y2);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glDisable(2848);
      GL11.glPopMatrix();
      Gui.func_73734_a(0, 0, 0, 0, 0);
   }

   public static void drawRectSOutline(double x, double y, double x2, double y2, Color color) {
      drawGradientRectSOutline(x, y, x2, y2, RenderUtil.GradientDirection.Normal, color, color);
   }

   public static void drawGradientRectSOutline(double x, double y, double x2, double y2, RenderUtil.GradientDirection direction, Color startColor, Color endColor) {
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glShadeModel(7425);
      Color[] result = checkColorDirection(direction, startColor, endColor);
      GL11.glBegin(2);
      GL11.glColor4f((float)result[2].getRed() / 255.0F, (float)result[2].getGreen() / 255.0F, (float)result[2].getBlue() / 255.0F, (float)result[2].getAlpha() / 255.0F);
      GL11.glVertex2d(x2, y);
      GL11.glColor4f((float)result[3].getRed() / 255.0F, (float)result[3].getGreen() / 255.0F, (float)result[3].getBlue() / 255.0F, (float)result[3].getAlpha() / 255.0F);
      GL11.glVertex2d(x, y);
      GL11.glColor4f((float)result[0].getRed() / 255.0F, (float)result[0].getGreen() / 255.0F, (float)result[0].getBlue() / 255.0F, (float)result[0].getAlpha() / 255.0F);
      GL11.glVertex2d(x, y2);
      GL11.glColor4f((float)result[1].getRed() / 255.0F, (float)result[1].getGreen() / 255.0F, (float)result[1].getBlue() / 255.0F, (float)result[1].getAlpha() / 255.0F);
      GL11.glVertex2d(x2, y2);
      GL11.glEnd();
      GL11.glDisable(3042);
      GL11.glEnable(3553);
   }

   public static void drawRectS(double x1, double y1, float x2, float y2, int color) {
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(2848);
      GL11.glPushMatrix();
      color(color);
      GL11.glBegin(7);
      GL11.glVertex2d((double)x2, y1);
      GL11.glVertex2d(x1, y1);
      GL11.glVertex2d(x1, (double)y2);
      GL11.glVertex2d((double)x2, (double)y2);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glDisable(2848);
      GL11.glPopMatrix();
      Gui.func_73734_a(0, 0, 0, 0, 0);
   }

   public static void color(int color) {
      float f = (float)(color >> 24 & 255) / 255.0F;
      float f1 = (float)(color >> 16 & 255) / 255.0F;
      float f2 = (float)(color >> 8 & 255) / 255.0F;
      float f3 = (float)(color & 255) / 255.0F;
      GL11.glColor4f(f1, f2, f3, f);
   }

   public static void prepareGL() {
      GL11.glBlendFunc(770, 771);
      GlStateManager.func_187428_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.func_187441_d(Float.intBitsToFloat(Float.floatToIntBits(5.0675106F) ^ 2132945164));
      GlStateManager.func_179090_x();
      GlStateManager.func_179132_a(false);
      GlStateManager.func_179147_l();
      GlStateManager.func_179097_i();
      GlStateManager.func_179140_f();
      GlStateManager.func_179129_p();
      GlStateManager.func_179141_d();
      GlStateManager.func_179124_c(Float.intBitsToFloat(Float.floatToIntBits(11.925059F) ^ 2126433547), Float.intBitsToFloat(Float.floatToIntBits(18.2283F) ^ 2115097487), Float.intBitsToFloat(Float.floatToIntBits(9.73656F) ^ 2124138739));
   }

   public static void releaseGL() {
      GlStateManager.func_179089_o();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179098_w();
      GlStateManager.func_179147_l();
      GlStateManager.func_179126_j();
      GlStateManager.func_179124_c(Float.intBitsToFloat(Float.floatToIntBits(12.552789F) ^ 2127091769), Float.intBitsToFloat(Float.floatToIntBits(7.122752F) ^ 2137255318), Float.intBitsToFloat(Float.floatToIntBits(5.4278784F) ^ 2133700910));
      GL11.glColor4f(Float.intBitsToFloat(Float.floatToIntBits(10.5715685F) ^ 2125014309), Float.intBitsToFloat(Float.floatToIntBits(4.9474883F) ^ 2132693459), Float.intBitsToFloat(Float.floatToIntBits(4.9044757F) ^ 2132603255), Float.intBitsToFloat(Float.floatToIntBits(9.482457F) ^ 2123872293));
   }

   public static void draw2DGradientRect(float left, float top, float right, float bottom, int leftBottomColor, int leftTopColor, int rightBottomColor, int rightTopColor) {
      float lba = (float)(leftBottomColor >> 24 & 255) / 255.0F;
      float lbr = (float)(leftBottomColor >> 16 & 255) / 255.0F;
      float lbg = (float)(leftBottomColor >> 8 & 255) / 255.0F;
      float lbb = (float)(leftBottomColor & 255) / 255.0F;
      float rba = (float)(rightBottomColor >> 24 & 255) / 255.0F;
      float rbr = (float)(rightBottomColor >> 16 & 255) / 255.0F;
      float rbg = (float)(rightBottomColor >> 8 & 255) / 255.0F;
      float rbb = (float)(rightBottomColor & 255) / 255.0F;
      float lta = (float)(leftTopColor >> 24 & 255) / 255.0F;
      float ltr = (float)(leftTopColor >> 16 & 255) / 255.0F;
      float ltg = (float)(leftTopColor >> 8 & 255) / 255.0F;
      float ltb = (float)(leftTopColor & 255) / 255.0F;
      float rta = (float)(rightTopColor >> 24 & 255) / 255.0F;
      float rtr = (float)(rightTopColor >> 16 & 255) / 255.0F;
      float rtg = (float)(rightTopColor >> 8 & 255) / 255.0F;
      float rtb = (float)(rightTopColor & 255) / 255.0F;
      GlStateManager.func_179090_x();
      GlStateManager.func_179147_l();
      GlStateManager.func_179118_c();
      GlStateManager.func_187428_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.func_179103_j(7425);
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
      bufferbuilder.func_181662_b((double)right, (double)top, 0.0D).func_181666_a(rtr, rtg, rtb, rta).func_181675_d();
      bufferbuilder.func_181662_b((double)left, (double)top, 0.0D).func_181666_a(ltr, ltg, ltb, lta).func_181675_d();
      bufferbuilder.func_181662_b((double)left, (double)bottom, 0.0D).func_181666_a(lbr, lbg, lbb, lba).func_181675_d();
      bufferbuilder.func_181662_b((double)right, (double)bottom, 0.0D).func_181666_a(rbr, rbg, rbb, rba).func_181675_d();
      tessellator.func_78381_a();
      GlStateManager.func_179103_j(7424);
      GlStateManager.func_179084_k();
      GlStateManager.func_179141_d();
      GlStateManager.func_179098_w();
   }

   public static void drawLine(double posx, double posy, double posz, double posx2, double posy2, double posz2, GSColor color, float width) {
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      GlStateManager.func_187441_d(width);
      color.glColor();
      bufferbuilder.func_181668_a(1, DefaultVertexFormats.field_181705_e);
      vertex(posx, posy, posz, bufferbuilder);
      vertex(posx2, posy2, posz2, bufferbuilder);
      tessellator.func_78381_a();
   }

   public static void draw2DRect(int posX, int posY, int width, int height, int zHeight, GSColor color) {
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      GlStateManager.func_179147_l();
      GlStateManager.func_179090_x();
      GlStateManager.func_187428_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      color.glColor();
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181705_e);
      bufferbuilder.func_181662_b((double)posX, (double)(posY + height), (double)zHeight).func_181675_d();
      bufferbuilder.func_181662_b((double)(posX + width), (double)(posY + height), (double)zHeight).func_181675_d();
      bufferbuilder.func_181662_b((double)(posX + width), (double)posY, (double)zHeight).func_181675_d();
      bufferbuilder.func_181662_b((double)posX, (double)posY, (double)zHeight).func_181675_d();
      tessellator.func_78381_a();
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
   }

   private static void drawBorderedRect(double x, double y, double x1, GSColor inside, GSColor border) {
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      inside.glColor();
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181705_e);
      bufferbuilder.func_181662_b(x, 1.0D, 0.0D).func_181675_d();
      bufferbuilder.func_181662_b(x1, 1.0D, 0.0D).func_181675_d();
      bufferbuilder.func_181662_b(x1, y, 0.0D).func_181675_d();
      bufferbuilder.func_181662_b(x, y, 0.0D).func_181675_d();
      tessellator.func_78381_a();
      border.glColor();
      GlStateManager.func_187441_d(1.8F);
      bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181705_e);
      bufferbuilder.func_181662_b(x, y, 0.0D).func_181675_d();
      bufferbuilder.func_181662_b(x, 1.0D, 0.0D).func_181675_d();
      bufferbuilder.func_181662_b(x1, 1.0D, 0.0D).func_181675_d();
      bufferbuilder.func_181662_b(x1, y, 0.0D).func_181675_d();
      bufferbuilder.func_181662_b(x, y, 0.0D).func_181675_d();
      tessellator.func_78381_a();
   }

   public static void drawCircle(float x, float y, float z, Double radius, GSColor colour) {
      GlStateManager.func_179129_p();
      GlStateManager.func_179118_c();
      GlStateManager.func_179103_j(7425);
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      int alpha = 255 - colour.getAlpha();
      if (alpha == 0) {
         alpha = 1;
      }

      for(int i = 0; i < 361; ++i) {
         bufferbuilder.func_181662_b((double)x + Math.sin(Math.toRadians((double)i)) * radius - mc.func_175598_ae().field_78730_l, (double)y - mc.func_175598_ae().field_78731_m, (double)z + Math.cos(Math.toRadians((double)i)) * radius - mc.func_175598_ae().field_78728_n).func_181666_a((float)colour.getRed() / 255.0F, (float)colour.getGreen() / 255.0F, (float)colour.getBlue() / 255.0F, (float)alpha).func_181675_d();
      }

      tessellator.func_78381_a();
      GlStateManager.func_179089_o();
      GlStateManager.func_179141_d();
      GlStateManager.func_179103_j(7424);
   }

   public static void drawCircle(float x, float y, float z, Double radius, int stepCircle, int alphaVal) {
      GlStateManager.func_179129_p();
      GlStateManager.func_179118_c();
      GlStateManager.func_179103_j(7425);
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      int alpha = 255 - alphaVal;
      if (alpha == 0) {
         alpha = 1;
      }

      for(int i = 0; i < 361; ++i) {
         GSColor colour = ColorSetting.getRainbowColor((double)(i % 180 * stepCircle));
         bufferbuilder.func_181662_b((double)x + Math.sin(Math.toRadians((double)i)) * radius - mc.func_175598_ae().field_78730_l, (double)y - mc.func_175598_ae().field_78731_m, (double)z + Math.cos(Math.toRadians((double)i)) * radius - mc.func_175598_ae().field_78728_n).func_181666_a((float)colour.getRed() / 255.0F, (float)colour.getGreen() / 255.0F, (float)colour.getBlue() / 255.0F, (float)alpha).func_181675_d();
      }

      tessellator.func_78381_a();
      GlStateManager.func_179089_o();
      GlStateManager.func_179141_d();
      GlStateManager.func_179103_j(7424);
   }

   public static void drawBox(BlockPos blockPos, double height, GSColor color, int sides) {
      drawBox((double)blockPos.func_177958_n(), (double)blockPos.func_177956_o(), (double)blockPos.func_177952_p(), 1.0D, height, 1.0D, color, color.getAlpha(), sides);
   }

   public static void drawBox(AxisAlignedBB bb, boolean check, double height, GSColor color, int sides) {
      drawBox(bb, check, height, color, color.getAlpha(), sides);
   }

   public static void drawBox(AxisAlignedBB bb, boolean check, double height, GSColor color, int alpha, int sides) {
      if (check) {
         drawBox(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, bb.field_72336_d - bb.field_72340_a, bb.field_72337_e - bb.field_72338_b, bb.field_72334_f - bb.field_72339_c, color, alpha, sides);
      } else {
         drawBox(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, bb.field_72336_d - bb.field_72340_a, height, bb.field_72334_f - bb.field_72339_c, color, alpha, sides);
      }

   }

   public static void drawBox(double x, double y, double z, double w, double h, double d, GSColor color, int alpha, int sides) {
      GlStateManager.func_179118_c();
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      color.glColor();
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
      doVerticies(new AxisAlignedBB(x, y, z, x + w, y + h, z + d), color, alpha, bufferbuilder, sides, false);
      tessellator.func_78381_a();
      GlStateManager.func_179141_d();
   }

   public static void drawBoxDire(AxisAlignedBB bb, double height, GSColor color, int alpha, int sides) {
      drawBoxDire(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, bb.field_72336_d - bb.field_72340_a, height, bb.field_72334_f - bb.field_72339_c, color, alpha, sides);
      drawFixBoxDire(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, bb.field_72336_d - bb.field_72340_a, height, bb.field_72334_f - bb.field_72339_c, color, alpha, sides);
   }

   public static void drawBoxDire(double x, double y, double z, double w, double h, double d, GSColor color, int alpha, int sides) {
      GlStateManager.func_179118_c();
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      color.glColor();
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
      doVerticies(new AxisAlignedBB(x, y, z, x + w, y + h, z + d), color, alpha, bufferbuilder, sides);
      tessellator.func_78381_a();
      GlStateManager.func_179141_d();
   }

   public static void drawFixBoxDire(double x, double y, double z, double w, double h, double d, GSColor color, int alpha, int sides) {
      GlStateManager.func_179118_c();
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      color.glColor();
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
      doFixVerticies(new AxisAlignedBB(x, y, z, x + w, y + h, z + d), color, alpha, bufferbuilder, sides);
      tessellator.func_78381_a();
      GlStateManager.func_179141_d();
   }

   public static void drawBoundingBoxDire(BlockPos pos, double height, double width, GSColor color, int alpha, int sides) {
      drawBoundingBoxDire(new AxisAlignedBB(pos), height, width, color, alpha, sides);
   }

   public static void drawBoundingBoxDire(AxisAlignedBB bb, double height, double width, GSColor color, int alpha, int sides) {
      drawBoundingBoxDire(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, bb.field_72336_d - bb.field_72340_a, height, bb.field_72334_f - bb.field_72339_c, width, color, alpha, sides);
   }

   public static void drawBoundingBoxDire(double x, double y, double z, double w, double h, double d, double width, GSColor color, int alpha, int sides) {
      GlStateManager.func_179118_c();
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      GlStateManager.func_187441_d((float)width);
      color.glColor();
      bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + w, y + h, z + d);
      if ((sides & 32) != 0) {
         colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
      }

      if ((sides & 16) != 0) {
         colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
      }

      if ((sides & 4) != 0) {
         colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
      }

      if ((sides & 8) != 0) {
         colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
      }

      tessellator.func_78381_a();
      GlStateManager.func_179141_d();
   }

   public static void drawBoundingBox(AxisAlignedBB bb, double width, GSColor[] otherPos) {
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      GlStateManager.func_187441_d((float)width);
      bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, otherPos[0], otherPos[0].getAlpha(), bufferbuilder);
      colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, otherPos[1], otherPos[1].getAlpha(), bufferbuilder);
      colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, otherPos[2], otherPos[2].getAlpha(), bufferbuilder);
      colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, otherPos[3], otherPos[3].getAlpha(), bufferbuilder);
      colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, otherPos[0], otherPos[0].getAlpha(), bufferbuilder);
      colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, otherPos[4], otherPos[4].getAlpha(), bufferbuilder);
      colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, otherPos[5], otherPos[5].getAlpha(), bufferbuilder);
      colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, otherPos[1], otherPos[1].getAlpha(), bufferbuilder);
      colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, otherPos[2], otherPos[2].getAlpha(), bufferbuilder);
      colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, otherPos[6], otherPos[6].getAlpha(), bufferbuilder);
      colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, otherPos[5], otherPos[5].getAlpha(), bufferbuilder);
      colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, otherPos[6], otherPos[6].getAlpha(), bufferbuilder);
      colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, otherPos[7], otherPos[7].getAlpha(), bufferbuilder);
      colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, otherPos[3], otherPos[3].getAlpha(), bufferbuilder);
      colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, otherPos[7], otherPos[7].getAlpha(), bufferbuilder);
      colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, otherPos[4], otherPos[4].getAlpha(), bufferbuilder);
      tessellator.func_78381_a();
   }

   public static void drawBoundingBox(AxisAlignedBB axisAlignedBB, double width, GSColor[] color, boolean five, int sides) {
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      GlStateManager.func_187441_d((float)width);
      bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      if ((sides & 32) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[2], color[2].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[7], color[7].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[6], color[6].getAlpha(), bufferbuilder);
         if (five) {
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[2], color[2].getAlpha(), bufferbuilder);
         }
      }

      if ((sides & 16) != 0) {
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[0], color[0].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[1], color[1].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[5], color[5].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[4], color[4].getAlpha(), bufferbuilder);
         if (five) {
            colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[0], color[0].getAlpha(), bufferbuilder);
         }
      }

      if ((sides & 4) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[0], color[0].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[4], color[4].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[7], color[7].getAlpha(), bufferbuilder);
         if (five) {
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
         }
      }

      if ((sides & 8) != 0) {
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[1], color[1].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[2], color[2].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[6], color[6].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[5], color[5].getAlpha(), bufferbuilder);
         if (five) {
            colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[1], color[1].getAlpha(), bufferbuilder);
         }
      }

      if ((sides & 2) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[7], color[7].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[6], color[6].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[5], color[5].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[4], color[4].getAlpha(), bufferbuilder);
         if (five) {
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[7], color[7].getAlpha(), bufferbuilder);
         }
      }

      if ((sides & 1) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[2], color[2].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[1], color[1].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[0], color[0].getAlpha(), bufferbuilder);
         if (five) {
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
         }
      }

      tessellator.func_78381_a();
   }

   public static void drawBoundingBox(BlockPos bp, double height, float width, GSColor color) {
      drawBoundingBox(getBoundingBox(bp, height), (double)width, color, color.getAlpha());
   }

   public static void drawBoundingBox(AxisAlignedBB bb, double width, GSColor color) {
      drawBoundingBox(bb, width, color, color.getAlpha());
   }

   public static void drawBoundingBox(AxisAlignedBB bb, double width, GSColor color, int alpha) {
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      GlStateManager.func_187441_d((float)width);
      color.glColor();
      bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
      colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
      colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
      colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
      colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
      colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
      colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
      colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
      colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
      colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
      colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
      colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
      colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
      colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
      colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
      colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
      tessellator.func_78381_a();
   }

   public static void drawBoundingBoxWithSides(BlockPos blockPos, double high, int width, GSColor color, int sides) {
      drawBoundingBoxWithSides(getBoundingBox(blockPos, high), width, color, color.getAlpha(), sides);
   }

   public static void drawBoundingBoxWithSides(BlockPos blockPos, int width, GSColor color, int sides) {
      drawBoundingBoxWithSides(getBoundingBox(blockPos, 1.0D), width, color, color.getAlpha(), sides);
   }

   public static void drawBoundingBoxWithSides(BlockPos blockPos, int width, GSColor color, int alpha, int sides) {
      drawBoundingBoxWithSides(getBoundingBox(blockPos, 1.0D), width, color, alpha, sides);
   }

   public static void drawBoundingBoxWithSides(AxisAlignedBB axisAlignedBB, int width, GSColor color, int sides) {
      drawBoundingBoxWithSides(axisAlignedBB, width, color, color.getAlpha(), sides);
   }

   public static void drawBoundingBoxWithSides(AxisAlignedBB axisAlignedBB, int width, GSColor color, int alpha, int sides) {
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      GlStateManager.func_187441_d((float)width);
      bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      doVerticies(axisAlignedBB, color, alpha, bufferbuilder, sides, true);
      tessellator.func_78381_a();
   }

   public static void drawBoxProva2(AxisAlignedBB bb, GSColor[] color, int sides) {
      drawBoxProva(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, bb.field_72336_d - bb.field_72340_a, bb.field_72337_e - bb.field_72338_b, bb.field_72334_f - bb.field_72339_c, color, sides);
   }

   public static void drawBoxProva(double x, double y, double z, double w, double h, double d, GSColor[] color, int sides) {
      GlStateManager.func_179118_c();
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
      doVerticiesProva(new AxisAlignedBB(x, y, z, x + w, y + h, z + d), color, bufferbuilder, sides);
      tessellator.func_78381_a();
      GlStateManager.func_179141_d();
   }

   private static void doVerticiesProva(AxisAlignedBB axisAlignedBB, GSColor[] color, BufferBuilder bufferbuilder, int sides) {
      if ((sides & 32) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[2], color[2].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[7], color[7].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[6], color[6].getAlpha(), bufferbuilder);
      }

      if ((sides & 16) != 0) {
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[0], color[0].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[1], color[1].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[5], color[5].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[4], color[4].getAlpha(), bufferbuilder);
      }

      if ((sides & 4) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[0], color[0].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[4], color[4].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[7], color[7].getAlpha(), bufferbuilder);
      }

      if ((sides & 8) != 0) {
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[1], color[1].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[2], color[2].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[6], color[6].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[5], color[5].getAlpha(), bufferbuilder);
      }

      if ((sides & 2) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[7], color[7].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[6], color[6].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[5], color[5].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[4], color[4].getAlpha(), bufferbuilder);
      }

      if ((sides & 1) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[2], color[2].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[1], color[1].getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[0], color[0].getAlpha(), bufferbuilder);
      }

   }

   public static void drawBoxWithDirection(AxisAlignedBB bb, GSColor color, float rotation, float width, int mode) {
      double xCenter = bb.field_72340_a + (bb.field_72336_d - bb.field_72340_a) / 2.0D;
      double zCenter = bb.field_72339_c + (bb.field_72334_f - bb.field_72339_c) / 2.0D;
      RenderUtil.Points square = new RenderUtil.Points(bb.field_72338_b, bb.field_72337_e, xCenter, zCenter, rotation);
      if (mode == 0) {
         square.addPoints(bb.field_72340_a, bb.field_72339_c);
         square.addPoints(bb.field_72340_a, bb.field_72334_f);
         square.addPoints(bb.field_72336_d, bb.field_72334_f);
         square.addPoints(bb.field_72336_d, bb.field_72339_c);
      }

      if (mode == 0) {
         drawDirection(square, color, width);
      }

   }

   public static void drawDirection(RenderUtil.Points square, GSColor color, float width) {
      int i;
      for(i = 0; i < 4; ++i) {
         drawLine(square.getPoint(i)[0], square.yMin, square.getPoint(i)[1], square.getPoint((i + 1) % 4)[0], square.yMin, square.getPoint((i + 1) % 4)[1], color, width);
      }

      for(i = 0; i < 4; ++i) {
         drawLine(square.getPoint(i)[0], square.yMax, square.getPoint(i)[1], square.getPoint((i + 1) % 4)[0], square.yMax, square.getPoint((i + 1) % 4)[1], color, width);
      }

      for(i = 0; i < 4; ++i) {
         drawLine(square.getPoint(i)[0], square.yMin, square.getPoint(i)[1], square.getPoint(i)[0], square.yMax, square.getPoint(i)[1], color, width);
      }

   }

   public static void drawSphere(double x, double y, double z, float size, int slices, int stacks, float lineWidth, GSColor color) {
      Sphere sphere = new Sphere();
      GlStateManager.func_187441_d(lineWidth);
      color.glColor();
      sphere.setDrawStyle(100013);
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(x - mc.func_175598_ae().field_78730_l, y - mc.func_175598_ae().field_78731_m, z - mc.func_175598_ae().field_78728_n);
      sphere.draw(size, slices, stacks);
      GlStateManager.func_179121_F();
   }

   public static void drawNametag(Entity entity, String[] text, GSColor color, int type) {
      Vec3d pos = EntityUtil.getInterpolatedPos(entity, mc.func_184121_ak());
      drawNametag(pos.field_72450_a, pos.field_72448_b + (double)entity.field_70131_O, pos.field_72449_c, text, color, type, 0.0D, 0.0D);
   }

   public static double getDistance(double x, double y, double z) {
      Entity viewEntity = mc.func_175606_aa();
      if (viewEntity == null) {
         viewEntity = mc.field_71439_g;
      }

      double d0 = ((Entity)viewEntity).field_70165_t - x;
      double d1 = ((Entity)viewEntity).field_70163_u - y;
      double d2 = ((Entity)viewEntity).field_70161_v - z;
      return (double)MathHelper.func_76133_a(d0 * d0 + d1 * d1 + d2 * d2);
   }

   public static void drawNametag(double x, double y, double z, String[] text, GSColor color, int type, double customScale, double maxSize) {
      ColorMain colorMain = (ColorMain)ModuleManager.getModule(ColorMain.class);
      double dist = getDistance(x, y, z);
      double scale = 1.0D;
      double offset = 0.0D;
      int start = 0;
      switch(type) {
      case 0:
         scale = dist / 20.0D * Math.pow(1.2589254D, 0.1D / (dist < 25.0D ? 0.5D : 2.0D));
         scale = Math.min(Math.max(scale, 0.5D), 5.0D);
         offset = scale > 2.0D ? scale / 2.0D : scale;
         scale /= 40.0D;
         start = 10;
         break;
      case 1:
         scale = customScale;
         break;
      case 2:
         scale = 0.0018D + customScale * dist;
         if (dist <= 8.0D) {
            scale = 6.125D * customScale;
         }

         start = -8;
      }

      if (maxSize != 0.0D && scale > maxSize) {
         scale = maxSize;
      }

      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(x - mc.func_175598_ae().field_78730_l, y + offset - mc.func_175598_ae().field_78731_m, z - mc.func_175598_ae().field_78728_n);
      GlStateManager.func_179114_b(-mc.func_175598_ae().field_78735_i, 0.0F, 1.0F, 0.0F);
      float var10001 = mc.field_71474_y.field_74320_O == 2 ? -1.0F : 1.0F;
      GlStateManager.func_179114_b(mc.func_175598_ae().field_78732_j, var10001, 0.0F, 0.0F);
      GlStateManager.func_179139_a(-scale, -scale, scale);
      if (type == 2) {
         Nametags nametags = (Nametags)ModuleManager.getModule(Nametags.class);
         double width = 0.0D;
         GSColor bcolor = new GSColor(0, 0, 0, 0);
         if ((Boolean)nametags.outline.getValue()) {
            bcolor = color;
            if ((Boolean)nametags.customColor.getValue()) {
               bcolor = nametags.borderColor.getValue();
            }
         }

         String[] var25 = text;
         int var26 = text.length;

         for(int var27 = 0; var27 < var26; ++var27) {
            String s = var25[var27];
            double w = (double)FontUtil.getStringWidth((Boolean)colorMain.customFont.getValue(), s) / 2.0D;
            if (w > width) {
               width = w;
            }
         }

         drawBorderedRect(-width - 1.0D, (double)(-mc.field_71466_p.field_78288_b), width + 2.0D, new GSColor(0, 4, 0, (Boolean)nametags.border.getValue() ? 85 : 0), bcolor);
      }

      GlStateManager.func_179098_w();

      for(int i = 0; i < text.length; ++i) {
         FontUtil.drawStringWithShadow((Boolean)colorMain.customFont.getValue(), text[i], (float)(-FontUtil.getStringWidth((Boolean)colorMain.customFont.getValue(), text[i]) / 2), (float)(i * (mc.field_71466_p.field_78288_b + 1) + start), color);
      }

      GlStateManager.func_179090_x();
      if (type != 2) {
         GlStateManager.func_179121_F();
      }

   }

   private static void vertex(double x, double y, double z, BufferBuilder bufferbuilder) {
      bufferbuilder.func_181662_b(x - mc.func_175598_ae().field_78730_l, y - mc.func_175598_ae().field_78731_m, z - mc.func_175598_ae().field_78728_n).func_181675_d();
   }

   private static void colorVertex(double x, double y, double z, GSColor color, int alpha, BufferBuilder bufferbuilder) {
      bufferbuilder.func_181662_b(x - mc.func_175598_ae().field_78730_l, y - mc.func_175598_ae().field_78731_m, z - mc.func_175598_ae().field_78728_n).func_181669_b(color.getRed(), color.getGreen(), color.getBlue(), alpha).func_181675_d();
   }

   private static AxisAlignedBB getBoundingBox(BlockPos bp, double height) {
      double x = (double)bp.func_177958_n();
      double y = (double)bp.func_177956_o();
      double z = (double)bp.func_177952_p();
      return new AxisAlignedBB(x, y, z, x + 1.0D, y + height, z + 1.0D);
   }

   private static void doVerticies(AxisAlignedBB axisAlignedBB, GSColor color, int alpha, BufferBuilder bufferbuilder, int sides, boolean five) {
      if ((sides & 32) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         if (five) {
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         }
      }

      if ((sides & 16) != 0) {
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         if (five) {
            colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         }
      }

      if ((sides & 4) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         if (five) {
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         }
      }

      if ((sides & 8) != 0) {
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         if (five) {
            colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         }
      }

      if ((sides & 2) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         if (five) {
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         }
      }

      if ((sides & 1) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         if (five) {
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         }
      }

   }

   public static void doVerticies(AxisAlignedBB axisAlignedBB, GSColor color, int alpha, BufferBuilder bufferbuilder, int sides) {
      if ((sides & 32) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
      }

      if ((sides & 16) != 0) {
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
      }

      if ((sides & 4) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
      }

      if ((sides & 8) != 0) {
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
      }

   }

   public static void doFixVerticies(AxisAlignedBB axisAlignedBB, GSColor color, int alpha, BufferBuilder bufferbuilder, int sides) {
      if ((sides & 32) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
      }

      if ((sides & 16) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
      }

      if ((sides & 4) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
      }

      if ((sides & 8) != 0) {
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
         colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
      }

   }

   public static void prepare() {
      GL11.glHint(3154, 4354);
      GlStateManager.func_179120_a(770, 771, 0, 1);
      GlStateManager.func_179103_j(7425);
      GlStateManager.func_179132_a(false);
      GlStateManager.func_179147_l();
      GlStateManager.func_179097_i();
      GlStateManager.func_179090_x();
      GlStateManager.func_179140_f();
      GlStateManager.func_179129_p();
      GlStateManager.func_179141_d();
      GL11.glEnable(2848);
      GL11.glEnable(34383);
   }

   public static void release() {
      GL11.glDisable(34383);
      GL11.glDisable(2848);
      GlStateManager.func_179141_d();
      GlStateManager.func_179089_o();
      GlStateManager.func_179098_w();
      GlStateManager.func_179126_j();
      GlStateManager.func_179084_k();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_187441_d(1.0F);
      GlStateManager.func_179103_j(7424);
      GL11.glHint(3154, 4352);
   }

   public static Vec3d getInterpolatedPos(Entity entity, float partialTicks, boolean wrap) {
      Vec3d amount = new Vec3d((entity.field_70165_t - entity.field_70142_S) * (double)partialTicks, (entity.field_70163_u - entity.field_70137_T) * (double)partialTicks, (entity.field_70161_v - entity.field_70136_U) * (double)partialTicks);
      Vec3d vec = (new Vec3d(entity.field_70142_S, entity.field_70137_T, entity.field_70136_U)).func_178787_e(amount);
      return wrap ? vec.func_178786_a(mc.func_175598_ae().field_78725_b, mc.func_175598_ae().field_78726_c, mc.func_175598_ae().field_78723_d) : vec;
   }

   public static AxisAlignedBB getAxisAlignedBB(BlockPos pos, double size) {
      AxisAlignedBB bb = mc.field_71441_e.func_180495_p(pos).func_185918_c(mc.field_71441_e, pos);
      Vec3d center = bb.func_189972_c();
      return new AxisAlignedBB(center.field_72450_a - (bb.field_72336_d - bb.field_72340_a) * size, center.field_72448_b - (bb.field_72337_e - bb.field_72340_a) * size, center.field_72449_c - (bb.field_72334_f - bb.field_72339_c) * size, center.field_72450_a + (bb.field_72336_d - bb.field_72340_a) * size, center.field_72448_b + (bb.field_72337_e - bb.field_72338_b) * size, center.field_72449_c + (bb.field_72334_f - bb.field_72339_c) * size);
   }

   public static AxisAlignedBB getInterpolatedAxis(AxisAlignedBB bb) {
      return new AxisAlignedBB(bb.field_72340_a - mc.func_175598_ae().field_78730_l, bb.field_72338_b - mc.func_175598_ae().field_78731_m, bb.field_72339_c - mc.func_175598_ae().field_78728_n, bb.field_72336_d - mc.func_175598_ae().field_78730_l, bb.field_72337_e - mc.func_175598_ae().field_78731_m, bb.field_72334_f - mc.func_175598_ae().field_78728_n);
   }

   public static Vec3d getInterpolatedRenderPos(Entity entity, float ticks) {
      return interpolateEntity(entity, ticks).func_178786_a(mc.func_175598_ae().field_78725_b, mc.func_175598_ae().field_78726_c, mc.func_175598_ae().field_78723_d);
   }

   public static Vec3d interpolateEntity(Entity entity, float time) {
      return new Vec3d(entity.field_70142_S + (entity.field_70165_t - entity.field_70142_S) * (double)time, entity.field_70137_T + (entity.field_70163_u - entity.field_70137_T) * (double)time, entity.field_70136_U + (entity.field_70161_v - entity.field_70136_U) * (double)time);
   }

   public static double getInterpolatedDouble(double pre, double current, float partialTicks) {
      return pre + (current - pre) * (double)partialTicks;
   }

   public static float getInterpolatedFloat(float pre, float current, float partialTicks) {
      return pre + (current - pre) * partialTicks;
   }

   private static class Points {
      double[][] point = new double[10][2];
      private int count = 0;
      private final double xCenter;
      private final double zCenter;
      public final double yMin;
      public final double yMax;
      private final float rotation;

      public Points(double yMin, double yMax, double xCenter, double zCenter, float rotation) {
         this.yMin = yMin;
         this.yMax = yMax;
         this.xCenter = xCenter;
         this.zCenter = zCenter;
         this.rotation = rotation;
      }

      public void addPoints(double x, double z) {
         x -= this.xCenter;
         z -= this.zCenter;
         double rotateX = x * Math.cos((double)this.rotation) - z * Math.sin((double)this.rotation);
         double rotateZ = x * Math.sin((double)this.rotation) + z * Math.cos((double)this.rotation);
         rotateX += this.xCenter;
         rotateZ += this.zCenter;
         this.point[this.count++] = new double[]{rotateX, rotateZ};
      }

      public double[] getPoint(int index) {
         return this.point[index];
      }
   }

   public static enum GradientDirection {
      LeftToRight,
      RightToLeft,
      UpToDown,
      DownToUp,
      Normal;
   }
}
