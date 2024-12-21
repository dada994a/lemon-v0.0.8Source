package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;

@FunctionalInterface
public interface IContainerRendererProxy extends IContainerRenderer {
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

   IContainerRenderer getRenderer();
}
