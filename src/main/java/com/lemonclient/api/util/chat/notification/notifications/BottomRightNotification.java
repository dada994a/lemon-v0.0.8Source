package com.lemonclient.api.util.chat.notification.notifications;

import com.lemonclient.api.util.chat.notification.Notification;
import com.lemonclient.api.util.chat.notification.NotificationType;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public class BottomRightNotification extends Notification {
   public BottomRightNotification(NotificationType type, String title, String message, int length) {
      super(type, title, message, length);
   }

   public void render(int RealDisplayWidth, int RealDisplayHeight) {
      int width = true;
      int height = true;
      int offset = this.getOffset(120.0D);
      Color color = new Color(0, 0, 0, 220);
      Color color2;
      if (this.type == NotificationType.INFO) {
         color2 = new Color(0, 26, 169);
      } else if (this.type == NotificationType.WARNING) {
         color2 = new Color(204, 193, 0);
      } else if (this.type == NotificationType.WELCOME) {
         color2 = new Color(255, 255, 75);
      } else if (this.type == NotificationType.LOAD) {
         color2 = new Color(255, 255, 150);
      } else {
         color2 = new Color(204, 0, 18);
         int i = Math.max(0, Math.min(255, (int)(Math.sin((double)this.getTime() / 100.0D) * 255.0D / 2.0D + 127.5D)));
         color = new Color(i, 0, 0, 220);
      }

      FontRenderer fontRenderer = Minecraft.func_71410_x().field_71466_p;
      Gui.func_73734_a(RealDisplayWidth - offset, RealDisplayHeight - 5 - 30, RealDisplayWidth, RealDisplayHeight - 5, color.getRGB());
      Gui.func_73734_a(RealDisplayWidth - offset, RealDisplayHeight - 5 - 30, RealDisplayWidth - offset + 4, RealDisplayHeight - 5, color2.getRGB());
      fontRenderer.func_78276_b(this.title, RealDisplayWidth - offset + 8, RealDisplayHeight - 2 - 30, -1);
      fontRenderer.func_78276_b(this.message, RealDisplayWidth - offset + 8, RealDisplayHeight - 15, -1);
   }
}
