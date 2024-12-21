package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.InventoryUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.HoleUtil;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;

@Module.Declaration(
   name = "AutoSkull",
   category = Category.Combat
)
public class AutoSkull extends Module {
   BooleanSetting moving = this.registerBoolean("Moving", false);
   IntegerSetting delay = this.registerInteger("Delay", 50, 0, 1000);
   BooleanSetting packet = this.registerBoolean("Packet Place", true);
   BooleanSetting rotate = this.registerBoolean("Rotate", false);
   BooleanSetting swing = this.registerBoolean("Swing", true);
   BooleanSetting onlyHoles = this.registerBoolean("Only Holes", false);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true);
   BooleanSetting disableAfter = this.registerBoolean("Disable After", true);
   BooleanSetting disable = this.registerBoolean("Auto Disable", true);
   Timing timer = new Timing();
   double y;
   @EventHandler
   private final Listener<InputUpdateEvent> inputUpdateEventListener = new Listener((event) -> {
      if ((Boolean)this.disable.getValue()) {
         if (event.getMovementInput() instanceof MovementInputFromOptions) {
            if (event.getMovementInput().field_78901_c) {
               this.disable();
            }

            if (event.getMovementInput().field_187255_c || event.getMovementInput().field_187256_d || event.getMovementInput().field_187257_e || event.getMovementInput().field_187258_f) {
               double posY = mc.field_71439_g.field_70163_u - this.y;
               if (posY * posY > 0.25D) {
                  this.disable();
               }
            }
         }

      }
   }, new Predicate[0]);

   public void onEnable() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         this.y = mc.field_71439_g.field_70163_u;
      } else {
         this.disable();
      }
   }

   public void fast() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (!(Boolean)this.onlyHoles.getValue() || HoleUtil.isInHole(mc.field_71439_g, true, true, false)) {
            if ((Boolean)this.moving.getValue() || !MotionUtil.isMoving(mc.field_71439_g)) {
               int slot = InventoryUtil.findSkullSlot();
               if (slot != -1) {
                  BlockPos pos = PlayerUtil.getPlayerPos();
                  if (BurrowUtil.getFirstFacing(pos) != null && BlockUtil.isAir(pos)) {
                     if (this.timer.passedMs((long)(Integer)this.delay.getValue())) {
                        this.run(slot, () -> {
                           BurrowUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                        });
                        if ((Boolean)this.disableAfter.getValue()) {
                           this.disable();
                        }

                        this.timer.reset();
                     }

                  }
               }
            }
         }
      }
   }

   private void run(int slot, Runnable runnable) {
      int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
      if (slot >= 0 && slot != oldslot) {
         if ((Boolean)this.packetSwitch.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = slot;
         }

         runnable.run();
         if ((Boolean)this.packetSwitch.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(oldslot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = oldslot;
         }

      } else {
         runnable.run();
      }
   }
}
