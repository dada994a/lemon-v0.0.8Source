package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;
import net.minecraft.util.EnumHandSide;

public class TransformSideFirstPersonEvent extends LemonClientEvent {
   private final EnumHandSide enumHandSide;

   public TransformSideFirstPersonEvent(EnumHandSide enumHandSide) {
      this.enumHandSide = enumHandSide;
   }

   public EnumHandSide getEnumHandSide() {
      return this.enumHandSide;
   }
}
