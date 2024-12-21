package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;

public class PlayerLeaveEvent extends LemonClientEvent {
   private final String name;

   public PlayerLeaveEvent(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }
}
