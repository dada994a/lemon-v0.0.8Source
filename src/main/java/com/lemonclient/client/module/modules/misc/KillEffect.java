package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;

@Module.Declaration(
   name = "KillEffect",
   category = Category.Misc
)
public class KillEffect extends Module {
   BooleanSetting thunder = this.registerBoolean("Thunder", true);
   IntegerSetting numbersThunder = this.registerInteger("Number Thunder", 1, 1, 10);
   BooleanSetting sound = this.registerBoolean("Sound", true);
   IntegerSetting numberSound = this.registerInteger("Number Sound", 1, 1, 10);
   ArrayList<EntityPlayer> playersDead = new ArrayList();

   protected void onEnable() {
      this.playersDead.clear();
   }

   public void onUpdate() {
      if (mc.field_71441_e == null) {
         this.playersDead.clear();
      } else {
         mc.field_71441_e.field_73010_i.forEach((entity) -> {
            if (this.playersDead.contains(entity)) {
               if (entity.func_110143_aJ() > 0.0F) {
                  this.playersDead.remove(entity);
               }
            } else if (entity.func_110143_aJ() == 0.0F) {
               int i;
               if ((Boolean)this.thunder.getValue()) {
                  for(i = 0; i < (Integer)this.numbersThunder.getValue(); ++i) {
                     mc.field_71441_e.func_72838_d(new EntityLightningBolt(mc.field_71441_e, entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, true));
                  }
               }

               if ((Boolean)this.sound.getValue()) {
                  for(i = 0; i < (Integer)this.numberSound.getValue(); ++i) {
                     mc.field_71439_g.func_184185_a(SoundEvents.field_187754_de, 0.5F, 1.0F);
                  }
               }

               this.playersDead.add(entity);
            }

         });
      }
   }
}
