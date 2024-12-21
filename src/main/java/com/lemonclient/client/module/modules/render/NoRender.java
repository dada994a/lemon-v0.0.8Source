package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.event.events.BossbarEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.material.Material;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

@Module.Declaration(
   name = "NoRender",
   category = Category.Render
)
public class NoRender extends Module {
   public BooleanSetting armor = this.registerBoolean("Armor", false);
   BooleanSetting fire = this.registerBoolean("Fire", false);
   BooleanSetting blind = this.registerBoolean("Blind", false);
   BooleanSetting nausea = this.registerBoolean("Nausea", false);
   public BooleanSetting hurtCam = this.registerBoolean("HurtCam", false);
   public BooleanSetting noSkylight = this.registerBoolean("Skylight", false);
   public BooleanSetting noOverlay = this.registerBoolean("No Overlay", false);
   BooleanSetting noBossBar = this.registerBoolean("No Boss Bar", false);
   public BooleanSetting nameTag = this.registerBoolean("No NameTag", false);
   public BooleanSetting noCluster = this.registerBoolean("No Cluster", false);
   IntegerSetting maxNoClusterRender = this.registerInteger("No Cluster Max", 5, 1, 25);
   public int currentClusterAmount = 0;
   @EventHandler
   public Listener<RenderBlockOverlayEvent> blockOverlayEventListener = new Listener((event) -> {
      if ((Boolean)this.fire.getValue() && event.getOverlayType() == OverlayType.FIRE) {
         event.setCanceled(true);
      }

      if ((Boolean)this.noOverlay.getValue() && event.getOverlayType() == OverlayType.WATER) {
         event.setCanceled(true);
      }

      if ((Boolean)this.noOverlay.getValue() && event.getOverlayType() == OverlayType.BLOCK) {
         event.setCanceled(true);
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<FogDensity> fogDensityListener = new Listener((event) -> {
      if ((Boolean)this.noOverlay.getValue() && (event.getState().func_185904_a().equals(Material.field_151586_h) || event.getState().func_185904_a().equals(Material.field_151587_i))) {
         event.setDensity(0.0F);
         event.setCanceled(true);
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<RenderBlockOverlayEvent> renderBlockOverlayEventListener = new Listener((event) -> {
      if ((Boolean)this.noOverlay.getValue()) {
         event.setCanceled(true);
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<RenderGameOverlayEvent> renderGameOverlayEventListener = new Listener((event) -> {
      if ((Boolean)this.noOverlay.getValue()) {
         if (event.getType().equals(ElementType.HELMET)) {
            event.setCanceled(true);
         }

         if (event.getType().equals(ElementType.PORTAL)) {
            event.setCanceled(true);
         }
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<BossbarEvent> bossbarEventListener = new Listener((event) -> {
      if ((Boolean)this.noBossBar.getValue()) {
         event.cancel();
      }

   }, new Predicate[0]);

   public void onUpdate() {
      if ((Boolean)this.blind.getValue() && mc.field_71439_g.func_70644_a(MobEffects.field_76440_q)) {
         mc.field_71439_g.func_184589_d(MobEffects.field_76440_q);
      }

      if ((Boolean)this.nausea.getValue() && mc.field_71439_g.func_70644_a(MobEffects.field_76431_k)) {
         mc.field_71439_g.func_184589_d(MobEffects.field_76431_k);
      }

   }

   public void onRender() {
      this.currentClusterAmount = 0;
   }

   public boolean incrementNoClusterRender() {
      ++this.currentClusterAmount;
      return this.currentClusterAmount > (Integer)this.maxNoClusterRender.getValue();
   }

   public boolean getNoClusterRender() {
      return this.currentClusterAmount <= (Integer)this.maxNoClusterRender.getValue();
   }
}
