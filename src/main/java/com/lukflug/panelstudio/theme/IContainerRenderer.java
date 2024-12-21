package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;

public interface IContainerRenderer {
   default void renderBackground(Context context, boolean focus) {
   }

   default int getBorder() {
      return 0;
   }

   default int getLeft() {
      return 0;
   }

   default int getRight() {
      return 0;
   }

   default int getTop() {
      return 0;
   }

   default int getBottom() {
      return 0;
   }
}
