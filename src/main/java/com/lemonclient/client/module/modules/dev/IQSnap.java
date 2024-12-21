package com.lemonclient.client.module.modules.dev;

import com.lemonclient.api.event.events.PlayerMoveEvent;
import com.lemonclient.api.event.events.StepEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.RotationUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.HoleUtil;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.api.util.world.TimerUtils;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.InputUpdateEvent;

@Module.Declaration(
   name = "IQSnap",
   category = Category.Dev,
   priority = 120
)
public class IQSnap extends Module {
   IntegerSetting targetRange = this.registerInteger("Target Range", 16, 0, 256);
   IntegerSetting fixedRange = this.registerInteger("Fixed Target Range", 16, 0, 256);
   IntegerSetting cancelRange = this.registerInteger("Cancel Range", 6, 0, 16);
   IntegerSetting downRange = this.registerInteger("Down Range", 5, 0, 8);
   IntegerSetting upRange = this.registerInteger("Up Range", 1, 0, 8);
   DoubleSetting hRange = this.registerDouble("H Range", 4.0D, 1.0D, 8.0D);
   DoubleSetting timer = this.registerDouble("Timer", 2.0D, 1.0D, 50.0D);
   DoubleSetting speed = this.registerDouble("Speed", 2.0D, 0.0D, 10.0D);
   BooleanSetting step = this.registerBoolean("Step", true);
   ModeSetting mode = this.registerMode("Mode", Arrays.asList("NCP", "Vanilla"), "NCP", () -> {
      return (Boolean)this.step.getValue();
   });
   ModeSetting height = this.registerMode("NCP Height", Arrays.asList("1", "1.5", "2", "2.5", "3", "4"), "2.5", () -> {
      return ((String)this.mode.getValue()).equalsIgnoreCase("NCP") && (Boolean)this.step.getValue();
   });
   ModeSetting vHeight = this.registerMode("Vanilla Height", Arrays.asList("1", "1.5", "2", "2.5", "3", "4"), "2.5", () -> {
      return ((String)this.mode.getValue()).equalsIgnoreCase("Vanilla") && (Boolean)this.step.getValue();
   });
   BooleanSetting abnormal = this.registerBoolean("Abnormal", false, () -> {
      return !((String)this.mode.getValue()).equalsIgnoreCase("Vanilla") && (Boolean)this.step.getValue();
   });
   IntegerSetting centerSpeed = this.registerInteger("Center Speed", 2, 10, 1);
   BooleanSetting only = this.registerBoolean("Only 1x1", true);
   BooleanSetting single = this.registerBoolean("Single Hole", true, () -> {
      return !(Boolean)this.only.getValue();
   });
   BooleanSetting twoBlocks = this.registerBoolean("Double Hole", true, () -> {
      return !(Boolean)this.only.getValue();
   });
   BooleanSetting custom = this.registerBoolean("Custom Hole", true, () -> {
      return !(Boolean)this.only.getValue();
   });
   BooleanSetting four = this.registerBoolean("Four Blocks", true, () -> {
      return !(Boolean)this.only.getValue();
   });
   BooleanSetting near = this.registerBoolean("Near Target", true);
   BooleanSetting disable = this.registerBoolean("Disable", true);
   BooleanSetting hud = this.registerBoolean("Hud", true);
   private int stuckTicks = 0;
   BlockPos originPos;
   BlockPos startPos;
   boolean isActive;
   boolean wasInHole;
   boolean slowDown;
   double playerSpeed;
   EntityPlayer target;
   @EventHandler
   private final Listener<InputUpdateEvent> inputUpdateEventListener = new Listener((event) -> {
      if (event.getMovementInput() instanceof MovementInputFromOptions && this.isActive) {
         event.getMovementInput().field_78901_c = false;
         event.getMovementInput().field_78899_d = false;
         event.getMovementInput().field_187255_c = false;
         event.getMovementInput().field_187256_d = false;
         event.getMovementInput().field_187257_e = false;
         event.getMovementInput().field_187258_f = false;
         event.getMovementInput().field_192832_b = 0.0F;
         event.getMovementInput().field_78902_a = 0.0F;
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<PlayerMoveEvent> playerMoveListener = new Listener((event) -> {
      this.isActive = false;
      TimerUtils.setTickLength(50.0F);
      if (mc.field_71439_g.func_70089_S() && !mc.field_71439_g.func_184613_cA() && !mc.field_71439_g.field_71075_bZ.field_75100_b) {
         double currentSpeed = Math.hypot(mc.field_71439_g.field_70159_w, mc.field_71439_g.field_70179_y);
         if (currentSpeed <= 0.05D) {
            this.originPos = PlayerUtil.getPlayerPos();
         }

         this.target = this.getNearestPlayer(this.target);
         if (this.target != null) {
            double range = (double)mc.field_71439_g.func_70032_d(this.target);
            boolean inRange = range <= (double)(Integer)this.cancelRange.getValue();
            if (this.shouldDisable(currentSpeed, inRange)) {
               if ((Boolean)this.disable.getValue()) {
                  this.disable();
               }

            } else {
               BlockPos hole = this.findHoles(this.target, inRange);
               if (hole != null) {
                  double x = (double)hole.func_177958_n() + 0.5D;
                  double y = (double)hole.func_177956_o();
                  double z = (double)hole.func_177952_p() + 0.5D;
                  if (this.checkYRange((int)mc.field_71439_g.field_70163_u, this.originPos.field_177960_b)) {
                     Vec3d playerPos = mc.field_71439_g.func_174791_d();
                     double yawRad = Math.toRadians((double)RotationUtil.getRotationTo(playerPos, new Vec3d(x, y, z)).field_189982_i);
                     double dist = Math.hypot(x - playerPos.field_72450_a, z - playerPos.field_72449_c);
                     if (mc.field_71439_g.field_70122_E) {
                        this.playerSpeed = MotionUtil.getBaseMoveSpeed() * (EntityUtil.isColliding(0.0D, -0.5D, 0.0D) instanceof BlockLiquid && !EntityUtil.isInLiquid() ? 0.91D : (Double)this.speed.getValue());
                        this.slowDown = true;
                     }

                     double speed = Math.min(dist, this.playerSpeed);
                     mc.field_71439_g.field_70159_w = 0.0D;
                     mc.field_71439_g.field_70179_y = 0.0D;
                     event.setX(-Math.sin(yawRad) * speed);
                     event.setZ(Math.cos(yawRad) * speed);
                     if (speed != 0.0D && (-Math.sin(yawRad) != 0.0D || Math.cos(yawRad) != 0.0D)) {
                        TimerUtils.setTickLength((float)(50.0D / (Double)this.timer.getValue()));
                        this.isActive = true;
                     }
                  }
               }

               if (mc.field_71439_g.field_70123_F && hole == null) {
                  ++this.stuckTicks;
               } else {
                  this.stuckTicks = 0;
               }

            }
         }
      }
   }, new Predicate[0]);
   double[] pointFiveToOne = new double[]{0.41999998688698D};
   double[] one = new double[]{0.41999998688698D, 0.7531999805212D};
   double[] oneFive = new double[]{0.42D, 0.753D, 1.001D, 1.084D, 1.006D};
   double[] oneSixTwoFive = new double[]{0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D};
   double[] oneEightSevenFive = new double[]{0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D};
   double[] two = new double[]{0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D, 1.869D};
   double[] twoFive = new double[]{0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D, 1.869D, 2.019D, 1.907D};
   double[] threeStep = new double[]{0.42D, 0.78D, 0.63D, 0.51D, 0.9D, 1.21D, 1.45D, 1.43D, 1.78D, 1.63D, 1.51D, 1.9D, 2.21D, 2.45D, 2.43D};
   double[] fourStep = new double[]{0.42D, 0.75D, 0.63D, 0.51D, 0.9D, 1.21D, 1.45D, 1.43D, 1.78D, 1.63D, 1.51D, 1.9D, 2.21D, 2.45D, 2.43D, 2.78D, 2.63D, 2.51D, 2.9D, 3.21D, 3.45D, 3.43D};
   double[] betaShared = new double[]{0.419999986887D, 0.7531999805212D, 1.0013359791121D, 1.1661092609382D, 1.249187078744682D, 1.176759275064238D};
   double[] betaTwo = new double[]{1.596759261951216D, 1.929959255585439D};
   double[] betaTwoFive = new double[]{1.596759261951216D, 1.929959255585439D, 2.178095254176385D, 2.3428685360024515D, 2.425946353808919D};
   @EventHandler
   private final Listener<StepEvent> stepEventListener = new Listener((event) -> {
      if (this.canStep()) {
         double step = event.getBB().field_72338_b - mc.field_71439_g.field_70163_u;
         if (!((String)this.mode.getValue()).equalsIgnoreCase("Vanilla")) {
            if (((String)this.mode.getValue()).equalsIgnoreCase("NCP")) {
               if (step == 0.625D && (Boolean)this.abnormal.getValue()) {
                  this.sendOffsets(this.pointFiveToOne);
               } else if (step == 1.0D || (step == 0.875D || step == 1.0625D || step == 0.9375D) && (Boolean)this.abnormal.getValue()) {
                  this.sendOffsets(this.one);
               } else if (step == 1.5D) {
                  this.sendOffsets(this.oneFive);
               } else if (step == 1.875D && (Boolean)this.abnormal.getValue()) {
                  this.sendOffsets(this.oneEightSevenFive);
               } else if (step == 1.625D && (Boolean)this.abnormal.getValue()) {
                  this.sendOffsets(this.oneSixTwoFive);
               } else if (step == 2.0D) {
                  this.sendOffsets(this.two);
               } else if (step == 2.5D) {
                  this.sendOffsets(this.twoFive);
               } else if (step == 3.0D) {
                  this.sendOffsets(this.threeStep);
               } else if (step == 4.0D) {
                  this.sendOffsets(this.fourStep);
               } else {
                  event.cancel();
               }
            } else if (((String)this.mode.getValue()).equalsIgnoreCase("Beta")) {
               if (step == 1.5D) {
                  this.sendOffsets(this.betaShared);
               } else if (step == 2.0D) {
                  this.sendOffsets(this.betaShared);
                  this.sendOffsets(this.betaTwo);
               } else if (step == 2.5D) {
                  this.sendOffsets(this.betaShared);
                  this.sendOffsets(this.betaTwoFive);
               } else if (step == 3.0D) {
                  this.sendOffsets(this.betaShared);
                  this.sendOffsets(this.threeStep);
               } else if (step == 4.0D) {
                  this.sendOffsets(this.betaShared);
                  this.sendOffsets(this.fourStep);
               } else {
                  event.cancel();
               }
            }

         }
      }
   }, new Predicate[0]);

   private EntityPlayer getNearestPlayer(EntityPlayer target) {
      return target != null && mc.field_71439_g.func_70032_d(target) <= (float)(Integer)this.fixedRange.getValue() && !EntityUtil.basicChecksEntity(target) ? target : (EntityPlayer)mc.field_71441_e.field_73010_i.stream().filter((p) -> {
         return mc.field_71439_g.func_70032_d(p) <= (float)(Integer)this.targetRange.getValue();
      }).filter((p) -> {
         return mc.field_71439_g.field_145783_c != p.field_145783_c;
      }).filter((p) -> {
         return !EntityUtil.basicChecksEntity(p);
      }).min(Comparator.comparing((p) -> {
         return mc.field_71439_g.func_70032_d(p);
      })).orElse((Object)null);
   }

   public void onEnable() {
      this.wasInHole = false;
      this.startPos = this.originPos = PlayerUtil.getPlayerPos();
   }

   public void onUpdate() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L && this.startPos != null) {
         if (this.canStep()) {
            mc.field_71439_g.field_70138_W = this.getHeight((String)this.mode.getValue());
         } else {
            if (mc.field_71439_g.func_184187_bx() != null) {
               mc.field_71439_g.func_184187_bx().field_70138_W = 1.0F;
            }

            mc.field_71439_g.field_70138_W = 0.6F;
         }

         if (this.target == null) {
            this.isActive = false;
         }

      } else {
         this.disable();
      }
   }

   public void onDisable() {
      this.isActive = false;
      this.stuckTicks = 0;
      TimerUtils.setTickLength(50.0F);
      if (mc.field_71439_g != null) {
         if (mc.field_71439_g.func_184187_bx() != null) {
            mc.field_71439_g.func_184187_bx().field_70138_W = 1.0F;
         }

         mc.field_71439_g.field_70138_W = 0.6F;
      }

   }

   private BlockPos findHoles(EntityPlayer target, boolean inRange) {
      if (inRange && this.wasInHole) {
         return null;
      } else {
         this.wasInHole = false;
         NonNullList<BlockPos> holes = NonNullList.func_191196_a();
         List<BlockPos> blockPosList = EntityUtil.getSphere(EntityUtil.getPlayerPos(target), (Double)this.hRange.getValue(), 8.0D, false, true, 0);
         blockPosList.forEach((pos) -> {
            if (this.checkYRange((int)mc.field_71439_g.field_70163_u, pos.field_177960_b)) {
               if (mc.field_71441_e.func_175623_d(PlayerUtil.getPlayerPos().func_177981_b(2)) || (int)mc.field_71439_g.field_70163_u >= pos.field_177960_b) {
                  HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, (Boolean)this.only.getValue(), false, false);
                  HoleUtil.HoleType holeType = holeInfo.getType();
                  if (holeType != HoleUtil.HoleType.NONE) {
                     if ((Boolean)this.only.getValue()) {
                        if (holeType != HoleUtil.HoleType.SINGLE) {
                           return;
                        }
                     } else {
                        if (!(Boolean)this.single.getValue() && holeType == HoleUtil.HoleType.SINGLE) {
                           return;
                        }

                        if (!(Boolean)this.twoBlocks.getValue() && holeType == HoleUtil.HoleType.DOUBLE) {
                           return;
                        }

                        if (!(Boolean)this.custom.getValue() && holeType == HoleUtil.HoleType.CUSTOM) {
                           return;
                        }

                        if (!(Boolean)this.four.getValue() && holeType == HoleUtil.HoleType.FOUR) {
                           return;
                        }
                     }

                     if (mc.field_71441_e.func_175623_d(pos) && mc.field_71441_e.func_175623_d(pos.func_177982_a(0, 1, 0)) && mc.field_71441_e.func_175623_d(pos.func_177982_a(0, 2, 0))) {
                        int high = 0;

                        while(true) {
                           if (!((double)high < mc.field_71439_g.field_70163_u - (double)pos.field_177960_b)) {
                              holes.add(pos);
                              break;
                           }

                           if (high != 0) {
                              if (mc.field_71439_g.field_70163_u > (double)pos.field_177960_b && !mc.field_71441_e.func_175623_d(new BlockPos(pos.field_177962_a, pos.field_177960_b + high, pos.field_177961_c))) {
                                 return;
                              }

                              if (mc.field_71439_g.field_70163_u < (double)pos.field_177960_b) {
                                 BlockPos newPos = new BlockPos(pos.field_177962_a, pos.field_177960_b + high, pos.field_177961_c);
                                 if (mc.field_71441_e.func_175623_d(newPos) && (mc.field_71441_e.func_175623_d(newPos.func_177977_b()) || mc.field_71441_e.func_175623_d(newPos.func_177984_a()))) {
                                    return;
                                 }
                              }
                           }

                           ++high;
                        }
                     }
                  }

               }
            }
         });
         return (BlockPos)holes.stream().min(Comparator.comparing((p) -> {
            return (Boolean)this.near.getValue() ? target.func_70011_f((double)p.field_177962_a + 0.5D, (double)p.field_177960_b, (double)p.field_177961_c + 0.5D) : mc.field_71439_g.func_70011_f((double)p.field_177962_a + 0.5D, (double)p.field_177960_b, (double)p.field_177961_c + 0.5D);
         })).orElse((Object)null);
      }
   }

