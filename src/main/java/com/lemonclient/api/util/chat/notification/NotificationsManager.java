package com.lemonclient.api.util.chat.notification;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class NotificationsManager {
   public static final LinkedBlockingQueue<Notification> pendingNotifications = new LinkedBlockingQueue();
   private static Notification currentNotification = null;
   public static ArrayList<Notifications> notifications = new ArrayList();

   public static void show(Notification notification) {
      pendingNotifications.add(notification);
   }

   public static void show(Notifications notification) {
      notifications.add(notification);
   }

   public static void update() {
      if (currentNotification != null && !currentNotification.isShown()) {
         currentNotification = null;
      }

      if (currentNotification == null && !pendingNotifications.isEmpty()) {
         (currentNotification = (Notification)pendingNotifications.poll()).show();
      }

   }

   public static void render() {
      try {
         int divider = Minecraft.func_71410_x().field_71474_y.field_74335_Z;
         int width = Minecraft.func_71410_x().field_71443_c / divider;
         int height = Minecraft.func_71410_x().field_71440_d / divider;
         update();
         if (currentNotification != null) {
            currentNotification.render(width, height);
         }
      } catch (Exception var3) {
      }

   }

   public static void drawNotifications() {
      try {
         ScaledResolution res = new ScaledResolution(Minecraft.func_71410_x());
         double lastY;
         double startY = lastY = (double)(res.func_78328_b() - 25);

         for(int i = 0; i < notifications.size(); ++i) {
            Notifications not = (Notifications)notifications.get(i);
            int number;
            if (not.shouldDelete()) {
               notifications.remove(not);

               for(number = 0; (double)number > not.width; --number) {
                  not.animationX = (double)number - not.width;
               }

               startY += not.getHeight() + 3.0D;
            }

            not.draw(startY, lastY);

            for(number = 0; (double)number < not.width; ++number) {
               not.animationX = (double)number + not.width;
            }

            startY -= not.getHeight() + 2.0D;
         }
      } catch (Throwable var8) {
      }

   }
}
