package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;

public class RenderEvent extends LemonClientEvent {
   private final float partialTicks;

   public RenderEvent(float partialTicks) {
      this.partialTicks = partialTicks;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }
}
