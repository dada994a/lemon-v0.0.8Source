package com.lukflug.panelstudio.layout;

import com.lukflug.panelstudio.setting.IClient;
import com.lukflug.panelstudio.theme.ITheme;

@FunctionalInterface
public interface ILayout {
   void populateGUI(IComponentAdder var1, IComponentGenerator var2, IClient var3, ITheme var4);
}
