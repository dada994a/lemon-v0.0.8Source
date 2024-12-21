package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;

@FunctionalInterface
public interface IEmptySpaceRenderer<T> {
   void renderSpace(Context var1, boolean var2, T var3);
}
