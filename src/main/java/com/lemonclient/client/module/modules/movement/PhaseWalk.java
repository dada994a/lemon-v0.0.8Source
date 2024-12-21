package com.lemonclient.client.module.modules.movement;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.player.MathUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;

@Module.Declaration(
   name = "PhaseWalk",
   category = Category.Movement
)
public class PhaseWalk extends Module {
   BooleanSetting phaseCheck = this.registerBoolean("Only In Block", true);
   ModeSetting noClipMode = this.registerMode("NoClipMode", Arrays.asList("Bypass", "NoClip", "None", "Fall"), "NoClip");
   BooleanSetting fallPacket = this.registerBoolean("Fall Packet", true);
   BooleanSetting sprintPacket = this.registerBoolean("Sprint Packet", true);
   BooleanSetting instantWalk = this.registerBoolean("Instant Walk", true);
   BooleanSetting antiVoid = this.registerBoolean("Anti Void", false);
   BooleanSetting clip = this.registerBoolean("Clip", true);
   IntegerSetting antiVoidHeight = this.registerInteger("Anti Void Height", 5, 1, 100);
   DoubleSetting instantWalkSpeed = this.registerDouble("Instant Speed", 1.8D, 0.1D, 2.0D, () -> {
      return (Boolean)this.instantWalk.getValue();
   });
   DoubleSetting phaseSpeed = this.registerDouble("Phase Walk Speed", 42.4D, 0.1D, 70.0D);
   BooleanSetting downOnShift = this.registerBoolean("Phase Down When Crouch", true);
   BooleanSetting stopMotion = this.registerBoolean("Attempt Clips", true);
   IntegerSetting stopMotionDelay = this.registerInteger("Attempt Clips Delay", 5, 0, 20, () -> {
      return (Boolean)this.stopMotion.getValue();
   });
   int delay;

   public void onDisable() {
      mc.field_71439_g.field_70145_X = false;
   }

   private boolean air(BlockPos pos) {
      Block blockState = BlockUtil.getBlock(pos);
      return !BlockUtil.airBlocks.contains(blockState) && blockState != Blocks.field_150321_G;
   }

