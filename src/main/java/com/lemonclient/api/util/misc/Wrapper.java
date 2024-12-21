package com.lemonclient.api.util.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;

public class Wrapper {
   public static EntityPlayerSP getPlayer() {
      EntityPlayerSP player = Minecraft.func_71410_x().field_71439_g;
      return player;
   }

   public static Minecraft getMinecraft() {
      Minecraft minecraft = Minecraft.func_71410_x();
      return minecraft;
   }

   public static World getWorld() {
      World world = Minecraft.func_71410_x().field_71441_e;
      return world;
   }
}
