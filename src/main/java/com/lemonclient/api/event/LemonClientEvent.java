package com.lemonclient.api.event;

import me.zero.alpine.event.type.Cancellable;
import net.minecraft.client.Minecraft;

public class LemonClientEvent extends Cancellable {
   private final LemonClientEvent.Era era;
   private final float partialTicks;

   public LemonClientEvent() {
      this.era = LemonClientEvent.Era.PRE;
      this.partialTicks = Minecraft.func_71410_x().func_184121_ak();
   }

   public LemonClientEvent.Era getEra() {
      return this.era;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }

   public static enum Era {
      PRE,
      PERI,
      POST;
   }
}
