package com.lemonclient.client.module.modules.movement;

import com.lemonclient.api.event.events.EntityCollisionEvent;
import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.WaterPushEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.InputUpdateEvent;
import org.lwjgl.input.Keyboard;

@Module.Declaration(
   name = "PlayerTweaks",
   category = Category.Movement
)
public class PlayerTweaks extends Module {
   public BooleanSetting guiMove = this.registerBoolean("Gui Move", false);
   BooleanSetting noPush = this.registerBoolean("No Push", false);
   BooleanSetting noFall = this.registerBoolean("No Fall", false);
   public BooleanSetting noSlow = this.registerBoolean("No Slow", false);
   BooleanSetting antiKnockBack = this.registerBoolean("Velocity", false);
   @EventHandler
   private final Listener<InputUpdateEvent> eventListener = new Listener((event) -> {
      if ((Boolean)this.noSlow.getValue() && mc.field_71439_g.func_184587_cr() && !mc.field_71439_g.func_184218_aH()) {
         MovementInput var10000 = event.getMovementInput();
         var10000.field_78902_a *= 5.0F;
         var10000 = event.getMovementInput();
         var10000.field_192832_b *= 5.0F;
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<EntityCollisionEvent> entityCollisionEventListener = new Listener((event) -> {
      if ((Boolean)this.noPush.getValue()) {
         event.cancel();
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if ((Boolean)this.antiKnockBack.getValue()) {
         if (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity)event.getPacket()).func_149412_c() == mc.field_71439_g.func_145782_y()) {
            event.cancel();
         }

         if (event.getPacket() instanceof SPacketExplosion) {
            event.cancel();
         }
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.Send> sendListener = new Listener((event) -> {
      if ((Boolean)this.noFall.getValue() && event.getPacket() instanceof CPacketPlayer && (double)mc.field_71439_g.field_70143_R >= 3.0D) {
         CPacketPlayer packet = (CPacketPlayer)event.getPacket();
         packet.field_149474_g = true;
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<WaterPushEvent> waterPushEventListener = new Listener((event) -> {
      if ((Boolean)this.noPush.getValue()) {
         event.cancel();
      }

   }, new Predicate[0]);

   public void onUpdate() {
      if ((Boolean)this.guiMove.getValue() && mc.field_71462_r != null && !(mc.field_71462_r instanceof GuiChat)) {
         EntityPlayerSP var10000;
         if (Keyboard.isKeyDown(200)) {
            var10000 = mc.field_71439_g;
            var10000.field_70125_A -= 5.0F;
         }

         if (Keyboard.isKeyDown(208)) {
            var10000 = mc.field_71439_g;
            var10000.field_70125_A += 5.0F;
         }

         if (Keyboard.isKeyDown(205)) {
            var10000 = mc.field_71439_g;
            var10000.field_70177_z += 5.0F;
         }

         if (Keyboard.isKeyDown(203)) {
            var10000 = mc.field_71439_g;
            var10000.field_70177_z -= 5.0F;
         }

         if (mc.field_71439_g.field_70125_A > 90.0F) {
            mc.field_71439_g.field_70125_A = 90.0F;
         }

         if (mc.field_71439_g.field_70125_A < -90.0F) {
            mc.field_71439_g.field_70125_A = -90.0F;
         }
      }

   }
}
