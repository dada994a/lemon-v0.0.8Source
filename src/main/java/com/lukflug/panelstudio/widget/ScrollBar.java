package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.component.FocusableComponent;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.IScrollBarRenderer;

public abstract class ScrollBar<T> extends FocusableComponent {
   protected boolean horizontal;
   protected boolean attached = false;
   protected IScrollBarRenderer<T> renderer;

   public ScrollBar(ILabeled label, boolean horizontal, IScrollBarRenderer<T> renderer) {
      super(label);
      this.horizontal = horizontal;
      this.renderer = renderer;
   }

   public void render(Context context) {
      super.render(context);
      int value = this.renderer.renderScrollBar(context, this.hasFocus(context), this.getState(), this.horizontal, this.getContentHeight(), this.getScrollPosition());
      if (this.attached) {
         this.setScrollPosition(value);
      }

      if (!context.getInterface().getButton(0)) {
         this.attached = false;
      }

   }

   public void handleButton(Context context, int button) {
      super.handleButton(context, button);
      if (button == 0 && context.isClicked(button)) {
         this.attached = true;
      }

   }

   public void handleScroll(Context context, int diff) {
      super.handleScroll(context, diff);
      if (context.isHovered()) {
         this.setScrollPosition(this.getScrollPosition() + diff);
      }

   }

   protected int getHeight() {
      return this.horizontal ? this.renderer.getThickness() : this.getLength();
   }

   public int getWidth() {
      return this.horizontal ? this.getLength() : this.renderer.getThickness();
   }

   protected abstract int getLength();

   protected abstract int getContentHeight();

   protected abstract int getScrollPosition();

   protected abstract void setScrollPosition(int var1);

   protected abstract T getState();
}
