package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;

public class SendMessageEvent extends LemonClientEvent {
   final String message;

   public SendMessageEvent(String message) {
      this.message = message;
   }

   public String getMessage() {
      return this.message;
   }
}
