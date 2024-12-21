package com.lemonclient.client.module.modules.hud;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.HUDModule;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

@Module.Declaration(
   name = "TextRadar",
   category = Category.HUD,
   drawn = false
)
@HUDModule.Declaration(
   posX = 0,
   posZ = 50
)
public class TextRadar extends HUDModule {
   ModeSetting display = this.registerMode("Display", Arrays.asList("All", "Friend", "Enemy"), "All");
   BooleanSetting sortUp = this.registerBoolean("Sort Up", false);
   BooleanSetting sortRight = this.registerBoolean("Sort Right", false);
   IntegerSetting range = this.registerInteger("Range", 100, 1, 260);
   private final TextRadar.PlayerList list = new TextRadar.PlayerList();

   public void populate(ITheme theme) {
      this.component = new ListComponent(new Labeled(this.getName(), (String)null, () -> {
         return true;
      }), this.position, this.getName(), this.list, 9, 1);
   }

   public void onRender() {
      this.list.players.clear();
      mc.field_71441_e.field_72996_f.stream().filter((e) -> {
         return e instanceof EntityPlayer;
      }).filter((e) -> {
         return e != mc.field_71439_g;
      }).forEach((e) -> {
         if (!(mc.field_71439_g.func_70032_d(e) > (float)(Integer)this.range.getValue())) {
            if (!((String)this.display.getValue()).equalsIgnoreCase("Friend") || SocialManager.isFriend(e.func_70005_c_())) {
               if (!((String)this.display.getValue()).equalsIgnoreCase("Enemy") || SocialManager.isEnemy(e.func_70005_c_())) {
                  this.list.players.add((EntityPlayer)e);
               }
            }
         }
      });
   }

   private class PlayerList implements HUDList {
      public List<EntityPlayer> players;

      private PlayerList() {
         this.players = new ArrayList();
      }

      public int getSize() {
         return this.players.size();
      }

      public String getItem(int index) {
         EntityPlayer e = (EntityPlayer)this.players.get(index);
         TextFormatting friendcolor;
         if (SocialManager.isFriend(e.func_70005_c_())) {
            friendcolor = ((ColorMain)ModuleManager.getModule(ColorMain.class)).getFriendColor();
         } else if (SocialManager.isEnemy(e.func_70005_c_())) {
            friendcolor = ((ColorMain)ModuleManager.getModule(ColorMain.class)).getEnemyColor();
         } else {
            friendcolor = TextFormatting.GRAY;
         }

         float health = e.func_110143_aJ() + e.func_110139_bj();
         TextFormatting healthcolor;
         if (health <= 5.0F) {
            healthcolor = TextFormatting.RED;
         } else if (health > 5.0F && health < 15.0F) {
            healthcolor = TextFormatting.YELLOW;
         } else {
            healthcolor = TextFormatting.GREEN;
         }

         float distance = TextRadar.mc.field_71439_g.func_70032_d(e);
         TextFormatting distancecolor;
         if (distance < 20.0F) {
            distancecolor = TextFormatting.RED;
         } else if (distance >= 20.0F && distance < 50.0F) {
            distancecolor = TextFormatting.YELLOW;
         } else {
            distancecolor = TextFormatting.GREEN;
         }

         return TextFormatting.GRAY + "[" + healthcolor + (int)health + TextFormatting.GRAY + "] " + friendcolor + e.func_70005_c_() + TextFormatting.GRAY + " [" + distancecolor + (int)distance + TextFormatting.GRAY + "]";
      }

      public Color getItemColor(int index) {
         return new Color(255, 255, 255);
      }

      public boolean sortUp() {
         return (Boolean)TextRadar.this.sortUp.getValue();
      }

      public boolean sortRight() {
         return (Boolean)TextRadar.this.sortRight.getValue();
      }

      // $FF: synthetic method
      PlayerList(Object x1) {
         this();
      }
   }
}
