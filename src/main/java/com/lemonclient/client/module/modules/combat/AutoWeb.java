package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.PredictUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.HoleUtil;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;
import java.util.Iterator;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "AutoWeb",
   category = Category.Combat
)
public class AutoWeb extends Module {
   ModeSetting page = this.registerMode("Page", Arrays.asList("Settings", "Predict"), "Settings");
   BooleanSetting silentSwitch = this.registerBoolean("Packet Switch", true, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   BooleanSetting check = this.registerBoolean("Switch Check", true, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   BooleanSetting rotate = this.registerBoolean("Rotate", true, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   BooleanSetting packet = this.registerBoolean("Packet", true, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   BooleanSetting swing = this.registerBoolean("Swing", true, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   IntegerSetting delay = this.registerInteger("Delay", 50, 0, 2000, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   IntegerSetting multiPlace = this.registerInteger("MultiPlace", 1, 1, 8, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   BooleanSetting strict = this.registerBoolean("Strict", true, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   BooleanSetting raytrace = this.registerBoolean("Raytrace", false, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   BooleanSetting noInWeb = this.registerBoolean("NoInWeb", true, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   BooleanSetting checkSelf = this.registerBoolean("CheckSelf", true, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   BooleanSetting onlyGround = this.registerBoolean("SelfGround", true, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   BooleanSetting down = this.registerBoolean("Down", false, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   BooleanSetting face = this.registerBoolean("Face", false, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   BooleanSetting feet = this.registerBoolean("Feet", true, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   BooleanSetting onlyAir = this.registerBoolean("OnlyAir", true, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   BooleanSetting air = this.registerBoolean("Air", true, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   DoubleSetting minTargetSpeed = this.registerDouble("MinTargetSpeed", 10.0D, 0.0D, 50.0D, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   DoubleSetting range = this.registerDouble("Range", 5.0D, 1.0D, 6.0D, () -> {
      return ((String)this.page.getValue()).equals("Settings");
   });
   IntegerSetting tickPredict = this.registerInteger("Tick Predict", 8, 0, 30, () -> {
      return ((String)this.page.getValue()).equals("Predict");
   });
   BooleanSetting calculateYPredict = this.registerBoolean("Calculate Y Predict", true, () -> {
      return ((String)this.page.getValue()).equals("Predict");
   });
   IntegerSetting startDecrease = this.registerInteger("Start Decrease", 39, 0, 200, () -> {
      return (Boolean)this.calculateYPredict.getValue() && ((String)this.page.getValue()).equals("Predict");
   });
   IntegerSetting exponentStartDecrease = this.registerInteger("Exponent Start", 2, 1, 5, () -> {
      return (Boolean)this.calculateYPredict.getValue() && ((String)this.page.getValue()).equals("Predict");
   });
   IntegerSetting decreaseY = this.registerInteger("Decrease Y", 2, 1, 5, () -> {
      return (Boolean)this.calculateYPredict.getValue() && ((String)this.page.getValue()).equals("Predict");
   });
   IntegerSetting exponentDecreaseY = this.registerInteger("Exponent Decrease Y", 1, 1, 3, () -> {
      return (Boolean)this.calculateYPredict.getValue() && ((String)this.page.getValue()).equals("Predict");
   });
   BooleanSetting splitXZ = this.registerBoolean("Split XZ", true, () -> {
      return ((String)this.page.getValue()).equals("Predict");
   });
   BooleanSetting manualOutHole = this.registerBoolean("Manual Out Hole", false, () -> {
      return ((String)this.page.getValue()).equals("Predict");
   });
   BooleanSetting aboveHoleManual = this.registerBoolean("Above Hole Manual", false, () -> {
      return (Boolean)this.manualOutHole.getValue() && ((String)this.page.getValue()).equals("Predict");
   });
   BooleanSetting stairPredict = this.registerBoolean("Stair Predict", false, () -> {
      return ((String)this.page.getValue()).equals("Predict");
   });
   IntegerSetting nStair = this.registerInteger("N Stair", 2, 1, 4, () -> {
      return (Boolean)this.stairPredict.getValue() && ((String)this.page.getValue()).equals("Predict");
   });
   DoubleSetting speedActivationStair = this.registerDouble("Speed Activation Stair", 0.3D, 0.0D, 1.0D, () -> {
      return (Boolean)this.stairPredict.getValue() && ((String)this.page.getValue()).equals("Predict");
   });
   private final Timing timer = new Timing();
   private int progress = 0;

   public void onTick() {
      if (this.timer.passedMs((long)(Integer)this.delay.getValue())) {
         if (!(Boolean)this.onlyGround.getValue() || mc.field_71439_g.field_70122_E) {
            this.progress = 0;
            PredictUtil.PredictSettings settings = new PredictUtil.PredictSettings((Integer)this.tickPredict.getValue(), (Boolean)this.calculateYPredict.getValue(), (Integer)this.startDecrease.getValue(), (Integer)this.exponentStartDecrease.getValue(), (Integer)this.decreaseY.getValue(), (Integer)this.exponentDecreaseY.getValue(), (Boolean)this.splitXZ.getValue(), (Boolean)this.manualOutHole.getValue(), (Boolean)this.aboveHoleManual.getValue(), (Boolean)this.stairPredict.getValue(), (Integer)this.nStair.getValue(), (Double)this.speedActivationStair.getValue());
            Iterator var2 = mc.field_71441_e.field_73010_i.iterator();

            while(true) {
               EntityPlayer player;
               EntityPlayer target;
               do {
                  do {
                     do {
                        do {
                           if (!var2.hasNext()) {
                              return;
                           }

                           player = (EntityPlayer)var2.next();
                           target = PredictUtil.predictPlayer(player, settings);
                        } while(EntityUtil.invalid(target, (Double)this.range.getValue() + 3.0D));
                     } while(isInWeb(player) && (Boolean)this.noInWeb.getValue());
                  } while(LemonClient.speedUtil.getPlayerSpeed(player) < (Double)this.minTargetSpeed.getValue());
               } while((Boolean)this.onlyAir.getValue() && player.field_70122_E);

               if ((Boolean)this.down.getValue()) {
                  this.placeWeb(new BlockPos(target.field_70165_t, target.field_70163_u - 0.3D, target.field_70161_v));
                  this.placeWeb(new BlockPos(target.field_70165_t + 0.1D, target.field_70163_u - 0.3D, target.field_70161_v + 0.1D));
                  this.placeWeb(new BlockPos(target.field_70165_t - 0.1D, target.field_70163_u - 0.3D, target.field_70161_v + 0.1D));
                  this.placeWeb(new BlockPos(target.field_70165_t - 0.1D, target.field_70163_u - 0.3D, target.field_70161_v - 0.1D));
                  this.placeWeb(new BlockPos(target.field_70165_t + 0.1D, target.field_70163_u - 0.3D, target.field_70161_v - 0.1D));
               }

               if ((Boolean)this.face.getValue()) {
                  this.placeWeb(new BlockPos(target.field_70165_t + 0.2D, target.field_70163_u + 1.5D, target.field_70161_v + 0.2D));
                  this.placeWeb(new BlockPos(target.field_70165_t - 0.2D, target.field_70163_u + 1.5D, target.field_70161_v + 0.2D));
                  this.placeWeb(new BlockPos(target.field_70165_t - 0.2D, target.field_70163_u + 1.5D, target.field_70161_v - 0.2D));
                  this.placeWeb(new BlockPos(target.field_70165_t + 0.2D, target.field_70163_u + 1.5D, target.field_70161_v - 0.2D));
               }

               if ((Boolean)this.air.getValue() && !player.field_70122_E && (Boolean)this.feet.getValue() && !HoleUtil.isHoleBlock(EntityUtil.getEntityPos(target), true, false, false)) {
                  this.placeWeb(new BlockPos(target.field_70165_t + 0.2D, target.field_70163_u + 0.5D, target.field_70161_v + 0.2D));
                  this.placeWeb(new BlockPos(target.field_70165_t - 0.2D, target.field_70163_u + 0.5D, target.field_70161_v + 0.2D));
                  this.placeWeb(new BlockPos(target.field_70165_t - 0.2D, target.field_70163_u + 0.5D, target.field_70161_v - 0.2D));
                  this.placeWeb(new BlockPos(target.field_70165_t + 0.2D, target.field_70163_u + 0.5D, target.field_70161_v - 0.2D));
               }
            }
         }
      }
   }

   public static boolean isInWeb(EntityPlayer player) {
      if (isWeb(new BlockPos(player.field_70165_t + 0.3D, player.field_70163_u + 1.5D, player.field_70161_v + 0.3D))) {
         return true;
      } else if (isWeb(new BlockPos(player.field_70165_t - 0.3D, player.field_70163_u + 1.5D, player.field_70161_v + 0.3D))) {
         return true;
      } else if (isWeb(new BlockPos(player.field_70165_t - 0.3D, player.field_70163_u + 1.5D, player.field_70161_v - 0.3D))) {
         return true;
      } else if (isWeb(new BlockPos(player.field_70165_t + 0.3D, player.field_70163_u + 1.5D, player.field_70161_v - 0.3D))) {
         return true;
      } else if (isWeb(new BlockPos(player.field_70165_t + 0.3D, player.field_70163_u - 0.5D, player.field_70161_v + 0.3D))) {
         return true;
      } else if (isWeb(new BlockPos(player.field_70165_t - 0.3D, player.field_70163_u - 0.5D, player.field_70161_v + 0.3D))) {
         return true;
      } else if (isWeb(new BlockPos(player.field_70165_t - 0.3D, player.field_70163_u - 0.5D, player.field_70161_v - 0.3D))) {
         return true;
      } else if (isWeb(new BlockPos(player.field_70165_t + 0.3D, player.field_70163_u - 0.5D, player.field_70161_v - 0.3D))) {
         return true;
      } else if (isWeb(new BlockPos(player.field_70165_t + 0.3D, player.field_70163_u + 0.5D, player.field_70161_v + 0.3D))) {
         return true;
      } else if (isWeb(new BlockPos(player.field_70165_t - 0.3D, player.field_70163_u + 0.5D, player.field_70161_v + 0.3D))) {
         return true;
      } else {
         return isWeb(new BlockPos(player.field_70165_t - 0.3D, player.field_70163_u + 0.5D, player.field_70161_v - 0.3D)) ? true : isWeb(new BlockPos(player.field_70165_t + 0.3D, player.field_70163_u + 0.5D, player.field_70161_v - 0.3D));
      }
   }

   private static boolean isWeb(BlockPos pos) {
      return mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150321_G && checkEntity(pos);
   }

   private boolean isSelf(BlockPos pos) {
      if (!(Boolean)this.checkSelf.getValue()) {
         return false;
      } else {
         Iterator var2 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(pos)).iterator();

         Entity entity;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            entity = (Entity)var2.next();
         } while(entity != mc.field_71439_g);

         return true;
      }
   }

   private static boolean checkEntity(BlockPos pos) {
      Iterator var1 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(pos)).iterator();

      Entity entity;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         entity = (Entity)var1.next();
      } while(!(entity instanceof EntityPlayer) || entity == mc.field_71439_g);

      return true;
   }

   private void placeWeb(BlockPos pos) {
      if (this.progress < (Integer)this.multiPlace.getValue() && !(PlayerUtil.getDistance(pos) > (Double)this.range.getValue())) {
         if (mc.field_71441_e.func_175623_d(pos.func_177984_a())) {
            if (this.canPlace(pos)) {
               if (!this.isSelf(pos)) {
                  if (BurrowUtil.findHotbarBlock(BlockWeb.class) != -1) {
                     int old = mc.field_71439_g.field_71071_by.field_70461_c;
                     this.switchTo(BurrowUtil.findHotbarBlock(BlockWeb.class));
                     BlockUtil.placeBlock(pos, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), (Boolean)this.strict.getValue(), (Boolean)this.raytrace.getValue(), (Boolean)this.swing.getValue());
                     ++this.progress;
                     this.switchTo(old);
                     this.timer.reset();
                  }
               }
            }
         }
      }
   }

   private void switchTo(int slot) {
      if (slot > -1 && slot < 9 && (!(Boolean)this.check.getValue() || mc.field_71439_g.field_71071_by.field_70461_c != slot)) {
         if ((Boolean)this.silentSwitch.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = slot;
            mc.field_71442_b.func_78765_e();
         }
      }

   }

   private boolean canPlace(BlockPos pos) {
      if (!BlockUtil.canBlockFacing(pos)) {
         return false;
      } else {
         return !BlockUtil.canReplace(pos) ? false : this.strictPlaceCheck(pos);
      }
   }

   private boolean strictPlaceCheck(BlockPos pos) {
      if (!(Boolean)this.strict.getValue() && (Boolean)this.raytrace.getValue()) {
         return true;
      } else {
         Iterator var2 = BlockUtil.getPlacableFacings(pos, true, (Boolean)this.raytrace.getValue()).iterator();

         EnumFacing side;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            side = (EnumFacing)var2.next();
         } while(!BlockUtil.canClick(pos.func_177972_a(side)));

         return true;
      }
   }
}
