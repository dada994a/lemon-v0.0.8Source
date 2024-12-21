package com.lukflug.panelstudio.setting;

import com.lukflug.panelstudio.base.IBoolean;

public class Labeled implements ILabeled {
   protected String title;
   protected String description;
   protected IBoolean visible;

   public Labeled(String title, String description, IBoolean visible) {
      this.title = title;
      this.description = description;
      this.visible = visible;
   }

   public String getDisplayName() {
      return this.title;
   }

   public String getDescription() {
      return this.description;
   }

   public IBoolean isVisible() {
      return this.visible;
   }
}
