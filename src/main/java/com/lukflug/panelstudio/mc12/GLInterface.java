package com.lukflug.panelstudio.mc12;

import com.lukflug.panelstudio.base.IInterface;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Stack;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public abstract class GLInterface implements IInterface {
   private final Stack<Rectangle> clipRect = new Stack();
   protected boolean clipX;

   public GLInterface(boolean clipX) {
      this.clipX = clipX;
   }

   public boolean getModifier(int modifier) {
      switch(modifier) {
      case 0:
         return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
      case 1:
         return Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
      case 2:
         return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
      case 3:
         return Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220);
      default:
         return false;
      }
   }

   public Dimension getWindowSize() {
      return new Dimension((int)Math.ceil(this.getScreenWidth()), (int)Math.ceil(this.getScreenHeight()));
   }

   public void drawString(Point pos, int height, String s, Color c) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)pos.x, (float)pos.y, 0.0F);
      double scale = (double)height / (double)Minecraft.func_71410_x().field_71466_p.field_78288_b;
      GlStateManager.func_179139_a(scale, scale, 1.0D);
      this.end(false);
      Minecraft.func_71410_x().field_71466_p.func_175063_a(s, 0.0F, 0.0F, c.getRGB());
      this.begin(false);
      GlStateManager.func_179121_F();
   }

   public int getFontWidth(int height, String s) {
      double scale = (double)height / (double)Minecraft.func_71410_x().field_71466_p.field_78288_b;
      return (int)Math.round((double)Minecraft.func_71410_x().field_71466_p.func_78256_a(s) * scale);
   }

   public void fillTriangle(Point pos1, Point pos2, Point pos3, Color c1, Color c2, Color c3) {
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      bufferbuilder.func_181668_a(4, DefaultVertexFormats.field_181706_f);
      bufferbuilder.func_181662_b((double)pos1.x, (double)pos1.y, (double)this.getZLevel()).func_181666_a((float)c1.getRed() / 255.0F, (float)c1.getGreen() / 255.0F, (float)c1.getBlue() / 255.0F, (float)c1.getAlpha() / 255.0F).func_181675_d();
      bufferbuilder.func_181662_b((double)pos2.x, (double)pos2.y, (double)this.getZLevel()).func_181666_a((float)c2.getRed() / 255.0F, (float)c2.getGreen() / 255.0F, (float)c2.getBlue() / 255.0F, (float)c2.getAlpha() / 255.0F).func_181675_d();
      bufferbuilder.func_181662_b((double)pos3.x, (double)pos3.y, (double)this.getZLevel()).func_181666_a((float)c3.getRed() / 255.0F, (float)c3.getGreen() / 255.0F, (float)c3.getBlue() / 255.0F, (float)c3.getAlpha() / 255.0F).func_181675_d();
      tessellator.func_78381_a();
   }

   public void drawLine(Point a, Point b, Color c1, Color c2) {
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      bufferbuilder.func_181668_a(1, DefaultVertexFormats.field_181706_f);
      bufferbuilder.func_181662_b((double)a.x, (double)a.y, (double)this.getZLevel()).func_181666_a((float)c1.getRed() / 255.0F, (float)c1.getGreen() / 255.0F, (float)c1.getBlue() / 255.0F, (float)c1.getAlpha() / 255.0F).func_181675_d();
      bufferbuilder.func_181662_b((double)b.x, (double)b.y, (double)this.getZLevel()).func_181666_a((float)c2.getRed() / 255.0F, (float)c2.getGreen() / 255.0F, (float)c2.getBlue() / 255.0F, (float)c2.getAlpha() / 255.0F).func_181675_d();
      tessellator.func_78381_a();
   }

   public void fillRect(Rectangle r, Color c1, Color c2, Color c3, Color c4) {
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
      bufferbuilder.func_181662_b((double)r.x, (double)(r.y + r.height), (double)this.getZLevel()).func_181666_a((float)c4.getRed() / 255.0F, (float)c4.getGreen() / 255.0F, (float)c4.getBlue() / 255.0F, (float)c4.getAlpha() / 255.0F).func_181675_d();
      bufferbuilder.func_181662_b((double)(r.x + r.width), (double)(r.y + r.height), (double)this.getZLevel()).func_181666_a((float)c3.getRed() / 255.0F, (float)c3.getGreen() / 255.0F, (float)c3.getBlue() / 255.0F, (float)c3.getAlpha() / 255.0F).func_181675_d();
      bufferbuilder.func_181662_b((double)(r.x + r.width), (double)r.y, (double)this.getZLevel()).func_181666_a((float)c2.getRed() / 255.0F, (float)c2.getGreen() / 255.0F, (float)c2.getBlue() / 255.0F, (float)c2.getAlpha() / 255.0F).func_181675_d();
      bufferbuilder.func_181662_b((double)r.x, (double)r.y, (double)this.getZLevel()).func_181666_a((float)c1.getRed() / 255.0F, (float)c1.getGreen() / 255.0F, (float)c1.getBlue() / 255.0F, (float)c1.getAlpha() / 255.0F).func_181675_d();
      tessellator.func_78381_a();
   }

   public void drawRect(Rectangle r, Color c1, Color c2, Color c3, Color c4) {
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      bufferbuilder.func_181668_a(2, DefaultVertexFormats.field_181706_f);
      bufferbuilder.func_181662_b((double)r.x, (double)(r.y + r.height), (double)this.getZLevel()).func_181666_a((float)c4.getRed() / 255.0F, (float)c4.getGreen() / 255.0F, (float)c4.getBlue() / 255.0F, (float)c4.getAlpha() / 255.0F).func_181675_d();
      bufferbuilder.func_181662_b((double)(r.x + r.width), (double)(r.y + r.height), (double)this.getZLevel()).func_181666_a((float)c3.getRed() / 255.0F, (float)c3.getGreen() / 255.0F, (float)c3.getBlue() / 255.0F, (float)c3.getAlpha() / 255.0F).func_181675_d();
      bufferbuilder.func_181662_b((double)(r.x + r.width), (double)r.y, (double)this.getZLevel()).func_181666_a((float)c2.getRed() / 255.0F, (float)c2.getGreen() / 255.0F, (float)c2.getBlue() / 255.0F, (float)c2.getAlpha() / 255.0F).func_181675_d();
      bufferbuilder.func_181662_b((double)r.x, (double)r.y, (double)this.getZLevel()).func_181666_a((float)c1.getRed() / 255.0F, (float)c1.getGreen() / 255.0F, (float)c1.getBlue() / 255.0F, (float)c1.getAlpha() / 255.0F).func_181675_d();
      tessellator.func_78381_a();
   }

   public synchronized int loadImage(String name) {
      try {
         ResourceLocation rl = new ResourceLocation(this.getResourcePrefix() + name);
         InputStream stream = Minecraft.func_71410_x().func_110442_L().func_110536_a(rl).func_110527_b();
         BufferedImage image = ImageIO.read(stream);
         int texture = TextureUtil.func_110996_a();
         TextureUtil.func_110987_a(texture, image);
         return texture;
      } catch (IOException var6) {
         var6.printStackTrace();
         return 0;
      }
   }

   public void drawImage(Rectangle r, int rotation, boolean parity, int image, Color color) {
      if (image != 0) {
         int[][] texCoords = new int[][]{{0, 1}, {1, 1}, {1, 0}, {0, 0}};

         int temp1;
         int temp2;
         for(temp1 = 0; temp1 < rotation % 4; ++temp1) {
            temp2 = texCoords[3][0];
            int temp2 = texCoords[3][1];
            texCoords[3][0] = texCoords[2][0];
            texCoords[3][1] = texCoords[2][1];
            texCoords[2][0] = texCoords[1][0];
            texCoords[2][1] = texCoords[1][1];
            texCoords[1][0] = texCoords[0][0];
            texCoords[1][1] = texCoords[0][1];
            texCoords[0][0] = temp2;
            texCoords[0][1] = temp2;
         }

         if (parity) {
            temp1 = texCoords[3][0];
            temp2 = texCoords[3][1];
            texCoords[3][0] = texCoords[0][0];
            texCoords[3][1] = texCoords[0][1];
            texCoords[0][0] = temp1;
            texCoords[0][1] = temp2;
            temp1 = texCoords[2][0];
            temp2 = texCoords[2][1];
            texCoords[2][0] = texCoords[1][0];
            texCoords[2][1] = texCoords[1][1];
            texCoords[1][0] = temp1;
            texCoords[1][1] = temp2;
         }

         Tessellator tessellator = Tessellator.func_178181_a();
         BufferBuilder bufferbuilder = tessellator.func_178180_c();
         FloatBuffer colorBuffer = GLAllocation.func_74529_h(4);
         colorBuffer.put(0, (float)color.getRed() / 255.0F);
         colorBuffer.put(1, (float)color.getGreen() / 255.0F);
         colorBuffer.put(2, (float)color.getBlue() / 255.0F);
         colorBuffer.put(3, (float)color.getAlpha() / 255.0F);
         GlStateManager.func_179144_i(image);
         GlStateManager.func_187448_b(8960, 8705, colorBuffer);
         GlStateManager.func_179098_w();
         bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         bufferbuilder.func_181662_b((double)r.x, (double)(r.y + r.height), (double)this.getZLevel()).func_187315_a((double)texCoords[0][0], (double)texCoords[0][1]).func_181675_d();
         bufferbuilder.func_181662_b((double)(r.x + r.width), (double)(r.y + r.height), (double)this.getZLevel()).func_187315_a((double)texCoords[1][0], (double)texCoords[1][1]).func_181675_d();
         bufferbuilder.func_181662_b((double)(r.x + r.width), (double)r.y, (double)this.getZLevel()).func_187315_a((double)texCoords[2][0], (double)texCoords[2][1]).func_181675_d();
         bufferbuilder.func_181662_b((double)r.x, (double)r.y, (double)this.getZLevel()).func_187315_a((double)texCoords[3][0], (double)texCoords[3][1]).func_181675_d();
         tessellator.func_78381_a();
         GlStateManager.func_179090_x();
      }
   }

   protected void scissor(Rectangle r) {
      if (r == null) {
         GL11.glScissor(0, 0, 0, 0);
         GL11.glEnable(3089);
      } else {
         Point a = this.guiToScreen(r.getLocation());
         Point b = this.guiToScreen(new Point(r.x + r.width, r.y + r.height));
         if (!this.clipX) {
            a.x = 0;
            b.x = Minecraft.func_71410_x().field_71443_c;
         }

         GL11.glScissor(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.abs(b.x - a.x), Math.abs(b.y - a.y));
         GL11.glEnable(3089);
      }
   }

   public void window(Rectangle r) {
      if (this.clipRect.isEmpty()) {
         this.scissor(r);
         this.clipRect.push(r);
      } else {
         Rectangle top = (Rectangle)this.clipRect.peek();
         if (top == null) {
            this.scissor((Rectangle)null);
            this.clipRect.push((Object)null);
         } else {
            int x1 = Math.max(r.x, top.x);
            int y1 = Math.max(r.y, top.y);
            int x2 = Math.min(r.x + r.width, top.x + top.width);
            int y2 = Math.min(r.y + r.height, top.y + top.height);
            if (x2 > x1 && y2 > y1) {
               Rectangle rect = new Rectangle(x1, y1, x2 - x1, y2 - y1);
               this.scissor(rect);
               this.clipRect.push(rect);
            } else {
               this.scissor((Rectangle)null);
               this.clipRect.push((Object)null);
            }
         }
      }

   }

   public void restore() {
      if (!this.clipRect.isEmpty()) {
         this.clipRect.pop();
         if (this.clipRect.isEmpty()) {
            GL11.glDisable(3089);
         } else {
            this.scissor((Rectangle)this.clipRect.peek());
         }
      }

   }

   public Point screenToGui(Point p) {
      int resX = this.getWindowSize().width;
      int resY = this.getWindowSize().height;
      return new Point(p.x * resX / Minecraft.func_71410_x().field_71443_c, resY - p.y * resY / Minecraft.func_71410_x().field_71440_d - 1);
   }

   public Point guiToScreen(Point p) {
      double resX = this.getScreenWidth();
      double resY = this.getScreenHeight();
      return new Point((int)Math.round((double)(p.x * Minecraft.func_71410_x().field_71443_c) / resX), (int)Math.round((resY - (double)p.y) * (double)Minecraft.func_71410_x().field_71440_d / resY));
   }

   protected double getScreenWidth() {
      return (new ScaledResolution(Minecraft.func_71410_x())).func_78327_c();
   }

   protected double getScreenHeight() {
      return (new ScaledResolution(Minecraft.func_71410_x())).func_78324_d();
   }

   public void begin(boolean matrix) {
      if (matrix) {
         GlStateManager.func_179128_n(5889);
         GlStateManager.func_179094_E();
         GlStateManager.func_179096_D();
         GlStateManager.func_179130_a(0.0D, this.getScreenWidth(), this.getScreenHeight(), 0.0D, -3000.0D, 3000.0D);
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179094_E();
         GlStateManager.func_179096_D();
      }

      GlStateManager.func_179147_l();
      GlStateManager.func_179090_x();
      GlStateManager.func_179118_c();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      GlStateManager.func_179103_j(7425);
      GlStateManager.func_187441_d(2.0F);
      GL11.glPushAttrib(262144);
      GlStateManager.func_187399_a(8960, 8704, 34160);
      GlStateManager.func_187399_a(8960, 34161, 8448);
      GlStateManager.func_187399_a(8960, 34162, 8448);
      GlStateManager.func_187399_a(8960, 34176, 5890);
      GlStateManager.func_187399_a(8960, 34192, 768);
      GlStateManager.func_187399_a(8960, 34184, 5890);
      GlStateManager.func_187399_a(8960, 34200, 770);
      GlStateManager.func_187399_a(8960, 34177, 34166);
      GlStateManager.func_187399_a(8960, 34193, 768);
      GlStateManager.func_187399_a(8960, 34185, 34166);
      GlStateManager.func_187399_a(8960, 34201, 770);
   }

   public void end(boolean matrix) {
      GL11.glPopAttrib();
      GlStateManager.func_179103_j(7424);
      GlStateManager.func_179141_d();
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
      if (matrix) {
         GlStateManager.func_179128_n(5889);
         GlStateManager.func_179121_F();
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179121_F();
      }

   }

   protected abstract float getZLevel();

   protected abstract String getResourcePrefix();
}
