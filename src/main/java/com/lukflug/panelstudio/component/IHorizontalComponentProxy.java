package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.IInterface;

@FunctionalInterface
public interface IHorizontalComponentProxy<T extends IHorizontalComponent> extends IComponentProxy<T>, IHorizontalComponent {
   default int getWidth(IInterface inter) {
      return ((IHorizontalComponent)this.getComponent()).getWidth(inter);
   }

   default int getWeight() {
      return ((IHorizontalComponent)this.getComponent()).getWeight();
   }
}
