package com.lukflug.panelstudio.layout;

import com.lukflug.panelstudio.base.AnimatedToggleable;
import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.HorizontalComponent;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.component.IResizable;
import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.container.HorizontalContainer;
import com.lukflug.panelstudio.container.IContainer;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.ITheme;
import com.lukflug.panelstudio.theme.RendererTuple;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.ResizableComponent;
import com.lukflug.panelstudio.widget.ScrollBarComponent;
import java.awt.Point;
import java.util.function.Supplier;

public class SinglePanelAdder implements IComponentAdder {
   protected IContainer<? super IFixedComponent> container;
   protected IBoolean isVisible;
   protected HorizontalContainer title;
   protected HorizontalContainer content;
   protected final IScrollSize size;

   public SinglePanelAdder(IContainer<? super IFixedComponent> container, ILabeled label, ITheme theme, Point position, int width, Supplier<Animation> animation, IBoolean isVisible, String configName) {
      this.container = container;
      this.isVisible = isVisible;
      this.title = new HorizontalContainer(label, theme.getContainerRenderer(-1, -1, true));
      this.content = new HorizontalContainer(label, theme.getContainerRenderer(-1, -1, true));
      AnimatedToggleable toggle = new AnimatedToggleable(new SimpleToggleable(true), (Animation)animation.get());
      RendererTuple<Void> renderer = new RendererTuple(Void.class, new ThemeTuple(theme, -1, -1));
      IResizable size = this.getResizable(width);
      this.size = this.getScrollSize(size);
      container.addComponent(ResizableComponent.createResizableComponent(this.title, this.content, () -> {
         return null;
      }, toggle, renderer, theme.getResizeRenderer(), size, new IScrollSize() {
         public int getComponentWidth(Context context) {
            return SinglePanelAdder.this.size.getComponentWidth(context);
         }
      }, position, width, true, configName), isVisible);
   }

   public <S extends IComponent, T extends IComponent> void addComponent(S title, T content, ThemeTuple theme, Point position, int width, Supplier<Animation> animation) {
      this.title.addComponent(new HorizontalComponent(title, 0, 1));
      this.content.addComponent(new HorizontalComponent(new ScrollBarComponent<Void, T>(content, theme.getScrollBarRenderer(Void.class), theme.getEmptySpaceRenderer(Void.class, false), theme.getEmptySpaceRenderer(Void.class, true)) {
         public int getScrollHeight(Context context, int componentHeight) {
            return SinglePanelAdder.this.size.getScrollHeight(context, componentHeight);
         }

         protected Void getState() {
            return null;
         }
      }, 0, 1));
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
