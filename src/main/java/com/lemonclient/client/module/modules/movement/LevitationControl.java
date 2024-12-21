package com.lemonclient.client.module.modules.movement;

import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Objects;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@Module.Declaration(
   name = "LevitationControl",
   category = Category.Movement
)
public class LevitationControl extends Module {
   DoubleSetting upAmplifier = this.registerDouble("Amplifier Up", 1.0D, 1.0D, 3.0D);
   DoubleSetting downAmplifier = this.registerDouble("Amplifier Down", 1.0D, 1.0D, 3.0D);

   public void onUpdate() {
      if (mc.field_71439_g.func_70644_a(MobEffects.field_188424_y)) {
         int amplifier = ((PotionEffect)Objects.requireNonNull(mc.field_71439_g.func_70660_b((Potion)Objects.requireNonNull(Potion.func_188412_a(25))))).func_76458_c();
         if (mc.field_71474_y.field_74314_A.func_151470_d()) {
            mc.field_71439_g.field_70181_x = (0.05D * (double)(amplifier + 1) - mc.field_71439_g.field_70181_x) * 0.2D * (Double)this.upAmplifier.getValue();
         } else if (mc.field_71474_y.field_74311_E.func_151470_d()) {
            mc.field_71439_g.field_70181_x = -((0.05D * (double)(amplifier + 1) - mc.field_71439_g.field_70181_x) * 0.2D * (Double)this.downAmplifier.getValue());
         } else {
            mc.field_71439_g.field_70181_x = 0.0D;
         }
      }

   }
}
