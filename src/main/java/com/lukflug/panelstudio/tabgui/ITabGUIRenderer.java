package com.lukflug.panelstudio.tabgui;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import java.awt.Rectangle;

public interface ITabGUIRenderer<T> {
   void renderTab(Context var1, int var2, double var3);

   void renderItem(Context var1, int var2, double var3, int var5, String var6, T var7);

   int getTabHeight(int var1);

   Rectangle getItemRect(IInterface var1, Rectangle var2, int var3, double var4);
}
