package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.PredictUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.combat.DamageUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.modules.qwq.AutoEz;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "AutoCreeper",
   category = Category.Combat
)
public class AutoCreeper extends Module {
   DoubleSetting minDamage = this.registerDouble("Min Damage", 6.0D, 0.0D, 36.0D);
   IntegerSetting delay = this.registerInteger("Delay", 50, 0, 1000);
   DoubleSetting enemyRange = this.registerDouble("Enemy Range", 10.0D, 0.0D, 16.0D);
   DoubleSetting range = this.registerDouble("Range", 5.0D, 0.0D, 6.0D);
   BooleanSetting rotate = this.registerBoolean("Rotate", false);
   BooleanSetting packet = this.registerBoolean("Packet", false);
   BooleanSetting swing = this.registerBoolean("Swing", true);
   BooleanSetting silent = this.registerBoolean("Silent Switch", true);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", false);
   BooleanSetting predict = this.registerBoolean("Predict", true);
   IntegerSetting tickPredict = this.registerInteger("TickPredict", 8, 0, 30, () -> {
      return (Boolean)this.predict.getValue();
   });
   BooleanSetting calculateYPredict = this.registerBoolean("CalculateYPredict", true, () -> {
      return (Boolean)this.predict.getValue();
   });
   IntegerSetting startDecrease = this.registerInteger("StartDecrease", 39, 0, 200, () -> {
      return (Boolean)this.predict.getValue() && (Boolean)this.calculateYPredict.getValue();
   });
   IntegerSetting exponentStartDecrease = this.registerInteger("ExponentStart", 2, 1, 5, () -> {
      return (Boolean)this.predict.getValue() && (Boolean)this.calculateYPredict.getValue();
   });
   IntegerSetting decreaseY = this.registerInteger("DecreaseY", 2, 1, 5, () -> {
      return (Boolean)this.predict.getValue() && (Boolean)this.calculateYPredict.getValue();
   });
   IntegerSetting exponentDecreaseY = this.registerInteger("ExponentDecreaseY", 1, 1, 3, () -> {
      return (Boolean)this.predict.getValue() && (Boolean)this.calculateYPredict.getValue();
   });
   BooleanSetting splitXZ = this.registerBoolean("SplitXZ", true, () -> {
      return (Boolean)this.predict.getValue();
   });
   BooleanSetting manualOutHole = this.registerBoolean("ManualOutHole", false, () -> {
      return (Boolean)this.predict.getValue();
   });
   BooleanSetting aboveHoleManual = this.registerBoolean("AboveHoleManual", false, () -> {
      return (Boolean)this.predict.getValue() && (Boolean)this.manualOutHole.getValue();
   });
   BooleanSetting stairPredict = this.registerBoolean("StairPredict", false, () -> {
      return (Boolean)this.predict.getValue();
   });
   IntegerSetting nStair = this.registerInteger("NStair", 2, 1, 4, () -> {
      return (Boolean)this.predict.getValue() && (Boolean)this.stairPredict.getValue();
   });
   DoubleSetting speedActivationStair = this.registerDouble("SpeedActivationStair", 0.11D, 0.0D, 1.0D, () -> {
      return (Boolean)this.predict.getValue() && (Boolean)this.stairPredict.getValue();
   });
   Timing timer = new Timing();
   EntityPlayer target;

   public void onTick() {
      int slot = this.getSlot();
      if (slot != -1) {
         this.target = PlayerUtil.getNearestPlayer((Double)this.enemyRange.getValue());
         if (this.target != null) {
            if (AutoEz.INSTANCE.isEnabled()) {
               AutoEz.INSTANCE.addTargetedPlayer(this.target.func_70005_c_());
            }

            PredictUtil.PredictSettings settings = new PredictUtil.PredictSettings((Integer)this.tickPredict.getValue(), (Boolean)this.calculateYPredict.getValue(), (Integer)this.startDecrease.getValue(), (Integer)this.exponentStartDecrease.getValue(), (Integer)this.decreaseY.getValue(), (Integer)this.exponentDecreaseY.getValue(), (Boolean)this.splitXZ.getValue(), (Boolean)this.manualOutHole.getValue(), (Boolean)this.aboveHoleManual.getValue(), (Boolean)this.stairPredict.getValue(), (Integer)this.nStair.getValue(), (Double)this.speedActivationStair.getValue());
            if ((Boolean)this.predict.getValue()) {
               this.target = PredictUtil.predictPlayer(this.target, settings);
            }

            BlockPos blockPos = null;
            double dmg = 0.0D;
            Iterator var6 = EntityUtil.getSphere(PlayerUtil.getEyesPos(), (Double)this.range.getValue(), (Double)this.range.getValue(), false, false, 0).iterator();

            while(var6.hasNext()) {
               BlockPos pos = (BlockPos)var6.next();
               if (BurrowUtil.getFirstFacing(pos) != null) {
                  double damage = (double)DamageUtil.calculateDamage(this.target, (double)pos.field_177962_a + 0.5D, (double)pos.field_177960_b, (double)pos.field_177961_c + 0.5D, 3.0F, "Default");
                  if (!(damage < (Double)this.minDamage.getValue()) && dmg < damage) {
                     blockPos = pos;
                     dmg = damage;
                  }
               }
            }

            if (blockPos != null) {
               if (this.timer.passedMs((long)(Integer)this.delay.getValue())) {
                  this.timer.reset();
                  int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
                  if ((Boolean)this.packetSwitch.getValue()) {
                     mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
                  } else {
                     mc.field_71439_g.field_71071_by.field_70461_c = slot;
                  }

                  BurrowUtil.placeBlock(blockPos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                  if ((Boolean)this.silent.getValue()) {
                     if ((Boolean)this.packetSwitch.getValue()) {
                        mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(oldslot));
                     } else {
                        mc.field_71439_g.field_71071_by.field_70461_c = oldslot;
                     }
                  }
               }

            }
         }
      }
   }

   public int getSlot() {
      int newSlot = -1;

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != ItemStack.field_190927_a && stack.func_77973_b() == Items.field_151063_bx) {
            newSlot = i;
            break;
         }
      }

      return newSlot;
   }
}
