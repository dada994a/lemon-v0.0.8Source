package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "FastBow",
   category = Category.Combat
)
public class FastBow extends Module {
   IntegerSetting drawLength = this.registerInteger("Draw Length", 3, 3, 21);

   public void onUpdate() {
      if (mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBow && mc.field_71439_g.func_184587_cr() && mc.field_71439_g.func_184612_cw() >= (Integer)this.drawLength.getValue()) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.field_177992_a, mc.field_71439_g.func_174811_aO()));
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItem(mc.field_71439_g.func_184600_cs()));
         mc.field_71439_g.func_184597_cx();
      }

   }
}
