package com.lemonclient.mixin.mixins;

import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({GuiPlayerTabOverlay.class})
public class MixinGuiPlayerTabOverlay {
   @Inject(
      method = {"getPlayerName"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getPlayerNameHead(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable<String> callbackInfoReturnable) {
      callbackInfoReturnable.setReturnValue(this.getPlayerNameGS(networkPlayerInfoIn));
   }

   private String getPlayerNameGS(NetworkPlayerInfo networkPlayerInfoIn) {
      String displayName = networkPlayerInfoIn.func_178854_k() != null ? networkPlayerInfoIn.func_178854_k().func_150254_d() : ScorePlayerTeam.func_96667_a(networkPlayerInfoIn.func_178850_i(), networkPlayerInfoIn.func_178845_a().getName());
      if (SocialManager.isFriend(displayName)) {
         return ((ColorMain)ModuleManager.getModule(ColorMain.class)).getFriendColor() + displayName;
      } else {
         return SocialManager.isEnemy(displayName) ? ((ColorMain)ModuleManager.getModule(ColorMain.class)).getEnemyColor() + displayName : displayName;
      }
   }
}
