package com.lemonclient.api.util.render.shaders.impl.outline;

import com.lemonclient.api.util.render.shaders.FramebufferShader;
import java.awt.Color;
import java.util.HashMap;
import java.util.function.Predicate;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public final class SmokeOutlineShader extends FramebufferShader {
   public static final SmokeOutlineShader INSTANCE = new SmokeOutlineShader();
   public float time = 0.0F;

   public SmokeOutlineShader() {
      super("smokeOutline.frag");
   }

   public void setupUniforms() {
      this.setupUniform("texture");
      this.setupUniform("texelSize");
      this.setupUniform("divider");
      this.setupUniform("radius");
      this.setupUniform("maxSample");
      this.setupUniform("alpha0");
      this.setupUniform("resolution");
      this.setupUniform("time");
      this.setupUniform("first");
      this.setupUniform("second");
      this.setupUniform("third");
      this.setupUniform("oct");
   }

   public void updateUniforms(Color first, float radius, float quality, boolean gradientAlpha, int alphaOutline, float duplicate, Color second, Color third, int oct) {
      GL20.glUniform1i(this.getUniform("texture"), 0);
      GL20.glUniform2f(this.getUniform("texelSize"), 1.0F / (float)this.mc.field_71443_c * radius * quality, 1.0F / (float)this.mc.field_71440_d * radius * quality);
      GL20.glUniform1f(this.getUniform("divider"), 140.0F);
      GL20.glUniform1f(this.getUniform("radius"), radius);
      GL20.glUniform1f(this.getUniform("maxSample"), 10.0F);
      GL20.glUniform1f(this.getUniform("alpha0"), gradientAlpha ? -1.0F : (float)alphaOutline / 255.0F);
      GL20.glUniform2f(this.getUniform("resolution"), (float)(new ScaledResolution(this.mc)).func_78326_a() / duplicate, (float)(new ScaledResolution(this.mc)).func_78328_b() / duplicate);
      GL20.glUniform1f(this.getUniform("time"), this.time);
      GL20.glUniform4f(this.getUniform("first"), (float)first.getRed() / 255.0F * 5.0F, (float)first.getGreen() / 255.0F * 5.0F, (float)first.getBlue() / 255.0F * 5.0F, (float)first.getAlpha() / 255.0F);
      GL20.glUniform3f(this.getUniform("second"), (float)second.getRed() / 255.0F * 5.0F, (float)second.getGreen() / 255.0F * 5.0F, (float)second.getBlue() / 255.0F * 5.0F);
      GL20.glUniform3f(this.getUniform("third"), (float)third.getRed() / 255.0F * 5.0F, (float)third.getGreen() / 255.0F * 5.0F, (float)third.getBlue() / 255.0F * 5.0F);
      GL20.glUniform1i(this.getUniform("oct"), oct);
   }

   public void stopDraw(Color color, float radius, float quality, boolean gradientAlpha, int alphaOutline, float duplicate, Color second, Color third, int oct) {
      this.mc.field_71474_y.field_181151_V = this.entityShadows;
      this.framebuffer.func_147609_e();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      this.mc.func_147110_a().func_147610_a(true);
      this.mc.field_71460_t.func_175072_h();
      RenderHelper.func_74518_a();
      this.startShader(color, radius, quality, gradientAlpha, alphaOutline, duplicate, second, third, oct);
      this.mc.field_71460_t.func_78478_c();
      this.drawFramebuffer(this.framebuffer);
      this.stopShader();
      this.mc.field_71460_t.func_175072_h();
      GlStateManager.func_179121_F();
      GlStateManager.func_179099_b();
   }

   public void stopDraw(Color color, float radius, float quality, boolean gradientAlpha, int alphaOutline, float duplicate, Color second, Color third, int oct, Predicate<Boolean> fill) {
      this.mc.field_71474_y.field_181151_V = this.entityShadows;
      this.framebuffer.func_147609_e();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      this.mc.func_147110_a().func_147610_a(true);
      this.mc.field_71460_t.func_175072_h();
      RenderHelper.func_74518_a();
      this.startShader(color, radius, quality, gradientAlpha, alphaOutline, duplicate, second, third, oct);
      this.mc.field_71460_t.func_78478_c();
      this.drawFramebuffer(this.framebuffer);
      fill.test(false);
      this.drawFramebuffer(this.framebuffer);
      this.stopShader();
      this.mc.field_71460_t.func_175072_h();
      GlStateManager.func_179121_F();
      GlStateManager.func_179099_b();
   }

   public void startShader(Color color, float radius, float quality, boolean gradientAlpha, int alphaOutline, float duplicate, Color second, Color third, int oct) {
      GL11.glPushMatrix();
      GL20.glUseProgram(this.program);
      if (this.uniformsMap == null) {
         this.uniformsMap = new HashMap();
         this.setupUniforms();
      }

      this.updateUniforms(color, radius, quality, gradientAlpha, alphaOutline, duplicate, second, third, oct);
   }

   public void update(double speed) {
      this.time = (float)((double)this.time + speed);
   }
}