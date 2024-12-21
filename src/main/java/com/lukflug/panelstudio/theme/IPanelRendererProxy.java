package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;

@FunctionalInterface
public interface IPanelRendererProxy<T> extends IPanelRenderer<T> {
   default void renderBackground(Context context, boolean focus) {
      this.getRenderer().renderBackground(context, focus);
   }

   default int getBorder() {
      return this.getRenderer().getBorder();
   }

   default int getLeft() {
      return this.getRenderer().getLeft();
   }

   default int getRight() {
      return this.getRenderer().getRight();
   }

   default int getTop() {
      return this.getRenderer().getTop();
   }

   default int getBottom() {
      return this.getRenderer().getBottom();
   }

   default void renderPanelOverlay(Context context, boolean focus, T state, boolean open) {
      this.getRenderer().renderPanelOverlay(context, focus, state, open);
   }

   default void renderTitleOverlay(Context context, boolean focus, T state, boolean open) {
      this.getRenderer().renderTitleOverlay(context, focus, state, open);
   }

   IPanelRenderer<T> getRenderer();
}
