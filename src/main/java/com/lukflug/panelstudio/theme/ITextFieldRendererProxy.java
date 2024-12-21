package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import java.awt.Rectangle;

@FunctionalInterface
public interface ITextFieldRendererProxy extends ITextFieldRenderer {
   default int renderTextField(Context context, String title, boolean focus, String content, int position, int select, int boxPosition, boolean insertMode) {
      return this.getRenderer().renderTextField(context, title, focus, content, position, select, boxPosition, insertMode);
   }

   default int getDefaultHeight() {
      return this.getRenderer().getDefaultHeight();
   }

   default Rectangle getTextArea(Context context, String title) {
      return this.getRenderer().getTextArea(context, title);
   }

   default int transformToCharPos(Context context, String title, String content, int boxPosition) {
      return this.getRenderer().transformToCharPos(context, title, content, boxPosition);
   }

   ITextFieldRenderer getRenderer();
}
