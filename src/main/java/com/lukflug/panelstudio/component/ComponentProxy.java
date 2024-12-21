package com.lukflug.panelstudio.component;

public class ComponentProxy<T extends IComponent> implements IComponentProxy<T> {
   protected final T component;

   public ComponentProxy(T component) {
      this.component = component;
   }

   public T getComponent() {
      return this.component;
   }
}
