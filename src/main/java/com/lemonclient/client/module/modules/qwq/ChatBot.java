package com.lemonclient.client.module.modules.qwq;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.misc.ServerUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketChat;

@Module.Declaration(
   name = "ChatBot",
   category = Category.qwq
)
public class ChatBot extends Module {
   ModeSetting mode = this.registerMode("Mode", Arrays.asList("Client", "Everyone"), "Everyone");
   IntegerSetting delay = this.registerInteger("Delay", 0, 0, 20, () -> {
      return ((String)this.mode.getValue()).equals("Everyone");
   });
   String botmessage;
   boolean msg;
   int waited;
   private final Pattern CHAT_PATTERN = Pattern.compile("<.*?> ");
   private final Pattern CHAT_PATTERN2 = Pattern.compile("(.*?)");
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if (!this.msg) {
         if (event.getPacket() instanceof SPacketChat) {
            String s = ((SPacketChat)event.getPacket()).func_148915_c().func_150260_c();
            Matcher matcher = this.CHAT_PATTERN.matcher(s);
            String username = "unnamed";
            Matcher matcher2 = this.CHAT_PATTERN2.matcher(s);
            if (matcher2.find()) {
               matcher2.group();
               s = matcher2.replaceFirst("");
            }

            if (matcher.find()) {
               username = matcher.group();
               username = username.substring(1, username.length() - 2);
               s = matcher.replaceFirst("");
            }

            if (!s.startsWith("!")) {
               return;
            }

            s = s.substring(Math.min(s.length(), 1));
            if (s.startsWith("online")) {
               return;
            }

            ArrayList infoMap;
            Iterator var7;
            Entity qwq;
            String name;
            String messageSanitizedx;
            NetworkPlayerInfo profile;
            if (s.startsWith("ping")) {
               s = s.substring(Math.min(s.length(), 5));
               infoMap = new ArrayList(Minecraft.func_71410_x().func_147114_u().func_175106_d());
               var7 = mc.field_71441_e.field_72996_f.iterator();

               while(var7.hasNext()) {
                  qwq = (Entity)var7.next();
                  if (qwq instanceof EntityPlayer && s.contains(qwq.func_70005_c_())) {
                     s = qwq.func_70005_c_();
                  }
               }

               profile = (NetworkPlayerInfo)infoMap.stream().filter((networkPlayerInfo) -> {
                  return s.toLowerCase().contains(networkPlayerInfo.func_178845_a().getName().toLowerCase());
               }).findFirst().orElse((Object)null);
               if (profile != null) {
                  name = profile.func_178845_a().getName() + "'s ping is " + profile.func_178853_c();
                  messageSanitizedx = name.replaceAll("禮", "");
                  if (messageSanitizedx.length() > 255) {
                     messageSanitizedx = messageSanitizedx.substring(0, 255);
                  }

                  this.botmessage = messageSanitizedx;
                  this.msg = true;
               }
            } else if (s.startsWith("myping")) {
               infoMap = new ArrayList(Minecraft.func_71410_x().func_147114_u().func_175106_d());
               profile = (NetworkPlayerInfo)infoMap.stream().filter((networkPlayerInfo) -> {
                  return networkPlayerInfo.func_178845_a().getName().equalsIgnoreCase(username);
               }).findFirst().orElse((Object)null);
               if (profile != null) {
                  name = "Your ping is " + profile.func_178853_c();
                  messageSanitizedx = name.replaceAll("禮", "");
                  if (messageSanitizedx.length() > 255) {
                     messageSanitizedx = messageSanitizedx.substring(0, 255);
                  }

                  this.botmessage = messageSanitizedx;
                  this.msg = true;
               }
            } else {
               String uwu;
               String messageSanitized;
               if (s.startsWith("tps")) {
                  uwu = "The tps is now " + ServerUtil.getTPS();
                  messageSanitized = uwu.replaceAll("禮", "");
                  if (messageSanitized.length() > 255) {
                     messageSanitized = messageSanitized.substring(0, 255);
                  }

                  this.botmessage = messageSanitized;
                  this.msg = true;
               } else if (s.startsWith("help")) {
                  uwu = "The commands are : tps, myping, ping playername";
                  messageSanitized = uwu.replaceAll("禮", "");
                  if (messageSanitized.length() > 255) {
                     messageSanitized = messageSanitized.substring(0, 255);
                  }

                  this.botmessage = messageSanitized;
                  this.msg = true;
               } else if (s.startsWith("gay")) {
                  s = s.substring(Math.min(s.length(), 4));
                  infoMap = new ArrayList(Minecraft.func_71410_x().func_147114_u().func_175106_d());
                  var7 = mc.field_71441_e.field_72996_f.iterator();

                  while(var7.hasNext()) {
                     qwq = (Entity)var7.next();
                     if (qwq instanceof EntityPlayer && s.contains(qwq.func_70005_c_())) {
                        s = qwq.func_70005_c_();
                     }
                  }

                  profile = (NetworkPlayerInfo)infoMap.stream().filter((networkPlayerInfo) -> {
                     return s.toLowerCase().contains(networkPlayerInfo.func_178845_a().getName().toLowerCase());
                  }).findFirst().orElse((Object)null);
                  if (profile != null) {
                     name = profile.func_178845_a().getName();
                     this.botmessage = name + " is " + String.format("%.1f", Math.random() * 100.0D) + "% gay";
                     this.msg = true;
                  }
               } else if (s.startsWith("byebyebot")) {
                  this.botmessage = "!online owob";
                  this.msg = true;
               } else {
                  uwu = "Sorry, I cant understand this command";
                  messageSanitized = uwu.replaceAll("禮", "");
                  if (messageSanitized.length() > 255) {
                     messageSanitized = messageSanitized.substring(0, 255);
                  }

                  this.botmessage = messageSanitized;
                  this.msg = true;
               }
            }
         }

      }
   }, new Predicate[0]);

   public void onUpdate() {
      if (this.msg) {
         if (((String)this.mode.getValue()).equals("Client")) {
            MessageBus.sendClientDeleteMessage(this.botmessage, Notification.Type.INFO, "ChatBot", 4);
            this.msg = false;
         } else if (this.waited++ >= (Integer)this.delay.getValue()) {
            MessageBus.sendServerMessage(this.botmessage);
            this.waited = 0;
            this.msg = false;
         }
      }

   }
}
