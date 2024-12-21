package com.lukflug.panelstudio.popup;

import com.lukflug.panelstudio.base.IInterface;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class FixedPositioner implements IPopupPositioner {
   protected Point pos;

   public FixedPositioner(Point pos) {
      this.pos = pos;
   }

   public Point getPosition(IInterface inter, Dimension popup, Rectangle component, Rectangle panel) {
      return new Point(this.pos);
   }
}
