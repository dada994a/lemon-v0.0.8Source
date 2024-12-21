package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;
import net.minecraft.util.math.BlockPos;

public class DestroyBlockEvent extends LemonClientEvent {
   private BlockPos blockPos;

   public DestroyBlockEvent(BlockPos blockPos) {
      this.blockPos = blockPos;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   public void setBlockPos(BlockPos blockPos) {
      this.blockPos = blockPos;
   }
}
