package com.lemonclient.api.util.chat.notification;

import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Notifications {
   public static String ICON_NOTIFY_INFO = "ℹ";
   public static String ICON_NOTIFY_SUCCESS = "✓";
   public static String ICON_NOTIFY_WARN = "⚠";
   public static String ICON_NOTIFY_ERROR = "⚠";
   public static String ICON_NOTIFY_DISABLED = "✗";
   public Timing timer = new Timing();
   public Notifications.Type t;
   public long stayTime;
   public String message;
   public double lastY;
   public double posY;
   public double width;
   public double height;
   public double animationX;
   public int color;

   public Notifications(String message, Notifications.Type type) {
      this.message = message;
      this.timer.reset();
      this.width = (double)(Minecraft.func_71410_x().field_71466_p.func_78256_a(message) + 35);
      this.height = 20.0D;
      this.animationX = this.width;
      this.stayTime = 1000L;
      this.posY = -1.0D;
      this.t = type;
      if (type.equals(Notifications.Type.INFO)) {
         this.color = -14342875;
      } else if (type.equals(Notifications.Type.ERROR)) {
         this.color = (new Color(36, 36, 36)).getRGB();
      } else if (type.equals(Notifications.Type.SUCCESS)) {
         this.color = (new Color(36, 36, 36)).getRGB();
      } else if (type.equals(Notifications.Type.DISABLE)) {
         this.color = (new Color(36, 36, 36)).getRGB();
      } else if (type.equals(Notifications.Type.WARNING)) {
         this.color = -14342875;
      }

   }

   public static int reAlpha(int color, float alpha) {
      Color c = new Color(color);
      float r = 0.003921569F * (float)c.getRed();
      float g = 0.003921569F * (float)c.getGreen();
      float b = 0.003921569F * (float)c.getBlue();
      return (new Color(r, g, b, alpha)).getRGB();
   }

   public void draw(double getY, double lastY) {
      this.width = (double)(Minecraft.func_71410_x().field_71466_p.func_78256_a(this.message) + 25);
      this.height = 22.0D;
      this.lastY = lastY;
      this.animationX = this.getAnimationState(this.animationX, this.isFinished() ? this.width : 0.0D, 450.0D);
      if (this.posY == -1.0D) {
         this.posY = getY;
      } else {
         this.posY = this.getAnimationState(this.posY, getY, 350.0D);
      }

      ScaledResolution res = new ScaledResolution(Minecraft.func_71410_x());
      int x1 = (int)((double)res.func_78326_a() - this.width + this.animationX / 2.0D);
      int x2 = (int)((double)res.func_78326_a() + this.animationX / 2.0D);
      int y1 = (int)this.posY - 22;
      int y2 = (int)((double)y1 + this.height);
      RenderUtil.drawRect((float)x1, (float)y1, (float)x2, (float)y2, reAlpha(this.color, 0.85F));
      RenderUtil.drawRect((float)x1, (float)(y2 - 1), (float)((long)x1 + Math.min((long)(x2 - x1) * (System.currentTimeMillis() - this.timer.getPassedTimeMs()) / this.stayTime, (long)(x2 - x1))), (float)y2, reAlpha(-1, 0.85F));
      switch(this.t) {
      case ERROR:
         Minecraft.func_71410_x().field_71466_p.func_78276_b(ICON_NOTIFY_ERROR, x1 + 5, y1 + 7, -65794);
         break;
      case INFO:
         Minecraft.func_71410_x().field_71466_p.func_78276_b(ICON_NOTIFY_INFO, x1 + 5, y1 + 7, -65794);
         break;
      case SUCCESS:
         Minecraft.func_71410_x().field_71466_p.func_78276_b(ICON_NOTIFY_SUCCESS, x1 + 5, y1 + 7, -65794);
         break;
      case WARNING:
         Minecraft.func_71410_x().field_71466_p.func_78276_b(ICON_NOTIFY_WARN, x1 + 5, y1 + 7, -65794);
         break;
      case DISABLE:
         Minecraft.func_71410_x().field_71466_p.func_78276_b(ICON_NOTIFY_DISABLED, x1 + 5, y1 + 7, -65794);
      }

      ++y1;
      int var10002;
      int var10003;
      if (this.message.contains(" Enabled")) {
         var10002 = x1 + 19;
         var10003 = (int)((double)y1 + this.height / 4.0D);
         Minecraft.func_71410_x().field_71466_p.func_78276_b(this.message, var10002, var10003, -1);
         Minecraft.func_71410_x().field_71466_p.func_78276_b(" Enabled", x1 + 20 + Minecraft.func_71410_x().field_71466_p.func_78256_a(this.message), (int)((double)y1 + this.height / 4.0D), -9868951);
      } else if (this.message.contains(" Disabled")) {
         var10002 = x1 + 19;
         var10003 = (int)((double)y1 + this.height / 4.0D);
         Minecraft.func_71410_x().field_71466_p.func_78276_b(this.message, var10002, var10003, -1);
         Minecraft.func_71410_x().field_71466_p.func_78276_b(" Disabled", x1 + 20 + Minecraft.func_71410_x().field_71466_p.func_78256_a(this.message), (int)((double)y1 + this.height / 4.0D), -9868951);
      } else {
         var10002 = x1 + 20;
         var10003 = (int)((double)y1 + this.height / 4.0D);
         Minecraft.func_71410_x().field_71466_p.func_78276_b(this.message, var10002, var10003, -1);
      }

   }

   public boolean shouldDelete() {
      return this.isFinished() && this.animationX >= this.width;
   }

   public boolean isFinished() {
      return this.timer.passedMs(this.stayTime) && this.posY == this.lastY;
   }

   public double getHeight() {
      return this.height;
   }

   public double getAnimationState(double animation, double finalState, double speed) {
      float add = (float)((double)Minecraft.func_71410_x().field_71428_T.field_194149_e * speed * speed);
      if (animation < finalState) {
         if (animation + (double)add < finalState) {
            animation += (double)add;
         } else {
            animation = finalState;
         }
      } else if (animation - (double)add > finalState) {
         animation -= (double)add;
      } else {
         animation = finalState;
      }

      return animation;
   }

   public static enum Type {
      SUCCESS,
      INFO,
      WARNING,
      ERROR,
      DISABLE;
   }
}
