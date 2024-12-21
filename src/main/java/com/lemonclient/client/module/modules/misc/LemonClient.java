package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketChatMessage;

@Module.Declaration(
   name = "LemonClient",
   category = Category.Misc
)
public class LemonClient extends Module {
   BooleanSetting commands = this.registerBoolean("Commands", false);
   String SUFFIX = " ⏐ ℓємℴภ";
   @EventHandler
   public Listener<PacketEvent.Send> listener = new Listener((event) -> {
      if (event.getPacket() instanceof CPacketChatMessage) {
         String s = ((CPacketChatMessage)event.getPacket()).func_149439_c();
         if (s.startsWith("/") && !(Boolean)this.commands.getValue()) {
            return;
         }

         if (s.contains(this.SUFFIX) || s.isEmpty()) {
            return;
         }

         s = s + this.SUFFIX;
         if (s.length() >= 256) {
            s = s.substring(0, 256);
         }

         ((CPacketChatMessage)event.getPacket()).field_149440_a = s;
      }

   }, new Predicate[0]);
}