   private boolean shouldDisable(Double currentSpeed, boolean inRange) {
      if (this.isActive) {
         return false;
      } else if (!mc.field_71439_g.field_70122_E) {
         return false;
      } else if (this.stuckTicks > 5 && currentSpeed < 0.05D) {
         return true;
      } else {
         HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(new BlockPos((double)PlayerUtil.getPlayerPos().field_177962_a, (double)PlayerUtil.getPlayerPos().field_177960_b + 0.5D, (double)PlayerUtil.getPlayerPos().field_177961_c), false, false, false);
         HoleUtil.HoleType holeType = holeInfo.getType();
         if (holeType != HoleUtil.HoleType.NONE && inRange) {
            if ((Boolean)this.only.getValue()) {
               if (holeType != HoleUtil.HoleType.SINGLE) {
                  return false;
               }
            } else {
               if (!(Boolean)this.single.getValue() && holeType == HoleUtil.HoleType.SINGLE) {
                  return false;
               }

               if (!(Boolean)this.twoBlocks.getValue() && holeType == HoleUtil.HoleType.DOUBLE) {
                  return false;
               }

               if (!(Boolean)this.custom.getValue() && holeType == HoleUtil.HoleType.CUSTOM) {
                  return false;
               }

               if (!(Boolean)this.four.getValue() && holeType == HoleUtil.HoleType.FOUR) {
                  return false;
               }
            }

            Vec3d center = this.getCenter(holeInfo.getCentre());
            double XDiff = Math.abs(center.field_72450_a - mc.field_71439_g.field_70165_t);
            double ZDiff = Math.abs(center.field_72449_c - mc.field_71439_g.field_70161_v);
            if ((!(XDiff <= 0.3D) || !(ZDiff <= 0.3D)) && !this.wasInHole) {
               double MotionX = center.field_72450_a - mc.field_71439_g.field_70165_t;
               double MotionZ = center.field_72449_c - mc.field_71439_g.field_70161_v;
               mc.field_71439_g.field_70159_w = MotionX / (double)(Integer)this.centerSpeed.getValue();
               mc.field_71439_g.field_70179_y = MotionZ / (double)(Integer)this.centerSpeed.getValue();
            }

            this.wasInHole = true;
            return true;
         } else {
            return false;
         }
      }
   }

