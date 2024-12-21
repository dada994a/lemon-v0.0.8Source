package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;

public interface IPanelRenderer<T> extends IContainerRenderer {
   void renderPanelOverlay(Context var1, boolean var2, T var3, boolean var4);

   void renderTitleOverlay(Context var1, boolean var2, T var3, boolean var4);
}
