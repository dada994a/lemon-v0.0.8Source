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

public final class AstralOutlineShader extends FramebufferShader {
   public static final AstralOutlineShader INSTANCE = new AstralOutlineShader();
   public float time = 0.0F;

   public AstralOutlineShader() {
      super("astralOutline.frag");
   }

   public void setupUniforms() {
      this.setupUniform("texture");
      this.setupUniform("texelSize");
      this.setupUniform("color");
      this.setupUniform("divider");
      this.setupUniform("radius");
      this.setupUniform("maxSample");
      this.setupUniform("alpha0");
      this.setupUniform("time");
      this.setupUniform("iterations");
      this.setupUniform("formuparam2");
      this.setupUniform("stepsize");
      this.setupUniform("volsteps");
      this.setupUniform("zoom");
      this.setupUniform("tile");
      this.setupUniform("distfading");
      this.setupUniform("saturation");
      this.setupUniform("fadeBol");
      this.setupUniform("resolution");
   }

   public void updateUniforms(Color color, float radius, float quality, boolean gradientAlpha, int alphaOutline, float duplicate, float red, float green, float blue, float alpha, int iteractions, float formuparam2, float zoom, float volumSteps, float stepSize, float title, float distfading, float saturation, float cloud, int fade) {
      GL20.glUniform1i(this.getUniform("texture"), 0);
      GL20.glUniform2f(this.getUniform("texelSize"), 1.0F / (float)this.mc.field_71443_c * radius * quality, 1.0F / (float)this.mc.field_71440_d * radius * quality);
      GL20.glUniform1f(this.getUniform("divider"), 140.0F);
      GL20.glUniform1f(this.getUniform("radius"), radius);
      GL20.glUniform1f(this.getUniform("maxSample"), 10.0F);
      GL20.glUniform1f(this.getUniform("alpha0"), gradientAlpha ? -1.0F : (float)alphaOutline / 255.0F);
      GL20.glUniform2f(this.getUniform("resolution"), (float)(new ScaledResolution(this.mc)).func_78326_a() / duplicate, (float)(new ScaledResolution(this.mc)).func_78328_b() / duplicate);
      GL20.glUniform1f(this.getUniform("time"), this.time);
      GL20.glUniform4f(this.getUniform("color"), red, green, blue, alpha);
      GL20.glUniform1i(this.getUniform("iterations"), iteractions);
      GL20.glUniform1f(this.getUniform("formuparam2"), formuparam2);
      GL20.glUniform1i(this.getUniform("volsteps"), (int)volumSteps);
      GL20.glUniform1f(this.getUniform("stepsize"), stepSize);
      GL20.glUniform1f(this.getUniform("zoom"), zoom);
      GL20.glUniform1f(this.getUniform("tile"), title);
      GL20.glUniform1f(this.getUniform("distfading"), distfading);
      GL20.glUniform1f(this.getUniform("saturation"), saturation);
      GL20.glUniform1i(this.getUniform("fadeBol"), fade);
   }

   public void stopDraw(Color color, float radius, float quality, boolean gradientAlpha, int alphaOutline, float duplicate, float red, float green, float blue, float alpha, int iteractions, float formuparam2, float zoom, float volumSteps, float stepSize, float title, float distfading, float saturation, float cloud, int fade) {
      this.mc.field_71474_y.field_181151_V = this.entityShadows;
      this.framebuffer.func_147609_e();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      this.mc.func_147110_a().func_147610_a(true);
      this.mc.field_71460_t.func_175072_h();
      RenderHelper.func_74518_a();
      this.startShader(color, radius, quality, gradientAlpha, alphaOutline, duplicate, red, green, blue, alpha, iteractions, formuparam2, zoom, volumSteps, stepSize, title, distfading, saturation, cloud, fade);
      this.mc.field_71460_t.func_78478_c();
      this.drawFramebuffer(this.framebuffer);
      this.stopShader();
      this.mc.field_71460_t.func_175072_h();
      GlStateManager.func_179121_F();
      GlStateManager.func_179099_b();
   }

   public void stopDraw(Color color, float radius, float quality, boolean gradientAlpha, int alphaOutline, float duplicate, float red, float green, float blue, float alpha, int iteractions, float formuparam2, float zoom, float volumSteps, float stepSize, float title, float distfading, float saturation, float cloud, int fade, Predicate<Boolean> fill) {
      this.mc.field_71474_y.field_181151_V = this.entityShadows;
      this.framebuffer.func_147609_e();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      this.mc.func_147110_a().func_147610_a(true);
      this.mc.field_71460_t.func_175072_h();
      RenderHelper.func_74518_a();
      this.startShader(color, radius, quality, gradientAlpha, alphaOutline, duplicate, red, green, blue, alpha, iteractions, formuparam2, zoom, volumSteps, stepSize, title, distfading, saturation, cloud, fade);
      this.mc.field_71460_t.func_78478_c();
      this.drawFramebuffer(this.framebuffer);
      fill.test(false);
      this.drawFramebuffer(this.framebuffer);
      this.stopShader();
      this.mc.field_71460_t.func_175072_h();
      GlStateManager.func_179121_F();
      GlStateManager.func_179099_b();
   }

   public void startShader(Color color, float radius, float quality, boolean gradientAlpha, int alphaOutline, float duplicate, float red, float green, float blue, float alpha, int iteractions, float formuparam2, float zoom, float volumSteps, float stepSize, float title, float distfading, float saturation, float cloud, int fade) {
      GL11.glPushMatrix();
      GL20.glUseProgram(this.program);
      if (this.uniformsMap == null) {
         this.uniformsMap = new HashMap();
         this.setupUniforms();
      }

      this.updateUniforms(color, radius, quality, gradientAlpha, alphaOutline, duplicate, red, green, blue, alpha, iteractions, formuparam2, zoom, volumSteps, stepSize, title, distfading, saturation, cloud, fade);
   }

   public void update(double speed) {
      this.time = (float)((double)this.time + speed);
   }
}