   public Vec3d getCenter(AxisAlignedBB box) {
      boolean air = mc.field_71441_e.func_175623_d(new BlockPos(box.field_72340_a, box.field_72338_b + 1.0D, box.field_72339_c));
      return air ? new Vec3d(box.field_72340_a + (box.field_72336_d - box.field_72340_a) / 2.0D, box.field_72338_b, box.field_72339_c + (box.field_72334_f - box.field_72339_c) / 2.0D) : new Vec3d(box.field_72336_d - 0.5D, box.field_72338_b, box.field_72334_f - 0.5D);
   }

   private boolean checkYRange(int playerY, int holeY) {
      if (playerY >= holeY) {
         return playerY - holeY <= (Integer)this.downRange.getValue();
      } else {
         return holeY - playerY <= -(Integer)this.upRange.getValue();
      }
   }

   float getHeight(String mode) {
      return Float.parseFloat(mode.equals("Vanilla") ? (String)this.vHeight.getValue() : (String)this.height.getValue());
   }

   protected boolean canStep() {
      return !mc.field_71439_g.func_70090_H() && mc.field_71439_g.field_70122_E && !mc.field_71439_g.func_70617_f_() && !mc.field_71439_g.field_71158_b.field_78901_c && mc.field_71439_g.field_70124_G && (double)mc.field_71439_g.field_70143_R < 0.1D && (Boolean)this.step.getValue() && this.isActive;
   }

   void sendOffsets(double[] offsets) {
      double[] var2 = offsets;
      int var3 = offsets.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         double i = var2[var4];
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + i + 0.0D, mc.field_71439_g.field_70161_v, false));
      }

   }

   public String getHudInfo() {
      return (Boolean)this.hud.getValue() ? "[" + ChatFormatting.WHITE + (this.target == null ? "None" : this.target.func_70005_c_() + ", " + (this.isActive ? "Chasing" : "Pausing")) + ChatFormatting.GRAY + "]" : "";
   }
}
