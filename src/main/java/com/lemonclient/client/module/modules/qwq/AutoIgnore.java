package com.lemonclient.client.module.modules.qwq;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.server.SPacketChat;

@Module.Declaration(
   name = "AutoIgnore",
   category = Category.qwq
)
public class AutoIgnore extends Module {
   BooleanSetting filterFriend = this.registerBoolean("Filter Friend", false);
   BooleanSetting ignoreAll = this.registerBoolean("AllWhisper", false);
   BooleanSetting playerCheck = this.registerBoolean("PlayerCheck", true);
   IntegerSetting times = this.registerInteger("Times", 10, 0, 30);
   IntegerSetting life = this.registerInteger("LifeTime", 600, 0, 3000);
   HashMap<String, Integer> messageTimes = new HashMap();
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if (mc.field_71439_g != null) {
         if (event.getPacket() instanceof SPacketChat) {
            String message = ((SPacketChat)event.getPacket()).func_148915_c().func_150260_c();
            String username;
            if ((Boolean)this.ignoreAll.getValue() && message.contains(":")) {
               username = "";
               int spaceIndexx = message.indexOf(" ");
               if (spaceIndexx != -1) {
                  username = message.substring(0, spaceIndexx);
               }

               if (!username.isEmpty() && !SocialManager.isOnIgnoreList(username) && !SocialManager.isOnFriendList(username) || !(Boolean)this.filterFriend.getValue()) {
                  SocialManager.addIgnore(username);
                  MessageBus.sendClientDeleteMessage(username + " has been added to ignore list", Notification.Type.INFO, "AutoIgnore", 13);
               }
            }

            username = message.replaceAll("\\[.*?]|<.*?>|\\d+", "");
            this.addToList(username);
            if ((Integer)this.messageTimes.get(username) > (Integer)this.times.getValue()) {
               Matcher matcher = Pattern.compile("<.*?> ").matcher(message);
               String usernamex = "";
               if (matcher.find()) {
                  usernamex = matcher.group();
                  usernamex = usernamex.substring(1, usernamex.length() - 2);
               } else if (message.contains(":")) {
                  int spaceIndex = message.indexOf(" ");
                  if (spaceIndex != -1) {
                     usernamex = message.substring(0, spaceIndex);
                  }
               }

               usernamex = ColorMain.cleanColor(usernamex);
               if (usernamex.equals(mc.field_71439_g.func_70005_c_()) || (Boolean)this.playerCheck.getValue() && mc.field_71439_g.field_71174_a.func_175104_a(usernamex) == null) {
                  return;
               }

               if (!usernamex.isEmpty() && !SocialManager.isOnIgnoreList(usernamex) && !SocialManager.isOnFriendList(usernamex) || !(Boolean)this.filterFriend.getValue()) {
                  SocialManager.addIgnore(usernamex);
                  MessageBus.sendClientDeleteMessage(usernamex + " has been added to ignore list", Notification.Type.INFO, "AutoIgnore", 13);
               }

               event.cancel();
            }

         }
      }
   }, new Predicate[0]);

   public void addToList(final String string) {
      int time = 1;
      if (this.messageTimes.containsKey(string)) {
         time += (Integer)this.messageTimes.get(string);
      }

      this.messageTimes.put(string, time);
      (new Timer()).schedule(new TimerTask() {
         public void run() {
            AutoIgnore.this.messageTimes.put(string, (Integer)AutoIgnore.this.messageTimes.get(string) - 1);
         }
      }, (long)((Integer)this.life.getValue() * 1000));
   }
}
