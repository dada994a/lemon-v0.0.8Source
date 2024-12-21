package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.Context;

public interface IComponent {
   String getTitle();

   void render(Context var1);

   void handleButton(Context var1, int var2);

   void handleKey(Context var1, int var2);

   void handleChar(Context var1, char var2);

   void handleScroll(Context var1, int var2);

   void getHeight(Context var1);

   void enter();

   void exit();

   void releaseFocus();

   boolean isVisible();
}
