package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;
import net.minecraft.entity.player.EntityPlayer;

public class DeathEvent extends LemonClientEvent {
   public EntityPlayer player;

   public DeathEvent(EntityPlayer player) {
      this.player = player;
   }
}
