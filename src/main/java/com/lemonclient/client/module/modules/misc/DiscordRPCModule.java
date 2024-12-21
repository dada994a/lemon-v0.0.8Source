package com.lemonclient.client.module.modules.misc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

@Module.Declaration(
   name = "DiscordRPC",
   category = Category.Misc
)
public class DiscordRPCModule extends Module {
   private static final String applicationId = "899193061324775454";
   BooleanSetting PlayerID = this.registerBoolean("Player ID", true);
   BooleanSetting ServerIp = this.registerBoolean("Server IP", true);
   BooleanSetting coords = this.registerBoolean("Coords", true);
   private final DiscordRPC discordRPC;
   DiscordEventHandlers handlers;
   DiscordRichPresence presence;
   static String lastChat;
   static ServerData svr;
   @EventHandler
   private final Listener<ClientChatReceivedEvent> listener;

   public DiscordRPCModule() {
      this.discordRPC = DiscordRPC.INSTANCE;
      this.handlers = new DiscordEventHandlers();
      this.presence = new DiscordRichPresence();
      this.listener = new Listener((event) -> {
         lastChat = event.getMessage().func_150260_c();
      }, new Predicate[0]);
   }

   public void onEnable() {
      this.init();
   }

   public void onDisable() {
      this.discordRPC.Discord_Shutdown();
      this.discordRPC.Discord_ClearPresence();
   }

   private void init() {
      this.discordRPC.Discord_Initialize("899193061324775454", this.handlers, true, "");
      this.presence.startTimestamp = System.currentTimeMillis() / 1000L;
      this.presence.state = "Main Menu";
      if ((Boolean)this.PlayerID.getValue()) {
         this.presence.details = ID();
      } else {
         this.presence.details = "";
      }

      this.presence.largeImageKey = "lemonclient";
      this.presence.largeImageText = "Lemon Client v0.0.8";
      this.discordRPC.Discord_UpdatePresence(this.presence);
      (new Thread(() -> {
         while(!Thread.currentThread().isInterrupted() && this.isEnabled()) {
            try {
               this.discordRPC.Discord_RunCallbacks();
               if ((Boolean)this.PlayerID.getValue()) {
                  this.presence.details = ID();
               } else {
                  this.presence.details = "";
               }

               this.presence.state = "";
               if ((Boolean)this.coords.getValue() && mc.field_71439_g != null && mc.field_71441_e != null) {
                  this.presence.smallImageKey = "lazy_crocodile";
                  String dimension;
                  if (this.dimension() == -1) {
                     dimension = "Nether";
                  } else if (this.dimension() == 0) {
                     dimension = "Overworld";
                  } else {
                     dimension = "The End";
                  }

                  this.presence.smallImageText = "X:" + (int)mc.field_71439_g.field_70165_t + " Y:" + (int)mc.field_71439_g.field_70163_u + " Z:" + (int)mc.field_71439_g.field_70161_v + " (" + dimension + ")";
               } else {
                  this.presence.smallImageText = "";
               }

               if (mc.func_71387_A()) {
                  this.presence.state = "Single Player";
               } else if (mc.func_147104_D() != null) {
                  svr = mc.func_147104_D();
                  if (!svr.field_78845_b.equals("")) {
                     if ((Boolean)this.ServerIp.getValue()) {
                        this.presence.state = "Multi Player (" + svr.field_78845_b + ")";
                        if (svr.field_78845_b.equals("2b2t.org")) {
                           try {
                              if (lastChat.contains("Position in queue: ")) {
                                 this.presence.details = this.presence.details + " (in queue" + Integer.parseInt(lastChat.substring(19)) + ")";
                              }
                           } catch (Throwable var3) {
                              var3.printStackTrace();
                           }
                        }
                     } else {
                        this.presence.state = "Multi Player";
                     }
                  }
               } else {
                  this.presence.details = "Main Menu";
               }

               this.discordRPC.Discord_UpdatePresence(this.presence);
            } catch (Exception var4) {
               var4.printStackTrace();
            }

            try {
               Thread.sleep(5000L);
            } catch (InterruptedException var2) {
               var2.printStackTrace();
            }
         }

      }, "Discord-RPC-Callback-Handler")).start();
   }

   private int dimension() {
      return mc.field_71439_g.field_71093_bK;
   }

   public static String ID() {
      return mc.field_71439_g != null ? mc.field_71439_g.func_70005_c_() : mc.func_110432_I().func_111285_a();
   }
}
