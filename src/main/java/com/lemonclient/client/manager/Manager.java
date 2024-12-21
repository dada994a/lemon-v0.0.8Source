package com.lemonclient.client.manager;

import me.zero.alpine.listener.Listenable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.profiler.Profiler;

public interface Manager extends Listenable {
   default Minecraft getMinecraft() {
      return Minecraft.func_71410_x();
   }

   default EntityPlayerSP getPlayer() {
      return this.getMinecraft().field_71439_g;
   }

   default WorldClient getWorld() {
      return this.getMinecraft().field_71441_e;
   }

   default Profiler getProfiler() {
      return this.getMinecraft().field_71424_I;
   }
}
