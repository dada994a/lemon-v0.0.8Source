package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.config.IPanelConfig;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import java.awt.Point;
import java.awt.Rectangle;

@FunctionalInterface
public interface IFixedComponentProxy<T extends IFixedComponent> extends IComponentProxy<T>, IFixedComponent {
   default Point getPosition(IInterface inter) {
      return ((IFixedComponent)this.getComponent()).getPosition(inter);
   }

   default void setPosition(IInterface inter, Point position) {
      ((IFixedComponent)this.getComponent()).setPosition(inter, position);
   }

   default void setPosition(IInterface inter, Rectangle component, Rectangle panel, IPopupPositioner positioner) {
      ((IFixedComponent)this.getComponent()).setPosition(inter, component, panel, positioner);
   }

   default int getWidth(IInterface inter) {
      return ((IFixedComponent)this.getComponent()).getWidth(inter);
   }

   default boolean savesState() {
      return ((IFixedComponent)this.getComponent()).savesState();
   }

   default void saveConfig(IInterface inter, IPanelConfig config) {
      ((IFixedComponent)this.getComponent()).saveConfig(inter, config);
   }

   default void loadConfig(IInterface inter, IPanelConfig config) {
      ((IFixedComponent)this.getComponent()).loadConfig(inter, config);
   }

   default String getConfigName() {
      return ((IFixedComponent)this.getComponent()).getConfigName();
   }
}
