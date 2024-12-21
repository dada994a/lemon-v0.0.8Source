package com.lemonclient.client.module.modules.qwq;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

@Module.Declaration(
   name = "AutoEz",
   category = Category.qwq
)
public class AutoEz extends Module {
   public static AutoEz INSTANCE;
   public BooleanSetting hi = this.registerBoolean("Use {name} for target name", true);
   StringSetting msg = this.registerString("Msg", ">Ez");
   IntegerSetting delay = this.registerInteger("Delay", 0, 0, 20);
   List<AutoEz.Target> targetedPlayers;
   int waited;
   @EventHandler
   private final Listener<PacketEvent.Send> sendListener = new Listener((event) -> {
      if (mc.field_71439_g != null) {
         if (this.targetedPlayers == null) {
            this.targetedPlayers = new ArrayList();
         }

         if (this.waited > 0) {
            return;
         }

         if (event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity cPacketUseEntity = (CPacketUseEntity)event.getPacket();
            if (cPacketUseEntity.func_149565_c().equals(Action.ATTACK)) {
               Entity targetEntity = cPacketUseEntity.func_149564_a(mc.field_71441_e);
               if (targetEntity instanceof EntityPlayer) {
                  this.addTargetedPlayer(targetEntity.func_70005_c_());
               }
            }
         }
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<LivingDeathEvent> livingDeathEventListener = new Listener((event) -> {
      if (mc.field_71439_g != null) {
         if (this.targetedPlayers == null) {
            this.targetedPlayers = new ArrayList();
         }

         if (this.waited > 0) {
            return;
         }

         EntityLivingBase entity = event.getEntityLiving();
         if (entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (player.func_110143_aJ() <= 0.0F) {
               String name = player.func_70005_c_();
               this.doAnnounce(name);
            }
         }
      }

   }, new Predicate[0]);

   public AutoEz() {
      INSTANCE = this;
   }

   public void onEnable() {
      this.targetedPlayers = new ArrayList();
   }

   public void onDisable() {
      this.targetedPlayers = null;
   }

   public void onUpdate() {
      if (this.targetedPlayers == null) {
         this.targetedPlayers = new ArrayList();
      }

      --this.waited;
      if (this.waited <= 0) {
         List<String> nameList = new ArrayList();
         Iterator var2 = mc.field_71441_e.field_73010_i.iterator();

         while(var2.hasNext()) {
            EntityPlayer player = (EntityPlayer)var2.next();
            String name = player.func_70005_c_();
            nameList.add(name);
            if (this.inList(name) && player.func_110143_aJ() <= 0.0F) {
               this.doAnnounce(name);
            }
         }

         this.targetedPlayers.removeIf((target) -> {
            if (nameList.contains(target.name) && !target.name.equals("")) {
               target.updateTime();
               return target.time <= 0;
            } else {
               return true;
            }
         });
      }
   }

   private void doAnnounce(String name) {
      if (!name.equals(mc.field_71439_g.func_70005_c_())) {
         boolean in = false;
         Iterator var3 = this.targetedPlayers.iterator();

         while(var3.hasNext()) {
            AutoEz.Target target = (AutoEz.Target)var3.next();
            if (target.name.equals(name)) {
               this.targetedPlayers.remove(target);
               in = true;
               break;
            }
         }

         if (in) {
            String message = this.msg.getText();
            String messageSanitized = message.replace("{name}", name);
            if (messageSanitized.length() > 255) {
               messageSanitized = messageSanitized.substring(0, 255);
            }

            MessageBus.sendServerMessage(messageSanitized);
            this.waited = (Integer)this.delay.getValue();
         }
      }
   }

   public void addTargetedPlayer(String name) {
      if (!Objects.equals(name, mc.field_71439_g.func_70005_c_())) {
         if (this.targetedPlayers == null) {
            this.targetedPlayers = new ArrayList();
         }

         boolean added = false;
         Iterator var3 = this.targetedPlayers.iterator();

         while(var3.hasNext()) {
            AutoEz.Target target = (AutoEz.Target)var3.next();
            if (target.name.equals(name)) {
               target.update();
               added = true;
               break;
            }
         }

         if (!added) {
            this.targetedPlayers.add(new AutoEz.Target(name));
         }
      }

   }

   private boolean inList(String name) {
      Iterator var2 = this.targetedPlayers.iterator();

      AutoEz.Target target;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         target = (AutoEz.Target)var2.next();
      } while(!target.name.equals(name));

      return true;
   }

   static class Target {
      String name;
      int time;

      public Target(String name) {
         this.name = name;
         this.time = 20;
      }

      void updateTime() {
         --this.time;
      }

      void update() {
         this.time = 20;
      }
   }
}
