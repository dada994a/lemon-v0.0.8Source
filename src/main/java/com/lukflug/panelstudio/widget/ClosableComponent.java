package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.AnimatedToggleable;
import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.ConstantToggleable;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.component.CollapsibleComponent;
import com.lukflug.panelstudio.component.ComponentProxy;
import com.lukflug.panelstudio.component.DraggableComponent;
import com.lukflug.panelstudio.component.FixedComponent;
import com.lukflug.panelstudio.component.FocusableComponentProxy;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.component.PopupComponent;
import com.lukflug.panelstudio.container.VerticalContainer;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.IPanelRenderer;
import com.lukflug.panelstudio.theme.RendererTuple;
import java.awt.Point;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class ClosableComponent<S extends IComponent, T extends IComponent> extends FocusableComponentProxy<VerticalContainer> {
   protected final S title;
   protected final CollapsibleComponent<T> collapsible;
   protected final VerticalContainer container;

   public <U> ClosableComponent(S title, final T content, final Supplier<U> state, final AnimatedToggleable open, final IPanelRenderer<U> panelRenderer, boolean focus) {
      super(focus);
      this.title = title;
      this.container = new VerticalContainer(new Labeled(content.getTitle(), (String)null, () -> {
         return content.isVisible();
      }), panelRenderer) {
         public void render(Context context) {
            super.render(context);
            panelRenderer.renderPanelOverlay(context, this.hasFocus(context), state.get(), open.isOn());
         }

         protected boolean hasFocus(Context context) {
            return ClosableComponent.this.hasFocus(context);
         }
      };
      this.collapsible = new CollapsibleComponent<T>(open) {
         public T getComponent() {
            return content;
         }
      };
      this.container.addComponent(new ComponentProxy<IComponent>(title) {
         public void render(Context context) {
            super.render(context);
            panelRenderer.renderTitleOverlay(context, ClosableComponent.this.hasFocus(context), state.get(), open.isOn());
         }

         public void handleButton(Context context, int button) {
            super.handleButton(context, button);
            if (button == 1 && context.isClicked(button)) {
               ClosableComponent.this.collapsible.getToggle().toggle();
            }

         }
      });
      this.container.addComponent(this.collapsible);
   }

   public final VerticalContainer getComponent() {
      return this.container;
   }

   public IComponent getTitleBar() {
      return this.title;
   }

   public CollapsibleComponent<T> getCollapsible() {
      return this.collapsible;
   }

   public static <S extends IComponent, T extends IComponent, U> DraggableComponent<FixedComponent<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>>> createStaticPopup(S title, T content, Supplier<U> state, Animation animation, RendererTuple<U> renderer, IScrollSize popupSize, final IToggleable shown, final IntSupplier widthSupplier, final boolean savesState, final String configName, final boolean closeOnClick) {
      final AtomicReference<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>> panel = new AtomicReference((Object)null);
      DraggableComponent<FixedComponent<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>>> draggable = new DraggableComponent<FixedComponent<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>>>() {
         FixedComponent<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>> fixedComponent = null;

         public void handleButton(Context context, int button) {
            super.handleButton(context, button);
            if (context.getInterface().getButton(button) && (!context.isHovered() || closeOnClick) && shown.isOn()) {
               shown.toggle();
            }

         }

         public boolean isVisible() {
            return super.isVisible() && shown.isOn();
         }

         public FixedComponent<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>> getComponent() {
            if (this.fixedComponent == null) {
               this.fixedComponent = new FixedComponent<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>>((ClosableComponent)panel.get(), new Point(0, 0), widthSupplier.getAsInt(), ((ClosableComponent)panel.get()).getCollapsible().getToggle(), savesState, configName) {
                  public int getWidth(IInterface inter) {
                     return widthSupplier.getAsInt();
                  }
               };
            }

            return this.fixedComponent;
         }
      };
      panel.set(createScrollableComponent(draggable.getWrappedDragComponent(title), content, state, new AnimatedToggleable(new ConstantToggleable(true), animation), renderer, popupSize, true));
      return draggable;
   }

   public static <S extends IComponent, T extends IComponent, U> PopupComponent<ClosableComponent<S, ScrollBarComponent<U, T>>> createDynamicPopup(S title, T content, Supplier<U> state, Animation animation, RendererTuple<U> renderer, IScrollSize popupSize, final IToggleable shown, int width) {
      ClosableComponent<S, ScrollBarComponent<U, T>> panel = createScrollableComponent(title, content, state, new AnimatedToggleable(new ConstantToggleable(true), animation), renderer, popupSize, true);
      return new PopupComponent<ClosableComponent<S, ScrollBarComponent<U, T>>>(panel, width) {
         public void handleButton(Context context, int button) {
            this.doOperation(context, (subContext) -> {
               ((ClosableComponent)this.getComponent()).handleButton(subContext, button);
            });
            if (context.getInterface().getButton(button) && !context.isHovered() && shown.isOn()) {
               shown.toggle();
            }

         }

         public boolean isVisible() {
            return ((ClosableComponent)this.getComponent()).isVisible() && shown.isOn();
         }
      };
   }

   public static <S extends IComponent, T extends IComponent, U> DraggableComponent<FixedComponent<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>>> createDraggableComponent(S title, T content, Supplier<U> state, AnimatedToggleable open, RendererTuple<U> renderer, IScrollSize scrollSize, Point position, int width, boolean savesState, String configName) {
      AtomicReference<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>> panel = new AtomicReference((Object)null);
      DraggableComponent<FixedComponent<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>>> draggable = createDraggableComponent(() -> {
         return (ClosableComponent)panel.get();
      }, position, width, savesState, configName);
      panel.set(createScrollableComponent(draggable.getWrappedDragComponent(title), content, state, open, renderer, scrollSize, false));
      return draggable;
   }

   public static <S extends IComponent, T extends IComponent, U> DraggableComponent<FixedComponent<ClosableComponent<S, T>>> createDraggableComponent(final Supplier<ClosableComponent<S, T>> panel, final Point position, final int width, final boolean savesState, final String configName) {
      return new DraggableComponent<FixedComponent<ClosableComponent<S, T>>>() {
         FixedComponent<ClosableComponent<S, T>> fixedComponent = null;

         public FixedComponent<ClosableComponent<S, T>> getComponent() {
            if (this.fixedComponent == null) {
               this.fixedComponent = new FixedComponent((IComponent)panel.get(), position, width, ((ClosableComponent)panel.get()).getCollapsible().getToggle(), savesState, configName);
            }

            return this.fixedComponent;
         }
      };
   }

   public static <S extends IComponent, T extends IComponent, U> ClosableComponent<S, ScrollBarComponent<U, T>> createScrollableComponent(S title, T content, final Supplier<U> state, AnimatedToggleable open, RendererTuple<U> renderer, final IScrollSize scrollSize, boolean focus) {
      return new ClosableComponent(title, new ScrollBarComponent<U, T>(content, renderer.scrollRenderer, renderer.cornerRenderer, renderer.emptyRenderer) {
         public int getScrollHeight(Context context, int componentHeight) {
            return scrollSize.getScrollHeight(context, componentHeight);
         }

         public int getComponentWidth(Context context) {
            return scrollSize.getComponentWidth(context);
         }

         protected U getState() {
            return state.get();
         }
      }, state, open, renderer.panelRenderer, focus);
   }
}
