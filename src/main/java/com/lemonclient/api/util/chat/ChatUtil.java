package com.lemonclient.api.util.chat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ChatUtil extends SubscriberImpl {
   public static Minecraft mc = Minecraft.func_71410_x();
   private static final Map<Integer, Map<String, Integer>> message_ids = new ConcurrentHashMap();
   private static final SkippingCounter counter = new SkippingCounter(1337, (i) -> {
      return i != -1;
   });

   public void clear() {
      if (mc.field_71456_v != null) {
         message_ids.values().forEach((m) -> {
            m.values().forEach((id) -> {
               mc.field_71456_v.func_146158_b().func_146242_c(id);
            });
         });
      }

      message_ids.clear();
      counter.reset();
   }

   public static void sendMessage(String message) {
      sendMessage(message, 0);
   }

   public static void sendClientMessage(String append, String modulename) {
      sendDeleteMessage(append, modulename, 1000);
   }

   public static void sendMessage(String message, int id) {
      sendComponent(new TextComponentString(message == null ? "null" : message), id);
   }

   public static void sendComponent(ITextComponent component) {
      sendComponent(component, 0);
   }

   public static void sendComponent(ITextComponent c, int id) {
      applyIfPresent((g) -> {
         g.func_146234_a(c, id);
      });
   }

   public void sendDeleteMessageScheduled(String message, String uniqueWord, int senderID) {
      Integer id = (Integer)((Map)message_ids.computeIfAbsent(senderID, (v) -> {
         return new ConcurrentHashMap();
      })).computeIfAbsent(uniqueWord, (v) -> {
         return counter.next();
      });
      mc.func_152344_a(() -> {
         sendMessage(message, id);
      });
   }

   public static void sendDeleteMessage(String message, String uniqueWord, int senderID) {
      Integer id = (Integer)((Map)message_ids.computeIfAbsent(senderID, (v) -> {
         return new ConcurrentHashMap();
      })).computeIfAbsent(uniqueWord, (v) -> {
         return counter.next();
      });
      sendMessage(message, id);
   }

   public void deleteMessage(String uniqueWord, int senderID) {
      Map<String, Integer> map = (Map)message_ids.get(senderID);
      if (map != null) {
         Integer id = (Integer)map.remove(uniqueWord);
         if (id != null) {
            deleteMessage(id);
         }
      }

   }

   public static void deleteMessage(int id) {
      applyIfPresent((g) -> {
         g.func_146242_c(id);
      });
   }

   public static void applyIfPresent(Consumer<GuiNewChat> consumer) {
      GuiNewChat chat = getChatGui();
      if (chat != null) {
         consumer.accept(chat);
      }

   }

   public static GuiNewChat getChatGui() {
      return mc.field_71456_v != null ? mc.field_71456_v.func_146158_b() : null;
   }
}
