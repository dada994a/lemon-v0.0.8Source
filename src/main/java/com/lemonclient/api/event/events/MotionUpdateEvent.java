package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;

public class MotionUpdateEvent extends LemonClientEvent {
   public int stage;

   public MotionUpdateEvent(int stage) {
      this.stage = stage;
   }
}
