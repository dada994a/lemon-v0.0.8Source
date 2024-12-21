package com.lemonclient.client.module.modules.movement;

import com.lemonclient.api.event.events.MotionUpdateEvent;
import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.PlayerMoveEvent;
import com.lemonclient.api.event.events.StepEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.api.util.misc.KeyBoardClass;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.RotationUtil;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.api.util.world.TimerUtils;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

@Module.Declaration(
   name = "Speed+",
   category = Category.Movement,
   priority = 999
)
public class SpeedPlus extends Module {
   public static SpeedPlus INSTANCE = new SpeedPlus();
   BooleanSetting damageBoost = this.registerBoolean("DamageBoost", true);
   public BooleanSetting sum = this.registerBoolean("Sum", false, () -> {
      return (Boolean)this.damageBoost.getValue();
   });
   BooleanSetting longJump = this.registerBoolean("TryLongJump", false);
   IntegerSetting lagCoolDown = this.registerInteger("LagCoolDown", 2200, 0, 8000, () -> {
      return (Boolean)this.longJump.getValue();
   });
   IntegerSetting jumpStage = this.registerInteger("JumpStage", 6, 1, 20, () -> {
      return (Boolean)this.longJump.getValue();
   });
   BooleanSetting motionJump = this.registerBoolean("MotionJump", false, () -> {
      return (Boolean)this.longJump.getValue();
   });
   BooleanSetting randomBoost = this.registerBoolean("RandomBoost", false);
   BooleanSetting lavaBoost = this.registerBoolean("LavaBoost", true);
   BooleanSetting SpeedInWater = this.registerBoolean("SpeedInWater", true);
   BooleanSetting strict = this.registerBoolean("Strict", false);
   BooleanSetting strictBoost = this.registerBoolean("StrictBoost", false, () -> {
      return (Boolean)this.damageBoost.getValue();
   });
   BooleanSetting useTimer = this.registerBoolean("UseTimer", true);
   BooleanSetting jump = this.registerBoolean("Jump", true);
   BooleanSetting stepCheck = this.registerBoolean("Step Check", true);
   BooleanSetting bindCheck = this.registerBoolean("Use Bind", false, () -> {
      return (Boolean)this.stepCheck.getValue();
   });
   StringSetting bind = this.registerString("Step Check Bind", "", () -> {
      return (Boolean)this.stepCheck.getValue() && (Boolean)this.bindCheck.getValue();
   });
   DoubleSetting minStepHeight = this.registerDouble("Min Step Height", 1.0D, 0.0D, 10.0D, () -> {
      return (Boolean)this.stepCheck.getValue();
   });
   DoubleSetting maxStepHeight = this.registerDouble("Max Step Height", 2.5D, 0.0D, 10.0D, () -> {
      return (Boolean)this.stepCheck.getValue();
   });
   BooleanSetting test = this.registerBoolean("Test Mode", false, () -> {
      return (Boolean)this.stepCheck.getValue();
   });
   Timing lagBackCoolDown = new Timing();
   Timing rdBoostTimer = new Timing();
   boolean lagDetected;
   boolean inCoolDown;
   boolean checkCoolDown;
   boolean warn;
   boolean checkStep;
   int readyStage;
   int stage = 1;
   int level = 1;
   double boostSpeed;
   double lastDist;
   double moveSpeed;
   double stepHigh;
   float boostFactor = 6.0F;
   long detectionTime;
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.lastDist = 0.0D;
            this.moveSpeed = this.applySpeedPotionEffects();
            this.stage = 2;
            this.detectionTime = System.currentTimeMillis();
            this.lagDetected = true;
            this.rdBoostTimer.reset();
            this.boostFactor = 8.0F;
            if ((Boolean)this.longJump.getValue()) {
               this.readyStage = 0;
               this.inCoolDown = true;
               if (!this.checkCoolDown) {
                  this.lagBackCoolDown.reset();
                  this.checkCoolDown = true;
               }
            }
         }

      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<MotionUpdateEvent> motionUpdateEventListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         try {
            if (this.lagBackCoolDown.passedMs((long)(Integer)this.lagCoolDown.getValue())) {
               this.checkCoolDown = false;
               this.inCoolDown = false;
               this.lagBackCoolDown.reset();
            }

            if (System.currentTimeMillis() - this.detectionTime > 3182L) {
               this.lagDetected = false;
            }

            if ((Boolean)this.useTimer.getValue()) {
               TimerUtils.setTickLength(45.955883F);
            }

            if (event.stage == 1) {
               this.lastDist = Math.sqrt((mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q) * (mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q) + (mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s) * (mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s));
            }
         } catch (NumberFormatException var3) {
         }

      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (mc.field_71439_g.field_71158_b.field_192832_b == 0.0F && mc.field_71439_g.field_71158_b.field_78902_a == 0.0F) {
            event.setX(0.0D);
            event.setZ(0.0D);
            event.setSpeed(0.0D);
         } else {
            if (this.checkStep && (Boolean)this.test.getValue()) {
               double yaw = this.calcMoveYaw(mc.field_71439_g.field_70177_z, mc.field_71439_g.field_71158_b.field_192832_b, mc.field_71439_g.field_71158_b.field_78902_a);
               double dirX = -Math.sin(yaw);
               double dirZ = Math.cos(yaw);
               double dist = this.calcBlockDistAhead(dirX * 6.0D, dirZ * 6.0D);
               double stepHeight = (Boolean)this.test.getValue() ? this.calcStepHeight(dist, dirX, dirZ) : this.stepHigh;
               double multiplier = this.applySpeedPotionEffects();
               if (stepHeight <= (Double)this.maxStepHeight.getValue()) {
                  if (dist < 3.0D * multiplier && stepHeight > (Double)this.minStepHeight.getValue() * 2.0D) {
                     return;
                  }

                  if (dist < 1.4D * multiplier && stepHeight > (Double)this.minStepHeight.getValue()) {
                     return;
                  }
               }
            }

            if ((Boolean)this.SpeedInWater.getValue() || !this.shouldReturn()) {
               if (mc.field_71439_g.field_70122_E) {
                  this.level = 2;
               }

               EntityPlayerSP var10000;
               if (round(mc.field_71439_g.field_70163_u - (double)((int)mc.field_71439_g.field_70163_u), 3) == round(0.138D, 3) && (Boolean)this.jump.getValue()) {
                  var10000 = mc.field_71439_g;
                  var10000.field_70181_x -= 0.07D;
                  event.setY(event.getY() - 0.08316090325960147D);
                  var10000 = mc.field_71439_g;
                  var10000.field_70163_u -= 0.08316090325960147D;
               }

               if (this.level != 1) {
                  if (this.level == 2) {
                     this.level = 3;
                     if (!mc.field_71439_g.func_180799_ab() && mc.field_71439_g.field_70122_E && (Boolean)this.jump.getValue()) {
                        event.setY(mc.field_71439_g.field_70181_x = this.applyJumpBoostPotionEffects());
                     }

                     if (!(Boolean)this.strict.getValue() && !mc.field_71439_g.func_70093_af()) {
                        this.moveSpeed *= 1.64847275D;
                     } else {
                        this.moveSpeed *= 1.433D;
                     }
                  } else if (this.level == 3) {
                     this.level = 4;
                     this.moveSpeed = this.lastDist - 0.6553D * (this.lastDist - this.applySpeedPotionEffects() + 0.04D);
                  } else {
                     if (mc.field_71439_g.field_70122_E && (!mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.field_70121_D.func_72317_d(0.0D, mc.field_71439_g.field_70181_x, 0.0D)).isEmpty() || mc.field_71439_g.field_70124_G)) {
                        this.level = 1;
                     }

                     this.moveSpeed = this.lastDist - this.lastDist / 201.0D;
                  }
               } else {
                  this.level = 2;
                  this.moveSpeed = 1.418D * this.applySpeedPotionEffects();
               }

               if ((Boolean)this.damageBoost.getValue() && ColorMain.INSTANCE.velocityBoost != 0.0D) {
                  if ((Boolean)this.longJump.getValue()) {
                     ++this.readyStage;
                  }

                  this.boostSpeed = ColorMain.INSTANCE.velocityBoost;
                  this.moveSpeed += this.boostSpeed;
                  if ((Boolean)this.strictBoost.getValue()) {
                     this.moveSpeed = Math.max((this.moveSpeed + 0.10000000149011612D) / 1.5D, this.applySpeedPotionEffects());
                  }

                  ColorMain.INSTANCE.velocityBoost = 0.0D;
               }

               if ((Boolean)this.randomBoost.getValue() && this.rdBoostTimer.passedMs(3500L) && !this.lagDetected && MotionUtil.moving(mc.field_71439_g) && mc.field_71439_g.field_70122_E) {
                  this.moveSpeed += this.moveSpeed / (double)this.boostFactor;
                  this.boostFactor = 6.0F;
                  this.rdBoostTimer.reset();
               }

               if ((Boolean)this.longJump.getValue() && this.readyStage >= (Integer)this.jumpStage.getValue() && !this.inCoolDown) {
                  if (!(Boolean)this.motionJump.getValue()) {
                     this.moveSpeed *= (double)((float)(Integer)this.jumpStage.getValue() / 10.0F);
                  } else {
                     motionJump();
                     var10000 = mc.field_71439_g;
                     var10000.field_70181_x *= 1.02D;
                     var10000 = mc.field_71439_g;
                     var10000.field_70181_x *= 1.13D;
                     var10000 = mc.field_71439_g;
                     var10000.field_70181_x *= 1.27D;
                     this.moveSpeed += Math.abs(this.moveSpeed - this.boostSpeed);
                  }

                  this.readyStage = 0;
               }

               this.moveSpeed = Math.max(this.moveSpeed, this.applySpeedPotionEffects());
               if (!this.shouldReturn()) {
                  event.setSpeed(this.moveSpeed);
               } else if ((Boolean)this.lavaBoost.getValue() && mc.field_71439_g.func_180799_ab()) {
                  event.setX(event.getX() * 3.1D);
                  event.setZ(event.getZ() * 3.1D);
                  if (mc.field_71474_y.field_74314_A.func_151470_d()) {
                     event.setY(event.getY() * 3.0D);
                  }
               }

            }
         }
      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<StepEvent> stepEventListener = new Listener((event) -> {
      this.stepHigh = event.getBB().field_72338_b - mc.field_71439_g.field_70163_u;
   }, new Predicate[0]);

   public void onTick() {
      this.checkStep = false;
      if ((Boolean)this.stepCheck.getValue()) {
         if ((Boolean)this.bindCheck.getValue()) {
            if (this.bind.getText().isEmpty() || !Keyboard.isKeyDown(KeyBoardClass.getKeyFromChar(this.bind.getText().charAt(0)))) {
               this.checkStep = !this.checkStep;
            }
         } else {
            this.checkStep = true;
         }
      }

   }

   public static double round(double n, int n2) {
      if (n2 < 0) {
         throw new IllegalArgumentException();
      } else {
         return (new BigDecimal(n)).setScale(n2, RoundingMode.HALF_UP).doubleValue();
      }
   }

   public void onEnable() {
      if (mc.field_71439_g == null) {
         this.disable();
      } else {
         this.boostSpeed = 0.0D;
         this.lagBackCoolDown.reset();
         this.readyStage = 0;
         this.warn = false;
         this.moveSpeed = this.applySpeedPotionEffects();
      }
   }

   public static void motionJump() {
      if (!mc.field_71439_g.field_70124_G) {
         EntityPlayerSP var10000;
         if (mc.field_71439_g.field_70181_x == -0.07190068807140403D) {
            var10000 = mc.field_71439_g;
            var10000.field_70181_x *= 0.3499999940395355D;
         } else if (mc.field_71439_g.field_70181_x == -0.10306193759436909D) {
            var10000 = mc.field_71439_g;
            var10000.field_70181_x *= 0.550000011920929D;
         } else if (mc.field_71439_g.field_70181_x == -0.13395038817442878D) {
            var10000 = mc.field_71439_g;
            var10000.field_70181_x *= 0.6700000166893005D;
         } else if (mc.field_71439_g.field_70181_x == -0.16635183030382D) {
            var10000 = mc.field_71439_g;
            var10000.field_70181_x *= 0.6899999976158142D;
         } else if (mc.field_71439_g.field_70181_x == -0.19088711097794803D) {
            var10000 = mc.field_71439_g;
            var10000.field_70181_x *= 0.7099999785423279D;
         } else if (mc.field_71439_g.field_70181_x == -0.21121925191528862D) {
            var10000 = mc.field_71439_g;
            var10000.field_70181_x *= 0.20000000298023224D;
         } else if (mc.field_71439_g.field_70181_x == -0.11979897632390576D) {
            var10000 = mc.field_71439_g;
            var10000.field_70181_x *= 0.9300000071525574D;
         } else if (mc.field_71439_g.field_70181_x == -0.18758479151225355D) {
            var10000 = mc.field_71439_g;
            var10000.field_70181_x *= 0.7200000286102295D;
         } else if (mc.field_71439_g.field_70181_x == -0.21075983825251726D) {
            var10000 = mc.field_71439_g;
            var10000.field_70181_x *= 0.7599999904632568D;
         }

         if (mc.field_71439_g.field_70181_x < -0.2D && mc.field_71439_g.field_70181_x > -0.24D) {
            var10000 = mc.field_71439_g;
            var10000.field_70181_x *= 0.7D;
         }

         if (mc.field_71439_g.field_70181_x < -0.25D && mc.field_71439_g.field_70181_x > -0.32D) {
            var10000 = mc.field_71439_g;
            var10000.field_70181_x *= 0.8D;
         }

         if (mc.field_71439_g.field_70181_x < -0.35D && mc.field_71439_g.field_70181_x > -0.8D) {
            var10000 = mc.field_71439_g;
            var10000.field_70181_x *= 0.98D;
         }

         if (mc.field_71439_g.field_70181_x < -0.8D && mc.field_71439_g.field_70181_x > -1.6D) {
            var10000 = mc.field_71439_g;
            var10000.field_70181_x *= 0.99D;
         }
      }

   }

   public boolean shouldReturn() {
      return mc.field_71439_g.func_180799_ab() || mc.field_71439_g.func_70090_H() || mc.field_71439_g.field_70134_J;
   }

   public void onDisable() {
      this.moveSpeed = 0.0D;
      this.stage = 2;
      if (mc.field_71439_g != null) {
         mc.field_71439_g.field_70138_W = 0.6F;
         TimerUtils.setTickLength(50.0F);
      }

   }

   private double calcBlockDistAhead(double offsetX, double offsetZ) {
      if (mc.field_71439_g.field_70123_F) {
         return 0.0D;
      } else {
         AxisAlignedBB box = mc.field_71439_g.field_70121_D;
         double x = offsetX > 0.0D ? box.field_72336_d : box.field_72340_a;
         double z = offsetX > 0.0D ? box.field_72334_f : box.field_72339_c;
         return Math.min(this.rayTraceDist(new Vec3d(x, box.field_72338_b + 0.6D, z), offsetX, offsetZ), this.rayTraceDist(new Vec3d(x, box.field_72337_e + 0.6D, z), offsetX, offsetZ));
      }
   }

   private double rayTraceDist(Vec3d start, double offsetX, double offsetZ) {
      RayTraceResult result = mc.field_71441_e.func_147447_a(start, start.func_72441_c(offsetX, 0.0D, offsetZ), false, true, false);
      if (result != null && result.field_72307_f != null) {
         double x = start.field_72450_a - result.field_72307_f.field_72450_a;
         double z = start.field_72449_c - result.field_72307_f.field_72449_c;
         return Math.sqrt(Math.pow(x, 2.0D) + Math.pow(z, 2.0D));
      } else {
         return 999.0D;
      }
   }

   private double calcMoveYaw(float yaw, float moveForward, float moveStrafe) {
      double moveYaw = moveForward == 0.0F && moveStrafe == 0.0F ? 0.0D : Math.toDegrees(Math.atan2((double)moveForward, (double)moveStrafe)) - 90.0D;
      return Math.toRadians(RotationUtil.normalizeAngle((double)yaw + moveYaw));
   }

   private double calcStepHeight(double dist, double motionX, double motionZ) {
      BlockPos pos = PlayerUtil.getPlayerPos();
      if (mc.field_71441_e.func_180495_p(pos).func_185890_d(mc.field_71441_e, pos) != null) {
         return 0.0D;
      } else {
         double i = (double)Math.max(Math.round(dist), 1L);
         double minStepHeight = Double.MAX_VALUE;
         double x = motionX * i;
         double z = motionZ * i;
         minStepHeight = this.checkBox(minStepHeight, x, 0.0D);
         minStepHeight = this.checkBox(minStepHeight, 0.0D, z);
         return minStepHeight == Double.MAX_VALUE ? 0.0D : minStepHeight;
      }
   }

   private double checkBox(double minStepHeight, double offsetX, double offsetZ) {
      AxisAlignedBB box = mc.field_71439_g.field_70121_D.func_72317_d(offsetX, 0.0D, offsetZ);
      if (!mc.field_71441_e.func_184143_b(box)) {
         return minStepHeight;
      } else {
         double stepHeight = minStepHeight;
         double[] var10 = new double[]{0.605D, 1.005D, 1.505D, 2.005D, 2.505D};
         int var11 = var10.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            double y = var10[var12];
            if (y > minStepHeight) {
               break;
            }

            AxisAlignedBB stepBox = new AxisAlignedBB(box.field_72340_a, box.field_72338_b + y - 0.5D, box.field_72339_c, box.field_72336_d, box.field_72338_b + y, box.field_72334_f);
            List<AxisAlignedBB> boxList = mc.field_71441_e.func_184144_a((Entity)null, stepBox);
            AxisAlignedBB maxHeight = (AxisAlignedBB)boxList.stream().max(Comparator.comparing((bb) -> {
               return bb.field_72337_e;
            })).orElse((Object)null);
            if (maxHeight != null) {
               double maxStepHeight = maxHeight.field_72337_e - mc.field_71439_g.field_70163_u;
               if (!mc.field_71441_e.func_184143_b(box.func_72317_d(0.0D, maxStepHeight, 0.0D))) {
                  stepHeight = maxStepHeight;
                  break;
               }
            }
         }

         return stepHeight;
      }
   }

   private double applySpeedPotionEffects() {
      double result = 0.2873D;
      if (mc.field_71439_g.func_70660_b(MobEffects.field_76424_c) != null) {
         result += 0.2873D * ((double)mc.field_71439_g.func_70660_b(MobEffects.field_76424_c).func_76458_c() + 1.0D) * 0.2D;
      }

      if (mc.field_71439_g.func_70660_b(MobEffects.field_76421_d) != null) {
         result -= 0.2873D * ((double)mc.field_71439_g.func_70660_b(MobEffects.field_76421_d).func_76458_c() + 1.0D) * 0.15D;
      }

      return result;
   }

   private double applyJumpBoostPotionEffects() {
      double result = 0.4D;
      if (mc.field_71439_g.func_70660_b(MobEffects.field_76430_j) != null) {
         result += (double)(mc.field_71439_g.func_70660_b(MobEffects.field_76430_j).func_76458_c() + 1) * 0.1D;
      }

      return result;
   }
}
