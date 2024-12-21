package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.event.events.PlayerJoinEvent;
import com.lemonclient.api.event.events.PlayerLeaveEvent;
import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.misc.Timer;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.util.render.RenderUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;

@Module.Declaration(
   name = "LogoutSpots",
   category = Category.Render
)
public class LogoutSpots extends Module {
   IntegerSetting range = this.registerInteger("Range", 100, 10, 260);
   BooleanSetting disconnectMsg = this.registerBoolean("Disconnect Msgs", true);
   BooleanSetting reconnectMsg = this.registerBoolean("Reconnect Msgs", true);
   BooleanSetting nameTag = this.registerBoolean("NameTag", true);
   IntegerSetting lineWidth = this.registerInteger("Width", 1, 1, 10);
   ModeSetting renderMode = this.registerMode("Render", Arrays.asList("Both", "Outline", "Fill", "None"), "Both");
   ColorSetting color = this.registerColor("Color", new GSColor(255, 0, 0, 255));
   Map<Entity, String> loggedPlayers = new ConcurrentHashMap();
   Set<EntityPlayer> worldPlayers = ConcurrentHashMap.newKeySet();
   Timer timer = new Timer();
   Timer timer2 = new Timer();
   @EventHandler
   private final Listener<PlayerJoinEvent> playerJoinEventListener = new Listener((event) -> {
      if (mc.field_71441_e != null) {
         this.loggedPlayers.keySet().removeIf((entity) -> {
            if (entity.func_70005_c_().equalsIgnoreCase(event.getName())) {
               if ((Boolean)this.reconnectMsg.getValue() && this.timer2.getTimePassed() / 50L >= 5L) {
                  MessageBus.sendClientPrefixMessage(event.getName() + " reconnected.", Notification.Type.INFO);
                  this.timer2.reset();
               }

               return true;
            } else {
               return false;
            }
         });
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<PlayerLeaveEvent> playerLeaveEventListener = new Listener((event) -> {
      if (mc.field_71441_e != null) {
         this.worldPlayers.removeIf((entity) -> {
            if (entity.func_70005_c_().equalsIgnoreCase(event.getName())) {
               String date = (new SimpleDateFormat("k:mm")).format(new Date());
               this.loggedPlayers.put(entity, date);
               if ((Boolean)this.disconnectMsg.getValue() && this.timer.getTimePassed() / 50L >= 5L) {
                  String location = "(" + (int)entity.field_70165_t + "," + (int)entity.field_70163_u + "," + (int)entity.field_70161_v + ")";
                  MessageBus.sendClientPrefixMessage(event.getName() + " disconnected at " + location + ".", Notification.Type.INFO);
                  this.timer.reset();
               }

               return true;
            } else {
               return false;
            }
         });
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<Unload> unloadListener = new Listener((event) -> {
      this.worldPlayers.clear();
      if (mc.field_71439_g == null || mc.field_71441_e == null) {
         this.loggedPlayers.clear();
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<Load> loadListener = new Listener((event) -> {
      this.worldPlayers.clear();
      if (mc.field_71439_g == null || mc.field_71441_e == null) {
         this.loggedPlayers.clear();
      }

   }, new Predicate[0]);

   public void onUpdate() {
      mc.field_71441_e.field_73010_i.stream().filter((entityPlayer) -> {
         return entityPlayer != mc.field_71439_g;
      }).filter((entityPlayer) -> {
         return entityPlayer.func_70032_d(mc.field_71439_g) <= (float)(Integer)this.range.getValue();
      }).forEach((entityPlayer) -> {
         this.worldPlayers.add(entityPlayer);
      });
   }

   public void onWorldRender(RenderEvent event) {
      if (mc.field_71439_g != null && mc.field_71441_e != null) {
         this.loggedPlayers.forEach(this::startFunction);
      }

   }

   public void onEnable() {
      this.loggedPlayers.clear();
      this.worldPlayers = ConcurrentHashMap.newKeySet();
   }

   public void onDisable() {
      this.worldPlayers.clear();
   }

   private void startFunction(Entity entity, String string) {
      if (!(entity.func_70032_d(mc.field_71439_g) > (float)(Integer)this.range.getValue())) {
         int posX = (int)entity.field_70165_t;
         int posY = (int)entity.field_70163_u;
         int posZ = (int)entity.field_70161_v;
         String[] nameTagMessage = new String[]{entity.func_70005_c_() + " (" + string + ")", "(" + posX + "," + posY + "," + posZ + ")"};
         GlStateManager.func_179094_E();
         if ((Boolean)this.nameTag.getValue()) {
            RenderUtil.drawNametag(entity, nameTagMessage, this.color.getValue(), 0);
         }

         String var7 = (String)this.renderMode.getValue();
         byte var8 = -1;
         switch(var7.hashCode()) {
         case 2076577:
            if (var7.equals("Both")) {
               var8 = 0;
            }
            break;
         case 2189731:
            if (var7.equals("Fill")) {
               var8 = 2;
            }
            break;
         case 558407714:
            if (var7.equals("Outline")) {
               var8 = 1;
            }
         }

         switch(var8) {
         case 0:
            RenderUtil.drawBoundingBox(entity.func_184177_bl(), (double)(Integer)this.lineWidth.getValue(), this.color.getValue());
            RenderUtil.drawBox(entity.func_184177_bl(), true, -0.4D, new GSColor(this.color.getValue(), 50), 63);
            break;
         case 1:
            RenderUtil.drawBoundingBox(entity.func_184177_bl(), (double)(Integer)this.lineWidth.getValue(), this.color.getValue());
            break;
         case 2:
            RenderUtil.drawBox(entity.func_184177_bl(), true, -0.4D, new GSColor(this.color.getValue(), 50), 63);
         }

         GlStateManager.func_179121_F();
      }
   }
}
