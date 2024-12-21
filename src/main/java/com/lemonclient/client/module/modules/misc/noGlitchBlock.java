package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(
   name = "NoGlitchBlock",
   category = Category.Misc
)
public class noGlitchBlock extends Module {
   public BooleanSetting breakBlock = this.registerBoolean("Break", true);
   public BooleanSetting placeBlock = this.registerBoolean("Place", true);
}
