package com.lemonclient.api.util.player;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.LemonClient;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

public class SpoofRotationUtil implements Listenable {
   private static final Minecraft mc = Minecraft.func_71410_x();
   public static final SpoofRotationUtil ROTATION_UTIL = new SpoofRotationUtil();
   private int rotationConnections = 0;
   private boolean shouldSpoofAngles;
   private boolean isSpoofingAngles;
   private double yaw;
   private double pitch;
   @EventHandler
   private final Listener<PacketEvent.Send> packetSendListener = new Listener((event) -> {
      Packet packet = event.getPacket();
      if (packet instanceof CPacketPlayer && this.shouldSpoofAngles && this.isSpoofingAngles) {
         ((CPacketPlayer)packet).field_149476_e = (float)this.yaw;
         ((CPacketPlayer)packet).field_149473_f = (float)this.pitch;
      }

   }, new Predicate[0]);

   private SpoofRotationUtil() {
   }

   public void onEnable() {
      ++this.rotationConnections;
      if (this.rotationConnections == 1) {
         LemonClient.EVENT_BUS.subscribe((Listenable)this);
      }

   }

   public void onDisable() {
      --this.rotationConnections;
      if (this.rotationConnections == 0) {
         LemonClient.EVENT_BUS.unsubscribe((Listenable)this);
      }

   }

   public void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
      double[] v = EntityUtil.calculateLookAt(px, py, pz, me);
      this.setYawAndPitch((float)v[0], (float)v[1]);
   }

   public void setYawAndPitch(float yaw1, float pitch1) {
      this.yaw = (double)yaw1;
      this.pitch = (double)pitch1;
      this.isSpoofingAngles = true;
   }

   public void resetRotation() {
      if (this.isSpoofingAngles) {
         this.yaw = (double)mc.field_71439_g.field_70177_z;
         this.pitch = (double)mc.field_71439_g.field_70125_A;
         this.isSpoofingAngles = false;
      }

   }

   public void shouldSpoofAngles(boolean e) {
      this.shouldSpoofAngles = e;
   }

   public boolean isSpoofingAngles() {
      return this.isSpoofingAngles;
   }
}
