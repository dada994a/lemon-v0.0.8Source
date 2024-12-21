package com.lukflug.panelstudio.layout;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.theme.ThemeTuple;
import java.awt.Point;
import java.util.function.Supplier;

public interface IComponentAdder {
   <S extends IComponent, T extends IComponent> void addComponent(S var1, T var2, ThemeTuple var3, Point var4, int var5, Supplier<Animation> var6);

   void addPopup(IFixedComponent var1);
}
