package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;
import com.lemonclient.api.event.MultiPhase;
import com.lemonclient.api.event.Phase;
import com.lemonclient.api.util.misc.EnumUtils;
import com.lemonclient.api.util.player.PlayerPacket;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class OnUpdateWalkingPlayerEvent extends LemonClientEvent implements MultiPhase<OnUpdateWalkingPlayerEvent> {
   private final Phase phase;
   private boolean moving;
   private boolean rotating;
   private Vec3d position;
   private Vec2f rotation;

   public OnUpdateWalkingPlayerEvent(Vec3d position, Vec2f rotation) {
      this(position, rotation, Phase.PRE);
   }

   private OnUpdateWalkingPlayerEvent(Vec3d position, Vec2f rotation, Phase phase) {
      this.moving = false;
      this.rotating = false;
      this.position = position;
      this.rotation = rotation;
      this.phase = phase;
   }

   public OnUpdateWalkingPlayerEvent nextPhase() {
      return new OnUpdateWalkingPlayerEvent(this.position, this.rotation, (Phase)EnumUtils.next(this.phase));
   }

   public void apply(PlayerPacket packet) {
      Vec3d position = packet.getPosition();
      Vec2f rotation = packet.getRotation();
      if (position != null) {
         this.moving = true;
         this.position = position;
      }

      if (rotation != null) {
         this.rotating = true;
         this.rotation = rotation;
      }

   }

   public boolean isMoving() {
      return this.moving;
   }

   public boolean isRotating() {
      return this.rotating;
   }

   public Vec3d getPosition() {
      return this.position;
   }

   public Vec2f getRotation() {
      return this.rotation;
   }

   public Phase getPhase() {
      return this.phase;
   }
}
