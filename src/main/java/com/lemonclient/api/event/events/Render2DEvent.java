package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;
import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent extends LemonClientEvent {
   public float partialTicks;
   public ScaledResolution scaledResolution;

   public Render2DEvent(float partialTicks, ScaledResolution scaledResolution) {
      this.partialTicks = partialTicks;
      this.scaledResolution = scaledResolution;
   }

   public void setPartialTicks(float partialTicks) {
      this.partialTicks = partialTicks;
   }

   public void setScaledResolution(ScaledResolution scaledResolution) {
      this.scaledResolution = scaledResolution;
   }

   public double getScreenWidth() {
      return this.scaledResolution.func_78327_c();
   }

   public double getScreenHeight() {
      return this.scaledResolution.func_78324_d();
   }
}
