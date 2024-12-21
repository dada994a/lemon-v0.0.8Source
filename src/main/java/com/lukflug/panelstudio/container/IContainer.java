package com.lukflug.panelstudio.container;

import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.component.IComponent;

public interface IContainer<T extends IComponent> {
   boolean addComponent(T var1);

   boolean addComponent(T var1, IBoolean var2);

   boolean removeComponent(T var1);
}
