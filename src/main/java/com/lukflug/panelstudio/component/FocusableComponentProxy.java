package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.Context;
import java.util.function.Consumer;

public abstract class FocusableComponentProxy<T extends IComponent> implements IComponentProxy<T> {
   private final boolean initFocus;
   private boolean focus;
   private boolean requestFocus = false;

   public FocusableComponentProxy(boolean focus) {
      this.initFocus = focus;
      this.focus = focus;
   }

   public void handleButton(Context context, int button) {
      IComponentProxy.super.handleButton(context, button);
      if (context.getInterface().getButton(button)) {
         this.focus = context.isHovered();
         if (this.focus) {
            context.requestFocus();
         }
      }

   }

   public Context doOperation(Context context, Consumer<Context> operation) {
      if (this.requestFocus) {
         context.requestFocus();
      } else if (!context.hasFocus()) {
         this.focus = false;
      }

      this.requestFocus = false;
      return IComponentProxy.super.doOperation(context, operation);
   }

   public void releaseFocus() {
      this.focus = false;
      IComponentProxy.super.releaseFocus();
   }

   public void enter() {
      this.focus = this.initFocus;
      if (this.focus) {
         this.requestFocus = true;
      }

      IComponentProxy.super.enter();
   }

   public void exit() {
      this.focus = this.initFocus;
      IComponentProxy.super.exit();
   }

   public boolean hasFocus(Context context) {
      return context.hasFocus() && this.focus;
   }
}
