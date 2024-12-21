package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;
import net.minecraft.entity.Entity;

public class EntityRemovedEvent extends LemonClientEvent {
   private final Entity entity;

   public EntityRemovedEvent(Entity entity) {
      this.entity = entity;
   }

   public Entity getEntity() {
      return this.entity;
   }
}
