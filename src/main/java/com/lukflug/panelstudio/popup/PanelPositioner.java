package com.lukflug.panelstudio.popup;

import com.lukflug.panelstudio.base.IInterface;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class PanelPositioner implements IPopupPositioner {
   protected Point offset;

   public PanelPositioner(Point offset) {
      this.offset = offset;
   }

   public Point getPosition(IInterface inter, Dimension popup, Rectangle component, Rectangle panel) {
      return new Point(panel.x + panel.width + this.offset.x, component.y + this.offset.y);
   }
}
