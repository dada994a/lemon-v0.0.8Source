package com.lemonclient.mixin.mixins;

import com.lemonclient.api.event.events.EntityCollisionEvent;
import com.lemonclient.api.event.events.StepEvent;
import com.lemonclient.client.LemonClient;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Entity.class})
public abstract class MixinEntity {
   @Shadow
   public double field_70165_t;
   @Shadow
   public double field_70163_u;
   @Shadow
   public double field_70161_v;
   @Shadow
   public double field_70159_w;
   @Shadow
   public double field_70181_x;
   @Shadow
   public double field_70179_y;
   @Shadow
   public float field_70177_z;
   @Shadow
   public float field_70125_A;
   @Shadow
   public boolean field_70122_E;
   @Shadow
   public World field_70170_p;
   @Shadow
   public float field_70138_W;
   @Shadow
   public boolean field_70128_L;
   @Shadow
   public float field_70130_N;
   @Shadow
   public float field_70131_O;
   private Float prevHeight;

   @Shadow
   public abstract AxisAlignedBB func_174813_aQ();

   @Shadow
   public abstract boolean func_70093_af();

   @Shadow
   public abstract boolean equals(Object var1);

   @Inject(
      method = {"applyEntityCollision"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void velocity(Entity entityIn, CallbackInfo ci) {
      EntityCollisionEvent event = new EntityCollisionEvent();
      LemonClient.EVENT_BUS.post(event);
      if (event.isCancelled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"move"},
      at = {@At(
   value = "INVOKE",
   target = "net/minecraft/entity/Entity.resetPositionToBB()V",
   ordinal = 1
)}
   )
   private void resetPositionToBBHook(MoverType type, double x, double y, double z, CallbackInfo info) {
      if (EntityPlayerSP.class.isInstance(this) && this.prevHeight != null) {
         this.field_70138_W = this.prevHeight;
         this.prevHeight = null;
      }

   }

   @Inject(
      method = {"move"},
      at = {@At("HEAD")}
   )
   public void move(MoverType type, double tx, double ty, double tz, CallbackInfo ci) {
      Minecraft mc = Minecraft.func_71410_x();
      if (mc.func_147104_D() != null) {
         double x = tx;
         double y = ty;
         double z = tz;
         if (!ci.isCancelled()) {
            AxisAlignedBB bb = mc.field_71439_g.func_174813_aQ();
            if (!mc.field_71439_g.field_70145_X) {
               if (type.equals(MoverType.PISTON)) {
                  return;
               }

               mc.field_71441_e.field_72984_F.func_76320_a("move");
               if (mc.field_71439_g.field_70134_J) {
                  return;
               }

               double d2 = tx;
               double d4 = tz;
               if ((type == MoverType.SELF || type == MoverType.PLAYER) && mc.field_71439_g.field_70122_E && mc.field_71439_g.func_70093_af()) {
                  for(double var23 = 0.05D; x != 0.0D && mc.field_71441_e.func_184144_a(mc.field_71439_g, bb.func_72317_d(x, (double)(-mc.field_71439_g.field_70138_W), 0.0D)).isEmpty(); d2 = x) {
                     if (x < 0.05D && x >= -0.05D) {
                        x = 0.0D;
                     } else if (x > 0.0D) {
                        x -= 0.05D;
                     } else {
                        x += 0.05D;
                     }
                  }

                  for(; z != 0.0D && mc.field_71441_e.func_184144_a(mc.field_71439_g, bb.func_72317_d(0.0D, (double)(-mc.field_71439_g.field_70138_W), z)).isEmpty(); d4 = z) {
                     if (z < 0.05D && z >= -0.05D) {
                        z = 0.0D;
                     } else if (z > 0.0D) {
                        z -= 0.05D;
                     } else {
                        z += 0.05D;
                     }
                  }

                  for(; x != 0.0D && z != 0.0D && mc.field_71441_e.func_184144_a(mc.field_71439_g, bb.func_72317_d(x, (double)(-mc.field_71439_g.field_70138_W), z)).isEmpty(); d4 = z) {
                     if (x < 0.05D && x >= -0.05D) {
                        x = 0.0D;
                     } else if (x > 0.0D) {
                        x -= 0.05D;
                     } else {
                        x += 0.05D;
                     }

                     d2 = x;
                     if (z < 0.05D && z >= -0.05D) {
                        z = 0.0D;
                     } else if (z > 0.0D) {
                        z -= 0.05D;
                     } else {
                        z += 0.05D;
                     }
                  }
               }

               List<AxisAlignedBB> list1 = mc.field_71441_e.func_184144_a(mc.field_71439_g, bb.func_72321_a(x, ty, z));
               int k5;
               int i6;
               if (ty != 0.0D) {
                  k5 = 0;

                  for(i6 = list1.size(); k5 < i6; ++k5) {
                     y = ((AxisAlignedBB)list1.get(k5)).func_72323_b(bb, y);
                  }

                  bb = bb.func_72317_d(0.0D, y, 0.0D);
               }

               if (x != 0.0D) {
                  k5 = 0;

                  for(i6 = list1.size(); k5 < i6; ++k5) {
                     x = ((AxisAlignedBB)list1.get(k5)).func_72316_a(bb, x);
                  }

                  if (x != 0.0D) {
                     bb = bb.func_72317_d(x, 0.0D, 0.0D);
                  }
               }

               if (z != 0.0D) {
                  k5 = 0;

                  for(i6 = list1.size(); k5 < i6; ++k5) {
                     z = ((AxisAlignedBB)list1.get(k5)).func_72322_c(bb, z);
                  }

                  if (z != 0.0D) {
                     bb = bb.func_72317_d(0.0D, 0.0D, z);
                  }
               }

               boolean flag = mc.field_71439_g.field_70122_E || ty != y && ty < 0.0D;
               if (mc.field_71439_g.field_70138_W > 0.0F && flag && (d2 != x || d4 != z)) {
                  double d14 = x;
                  double d7 = z;
                  y = (double)mc.field_71439_g.field_70138_W;
                  List<AxisAlignedBB> list = mc.field_71441_e.func_184144_a(mc.field_71439_g, bb.func_72321_a(d2, y, d4));
                  AxisAlignedBB axisalignedbb3 = bb.func_72321_a(d2, 0.0D, d4);
                  double d8 = y;
                  int j1 = 0;

                  for(int k1 = list.size(); j1 < k1; ++j1) {
                     d8 = ((AxisAlignedBB)list.get(j1)).func_72323_b(axisalignedbb3, d8);
                  }

                  AxisAlignedBB axisalignedbb2 = bb.func_72317_d(0.0D, d8, 0.0D);
                  double d18 = d2;
                  int l1 = 0;

                  for(int i2 = list.size(); l1 < i2; ++l1) {
                     d18 = ((AxisAlignedBB)list.get(l1)).func_72316_a(axisalignedbb2, d18);
                  }

                  axisalignedbb2 = axisalignedbb2.func_72317_d(d18, 0.0D, 0.0D);
                  double d19 = d4;
                  int j2 = 0;

                  for(int k2 = list.size(); j2 < k2; ++j2) {
                     d19 = ((AxisAlignedBB)list.get(j2)).func_72322_c(axisalignedbb2, d19);
                  }

                  axisalignedbb2 = axisalignedbb2.func_72317_d(0.0D, 0.0D, d19);
                  AxisAlignedBB axisalignedbb4 = bb;
                  double d20 = y;
                  int l2 = 0;

                  for(int i3 = list.size(); l2 < i3; ++l2) {
                     d20 = ((AxisAlignedBB)list.get(l2)).func_72323_b(axisalignedbb4, d20);
                  }

                  axisalignedbb4 = axisalignedbb4.func_72317_d(0.0D, d20, 0.0D);
                  double d21 = d2;
                  int j3 = 0;

                  for(int k3 = list.size(); j3 < k3; ++j3) {
                     d21 = ((AxisAlignedBB)list.get(j3)).func_72316_a(axisalignedbb4, d21);
                  }

                  axisalignedbb4 = axisalignedbb4.func_72317_d(d21, 0.0D, 0.0D);
                  double d22 = d4;
                  int l3 = 0;

                  for(int i4 = list.size(); l3 < i4; ++l3) {
                     d22 = ((AxisAlignedBB)list.get(l3)).func_72322_c(axisalignedbb4, d22);
                  }

                  axisalignedbb4 = axisalignedbb4.func_72317_d(0.0D, 0.0D, d22);
                  double d23 = d18 * d18 + d19 * d19;
                  double d9 = d21 * d21 + d22 * d22;
                  if (d23 > d9) {
                     x = d18;
                     z = d19;
                     y = -d8;
                     bb = axisalignedbb2;
                  } else {
                     x = d21;
                     z = d22;
                     y = -d20;
                     bb = axisalignedbb4;
                  }

                  int j4 = 0;

                  for(int k4 = list.size(); j4 < k4; ++j4) {
                     y = ((AxisAlignedBB)list.get(j4)).func_72323_b(bb, y);
                  }

                  bb = bb.func_72317_d(0.0D, y, 0.0D);
                  if (!(d14 * d14 + d7 * d7 >= x * x + z * z)) {
                     StepEvent event = new StepEvent(bb);
                     LemonClient.EVENT_BUS.post(event);
                     if (event.isCancelled()) {
                        mc.field_71439_g.field_70138_W = 0.5F;
                     }
                  }
               }
            }

         }
      }
   }
}
