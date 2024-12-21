package com.lemonclient.api.util.player;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer.Position;

public class PositionUtil {
   Minecraft mc = Minecraft.func_71410_x();
   private double x;
   private double y;
   private double z;
   private boolean onground;

   public void updatePosition() {
      this.x = this.mc.field_71439_g.field_70165_t;
      this.y = this.mc.field_71439_g.field_70163_u;
      this.z = this.mc.field_71439_g.field_70161_v;
      this.onground = this.mc.field_71439_g.field_70122_E;
   }

   public void restorePosition() {
      this.mc.field_71439_g.field_70165_t = this.x;
      this.mc.field_71439_g.field_70163_u = this.y;
      this.mc.field_71439_g.field_70161_v = this.z;
      this.mc.field_71439_g.field_70122_E = this.onground;
   }

   public void setPlayerPosition(double x, double y, double z) {
      this.mc.field_71439_g.field_70165_t = x;
      this.mc.field_71439_g.field_70163_u = y;
      this.mc.field_71439_g.field_70161_v = z;
   }

   public void setPlayerPosition(double x, double y, double z, boolean onground) {
      this.mc.field_71439_g.field_70165_t = x;
      this.mc.field_71439_g.field_70163_u = y;
      this.mc.field_71439_g.field_70161_v = z;
      this.mc.field_71439_g.field_70122_E = onground;
   }

   public void setPositionPacket(double x, double y, double z, boolean onGround, boolean setPos, boolean noLagBack) {
      this.mc.field_71439_g.field_71174_a.func_147297_a(new Position(x, y, z, onGround));
      if (setPos) {
         this.mc.field_71439_g.func_70107_b(x, y, z);
         if (noLagBack) {
            this.updatePosition();
         }
      }

   }

   public double getX() {
      return this.x;
   }

   public void setX(double x) {
      this.x = x;
   }

   public double getY() {
      return this.y;
   }

   public void setY(double y) {
      this.y = y;
   }

   public double getZ() {
      return this.z;
   }

   public void setZ(double z) {
      this.z = z;
   }
}
