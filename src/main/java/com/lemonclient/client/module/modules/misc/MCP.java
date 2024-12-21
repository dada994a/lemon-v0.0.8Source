package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.player.InventoryUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import org.lwjgl.input.Mouse;

@Module.Declaration(
   name = "MCP",
   category = Category.Misc
)
public class MCP extends Module {
   BooleanSetting clipRotate = this.registerBoolean("clipRotate", false);
   IntegerSetting pearlPitch = this.registerInteger("Pitch", 85, -90, 90, () -> {
      return (Boolean)this.clipRotate.getValue();
   });
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", false);
   BooleanSetting check = this.registerBoolean("Switch Check", false);
   @EventHandler
   private final Listener<MouseInputEvent> listener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L && mc.field_71439_g.field_71071_by != null) {
         if (Mouse.getEventButton() == 2) {
            if (mc.field_71476_x.field_72313_a == Type.ENTITY) {
               return;
            }

            if ((Boolean)this.clipRotate.getValue()) {
               mc.field_71439_g.field_71174_a.func_147297_a(new Rotation(mc.field_71439_g.field_70177_z, ((Integer)this.pearlPitch.getValue()).floatValue(), mc.field_71439_g.field_70122_E));
            }

            int pearlInvSlot = InventoryUtil.findFirstItemSlot(ItemEnderPearl.class, 0, 35);
            int pearlHotSlot = InventoryUtil.findFirstItemSlot(ItemEnderPearl.class, 0, 8);
            if (pearlInvSlot == -1 && pearlHotSlot == -1) {
               return;
            }

            int oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
            if (pearlHotSlot == -1) {
               ItemStack itemStack = mc.field_71439_g.field_71071_by.func_70301_a(pearlInvSlot);
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketClickWindow(0, pearlInvSlot, mc.field_71439_g.field_71071_by.field_70461_c, ClickType.SWAP, ItemStack.field_190927_a, mc.field_71439_g.field_71070_bA.func_75136_a(mc.field_71439_g.field_71071_by)));
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketClickWindow(0, pearlInvSlot, mc.field_71439_g.field_71071_by.field_70461_c, ClickType.SWAP, itemStack, mc.field_71439_g.field_71070_bA.func_75136_a(mc.field_71439_g.field_71071_by)));
            } else {
               this.switchTo(pearlHotSlot);
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
               this.switchTo(oldSlot);
            }
         }

      }
   }, new Predicate[0]);

   private void switchTo(int slot) {
      if (slot > -1 && slot < 9 && (!(Boolean)this.check.getValue() || mc.field_71439_g.field_71071_by.field_70461_c != slot)) {
         if ((Boolean)this.packetSwitch.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = slot;
         }
      }

   }
}
