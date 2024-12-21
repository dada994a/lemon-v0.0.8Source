package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.popup.IPopup;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import java.awt.Point;
import java.awt.Rectangle;

public class PopupComponent<T extends IComponent> extends FixedComponent<T> implements IPopup {
   protected Rectangle component;
   protected Rectangle panel;
   protected IPopupPositioner positioner;

   public PopupComponent(T component, int width) {
      super(component, new Point(0, 0), width, (IToggleable)null, false, "");
   }

   public Point getPosition(IInterface inter) {
      Context temp = new Context(inter, this.width, this.position, true, true);
      this.getHeight(temp);
      return this.positioner.getPosition(inter, temp.getSize(), this.component, this.panel);
   }

   public void setPosition(IInterface inter, Rectangle component, Rectangle panel, IPopupPositioner positioner) {
      this.component = component;
      this.panel = panel;
      this.positioner = positioner;
   }
}
