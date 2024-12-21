package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;

public class ReachDistanceEvent extends LemonClientEvent {
   private float distance;

   public ReachDistanceEvent(float distance) {
      this.distance = distance;
   }

   public float getDistance() {
      return this.distance;
   }

   public void setDistance(float distance) {
      this.distance = distance;
   }
}
