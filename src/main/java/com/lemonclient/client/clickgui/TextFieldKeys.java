package com.lemonclient.client.clickgui;

import com.lukflug.panelstudio.widget.ITextFieldKeys;

public class TextFieldKeys implements ITextFieldKeys {
   public boolean isBackspaceKey(int scancode) {
      return scancode == 14;
   }

   public boolean isDeleteKey(int scancode) {
      return scancode == 211;
   }

   public boolean isInsertKey(int scancode) {
      return scancode == 210;
   }

   public boolean isLeftKey(int scancode) {
      return scancode == 203;
   }

   public boolean isRightKey(int scancode) {
      return scancode == 205;
   }

   public boolean isHomeKey(int scancode) {
      return scancode == 199;
   }

   public boolean isEndKey(int scancode) {
      return scancode == 207;
   }

   public boolean isCopyKey(int scancode) {
      return scancode == 46;
   }

   public boolean isPasteKey(int scancode) {
      return scancode == 47;
   }

   public boolean isCutKey(int scancode) {
      return scancode == 45;
   }

   public boolean isAllKey(int scancode) {
      return scancode == 30;
   }
}
