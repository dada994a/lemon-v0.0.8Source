package com.lemonclient.api.util.misc;

import com.lemonclient.api.util.chat.ChatUtil;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.chat.NotificationManager;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.lemonclient.client.module.modules.hud.Notifications;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class MessageBus {
   public static String watermark;
   public static ChatFormatting messageFormatting;
   protected static final Minecraft mc;

   public static void printDebug(String text, Boolean error) {
      ColorMain colorMain = (ColorMain)ModuleManager.getModule(ColorMain.class);
      sendClientPrefixMessage((error ? colorMain.getDisabledColor() : colorMain.getEnabledColor()) + text, error ? Notification.Type.ERROR : Notification.Type.INFO);
   }

   public static void sendClientPrefixMessage(String message, Notification.Type type) {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         TextComponentString string1 = new TextComponentString(watermark + messageFormatting + message);
         Notifications notifications = (Notifications)ModuleManager.getModule(Notifications.class);
         if (notifications.isEnabled()) {
            NotificationManager.add(new Notification(TextFormatting.GRAY + message, type));
            if ((Boolean)notifications.disableChat.getValue()) {
               return;
            }
         }

         mc.field_71439_g.func_145747_a(string1);
      }
   }

   public static void sendMessage(String message, Notification.Type type, String uniqueWord, int senderID, boolean notification) {
      if (notification) {
         sendClientDeleteMessage(message, type, uniqueWord, senderID);
      } else {
         sendDeleteMessage(message, uniqueWord, senderID);
      }

   }

   public static void sendClientDeleteMessage(String message, Notification.Type type, String uniqueWord, int senderID) {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         Notifications notifications = (Notifications)ModuleManager.getModule(Notifications.class);
         if (notifications.isEnabled()) {
            NotificationManager.add(new Notification(TextFormatting.GRAY + message, type));
            if ((Boolean)notifications.disableChat.getValue()) {
               return;
            }
         }

         ChatUtil.sendDeleteMessage(watermark + messageFormatting + message, uniqueWord, senderID);
      }
   }

   public static void sendDeleteMessage(String message, String uniqueWord, int senderID) {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         ChatUtil.sendDeleteMessage(watermark + messageFormatting + message, uniqueWord, senderID);
      }
   }

   public static void sendCommandMessage(String message, boolean prefix) {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         String watermark1 = prefix ? watermark : "";
         ChatUtil.sendDeleteMessage(watermark1 + messageFormatting + message, "Command", 6);
      }
   }

   public static void sendMessage(String message, boolean prefix) {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         String watermark1 = prefix ? watermark : "";
         TextComponentString string = new TextComponentString(watermark1 + messageFormatting + message);
         mc.field_71456_v.func_146158_b().func_146234_a(string, getIdFromString(message));
      }
   }

   public static int getIdFromString(String name) {
      StringBuilder s = new StringBuilder();
      name = name.replace("禮", "e");
      String blacklist = "[^a-z]";

      for(int i = 0; i < name.length(); ++i) {
         s.append(Integer.parseInt(String.valueOf(name.charAt(i)).replaceAll(blacklist, "e"), 36));
      }

      try {
         s = new StringBuilder(s.substring(0, 8));
      } catch (StringIndexOutOfBoundsException var4) {
         s = new StringBuilder(Integer.MAX_VALUE);
      }

      return Integer.MAX_VALUE - Integer.parseInt(s.toString().toLowerCase());
   }

   public static void sendClientRawMessage(String message) {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         TextComponentString string = new TextComponentString(messageFormatting + message);
         mc.field_71439_g.func_145747_a(string);
      }
   }

   public static void sendServerMessage(String message) {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketChatMessage(message));
      }
   }

   static {
      watermark = ChatFormatting.GREEN + "[" + ChatFormatting.YELLOW + "Lemon" + ChatFormatting.GREEN + "] " + ChatFormatting.RESET;
      messageFormatting = ChatFormatting.GRAY;
      mc = Minecraft.func_71410_x();
   }
}
