package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Position;

@Module.Declaration(
   name = "AntiHunger",
   category = Category.Misc,
   priority = 999
)
public class AntiHunger extends Module {
   BooleanSetting cancelMove = this.registerBoolean("Cancel Spring", false);
   @EventHandler
   private final Listener<PacketEvent.Send> sendListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         if (event.getPacket() instanceof Position) {
            this.onPacket((Position)event.getPacket());
         }

         if (event.getPacket() instanceof CPacketEntityAction && (Boolean)this.cancelMove.getValue()) {
            CPacketEntityAction packet = (CPacketEntityAction)event.getPacket();
            if (packet.func_180764_b() == Action.START_SPRINTING || packet.func_180764_b() == Action.STOP_SPRINTING) {
               event.cancel();
            }
         }

      }
   }, new Predicate[0]);

   private void onPacket(CPacketPlayer packet) {
      packet.field_149474_g = (mc.field_71439_g.field_70143_R <= 0.0F || mc.field_71442_b.field_78778_j) && mc.field_71439_g.func_184613_cA();
   }
}
