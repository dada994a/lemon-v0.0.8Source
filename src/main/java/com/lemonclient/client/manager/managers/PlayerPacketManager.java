package com.lemonclient.client.manager.managers;

import com.lemonclient.api.event.Phase;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.RenderEntityEvent;
import com.lemonclient.api.util.misc.CollectionUtil;
import com.lemonclient.api.util.player.PlayerPacket;
import com.lemonclient.client.manager.Manager;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.F5Fix;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public enum PlayerPacketManager implements Manager {
   INSTANCE;

   private final List<PlayerPacket> packets = new ArrayList();
   private Vec3d prevServerSidePosition;
   private Vec3d serverSidePosition;
   private Vec2f prevServerSideRotation;
   private Vec2f serverSideRotation;
   private Vec2f clientSidePitch;
   @EventHandler
   private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener;
   @EventHandler
   private final Listener<PacketEvent.PostSend> postSendListener;
   @EventHandler
   private final Listener<ClientTickEvent> tickEventListener;
   @EventHandler
   private final Listener<RenderEntityEvent.Head> renderEntityEventHeadListener;
   @EventHandler
   private final Listener<RenderEntityEvent.Return> renderEntityEventReturnListener;

   private PlayerPacketManager() {
      this.prevServerSidePosition = Vec3d.field_186680_a;
      this.serverSidePosition = Vec3d.field_186680_a;
      this.prevServerSideRotation = Vec2f.field_189974_a;
      this.serverSideRotation = Vec2f.field_189974_a;
      this.clientSidePitch = Vec2f.field_189974_a;
      this.onUpdateWalkingPlayerEventListener = new Listener((event) -> {
         if (event.getPhase() == Phase.BY && !this.packets.isEmpty()) {
            PlayerPacket packet = (PlayerPacket)CollectionUtil.maxOrNull(this.packets, PlayerPacket::getPriority);
            if (packet != null) {
               event.cancel();
               event.apply(packet);
            }

            this.packets.clear();
         }
      }, new Predicate[0]);
      this.postSendListener = new Listener((event) -> {
         if (!event.isCancelled()) {
            Packet<?> rawPacket = event.getPacket();
            EntityPlayerSP player = this.getPlayer();
            if (player != null && rawPacket instanceof CPacketPlayer) {
               CPacketPlayer packet = (CPacketPlayer)rawPacket;
               if (packet.field_149480_h) {
                  this.serverSidePosition = new Vec3d(packet.field_149479_a, packet.field_149477_b, packet.field_149478_c);
               }

               if (packet.field_149481_i) {
                  this.serverSideRotation = new Vec2f(packet.field_149476_e, packet.field_149473_f);
                  player.field_70759_as = packet.field_149476_e;
               }
            }

         }
      }, -200, new Predicate[0]);
      this.tickEventListener = new Listener((event) -> {
         if (event.phase == net.minecraftforge.fml.common.gameevent.TickEvent.Phase.START) {
            this.prevServerSidePosition = this.serverSidePosition;
            this.prevServerSideRotation = this.serverSideRotation;
         }
      }, new Predicate[0]);
      this.renderEntityEventHeadListener = new Listener((event) -> {
         if (ModuleManager.isModuleEnabled(F5Fix.class)) {
            EntityPlayerSP player = this.getPlayer();
            if (player != null && !player.func_184218_aH() && event.getType() == RenderEntityEvent.Type.TEXTURE && event.getEntity() == player) {
               this.clientSidePitch = new Vec2f(player.field_70127_C, player.field_70125_A);
               player.field_70127_C = this.prevServerSideRotation.field_189983_j;
               player.field_70125_A = this.serverSideRotation.field_189983_j;
            }
         }
      }, new Predicate[0]);
      this.renderEntityEventReturnListener = new Listener((event) -> {
         if (ModuleManager.isModuleEnabled(F5Fix.class)) {
            EntityPlayerSP player = this.getPlayer();
            if (player != null && !player.func_184218_aH() && event.getType() == RenderEntityEvent.Type.TEXTURE && event.getEntity() == player) {
               player.field_70127_C = this.clientSidePitch.field_189982_i;
               player.field_70125_A = this.clientSidePitch.field_189983_j;
            }
         }
      }, new Predicate[0]);
   }

   public void addPacket(PlayerPacket packet) {
      this.packets.add(packet);
   }

   public Vec3d getPrevServerSidePosition() {
      return this.prevServerSidePosition;
   }

   public Vec3d getServerSidePosition() {
      return this.serverSidePosition;
   }

   public Vec2f getPrevServerSideRotation() {
      return this.prevServerSideRotation;
   }

   public Vec2f getServerSideRotation() {
      return this.serverSideRotation;
   }
}
