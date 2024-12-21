package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.RenderEntityEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.util.SoundCategory;

@Module.Declaration(
   name = "AntiLag",
   category = Category.Misc
)
public class NoLag extends Module {
   BooleanSetting particles = this.registerBoolean("Particles", true);
   BooleanSetting effect = this.registerBoolean("Effect", true);
   BooleanSetting soundEffect = this.registerBoolean("Sound Effect", true);
   BooleanSetting skulls = this.registerBoolean("Skull", true);
   BooleanSetting tnt = this.registerBoolean("Tnt", true);
   BooleanSetting parrots = this.registerBoolean("Parrot", true);
   BooleanSetting spawn = this.registerBoolean("Spawn", true);
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if (event.getPacket() instanceof SPacketParticles && (Boolean)this.particles.getValue()) {
         event.cancel();
      }

      if (event.getPacket() instanceof SPacketEffect && (Boolean)this.effect.getValue()) {
         event.cancel();
      }

      if (event.getPacket() instanceof SPacketSoundEffect && (Boolean)this.soundEffect.getValue()) {
         SPacketSoundEffect packetx = (SPacketSoundEffect)event.getPacket();
         if (packetx.func_186977_b() == SoundCategory.PLAYERS && packetx.func_186978_a() == SoundEvents.field_187719_p) {
            event.cancel();
         }

         if (packetx.func_186977_b() == SoundCategory.WEATHER && packetx.func_186978_a() == SoundEvents.field_187754_de) {
            event.cancel();
         }
      }

      if (event.getPacket() instanceof SPacketSpawnMob && (Boolean)this.spawn.getValue()) {
         SPacketSpawnMob packet = (SPacketSpawnMob)event.getPacket();
         if (packet.func_149025_e() == 55) {
            event.cancel();
         }
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<RenderEntityEvent> renderEntityEventListener = new Listener((event) -> {
      if ((Boolean)this.skulls.getValue() && event.getEntity() instanceof EntityWitherSkull) {
         event.cancel();
      }

      if ((Boolean)this.tnt.getValue() && event.getEntity() instanceof EntityTNTPrimed) {
         event.cancel();
      }

      if ((Boolean)this.parrots.getValue() && event.getEntity() instanceof EntityParrot) {
         event.cancel();
      }

   }, new Predicate[0]);

   public void onDisable() {
      mc.field_71438_f.func_72712_a();
      super.onDisable();
   }
}
