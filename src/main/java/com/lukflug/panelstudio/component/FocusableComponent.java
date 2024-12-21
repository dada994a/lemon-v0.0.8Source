package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.setting.ILabeled;

public abstract class FocusableComponent extends ComponentBase {
   private boolean focus = false;

   public FocusableComponent(ILabeled label) {
      super(label);
   }

   public void handleButton(Context context, int button) {
      super.handleButton(context, button);
      this.updateFocus(context, button);
   }

   public void releaseFocus() {
      this.focus = false;
   }

   public void exit() {
      this.focus = false;
   }

   public boolean hasFocus(Context context) {
      if (!context.hasFocus()) {
         this.focus = false;
      }

      return this.focus;
   }

   protected void updateFocus(Context context, int button) {
      if (context.getInterface().getButton(button)) {
         this.focus = context.isHovered();
         if (this.focus) {
            context.requestFocus();
         }

         this.handleFocus(context, this.focus && context.hasFocus());
      }

   }

   protected void handleFocus(Context context, boolean focus) {
   }
}
