package com.lemonclient.client.module.modules.gui;

import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(
   name = "HudEditor",
   category = Category.GUI,
   drawn = false
)
public class HUDEditor extends Module {
   public void onEnable() {
      LemonClient.INSTANCE.gameSenseGUI.enterHUDEditor();
      this.disable();
   }
}
