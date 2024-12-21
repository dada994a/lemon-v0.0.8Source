package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;

public interface IButtonRenderer<T> {
   void renderButton(Context var1, String var2, boolean var3, T var4);

   int getDefaultHeight();
}
