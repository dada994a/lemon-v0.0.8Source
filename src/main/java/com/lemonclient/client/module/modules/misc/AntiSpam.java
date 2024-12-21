package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.misc.MultiThreading;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.server.SPacketChat;

@Module.Declaration(
   name = "AntiSpam",
   category = Category.Misc
)
public class AntiSpam extends Module {
   BooleanSetting greenText = this.registerBoolean("Green Text", true);
   BooleanSetting discordLinks = this.registerBoolean("Discord Link", true);
   BooleanSetting webLinks = this.registerBoolean("Web Link", true);
   BooleanSetting announcers = this.registerBoolean("Announcer", true);
   BooleanSetting spammers = this.registerBoolean("Spammer", true);
   BooleanSetting insulter = this.registerBoolean("Insulter", true);
   BooleanSetting greeters = this.registerBoolean("Greeter", true);
   BooleanSetting tradeChat = this.registerBoolean("Trade Chat", true);
   BooleanSetting ips = this.registerBoolean("Server Ip", true);
   BooleanSetting ipsAgr = this.registerBoolean("Ip Aggressive", true);
   BooleanSetting numberSuffix = this.registerBoolean("Number Suffix", true);
   BooleanSetting duplicates = this.registerBoolean("Duplicates", true);
   IntegerSetting duplicatesTimeout = this.registerInteger("Duplicates Timeout", 30, 1, 600, () -> {
      return (Boolean)this.duplicates.getValue();
   });
   BooleanSetting filterFriend = this.registerBoolean("Filter Friend", false);
   BooleanSetting showBlocked = this.registerBoolean("Show Blocked", false);
   BooleanSetting autoIgnore = this.registerBoolean("Auto Ignore", true);
   IntegerSetting ignoreDuration = this.registerInteger("Ignore Duration", 120, 0, 43200, () -> {
      return (Boolean)this.autoIgnore.getValue();
   });
   IntegerSetting violations = this.registerInteger("Violations", 3, 1, 100, () -> {
      return (Boolean)this.autoIgnore.getValue();
   });
   private final Pattern CHAT_PATTERN = Pattern.compile("<.*?> ");
   private ConcurrentHashMap<String, Long> messageHistory;
   public List<String> ignoredBySpamCheck = new ArrayList();
   public Map<String, Integer> violate = new ConcurrentHashMap();
   public List<String> ignoredList = new ArrayList();
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if (mc.field_71439_g != null && this.isEnabled()) {
         if (event.getPacket() instanceof SPacketChat) {
            String s = ((SPacketChat)event.getPacket()).func_148915_c().func_150260_c();
            Matcher matcher = this.CHAT_PATTERN.matcher(s);
            String username = "null";
            if (matcher.find()) {
               username = matcher.group();
               username = username.substring(1, username.length() - 2);
            } else if (s.contains(":")) {
               int spaceIndex = s.indexOf(" ");
               if (spaceIndex != -1) {
                  username = s.substring(0, spaceIndex);
               }
            }

            username = cleanColor(username);
            if (!username.equals("null") && mc.field_71439_g.field_71174_a.func_175104_a(username) != null && !SocialManager.isIgnore(username) && (!(Boolean)this.filterFriend.getValue() || !SocialManager.isOnFriendList(username))) {
               SPacketChat sPacketChat = (SPacketChat)event.getPacket();
               if (this.detectSpam(sPacketChat.func_148915_c().func_150260_c()) && !username.equalsIgnoreCase(mc.field_71439_g.func_70005_c_())) {
                  if ((Boolean)this.autoIgnore.getValue()) {
                     if ((this.violate.get(username) == null || (Integer)this.violate.get(username) < (Integer)this.violations.getValue()) && (Integer)this.violations.getValue() != 0) {
                        if (this.violate.get(username) == null) {
                           this.violate.put(username, 1);
                        } else {
                           this.violate.put(username, (Integer)this.violate.get(username) + 1);
                        }
                     } else if (!SocialManager.isIgnore(username)) {
                        MessageBus.sendMessage(ChatFormatting.RED + username + " has exceeded the limitation of spam violation, ignoring.", Notification.Type.INFO, "AntiSpam", 13, false);
                        MultiThreading.runAsync(() -> {
                           this.startIgnore(username);
                        });
                        this.violate.remove(username);
                     }
                  }

                  event.cancel();
               }

            }
         }
      }
   }, new Predicate[0]);

   public static String cleanColor(String input) {
      return input.replaceAll("(?i)\\u00A7.", "");
   }

   public void startIgnore(final String finalUsername) {
      if (!mc.field_71439_g.func_70005_c_().equalsIgnoreCase(finalUsername)) {
         if (!SocialManager.isIgnore(finalUsername)) {
            SocialManager.addIgnore(finalUsername);
            this.ignoredBySpamCheck.add(finalUsername);
            this.ignoredList.add(finalUsername);
            MessageBus.sendMessage(ChatFormatting.RED + finalUsername + " has been auto ignored by AntiSpam for " + this.ignoreDuration.getValue() + ((Integer)this.ignoreDuration.getValue() > 1 ? " seconds" : " second"), Notification.Type.INFO, "AntiSpam", 13, false);
            (new Timer()).schedule(new TimerTask() {
               public void run() {
                  AntiSpam.this.ignoredList.remove(finalUsername);
                  if (SocialManager.isIgnore(finalUsername)) {
                     SocialManager.delIgnore(finalUsername);
                  }
               }
            }, (long)((Integer)this.ignoreDuration.getValue() * 1000));
         }
      }
   }

   public void onEnable() {
      this.messageHistory = new ConcurrentHashMap();
   }

   public void onDisable() {
      this.messageHistory = null;
   }

   private boolean detectSpam(String message) {
      if ((Boolean)this.greenText.getValue() && this.findPatterns(AntiSpam.FilterPatterns.GREEN_TEXT, message)) {
         if ((Boolean)this.showBlocked.getValue()) {
            MessageBus.sendMessage("[AntiSpam] Green Text: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
         }

         return true;
      } else if ((Boolean)this.discordLinks.getValue() && this.findPatterns(AntiSpam.FilterPatterns.DISCORD, message)) {
         if ((Boolean)this.showBlocked.getValue()) {
            MessageBus.sendMessage("[AntiSpam] Discord Link: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
         }

         return true;
      } else if ((Boolean)this.webLinks.getValue() && this.findPatterns(AntiSpam.FilterPatterns.WEB_LINK, message)) {
         if ((Boolean)this.showBlocked.getValue()) {
            MessageBus.sendMessage("[AntiSpam] Web Link: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
         }

         return true;
      } else if ((Boolean)this.ips.getValue() && this.findPatterns(AntiSpam.FilterPatterns.IP_ADDR, message)) {
         if ((Boolean)this.showBlocked.getValue()) {
            MessageBus.sendMessage("[AntiSpam] IP Address: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
         }

         return true;
      } else if ((Boolean)this.ipsAgr.getValue() && this.findPatterns(AntiSpam.FilterPatterns.IP_ADDR_AGR, message)) {
         if ((Boolean)this.showBlocked.getValue()) {
            MessageBus.sendMessage("[AntiSpam] IP Aggressive: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
         }

         return true;
      } else if ((Boolean)this.tradeChat.getValue() && this.findPatterns(AntiSpam.FilterPatterns.TRADE_CHAT, message)) {
         if ((Boolean)this.showBlocked.getValue()) {
            MessageBus.sendMessage("[AntiSpam] Trade Chat: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
         }

         return true;
      } else if ((Boolean)this.numberSuffix.getValue() && this.findPatterns(AntiSpam.FilterPatterns.NUMBER_SUFFIX, message)) {
         if ((Boolean)this.showBlocked.getValue()) {
            MessageBus.sendMessage("[AntiSpam] Number Suffix: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
         }

         return true;
      } else if ((Boolean)this.announcers.getValue() && this.findPatterns(AntiSpam.FilterPatterns.ANNOUNCER, message)) {
         if ((Boolean)this.showBlocked.getValue()) {
            MessageBus.sendMessage("[AntiSpam] Announcer: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
         }

         return true;
      } else if ((Boolean)this.spammers.getValue() && this.findPatterns(AntiSpam.FilterPatterns.SPAMMER, message)) {
         if ((Boolean)this.showBlocked.getValue()) {
            MessageBus.sendMessage("[AntiSpam] Spammers: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
         }

         return true;
      } else if ((Boolean)this.insulter.getValue() && this.findPatterns(AntiSpam.FilterPatterns.INSULTER, message)) {
         if ((Boolean)this.showBlocked.getValue()) {
            MessageBus.sendMessage("[AntiSpam] Insulter: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
         }

         return true;
      } else if ((Boolean)this.greeters.getValue() && this.findPatterns(AntiSpam.FilterPatterns.GREETER, message)) {
         if ((Boolean)this.showBlocked.getValue()) {
            MessageBus.sendMessage("[AntiSpam] Greeter: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
         }

         return true;
      } else {
         if ((Boolean)this.duplicates.getValue()) {
            if (this.messageHistory == null) {
               this.messageHistory = new ConcurrentHashMap();
            }

            boolean isDuplicate = this.messageHistory.containsKey(message) && (System.currentTimeMillis() - (Long)this.messageHistory.get(message)) / 1000L < (long)(Integer)this.duplicatesTimeout.getValue();
            this.messageHistory.put(message, System.currentTimeMillis());
            if (isDuplicate) {
               if ((Boolean)this.showBlocked.getValue()) {
                  MessageBus.sendMessage("[AntiSpam] Duplicate: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
               }

               return true;
            }
         }

         return false;
      }
   }

   private boolean findPatterns(String[] patterns, String string) {
      String[] var3 = patterns;
      int var4 = patterns.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String pattern = var3[var5];
         if (Pattern.compile(pattern).matcher(string).find()) {
            return true;
         }
      }

      return false;
   }

   private static class FilterPatterns {
      private static final String[] ANNOUNCER = new String[]{"I just walked .+ feet!", "I just placed a .+!", "I just attacked .+ with a .+!", "I just dropped a .+!", "I just opened chat!", "I just opened my console!", "I just opened my GUI!", "I just went into full screen mode!", "I just paused my game!", "I just opened my inventory!", "I just looked at the player list!", "I just took a screen shot!", "I just swaped hands!", "I just ducked!", "I just changed perspectives!", "I just jumped!", "I just ate a .+!", "I just crafted .+ .+!", "I just picked up a .+!", "I just smelted .+ .+!", "I just respawned!", "I just attacked .+ with my hands", "I just broke a .+!", "I recently walked .+ blocks", "I just droped a .+ called, .+!", "I just placed a block called, .+!", "Im currently breaking a block called, .+!", "I just broke a block called, .+!", "I just opened chat!", "I just opened chat and typed a slash!", "I just paused my game!", "I just opened my inventory!", "I just looked at the player list!", "I just changed perspectives, now im in .+!", "I just crouched!", "I just jumped!", "I just attacked a entity called, .+ with a .+", "Im currently eatting a peice of food called, .+!", "Im currently using a item called, .+!", "I just toggled full screen mode!", "I just took a screen shot!", "I just swaped hands and now theres a .+ in my main hand and a .+ in my off hand!", "I just used pick block on a block called, .+!", "Ra just completed his blazing ark", "Its a new day yes it is", "I just placed .+ thanks to (http:\\/\\/)?DotGod\\.CC!", "I just flew .+ meters like a butterfly thanks to (http:\\/\\/)?DotGod\\.CC!"};
      private static final String[] SPAMMER = new String[]{"WWE Client's spammer", "Lol get gud", "Future client is bad", "WWE > Future", "WWE > Impact", "Default Message", "IKnowImEZ is a god", "THEREALWWEFAN231 is a god", "WWE Client made by IKnowImEZ/THEREALWWEFAN231", "WWE Client was the first public client to have Path Finder/New Chunks", "WWE Client was the first public client to have color signs", "WWE Client was the first client to have Teleport Finder", "WWE Client was the first client to have Tunneller & Tunneller Back Fill"};
      private static final String[] INSULTER = new String[]{".+ Download WWE utility mod, Its free!", ".+ 4b4t is da best mintscreft serber", ".+ dont abouse", ".+ you cuck", ".+ https://www.youtube.com/channel/UCJGCNPEjvsCn0FKw3zso0TA", ".+ is my step dad", ".+ again daddy!", "dont worry .+ it happens to every one", ".+ dont buy future it's crap, compared to WWE!", "What are you, fucking gay, .+?", "Did you know? .+ hates you, .+", "You are literally 10, .+", ".+ finally lost their virginity, sadly they lost it to .+... yeah, that's unfortunate.", ".+, don't be upset, it's not like anyone cares about you, fag.", ".+, see that rubbish bin over there? Get your ass in it, or I'll get .+ to whoop your ass.", ".+, may I borrow that dirt block? that guy named .+ needs it...", "Yo, .+, btfo you virgin", "Hey .+ want to play some High School RP with me and .+?", ".+ is an Archon player. Why is he on here? Fucking factions player.", "Did you know? .+ just joined The Vortex Coalition!", ".+ has successfully conducted the cactus dupe and duped a itemhand!", ".+, are you even human? You act like my dog, holy shit.", ".+, you were never loved by your family.", "Come on .+, you hurt .+'s feelings. You meany.", "Stop trying to meme .+, you can't do that. kek", ".+, .+ is gay. Don't go near him.", "Whoa .+ didn't mean to offend you, .+.", ".+ im not pvping .+, im WWE'ing .+.", "Did you know? .+ just joined The Vortex Coalition!", ".+, are you even human? You act like my dog, holy shit."};
      private static final String[] GREETER = new String[]{"Bye, Bye .+", "Farwell, .+"};
      private static final String[] DISCORD = new String[]{"discord.gg", "discordapp.com", "discord.io", "invite.gg"};
      private static final String[] NUMBER_SUFFIX = new String[]{".+\\d{3,}$"};
      private static final String[] GREEN_TEXT = new String[]{"^<.+> >"};
      private static final String[] TRADE_CHAT = new String[]{"buy", "sell"};
      private static final String[] WEB_LINK = new String[]{"http:\\/\\/", "https:\\/\\/", "www."};
      private static final String[] IP_ADDR = new String[]{"\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\:\\d{1,5}\\b", "\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", "^(?:http(?:s)?:\\/\\/)?(?:[^\\.]+\\.)?.*\\..*\\..*$", ".*\\..*\\:\\d{1,5}$"};
      private static final String[] IP_ADDR_AGR = new String[]{".*\\..*$"};
   }
}
