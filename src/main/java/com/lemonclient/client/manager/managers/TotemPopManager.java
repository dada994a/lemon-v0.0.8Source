package com.lemonclient.client.manager.managers;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.TotemPopEvent;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.manager.Manager;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.HashMap;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;

public enum TotemPopManager implements Manager {
   INSTANCE;

   public boolean sendMsgs = false;
   public ChatFormatting chatFormatting;
   public ChatFormatting nameFormatting;
   public ChatFormatting friFormatting;
   public ChatFormatting numberFormatting;
   public boolean friend;
   public String self;
   public String type4;
   private final HashMap<String, Integer> playerPopCount;
   @EventHandler
   private final Listener<PacketEvent.Receive> packetEventListener;
   @EventHandler
   private final Listener<TotemPopEvent> totemPopEventListener;

   private TotemPopManager() {
      this.chatFormatting = ChatFormatting.WHITE;
      this.nameFormatting = ChatFormatting.WHITE;
      this.friFormatting = ChatFormatting.WHITE;
      this.numberFormatting = ChatFormatting.WHITE;
      this.playerPopCount = new HashMap();
      this.packetEventListener = new Listener((event) -> {
         if (this.getPlayer() != null && this.getWorld() != null) {
            if (event.getPacket() instanceof SPacketEntityStatus) {
               SPacketEntityStatus packet = (SPacketEntityStatus)event.getPacket();
               Entity entity = packet.func_149161_a(this.getWorld());
               if (packet.func_149160_c() == 35) {
                  LemonClient.EVENT_BUS.post(new TotemPopEvent(entity));
               }
            }

         }
      }, new Predicate[0]);
      this.totemPopEventListener = new Listener((event) -> {
         if (this.getPlayer() != null && this.getWorld() != null) {
            if (event.getEntity() != null) {
               String entityName = event.getEntity().func_70005_c_();
               if (this.playerPopCount.get(entityName) == null) {
                  this.playerPopCount.put(entityName, 1);
                  if (this.sendMsgs) {
                     if (Minecraft.func_71410_x().field_71439_g.func_70005_c_().equals(entityName) && this.self.equals("Disable")) {
                        return;
                     }

                     String namex = entityName.equals(Minecraft.func_71410_x().field_71439_g.func_70005_c_()) && this.self.equals("I") ? "I" : entityName;
                     if (namex.equals("") || namex.equals(" ")) {
                        return;
                     }

                     if (namex.equals("I") || SocialManager.isFriend(namex) && !this.type4.equals("Enemy")) {
                        if (this.friend) {
                           namex = "My Friend " + namex;
                        }

                        MessageBus.sendClientDeleteMessage(this.friFormatting + namex + this.chatFormatting + " popped " + this.numberFormatting + 1 + this.chatFormatting + " totem.", Notification.Type.INFO, "TotemPopCounter" + namex, 1000);
                     }

                     if (!namex.equals("I") && !SocialManager.isFriend(namex) && !this.type4.equals("Friend")) {
                        MessageBus.sendClientDeleteMessage(this.nameFormatting + namex + this.chatFormatting + " popped " + this.numberFormatting + 1 + this.chatFormatting + " totem.", Notification.Type.INFO, "TotemPopCounter" + namex, 1000);
                     }
                  }
               } else {
                  int popCounter = (Integer)this.playerPopCount.get(entityName) + 1;
                  this.playerPopCount.put(entityName, popCounter);
                  if (this.sendMsgs) {
                     if (Minecraft.func_71410_x().field_71439_g.func_70005_c_().equals(entityName) && this.self.equals("Disable")) {
                        return;
                     }

                     String name = entityName.equals(Minecraft.func_71410_x().field_71439_g.func_70005_c_()) && this.self.equals("I") ? "I" : entityName;
                     if (name.equals("") || name.equals(" ")) {
                        return;
                     }

                     if (name.equals("I") || SocialManager.isFriend(name) && !this.type4.equals("Enemy")) {
                        if (this.friend) {
                           name = "My Friend " + name;
                        }

                        MessageBus.sendClientDeleteMessage(this.friFormatting + name + this.chatFormatting + " popped " + this.numberFormatting + popCounter + this.chatFormatting + " totems.", Notification.Type.INFO, "TotemPopCounter" + name, 1000);
                     }

                     if (!name.equals("I") && !SocialManager.isFriend(name) && !this.type4.equals("Friend")) {
                        MessageBus.sendClientDeleteMessage(this.nameFormatting + name + this.chatFormatting + " popped " + this.numberFormatting + popCounter + this.chatFormatting + " totems.", Notification.Type.INFO, "TotemPopCounter" + name, 1000);
                     }
                  }
               }

            }
         }
      }, new Predicate[0]);
   }

   public void death(EntityPlayer entityPlayer) {
      if (this.playerPopCount.containsKey(entityPlayer.func_70005_c_())) {
         int pop = this.getPlayerPopCount(entityPlayer.func_70005_c_());
         if (this.sendMsgs) {
            if (Minecraft.func_71410_x().field_71439_g.func_70005_c_().equals(entityPlayer.func_70005_c_()) && this.self.equals("Disable")) {
               return;
            }

            String name = entityPlayer.func_70005_c_().equals(Minecraft.func_71410_x().field_71439_g.func_70005_c_()) && this.self.equals("I") ? "I" : entityPlayer.func_70005_c_();
            if (name.equals("") || name.equals(" ")) {
               return;
            }

            if (name.equals("I") || SocialManager.isFriend(name) && !this.type4.equals("Enemy")) {
               if (this.friend) {
                  name = "My Friend " + name;
               }

               MessageBus.sendClientPrefixMessage(this.friFormatting + name + this.chatFormatting + " died after popping " + this.numberFormatting + this.getPlayerPopCount(entityPlayer.func_70005_c_()) + this.chatFormatting + " totem" + (pop > 1 ? "s." : "."), Notification.Type.INFO);
            }

            if (!name.equals("I") && !SocialManager.isFriend(name) && !this.type4.equals("Friend")) {
               MessageBus.sendClientPrefixMessage(this.nameFormatting + name + this.chatFormatting + " died after popping " + this.numberFormatting + this.getPlayerPopCount(entityPlayer.func_70005_c_()) + this.chatFormatting + " totem" + (pop > 1 ? "s." : "."), Notification.Type.INFO);
            }
         }

         this.playerPopCount.remove(entityPlayer.func_70005_c_());
      }
   }

   public int getPlayerPopCount(String name) {
      return this.playerPopCount.containsKey(name) ? (Integer)this.playerPopCount.get(name) : 0;
   }
}
