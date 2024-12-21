package com.lemonclient.client.module.modules.movement;

import com.lemonclient.api.event.events.PlayerMoveEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.player.PlacementUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemChorusFruit;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "AntiVoid",
   category = Category.Movement
)
public class AntiVoid extends Module {
   ModeSetting mode = this.registerMode("Mode", Arrays.asList("Freeze", "Glitch", "Catch"), "Freeze");
   DoubleSetting height = this.registerDouble("Height", 2.0D, 0.0D, 5.0D);
   BooleanSetting chorus = this.registerBoolean("Chorus", false, () -> {
      return ((String)this.mode.getValue()).equals("Freeze");
   });
   BooleanSetting packetFly = this.registerBoolean("PacketFly", false, () -> {
      return ((String)this.mode.getValue()).equals("Catch");
   });
   boolean chorused;
   @EventHandler
   private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener((event) -> {
      try {
         if (mc.field_71439_g.field_70163_u < (Double)this.height.getValue() + 0.1D && ((String)this.mode.getValue()).equalsIgnoreCase("Freeze") && mc.field_71441_e.func_180495_p(new BlockPos(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v)).func_185904_a().func_76222_j()) {
            String var2 = (String)this.mode.getValue();
            byte var3 = -1;
            switch(var2.hashCode()) {
            case 64880283:
               if (var2.equals("Catch")) {
                  var3 = 2;
               }
               break;
            case 2112431799:
               if (var2.equals("Freeze")) {
                  var3 = 0;
               }
               break;
            case 2135652693:
               if (var2.equals("Glitch")) {
                  var3 = 1;
               }
            }

            int newSlot;
            int ix;
            switch(var3) {
            case 0:
               mc.field_71439_g.field_70163_u = (Double)this.height.getValue();
               event.setY(0.0D);
               if (mc.field_71439_g.func_184187_bx() != null) {
                  mc.field_71439_g.field_184239_as.func_70016_h(0.0D, 0.0D, 0.0D);
               }

               if ((Boolean)this.chorus.getValue()) {
                  newSlot = -1;

                  for(ix = 0; ix < 9; ++ix) {
                     ItemStack stackx = mc.field_71439_g.field_71071_by.func_70301_a(ix);
                     if (stackx != ItemStack.field_190927_a && stackx.func_77973_b() instanceof ItemChorusFruit) {
                        newSlot = ix;
                        break;
                     }
                  }

                  if (newSlot == -1) {
                     newSlot = 1;
                     MessageBus.sendClientPrefixMessage(((ColorMain)ModuleManager.getModule(ColorMain.class)).getDisabledColor() + "Out of chorus!", Notification.Type.ERROR);
                     this.chorused = false;
                  } else {
                     this.chorused = true;
                  }

                  if (this.chorused) {
                     mc.field_71439_g.field_71071_by.field_70461_c = newSlot;
                     if (mc.field_71439_g.func_71043_e(true)) {
                        mc.field_71439_g.func_184598_c(EnumHand.MAIN_HAND);
                     }
                  }
               }
               break;
            case 1:
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 69.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
               break;
            case 2:
               newSlot = mc.field_71439_g.field_71071_by.field_70461_c;
               ix = -1;

               for(int i = 0; i < 9; ++i) {
                  ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
                  if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock && Block.func_149634_a(stack.func_77973_b()).func_176223_P().func_185913_b() && !(((ItemBlock)stack.func_77973_b()).func_179223_d() instanceof BlockFalling)) {
                     ix = i;
                     break;
                  }
               }

               if (ix == -1) {
                  ix = 1;
                  MessageBus.sendClientPrefixMessage(((ColorMain)ModuleManager.getModule(ColorMain.class)).getDisabledColor() + "Out of valid blocks. Disabling!", Notification.Type.DISABLE);
                  this.disable();
               }

               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(ix));
               PlacementUtil.place(new BlockPos(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v), EnumHand.MAIN_HAND, true);
               if (mc.field_71441_e.func_180495_p(new BlockPos(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v)).func_185904_a().func_76222_j() && (Boolean)this.packetFly.getValue()) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t + mc.field_71439_g.field_70159_w, mc.field_71439_g.field_70163_u + 0.0624D, mc.field_71439_g.field_70161_v + mc.field_71439_g.field_70179_y, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false));
                  mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 69420.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false));
               }

               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(newSlot));
            }
         }
      } catch (Exception var8) {
      }

   }, new Predicate[0]);
}
