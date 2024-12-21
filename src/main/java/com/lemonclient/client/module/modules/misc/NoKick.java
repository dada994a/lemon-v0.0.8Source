package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;

@Module.Declaration(
   name = "NoKick",
   category = Category.Misc
)
public class NoKick extends Module {
   public BooleanSetting noPacketKick = this.registerBoolean("Packet", true);
   BooleanSetting noSlimeCrash = this.registerBoolean("Slime", false);
   BooleanSetting noOffhandCrash = this.registerBoolean("Offhand", false);
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if ((Boolean)this.noOffhandCrash.getValue() && event.getPacket() instanceof SPacketSoundEffect && ((SPacketSoundEffect)event.getPacket()).func_186978_a() == SoundEvents.field_187719_p) {
         event.cancel();
      }

   }, new Predicate[0]);

   public void onUpdate() {
      if (mc.field_71441_e != null && (Boolean)this.noSlimeCrash.getValue()) {
         mc.field_71441_e.field_72996_f.forEach((entity) -> {
            if (entity instanceof EntitySlime) {
               EntitySlime slime = (EntitySlime)entity;
               if (slime.func_70809_q() > 4) {
                  mc.field_71441_e.func_72900_e(entity);
               }
            }

         });
      }

   }
}
