package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;

public class Render3DEvent extends LemonClientEvent {
   private final float partialTicks;

   public Render3DEvent(float partialTicks) {
      this.partialTicks = partialTicks;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }
}
