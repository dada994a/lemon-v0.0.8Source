package com.lemonclient.mixin.mixins;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.client.C00Handshake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({C00Handshake.class})
public class MixinC00Handshake {
   @Shadow
   private int field_149600_a;
   @Shadow
   private String field_149598_b;
   @Shadow
   private int field_149599_c;
   @Shadow
   private EnumConnectionState field_149597_d;

   @Inject(
      method = {"writePacketData"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void writePacketData(PacketBuffer buf, CallbackInfo info) {
      info.cancel();
      buf.func_150787_b(this.field_149600_a);
      buf.func_180714_a(this.field_149598_b);
      buf.writeShort(this.field_149599_c);
      buf.func_150787_b(this.field_149597_d.func_150759_c());
   }
}
