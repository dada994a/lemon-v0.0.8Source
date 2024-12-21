package com.lemonclient.mixin.mixins;

import club.minnced.discord.rpc.DiscordRPC;
import com.lemonclient.api.config.SaveConfig;
import com.lemonclient.api.util.player.Locks;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.misc.AntiSpam;
import com.lemonclient.client.module.modules.misc.MultiTask;
import com.lemonclient.mixin.mixins.accessor.AccessorEntityPlayerSP;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public class MixinMinecraft {
   @Unique
   private final DiscordRPC lemonclient$discordRPC;
   @Shadow
   public EntityPlayerSP field_71439_g;
   @Shadow
   public PlayerControllerMP field_71442_b;
   @Unique
   private boolean lemonclient$handActive;
   @Unique
   private boolean lemonclient$isHittingBlock;

   public MixinMinecraft() {
      this.lemonclient$discordRPC = DiscordRPC.INSTANCE;
      this.lemonclient$handActive = false;
      this.lemonclient$isHittingBlock = false;
   }

   @Redirect(
      method = {"rightClickMouse"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;processRightClick(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;"
)
   )
   private EnumActionResult processRightClickHook(PlayerControllerMP pCMP, EntityPlayer player, World worldIn, EnumHand hand) {
      EnumActionResult var5;
      try {
         Locks.PLACE_SWITCH_LOCK.lock();
         var5 = pCMP.func_187101_a(player, worldIn, hand);
      } finally {
         Locks.PLACE_SWITCH_LOCK.unlock();
      }

      return var5;
   }

   @Redirect(
      method = {"rightClickMouse"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;processRightClickBlock(Lnet/minecraft/client/entity/EntityPlayerSP;Lnet/minecraft/client/multiplayer/WorldClient;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;"
)
   )
   private EnumActionResult processRightClickBlockHook(PlayerControllerMP pCMP, EntityPlayerSP player, WorldClient worldIn, BlockPos pos, EnumFacing direction, Vec3d vec, EnumHand hand) {
      EnumActionResult var8;
      try {
         Locks.PLACE_SWITCH_LOCK.lock();
         var8 = pCMP.func_187099_a(player, worldIn, pos, direction, vec, hand);
      } finally {
         Locks.PLACE_SWITCH_LOCK.unlock();
      }

      return var8;
   }

   @Redirect(
      method = {"rightClickMouse"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;interactWithEntity(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;"
)
   )
   private EnumActionResult interactWithEntityHook(PlayerControllerMP pCMP, EntityPlayer player, Entity target, EnumHand hand) {
      EnumActionResult var5;
      try {
         Locks.PLACE_SWITCH_LOCK.lock();
         var5 = pCMP.func_187097_a(player, target, hand);
      } finally {
         Locks.PLACE_SWITCH_LOCK.unlock();
      }

      return var5;
   }

   @Redirect(
      method = {"rightClickMouse"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;interactWithEntity(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/RayTraceResult;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;"
)
   )
   private EnumActionResult interactWithEntity2Hook(PlayerControllerMP pCMP, EntityPlayer player, Entity target, RayTraceResult ray, EnumHand hand) {
      EnumActionResult var6;
      try {
         Locks.PLACE_SWITCH_LOCK.lock();
         var6 = pCMP.func_187102_a(player, target, ray, hand);
      } finally {
         Locks.PLACE_SWITCH_LOCK.unlock();
      }

      return var6;
   }

   @Redirect(
      method = {"clickMouse"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;attackEntity(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;)V"
)
   )
   public void attackEntityHook(PlayerControllerMP playerControllerMP, EntityPlayer playerIn, Entity targetEntity) {
      Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
         playerControllerMP.func_78764_a(playerIn, targetEntity);
      });
   }

   @Redirect(
      method = {"runTickMouse"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/player/InventoryPlayer;changeCurrentItem(I)V"
)
   )
   public void changeCurrentItemHook(InventoryPlayer inventoryPlayer, int direction) {
      Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
         inventoryPlayer.func_70453_c(direction);
      });
   }

   @Redirect(
      method = {"sendClickBlockToController"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;onPlayerDamageBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z"
)
   )
   public boolean onPlayerDamageBlockHook(PlayerControllerMP pCMP, BlockPos posBlock, EnumFacing directionFacing) {
      boolean var4;
      try {
         Locks.PLACE_SWITCH_LOCK.lock();
         var4 = pCMP.func_180512_c(posBlock, directionFacing);
      } finally {
         Locks.PLACE_SWITCH_LOCK.unlock();
      }

      return var4;
   }

   @Inject(
      method = {"rightClickMouse"},
      at = {@At("HEAD")}
   )
   public void rightClickMousePre(CallbackInfo ci) {
      if (ModuleManager.isModuleEnabled(MultiTask.class)) {
         this.lemonclient$isHittingBlock = this.field_71442_b.func_181040_m();
         this.field_71442_b.field_78778_j = false;
      }

   }

   @Inject(
      method = {"rightClickMouse"},
      at = {@At("RETURN")}
   )
   public void rightClickMousePost(CallbackInfo ci) {
      if (ModuleManager.isModuleEnabled(MultiTask.class) && !this.field_71442_b.func_181040_m()) {
         this.field_71442_b.field_78778_j = this.lemonclient$isHittingBlock;
      }

   }

   @Inject(
      method = {"sendClickBlockToController"},
      at = {@At("HEAD")}
   )
   public void sendClickBlockToControllerPre(boolean leftClick, CallbackInfo ci) {
      if (ModuleManager.isModuleEnabled(MultiTask.class)) {
         this.lemonclient$handActive = this.field_71439_g.func_184587_cr();
         ((AccessorEntityPlayerSP)this.field_71439_g).gsSetHandActive(false);
      }

   }

   @Inject(
      method = {"sendClickBlockToController"},
      at = {@At("RETURN")}
   )
   public void sendClickBlockToControllerPost(boolean leftClick, CallbackInfo ci) {
      if (ModuleManager.isModuleEnabled(MultiTask.class) && !this.field_71439_g.func_184587_cr()) {
         ((AccessorEntityPlayerSP)this.field_71439_g).gsSetHandActive(this.lemonclient$handActive);
      }

   }

   @Redirect(
      method = {"run"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"
)
   )
   public void displayCrashReportHook(Minecraft minecraft, CrashReport crashReport) {
      this.lemonclient$removeIgnore();
      SaveConfig.init();
      this.lemonclient$discordRPC.Discord_Shutdown();
      this.lemonclient$discordRPC.Discord_ClearPresence();
      LemonClient.shutdown();
   }

   @Inject(
      method = {"shutdownMinecraftApplet"},
      at = {@At("HEAD")}
   )
   private void stopClient(CallbackInfo callbackInfo) {
      this.lemonclient$removeIgnore();
      SaveConfig.init();
      this.lemonclient$discordRPC.Discord_Shutdown();
      this.lemonclient$discordRPC.Discord_ClearPresence();
      LemonClient.shutdown();
   }

   @Inject(
      method = {"crashed"},
      at = {@At("HEAD")}
   )
   public void crashed(CrashReport crash, CallbackInfo callbackInfo) {
      this.lemonclient$removeIgnore();
      SaveConfig.init();
      this.lemonclient$discordRPC.Discord_Shutdown();
      this.lemonclient$discordRPC.Discord_ClearPresence();
      LemonClient.shutdown();
   }

   @Inject(
      method = {"shutdown"},
      at = {@At("HEAD")}
   )
   public void shutdown(CallbackInfo callbackInfo) {
      this.lemonclient$removeIgnore();
      SaveConfig.init();
      this.lemonclient$discordRPC.Discord_Shutdown();
      this.lemonclient$discordRPC.Discord_ClearPresence();
      LemonClient.shutdown();
   }

   @Unique
   public void lemonclient$removeIgnore() {
      AntiSpam antiSpam = (AntiSpam)ModuleManager.getModule(AntiSpam.class);
      Iterator var2 = antiSpam.ignoredList.iterator();

      while(var2.hasNext()) {
         String name = (String)var2.next();
         SocialManager.delIgnore(name);
      }

   }
}
