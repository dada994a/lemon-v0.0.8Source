package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.config.IPanelConfig;
import com.lukflug.panelstudio.popup.IPopup;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public interface IFixedComponent extends IComponent, IPopup {
   Point getPosition(IInterface var1);

   void setPosition(IInterface var1, Point var2);

   default void setPosition(IInterface inter, Rectangle component, Rectangle panel, IPopupPositioner positioner) {
      this.setPosition(inter, positioner.getPosition(inter, (Dimension)null, component, panel));
   }

   int getWidth(IInterface var1);

   boolean savesState();

   void saveConfig(IInterface var1, IPanelConfig var2);

   void loadConfig(IInterface var1, IPanelConfig var2);

   String getConfigName();
}
