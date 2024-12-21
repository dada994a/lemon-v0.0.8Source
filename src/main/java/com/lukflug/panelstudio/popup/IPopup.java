package com.lukflug.panelstudio.popup;

import com.lukflug.panelstudio.base.IInterface;
import java.awt.Rectangle;

@FunctionalInterface
public interface IPopup {
   void setPosition(IInterface var1, Rectangle var2, Rectangle var3, IPopupPositioner var4);
}
