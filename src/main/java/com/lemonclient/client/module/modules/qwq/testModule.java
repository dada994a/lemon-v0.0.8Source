package com.lemonclient.client.module.modules.qwq;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "test Module",
   category = Category.qwq
)
public class testModule extends Module {
   BooleanSetting ewe = this.registerBoolean("Don't Use or AutoCrash", true);

   public void onEnable() {
   }

   public void onUpdate() {
      BlockPos pos = mc.field_71476_x.func_178782_a();
      if (pos != null) {
         MessageBus.sendClientRawMessage(String.valueOf(BlockUtil.getBlock(pos)));
      }

   }
}