   public void onUpdate() {
      ++this.delay;
      double n = (Double)this.phaseSpeed.getValue() / 1000.0D;
      double n2 = (Double)this.instantWalkSpeed.getValue() / 10.0D;
      RayTraceResult rayTraceBlocks;
      if ((Boolean)this.antiVoid.getValue() && mc.field_71439_g.field_70163_u <= (double)(Integer)this.antiVoidHeight.getValue() && ((rayTraceBlocks = mc.field_71441_e.func_147447_a(mc.field_71439_g.func_174791_d(), new Vec3d(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v), false, false, false)) == null || rayTraceBlocks.field_72313_a != Type.BLOCK)) {
         mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
      }

      double[] motion2;
      double[] directionSpeed2;
      label378: {
         if ((Boolean)this.phaseCheck.getValue()) {
            if ((mc.field_71474_y.field_74351_w.func_151470_d() || mc.field_71474_y.field_74366_z.func_151470_d() || mc.field_71474_y.field_74370_x.func_151470_d() || mc.field_71474_y.field_74368_y.func_151470_d() || mc.field_71474_y.field_74311_E.func_151470_d()) && (!this.eChestCheck() && this.air(PlayerUtil.getPlayerPos()) || this.air(PlayerUtil.getPlayerPos().func_177984_a()))) {
               if (mc.field_71439_g.field_70124_G && mc.field_71474_y.field_74311_E.func_151468_f() && mc.field_71439_g.func_70093_af()) {
                  motion2 = this.getMotion(n);
                  if ((Boolean)this.downOnShift.getValue() && mc.field_71439_g.field_70124_G && mc.field_71474_y.field_74311_E.func_151470_d()) {
                     mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t + motion2[0], mc.field_71439_g.field_70163_u - 0.0424D, mc.field_71439_g.field_70161_v + motion2[1], mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false));
                  } else {
                     mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t + motion2[0], mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + motion2[1], mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false));
                  }

                  if (((String)this.noClipMode.getValue()).equals("Fall")) {
                     mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t, -1300.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z * -5.0F, mc.field_71439_g.field_70125_A * -5.0F, true));
                  }

                  if (((String)this.noClipMode.getValue()).equals("NoClip")) {
                     mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
                     if (mc.field_71474_y.field_74351_w.func_151470_d() || mc.field_71474_y.field_74368_y.func_151470_d() || mc.field_71474_y.field_74370_x.func_151470_d() || mc.field_71474_y.field_74366_z.func_151470_d()) {
                        directionSpeed2 = MathUtil.directionSpeed(0.05999999865889549D);
                        mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t + directionSpeed2[0], mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + directionSpeed2[1], mc.field_71439_g.field_70122_E));
                        mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                     }

                     if (mc.field_71474_y.field_74311_E.func_151470_d()) {
                        mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 0.05999999865889549D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                        mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                     }

                     if (mc.field_71474_y.field_74314_A.func_151470_d()) {
                        mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.05999999865889549D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                        mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                     }
                  }

                  if (((String)this.noClipMode.getValue()).equals("Bypass")) {
                     mc.field_71439_g.field_70145_X = true;
                  }

                  if ((Boolean)this.fallPacket.getValue()) {
                     mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_RIDING_JUMP));
                  }

                  if ((Boolean)this.sprintPacket.getValue()) {
                     mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SPRINTING));
                  }

                  if ((Boolean)this.downOnShift.getValue() && mc.field_71439_g.field_70124_G && mc.field_71474_y.field_74311_E.func_151470_d()) {
                     mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t + motion2[0], mc.field_71439_g.field_70163_u - 0.0424D, mc.field_71439_g.field_70161_v + motion2[1]);
                  } else {
                     mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t + motion2[0], mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + motion2[1]);
                  }

                  mc.field_71439_g.field_70179_y = 0.0D;
                  mc.field_71439_g.field_70181_x = 0.0D;
                  mc.field_71439_g.field_70159_w = 0.0D;
                  mc.field_71439_g.field_70145_X = true;
               }

               if (mc.field_71439_g.field_70123_F && (Boolean)this.clip.getValue() && !mc.field_71474_y.field_74351_w.func_151470_d() && !mc.field_71474_y.field_74368_y.func_151470_d() && !mc.field_71474_y.field_74370_x.func_151470_d()) {
                  mc.field_71474_y.field_74366_z.func_151470_d();
               }

               if (mc.field_71439_g.field_70123_F && (Boolean)this.stopMotion.getValue()) {
                  if (this.delay >= (Integer)this.stopMotionDelay.getValue()) {
                     break label378;
                  }
               } else if (mc.field_71439_g.field_70123_F) {
                  break label378;
               }

               if ((Boolean)this.instantWalk.getValue()) {
                  motion2 = MathUtil.directionSpeed(n2);
                  mc.field_71439_g.field_70159_w = motion2[0];
                  mc.field_71439_g.field_70179_y = motion2[1];
               }
            }
         } else if (mc.field_71474_y.field_74351_w.func_151470_d() || mc.field_71474_y.field_74366_z.func_151470_d() || mc.field_71474_y.field_74370_x.func_151470_d() || mc.field_71474_y.field_74368_y.func_151470_d() || mc.field_71474_y.field_74311_E.func_151470_d()) {
            if (mc.field_71439_g.field_70124_G && mc.field_71474_y.field_74311_E.func_151468_f() && mc.field_71439_g.func_70093_af()) {
               motion2 = this.getMotion(n);
               if ((Boolean)this.downOnShift.getValue() && mc.field_71439_g.field_70124_G && mc.field_71474_y.field_74311_E.func_151470_d()) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t + motion2[0], mc.field_71439_g.field_70163_u - 0.0424D, mc.field_71439_g.field_70161_v + motion2[1], mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false));
               } else {
                  mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t + motion2[0], mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + motion2[1], mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false));
               }

               if (((String)this.noClipMode.getValue()).equals("Fall")) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t, -1300.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z * -5.0F, mc.field_71439_g.field_70125_A * -5.0F, true));
               }

               if (((String)this.noClipMode.getValue()).equals("NoClip")) {
                  mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
                  if (mc.field_71474_y.field_74351_w.func_151470_d() || mc.field_71474_y.field_74368_y.func_151470_d() || mc.field_71474_y.field_74370_x.func_151470_d() || mc.field_71474_y.field_74366_z.func_151470_d()) {
                     directionSpeed2 = MathUtil.directionSpeed(0.05999999865889549D);
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t + directionSpeed2[0], mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + directionSpeed2[1], mc.field_71439_g.field_70122_E));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  }

                  if (mc.field_71474_y.field_74311_E.func_151470_d()) {
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 0.05999999865889549D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  }

                  if (mc.field_71474_y.field_74314_A.func_151470_d()) {
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.05999999865889549D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  }
               }

               if (((String)this.noClipMode.getValue()).equals("Bypass")) {
                  mc.field_71439_g.field_70145_X = true;
               }

               if ((Boolean)this.fallPacket.getValue()) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_RIDING_JUMP));
               }

               if ((Boolean)this.sprintPacket.getValue()) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SPRINTING));
               }

               if ((Boolean)this.downOnShift.getValue() && mc.field_71439_g.field_70124_G && mc.field_71474_y.field_74311_E.func_151470_d()) {
                  mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t + motion2[0], mc.field_71439_g.field_70163_u - 0.0424D, mc.field_71439_g.field_70161_v + motion2[1]);
               } else {
                  mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t + motion2[0], mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + motion2[1]);
               }

               mc.field_71439_g.field_70179_y = 0.0D;
               mc.field_71439_g.field_70181_x = 0.0D;
               mc.field_71439_g.field_70159_w = 0.0D;
               mc.field_71439_g.field_70145_X = true;
            }

            label397: {
               if (mc.field_71439_g.field_70123_F && (Boolean)this.stopMotion.getValue()) {
                  if (this.delay >= (Integer)this.stopMotionDelay.getValue()) {
                     break label397;
                  }
               } else if (mc.field_71439_g.field_70123_F) {
                  break label397;
               }

               if ((Boolean)this.instantWalk.getValue()) {
                  motion2 = MathUtil.directionSpeed(n2);
                  mc.field_71439_g.field_70159_w = motion2[0];
                  mc.field_71439_g.field_70179_y = motion2[1];
               }

               return;
            }

            motion2 = this.getMotion(n);
            if ((Boolean)this.downOnShift.getValue() && mc.field_71439_g.field_70124_G && mc.field_71474_y.field_74311_E.func_151470_d()) {
               mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t + motion2[0], mc.field_71439_g.field_70163_u - 0.1D, mc.field_71439_g.field_70161_v + motion2[1], mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false));
            } else {
               mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t + motion2[0], mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + motion2[1], mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false));
            }

            if (((String)this.noClipMode.getValue()).equals("Fall")) {
               mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t, -1300.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z * -5.0F, mc.field_71439_g.field_70125_A * -5.0F, true));
            }

            if (((String)this.noClipMode.getValue()).equals("NoClip")) {
               mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
               if (mc.field_71474_y.field_74351_w.func_151470_d() || mc.field_71474_y.field_74368_y.func_151470_d() || mc.field_71474_y.field_74370_x.func_151470_d() || mc.field_71474_y.field_74366_z.func_151470_d()) {
                  directionSpeed2 = MathUtil.directionSpeed(0.05999999865889549D);
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t + directionSpeed2[0], mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + directionSpeed2[1], mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
               }

               if (mc.field_71474_y.field_74311_E.func_151470_d()) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 0.05999999865889549D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
               }

               if (mc.field_71474_y.field_74314_A.func_151470_d()) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.05999999865889549D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
               }
            }

            if (((String)this.noClipMode.getValue()).equals("Bypass")) {
               mc.field_71439_g.field_70145_X = true;
            }

            if ((Boolean)this.fallPacket.getValue()) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_RIDING_JUMP));
            }

            if ((Boolean)this.sprintPacket.getValue()) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SPRINTING));
            }

            if ((Boolean)this.downOnShift.getValue() && mc.field_71439_g.field_70124_G && mc.field_71474_y.field_74311_E.func_151470_d()) {
               mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t + motion2[0], mc.field_71439_g.field_70163_u - 0.1D, mc.field_71439_g.field_70161_v + motion2[1]);
            } else {
               mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t + motion2[0], mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + motion2[1]);
            }

            mc.field_71439_g.field_70179_y = 0.0D;
            mc.field_71439_g.field_70181_x = 0.0D;
            mc.field_71439_g.field_70159_w = 0.0D;
            mc.field_71439_g.field_70145_X = true;
            this.delay = 0;
            return;
         }

         return;
      }

      motion2 = this.getMotion(n);
      if ((Boolean)this.downOnShift.getValue() && mc.field_71439_g.field_70124_G && mc.field_71474_y.field_74311_E.func_151470_d()) {
         mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t + motion2[0], mc.field_71439_g.field_70163_u - 0.1D, mc.field_71439_g.field_70161_v + motion2[1], mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false));
      } else {
         mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t + motion2[0], mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + motion2[1], mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false));
      }

      if (((String)this.noClipMode.getValue()).equals("Fall")) {
         mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t, -1300.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z * -5.0F, mc.field_71439_g.field_70125_A * -5.0F, true));
      }

      if (((String)this.noClipMode.getValue()).equals("NoClip")) {
         mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
         if (mc.field_71474_y.field_74351_w.func_151470_d() || mc.field_71474_y.field_74368_y.func_151470_d() || mc.field_71474_y.field_74370_x.func_151470_d() || mc.field_71474_y.field_74366_z.func_151470_d()) {
            directionSpeed2 = MathUtil.directionSpeed(0.05999999865889549D);
            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t + directionSpeed2[0], mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + directionSpeed2[1], mc.field_71439_g.field_70122_E));
            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
         }

         if (mc.field_71474_y.field_74311_E.func_151470_d()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 0.05999999865889549D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
         }

         if (mc.field_71474_y.field_74314_A.func_151470_d()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.05999999865889549D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
         }
      }

      if (((String)this.noClipMode.getValue()).equals("Bypass")) {
         mc.field_71439_g.field_70145_X = true;
      }

      if ((Boolean)this.fallPacket.getValue()) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_RIDING_JUMP));
      }

      if ((Boolean)this.sprintPacket.getValue()) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SPRINTING));
      }

      if ((Boolean)this.downOnShift.getValue() && mc.field_71439_g.field_70124_G && mc.field_71474_y.field_74311_E.func_151470_d()) {
         mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t + motion2[0], mc.field_71439_g.field_70163_u - 0.1D, mc.field_71439_g.field_70161_v + motion2[1]);
      } else {
         mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t + motion2[0], mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + motion2[1]);
      }

      mc.field_71439_g.field_70179_y = 0.0D;
      mc.field_71439_g.field_70181_x = 0.0D;
      mc.field_71439_g.field_70159_w = 0.0D;
      mc.field_71439_g.field_70145_X = true;
      this.delay = 0;
   }

   private boolean eChestCheck() {
      return String.valueOf(mc.field_71439_g.field_70163_u).split("\\.")[1].equals("875") || String.valueOf(mc.field_71439_g.field_70163_u).split("\\.")[1].equals("5");
   }

   private double[] getMotion(double n) {
      float moveForward = mc.field_71439_g.field_71158_b.field_192832_b;
      float moveStrafe = mc.field_71439_g.field_71158_b.field_78902_a;
      float n2 = mc.field_71439_g.field_70126_B + (mc.field_71439_g.field_70177_z - mc.field_71439_g.field_70126_B) * mc.func_184121_ak();
      if (moveForward != 0.0F) {
         if (moveStrafe > 0.0F) {
            n2 += (float)(moveForward > 0.0F ? -45 : 45);
         } else if (moveStrafe < 0.0F) {
            n2 += (float)(moveForward > 0.0F ? 45 : -45);
         }

         moveStrafe = 0.0F;
         if (moveForward > 0.0F) {
            moveForward = 1.0F;
         } else if (moveForward < 0.0F) {
            moveForward = -1.0F;
         }
      }

      return new double[]{(double)moveForward * n * -Math.sin(Math.toRadians((double)n2)) + (double)moveStrafe * n * Math.cos(Math.toRadians((double)n2)), (double)moveForward * n * Math.cos(Math.toRadians((double)n2)) - (double)moveStrafe * n * -Math.sin(Math.toRadians((double)n2))};
   }
}
