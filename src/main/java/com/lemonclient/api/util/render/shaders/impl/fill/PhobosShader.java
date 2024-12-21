package com.lemonclient.api.util.render.shaders.impl.fill;

import com.lemonclient.api.util.render.shaders.FramebufferShader;
import java.awt.Color;
import java.util.HashMap;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class PhobosShader extends FramebufferShader {
   public static final PhobosShader INSTANCE = new PhobosShader();
   public float time;

   public PhobosShader() {
      super("phobos.frag");
   }

   public void setupUniforms() {
      this.setupUniform("resolution");
      this.setupUniform("time");
      this.setupUniform("color");
      this.setupUniform("texelSize");
      this.setupUniform("texture");
   }

   public void updateUniforms(float duplicate, Color color, int lines, double tau) {
      GL20.glUniform2f(this.getUniform("resolution"), (float)(new ScaledResolution(this.mc)).func_78326_a() / duplicate, (float)(new ScaledResolution(this.mc)).func_78328_b() / duplicate);
      GL20.glUniform1i(this.getUniform("texture"), 0);
      GL20.glUniform1f(this.getUniform("time"), this.time);
      GL20.glUniform4f(this.getUniform("color"), (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
      GL20.glUniform2f(this.getUniform("texelSize"), 1.0F / (float)this.mc.field_71443_c * this.radius * this.quality, 1.0F / (float)this.mc.field_71440_d * this.radius * this.quality);
   }

   public void stopDraw(Color color, float radius, float quality, float duplicate, int lines, double tau) {
      this.mc.field_71474_y.field_181151_V = this.entityShadows;
      this.framebuffer.func_147609_e();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      this.mc.func_147110_a().func_147610_a(true);
      this.red = (float)color.getRed() / 255.0F;
      this.green = (float)color.getGreen() / 255.0F;
      this.blue = (float)color.getBlue() / 255.0F;
      this.alpha = (float)color.getAlpha() / 255.0F;
      this.radius = radius;
      this.quality = quality;
      this.mc.field_71460_t.func_175072_h();
      RenderHelper.func_74518_a();
      GL11.glPushMatrix();
      this.startShader(duplicate, color, lines, tau);
      this.mc.field_71460_t.func_78478_c();
      this.drawFramebuffer(this.framebuffer);
      this.stopShader();
      this.mc.field_71460_t.func_175072_h();
      GlStateManager.func_179121_F();
      GlStateManager.func_179099_b();
   }

   public void startShader(float duplicate, Color color, int lines, double tau) {
      GL20.glUseProgram(this.program);
      if (this.uniformsMap == null) {
         this.uniformsMap = new HashMap();
         this.setupUniforms();
      }

      this.updateUniforms(duplicate, color, lines, tau);
   }

   public void update(double speed) {
      this.time = (float)((double)this.time + speed);
   }
}
