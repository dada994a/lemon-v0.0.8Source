package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.IInterface;

public class HorizontalComponent<T extends IComponent> extends ComponentProxy<T> implements IHorizontalComponent {
   protected int width;
   protected int weight;

   public HorizontalComponent(T component, int width, int weight) {
      super(component);
      this.width = width;
      this.weight = weight;
   }

   public int getWidth(IInterface inter) {
      return this.width;
   }

   public int getWeight() {
      return this.weight;
   }
}
