package com.lemonclient.api.event.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class UpdateEvent extends Event {
   private final UpdateEvent.Stage stage;

   public UpdateEvent(UpdateEvent.Stage stage) {
      this.stage = stage;
   }

   public UpdateEvent.Stage getStage() {
      return this.stage;
   }

   public static enum Stage {
      PRE,
      POST;
   }
}
