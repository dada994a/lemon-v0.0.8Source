package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;
import net.minecraft.entity.Entity;

public class RenderEntityEvent extends LemonClientEvent {
   private final Entity entity;
   private final RenderEntityEvent.Type type;

   public RenderEntityEvent(Entity entity, RenderEntityEvent.Type type) {
      this.entity = entity;
      this.type = type;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public RenderEntityEvent.Type getType() {
      return this.type;
   }

   public static class Return extends RenderEntityEvent {
      public Return(Entity entity, RenderEntityEvent.Type type) {
         super(entity, type);
      }
   }

   public static class Head extends RenderEntityEvent {
      public Head(Entity entity, RenderEntityEvent.Type type) {
         super(entity, type);
      }
   }

   public static enum Type {
      TEXTURE,
      COLOR;
   }
}
