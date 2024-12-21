package com.lukflug.panelstudio.tabgui;

import com.lukflug.panelstudio.popup.IPopupPositioner;

public interface ITabGUITheme {
   int getTabWidth();

   IPopupPositioner getPositioner();

   ITabGUIRenderer<Void> getParentRenderer();

   ITabGUIRenderer<Boolean> getChildRenderer();
}
