package com.lemonclient.api.util.chat;

import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.hud.Notifications;
import java.util.ArrayList;
import java.util.Iterator;

public class NotificationManager {
   public static ArrayList<Notification> notifications = new ArrayList();

   public static void add(Notification notify) {
      Notifications notification = (Notifications)ModuleManager.getModule(Notifications.class);
      int max = (Integer)notification.max.getValue();
      if (max != 0 && notifications.size() >= max) {
         String var3 = (String)notification.mode.getValue();
         byte var4 = -1;
         switch(var3.hashCode()) {
         case -1850743644:
            if (var3.equals("Remove")) {
               var4 = 0;
            }
            break;
         case 2011110042:
            if (var3.equals("Cancel")) {
               var4 = 1;
            }
         }

         switch(var4) {
         case 0:
            notifications.remove(notifications.get(0));
            break;
         case 1:
            return;
         }
      }

      notify.y = (float)(notifications.size() * 25);
      notifications.add(notify);
   }

   public static void draw() {
      if (!NotificationManager.notifications.isEmpty()) {
         Notification remove = null;

         Notification notify;
         for(Iterator var1 = NotificationManager.notifications.iterator(); var1.hasNext(); notify.onRender()) {
            notify = (Notification)var1.next();
            if (notify.x == 0.0F) {
               notify.in = !notify.in;
            }

            if (Math.abs((double)notify.x - notify.width) < 0.1D && !notify.in) {
               remove = notify;
            }

            Notifications notifications = (Notifications)ModuleManager.getModule(Notifications.class);
            if (notify.in) {
               notify.x = notify.animationUtils.animate(0.0F, notify.x, ((Double)notifications.xSpeed.getValue()).floatValue());
            } else {
               notify.x = (float)notify.animationUtils.animate(notify.width, (double)notify.x, (double)((Double)notifications.xSpeed.getValue()).floatValue());
            }
         }

         if (remove != null) {
            NotificationManager.notifications.remove(remove);
         }

      }
   }
}
