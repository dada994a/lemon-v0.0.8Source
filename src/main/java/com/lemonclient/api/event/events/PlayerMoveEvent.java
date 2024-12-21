package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.MoverType;

public class PlayerMoveEvent extends LemonClientEvent {
   private MoverType type;
   private double x;
   private double y;
   private double z;

   public PlayerMoveEvent(MoverType moverType, double x, double y, double z) {
      this.type = moverType;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public MoverType getType() {
      return this.type;
   }

   public void setType(MoverType type) {
      this.type = type;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public void setX(double x) {
      this.x = x;
   }

   public void setY(double y) {
      this.y = y;
   }

   public void setZ(double z) {
      this.z = z;
   }

   public void setSpeed(double speed) {
      float yaw = Minecraft.func_71410_x().field_71439_g.field_70177_z;
      double forward = (double)Minecraft.func_71410_x().field_71439_g.field_71158_b.field_192832_b;
      double strafe = (double)Minecraft.func_71410_x().field_71439_g.field_71158_b.field_78902_a;
      if (forward == 0.0D && strafe == 0.0D) {
         this.setX(0.0D);
         this.setZ(0.0D);
      } else {
         if (forward != 0.0D) {
            if (strafe > 0.0D) {
               yaw += (float)(forward > 0.0D ? -45 : 45);
            } else if (strafe < 0.0D) {
               yaw += (float)(forward > 0.0D ? 45 : -45);
            }

            strafe = 0.0D;
            if (forward > 0.0D) {
               forward = 1.0D;
            } else {
               forward = -1.0D;
            }
         }

         double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
         double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
         this.setX(forward * speed * cos + strafe * speed * sin);
         this.setZ(forward * speed * sin - strafe * speed * cos);
      }

   }
}
