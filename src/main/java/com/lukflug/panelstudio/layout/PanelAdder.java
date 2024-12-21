package com.lukflug.panelstudio.layout;

import com.lukflug.panelstudio.base.AnimatedToggleable;
import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.component.IResizable;
import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.container.IContainer;
import com.lukflug.panelstudio.theme.RendererTuple;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.ResizableComponent;
import java.awt.Point;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class PanelAdder implements IComponentAdder {
   protected IContainer<? super IFixedComponent> container;
   protected boolean open;
   protected IBoolean isVisible;
   protected UnaryOperator<String> configName;

   public PanelAdder(IContainer<? super IFixedComponent> container, boolean open, IBoolean isVisible, UnaryOperator<String> configName) {
      this.container = container;
      this.open = open;
      this.isVisible = isVisible;
      this.configName = configName;
   }

   public <S extends IComponent, T extends IComponent> void addComponent(S title, T content, ThemeTuple theme, Point position, int width, Supplier<Animation> animation) {
      AnimatedToggleable toggle = new AnimatedToggleable(new SimpleToggleable(this.open), (Animation)animation.get());
      RendererTuple<Void> renderer = new RendererTuple(Void.class, theme);
      IResizable size = this.getResizable(width);
      this.container.addComponent(ResizableComponent.createResizableComponent(title, content, () -> {
         return null;
      }, toggle, renderer, theme.theme.getResizeRenderer(), size, this.getScrollSize(size), position, width, true, (String)this.configName.apply(content.getTitle())), this.isVisible);
   }

   public void addPopup(IFixedComponent popup) {
      this.container.addComponent(popup, this.isVisible);
   }

   protected IResizable getResizable(int width) {
      return null;
   }

   protected IScrollSize getScrollSize(IResizable size) {
      return new IScrollSize() {
      };
   }
}
