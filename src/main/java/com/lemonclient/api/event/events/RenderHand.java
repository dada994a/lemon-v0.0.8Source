package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;

public class RenderHand extends LemonClientEvent {
   private final float ticks;

   public RenderHand(float ticks) {
      this.ticks = ticks;
   }

   public float getPartialTicks() {
      return this.ticks;
   }

   public static class PreBoth extends RenderHand {
      public PreBoth(float ticks) {
         super(ticks);
      }
   }

   public static class PostBoth extends RenderHand {
      public PostBoth(float ticks) {
         super(ticks);
      }
   }

   public static class PreFill extends RenderHand {
      public PreFill(float ticks) {
         super(ticks);
      }
   }

   public static class PostFill extends RenderHand {
      public PostFill(float ticks) {
         super(ticks);
      }
   }

   public static class PreOutline extends RenderHand {
      public PreOutline(float ticks) {
         super(ticks);
      }
   }

   public static class PostOutline extends RenderHand {
      public PostOutline(float ticks) {
         super(ticks);
      }
   }
}
