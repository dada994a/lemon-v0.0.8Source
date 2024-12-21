package com.lemonclient.client.module.modules.hud;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.HUDModule;
import com.lemonclient.client.module.Module;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@Module.Declaration(
   name = "PotionEffects",
   category = Category.HUD,
   drawn = false
)
@HUDModule.Declaration(
   posX = 0,
   posZ = 300
)
public class PotionEffects extends HUDModule {
   BooleanSetting sortUp = this.registerBoolean("Sort Up", false);
   BooleanSetting sortRight = this.registerBoolean("Sort Right", false);
   private final PotionEffects.PotionList list = new PotionEffects.PotionList();

   public void populate(ITheme theme) {
      this.component = new ListComponent(new Labeled(this.getName(), (String)null, () -> {
         return true;
      }), this.position, this.getName(), this.list, 9, 1);
   }

   Color getColour(PotionEffect potion) {
      int colour = potion.func_188419_a().func_76401_j();
      float r = (float)(colour >> 16 & 255) / 255.0F;
      float g = (float)(colour >> 8 & 255) / 255.0F;
      float b = (float)(colour & 255) / 255.0F;
      return new Color(r, g, b);
   }

   private class PotionList implements HUDList {
      private PotionList() {
      }

      public int getSize() {
         return PotionEffects.mc.field_71439_g.func_70651_bq().size();
      }

      public String getItem(int index) {
         PotionEffect effect = (PotionEffect)PotionEffects.mc.field_71439_g.func_70651_bq().toArray()[index];
         String name = I18n.func_135052_a(effect.func_188419_a().func_76393_a(), new Object[0]);
         int amplifier = effect.func_76458_c() + 1;
         return name + " " + amplifier + ChatFormatting.GRAY + " " + Potion.func_188410_a(effect, 1.0F);
      }

      public Color getItemColor(int i) {
         return PotionEffects.mc.field_71439_g.func_70651_bq().toArray().length != 0 ? PotionEffects.this.getColour((PotionEffect)PotionEffects.mc.field_71439_g.func_70651_bq().toArray()[i]) : null;
      }

      public boolean sortUp() {
         return (Boolean)PotionEffects.this.sortUp.getValue();
      }

      public boolean sortRight() {
         return (Boolean)PotionEffects.this.sortRight.getValue();
      }

      // $FF: synthetic method
      PotionList(Object x1) {
         this();
      }
   }
}
