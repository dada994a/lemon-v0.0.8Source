package com.lukflug.panelstudio.tabgui;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.setting.ICategory;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

public class Tab extends TabItem<IToggleable, Boolean> {
   public Tab(ICategory category, ITabGUIRenderer<Boolean> renderer, Animation animation, IntPredicate up, IntPredicate down, IntPredicate enter) {
      super(category, renderer, animation, up, down, enter, (key) -> {
         return false;
      });
      this.contents = (List)category.getModules().map((module) -> {
         return new TabItem.ContentItem(module.getDisplayName(), module.isEnabled());
      }).collect(Collectors.toList());
   }

   protected void handleSelect(Context context) {
      ((IToggleable)((TabItem.ContentItem)this.contents.get((int)this.tabState.getTarget())).content).toggle();
   }

   protected void handleExit(Context context) {
   }
}
