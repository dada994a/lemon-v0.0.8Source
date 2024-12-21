package com.lemonclient.client.module.modules.movement;

import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "FastFall",
   category = Category.Movement
)
public class FastFall extends Module {
   DoubleSetting dist = this.registerDouble("Min Distance", 3.0D, 0.0D, 25.0D);
   DoubleSetting speed = this.registerDouble("Multiplier", 3.0D, 0.0D, 10.0D);

   public void onUpdate() {
      if (mc.field_71441_e.func_175623_d(new BlockPos(mc.field_71439_g.func_174791_d())) && mc.field_71439_g.field_70122_E && (!mc.field_71439_g.func_184613_cA() || (double)mc.field_71439_g.field_70143_R < (Double)this.dist.getValue() || !mc.field_71439_g.field_71075_bZ.field_75100_b)) {
         EntityPlayerSP var10000 = mc.field_71439_g;
         var10000.field_70181_x -= (Double)this.speed.getValue();
      }

   }
}
