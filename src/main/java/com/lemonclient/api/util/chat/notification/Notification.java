package com.lemonclient.api.util.chat.notification;

import java.awt.Color;

public abstract class Notification {
   protected final NotificationType type;
   protected final String title;
   protected final String message;
   protected long start;
   protected final long fadedIn;
   protected final long fadeOut;
   protected final long end;

   public Notification(NotificationType type, String title, String message, int length) {
      this.type = type;
      this.title = title;
      this.message = message;
      this.fadedIn = 100L * (long)length;
      this.fadeOut = this.fadedIn + 150L * (long)length;
      this.end = this.fadeOut + this.fadedIn;
   }

   public void show() {
      this.start = System.currentTimeMillis();
   }

   public boolean isShown() {
      return this.getTime() <= this.end;
   }

   protected long getTime() {
      return System.currentTimeMillis() - this.start;
   }

   protected int getOffset(double maxWidth) {
      if (this.getTime() < this.fadedIn) {
         return (int)(Math.tanh((double)this.getTime() / (double)this.fadedIn * 3.0D) * maxWidth);
      } else {
         return this.getTime() > this.fadeOut ? (int)(Math.tanh(3.0D - (double)(this.getTime() - this.fadeOut) / (double)(this.end - this.fadeOut) * 3.0D) * maxWidth) : (int)maxWidth;
      }
   }

   protected Color getDefaultTypeColor() {
      if (this.type == NotificationType.INFO) {
         return Color.BLUE;
      } else if (this.type == NotificationType.WARNING) {
         return new Color(218, 165, 32);
      } else if (this.type == NotificationType.LOAD) {
         return new Color(255, 255, 150);
      } else {
         return this.type == NotificationType.WELCOME ? new Color(255, 255, 75) : Color.RED;
      }
   }

   public abstract void render(int var1, int var2);
}
