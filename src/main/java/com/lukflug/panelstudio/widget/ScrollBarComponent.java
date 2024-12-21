package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.component.HorizontalComponent;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.component.ScrollableComponent;
import com.lukflug.panelstudio.container.HorizontalContainer;
import com.lukflug.panelstudio.container.VerticalContainer;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import com.lukflug.panelstudio.theme.IEmptySpaceRenderer;
import com.lukflug.panelstudio.theme.IScrollBarRenderer;
import java.awt.Rectangle;

public abstract class ScrollBarComponent<S, T extends IComponent> extends HorizontalContainer implements IScrollSize {
   protected final T component;

   public ScrollBarComponent(final T component, final IScrollBarRenderer<S> renderer, IEmptySpaceRenderer<S> cornerRenderer, final IEmptySpaceRenderer<S> emptyRenderer) {
      super(new Labeled(component.getTitle(), (String)null, () -> {
         return component.isVisible();
      }), new IContainerRenderer() {
      });
      this.component = component;
      final ScrollableComponent<T> scrollComponent = new ScrollableComponent<T>() {
         public T getComponent() {
            return component;
         }

         public int getScrollHeight(Context context, int height) {
            return ScrollBarComponent.this.getScrollHeight(context, height);
         }

         public int getComponentWidth(Context context) {
            return ScrollBarComponent.this.getComponentWidth(context);
         }

         public void fillEmptySpace(Context context, Rectangle rect) {
            Context subContext = new Context(context.getInterface(), rect.width, rect.getLocation(), context.hasFocus(), context.onTop());
            subContext.setHeight(rect.height);
            emptyRenderer.renderSpace(subContext, context.hasFocus(), ScrollBarComponent.this.getState());
         }
      };
      ScrollBar<S> verticalBar = new ScrollBar<S>(new Labeled(component.getTitle(), (String)null, () -> {
         return scrollComponent.isScrollingY();
      }), false, renderer) {
         protected int getLength() {
            return scrollComponent.getScrollSize().height;
         }

         protected int getContentHeight() {
            return scrollComponent.getContentSize().height;
         }

         protected int getScrollPosition() {
            return scrollComponent.getScrollPos().y;
         }

         protected void setScrollPosition(int position) {
            scrollComponent.setScrollPosY(position);
         }

         protected S getState() {
            return ScrollBarComponent.this.getState();
         }
      };
      ScrollBar<S> horizontalBar = new ScrollBar<S>(new Labeled(component.getTitle(), (String)null, () -> {
         return scrollComponent.isScrollingX();
      }), true, renderer) {
         protected int getLength() {
            return scrollComponent.getScrollSize().width;
         }

         protected int getContentHeight() {
            return scrollComponent.getContentSize().width;
         }

         protected int getScrollPosition() {
            return scrollComponent.getScrollPos().x;
         }

         protected void setScrollPosition(int position) {
            scrollComponent.setScrollPosX(position);
         }

         protected S getState() {
            return ScrollBarComponent.this.getState();
         }
      };
      VerticalContainer leftContainer = new VerticalContainer(new Labeled(component.getTitle(), (String)null, () -> {
         return true;
      }), new IContainerRenderer() {
      });
      leftContainer.addComponent(scrollComponent);
      leftContainer.addComponent(horizontalBar);
      VerticalContainer rightContainer = new VerticalContainer(new Labeled(component.getTitle(), (String)null, () -> {
         return true;
      }), new IContainerRenderer() {
      });
      rightContainer.addComponent(verticalBar);
      rightContainer.addComponent(new EmptySpace<S>(new Labeled("Empty", (String)null, () -> {
         return scrollComponent.isScrollingX() && scrollComponent.isScrollingY();
      }), () -> {
         return renderer.getThickness();
      }, cornerRenderer) {
         protected S getState() {
            return ScrollBarComponent.this.getState();
         }
      });
      this.addComponent(new HorizontalComponent(leftContainer, 0, 1));
      this.addComponent(new HorizontalComponent<VerticalContainer>(rightContainer, 0, 0) {
         public int getWidth(IInterface inter) {
            return renderer.getThickness();
         }
      }, () -> {
         return scrollComponent.isScrollingY();
      });
   }

   public T getContentComponent() {
      return this.component;
   }

   protected abstract S getState();
}
