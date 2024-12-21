package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.event.events.NewRenderEntityEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

@Module.Declaration(
   name = "CrystalChams",
   category = Category.Render
)
public class CrystalChams extends Module {
   IntegerSetting range = this.registerInteger("Range", 32, 0, 256);
   ModeSetting mode = this.registerMode("Mode", Arrays.asList("Normal", "Gradient"), "Normal");
   BooleanSetting chams = this.registerBoolean("Chams", false);
   BooleanSetting throughWalls = this.registerBoolean("ThroughWalls", false);
   BooleanSetting wireframe = this.registerBoolean("Wireframe", false);
   BooleanSetting wireWalls = this.registerBoolean("WireThroughWalls", false);
   DoubleSetting spinSpeed = this.registerDouble("SpinSpeed", 1.0D, 0.0D, 4.0D);
   DoubleSetting floatSpeed = this.registerDouble("FloatSpeed", 1.0D, 0.0D, 4.0D);
   ColorSetting color = this.registerColor("Color", new GSColor(255, 255, 255, 255), true);
   ColorSetting wireFrameColor = this.registerColor("WireframeColor", new GSColor(255, 255, 255, 255), true);
   DoubleSetting lineWidth = this.registerDouble("lineWidth", 1.0D, 0.0D, 4.0D);
   DoubleSetting lineWidthInterp = this.registerDouble("lineWidthInterp", 1.0D, 0.1D, 4.0D);
   BooleanSetting show = this.registerBoolean("ShowEntity ;;", false);
   @EventHandler
   private final Listener<NewRenderEntityEvent> renderEntityHeadEventListener = new Listener((event) -> {
      if (mc.field_71439_g != null && mc.field_71441_e != null && event.entityIn != null && event.entityIn.func_70005_c_().length() != 0) {
         if (event.entityIn instanceof EntityEnderCrystal && !(mc.field_71439_g.func_70032_d(event.entityIn) > (float)(Integer)this.range.getValue())) {
            if (!(Boolean)this.show.getValue()) {
               event.cancel();
            }

            this.prepare();
            float spinTicks = (float)((EntityEnderCrystal)event.entityIn).field_70261_a + Minecraft.func_71410_x().func_184121_ak();
            float floatTicks = MathHelper.func_76126_a(spinTicks * 0.2F * ((Double)this.floatSpeed.getValue()).floatValue()) / 2.0F + 0.5F;
            float spinSpeed = ((Double)this.spinSpeed.getValue()).floatValue();
            float scale = 0.0625F;
            float swingAmount = spinTicks * 3.0F * spinSpeed;
            floatTicks += floatTicks * floatTicks;
            floatTicks *= 0.2F;
            GlStateManager.func_187441_d(this.getInterpolatedLinWid(mc.field_71439_g.func_70032_d(event.entityIn) + 1.0F, ((Double)this.lineWidth.getValue()).floatValue(), ((Double)this.lineWidthInterp.getValue()).floatValue()));
            GL11.glDisable(3553);
            if (((String)this.mode.getValue()).equals("Gradient")) {
               GL11.glPushAttrib(1048575);
               GL11.glEnable(3042);
               GL11.glDisable(2896);
               GL11.glDisable(3553);
               float alpha = (float)this.color.getValue().getAlpha() / 255.0F;
               GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
               event.modelBase.func_78088_a(event.entityIn, 0.0F, swingAmount, floatTicks, 0.0F, 0.0F, scale);
               GL11.glEnable(3553);
               GL11.glBlendFunc(770, 771);
               float f = (float)event.entityIn.field_70173_aa + Minecraft.func_71410_x().func_184121_ak();
               mc.func_110434_K().func_110577_a(new ResourceLocation("textures/rainbow.png"));
               Minecraft.func_71410_x().field_71460_t.func_191514_d(true);
               GlStateManager.func_179147_l();
               GlStateManager.func_179143_c(514);
               GlStateManager.func_179132_a(false);
               GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, alpha);

               for(int i = 0; i < 2; ++i) {
                  GlStateManager.func_179140_f();
                  GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, alpha);
                  GlStateManager.func_179128_n(5890);
                  GlStateManager.func_179096_D();
                  GlStateManager.func_179114_b(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 0.5F);
                  GlStateManager.func_179109_b(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
                  GlStateManager.func_179128_n(5888);
                  event.modelBase.func_78088_a(event.entityIn, 0.0F, swingAmount, floatTicks, 0.0F, 0.0F, scale);
               }

               GlStateManager.func_179128_n(5890);
               GlStateManager.func_179096_D();
               GlStateManager.func_179128_n(5888);
               GlStateManager.func_179145_e();
               GlStateManager.func_179132_a(true);
               GlStateManager.func_179143_c(515);
               GlStateManager.func_179084_k();
               mc.field_71460_t.func_191514_d(false);
               GL11.glPopAttrib();
            } else {
               GSColor chamsColor;
               if ((Boolean)this.wireframe.getValue()) {
                  chamsColor = this.wireFrameColor.getValue();
                  GL11.glPushAttrib(1048575);
                  GL11.glEnable(3042);
                  GL11.glDisable(3553);
                  GL11.glDisable(2896);
                  GL11.glBlendFunc(770, 771);
                  GL11.glPolygonMode(1032, 6913);
                  if ((Boolean)this.wireWalls.getValue()) {
                     GL11.glDepthMask(false);
                     GL11.glDisable(2929);
                  }

                  GL11.glColor4f((float)chamsColor.getRed() / 255.0F, (float)chamsColor.getGreen() / 255.0F, (float)chamsColor.getBlue() / 255.0F, (float)chamsColor.getAlpha() / 255.0F);
                  event.modelBase.func_78088_a(event.entityIn, 0.0F, swingAmount, floatTicks, 0.0F, 0.0F, scale);
                  GL11.glPopAttrib();
               }

               if ((Boolean)this.chams.getValue()) {
                  chamsColor = this.color.getValue();
                  GL11.glPushAttrib(1048575);
                  GL11.glEnable(3042);
                  GL11.glDisable(3553);
                  GL11.glDisable(2896);
                  GL11.glDisable(3008);
                  GL11.glBlendFunc(770, 771);
                  GL11.glEnable(2960);
                  GL11.glEnable(10754);
                  if ((Boolean)this.throughWalls.getValue()) {
                     GL11.glDepthMask(false);
                     GL11.glDisable(2929);
                  }

                  GL11.glColor4f((float)chamsColor.getRed() / 255.0F, (float)chamsColor.getGreen() / 255.0F, (float)chamsColor.getBlue() / 255.0F, (float)chamsColor.getAlpha() / 255.0F);
                  event.modelBase.func_78088_a(event.entityIn, 0.0F, swingAmount, floatTicks, 0.0F, 0.0F, scale);
                  GL11.glPopAttrib();
               }
            }

            event.limbSwing = 0.0F;
            event.limbSwingAmount = swingAmount;
            event.ageInTicks = floatTicks;
            event.netHeadYaw = 0.0F;
            event.headPitch = 0.0F;
            event.scale = scale;
            this.release();
         }
      }
   }, new Predicate[0]);

   void prepare() {
      GlStateManager.func_179094_E();
      GlStateManager.func_179097_i();
      GlStateManager.func_179140_f();
      GlStateManager.func_179132_a(false);
      GlStateManager.func_179118_c();
      GlStateManager.func_179147_l();
      GL11.glDisable(3553);
      GL11.glEnable(2848);
      GL11.glBlendFunc(770, 771);
   }

   void release() {
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179145_e();
      GlStateManager.func_179126_j();
      GlStateManager.func_179141_d();
      GlStateManager.func_179121_F();
      GL11.glEnable(3553);
      GL11.glPolygonMode(1032, 6914);
      (new GSColor(255, 255, 255, 255)).glColor();
   }

   float getInterpolatedLinWid(float distance, float line, float lineFactor) {
      return line * lineFactor / distance;
   }
}
