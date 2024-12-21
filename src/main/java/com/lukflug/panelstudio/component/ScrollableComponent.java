package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.Context;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.function.Consumer;

public abstract class ScrollableComponent<T extends IComponent> implements IComponentProxy<T>, IScrollSize {
   private Context tempContext;
   protected Point scrollPos = new Point(0, 0);
   protected Point nextScrollPos = null;
   protected Dimension contentSize = new Dimension(0, 0);
   protected Dimension scrollSize = new Dimension(0, 0);

   public void render(Context context) {
      this.doOperation(context, (subContext) -> {
         context.getInterface().window(context.getRect());
         this.getComponent().render(subContext);
         Rectangle a = context.getRect();
         Rectangle b = subContext.getRect();
         if (b.width < a.width) {
            this.fillEmptySpace(context, new Rectangle(a.x + b.width, a.y, a.width - b.width, b.height));
         }

         if (b.height < a.height) {
            this.fillEmptySpace(context, new Rectangle(a.x, a.y + b.height, b.width, a.height - b.height));
         }

         if (b.width < a.width && b.height < a.height) {
            this.fillEmptySpace(context, new Rectangle(a.x + b.width, a.y + b.height, a.width - b.width, a.height - b.height));
         }

         context.getInterface().restore();
      });
   }

   public void handleScroll(Context context, int diff) {
      Context sContext = this.doOperation(context, (subContext) -> {
         this.getComponent().handleScroll(subContext, diff);
      });
      if (context.isHovered()) {
         if (this.isScrollingY()) {
            this.scrollPos.translate(0, diff);
         } else if (this.isScrollingX()) {
            this.scrollPos.translate(diff, 0);
         }

         this.clampScrollPos(context.getSize(), sContext.getSize());
      }

   }

   public Context doOperation(Context context, Consumer<Context> operation) {
      this.tempContext = context;
      Context subContext = IComponentProxy.super.doOperation(context, operation);
      if (this.nextScrollPos != null) {
         this.scrollPos = this.nextScrollPos;
         this.nextScrollPos = null;
      }

      this.clampScrollPos(context.getSize(), subContext.getSize());
      this.contentSize = subContext.getSize();
      this.scrollSize = context.getSize();
      return subContext;
   }

   public Context getContext(Context context) {
      Context subContext = new Context(context, context.getSize().width, new Point(-this.scrollPos.x, -this.scrollPos.y), true, true);
      this.getComponent().getHeight(subContext);
      int height = this.getScrollHeight(context, subContext.getSize().height);
      context.setHeight(height);
      return new Context(context, this.getComponentWidth(context), new Point(-this.scrollPos.x, -this.scrollPos.y), true, context.isHovered());
   }

   public Point getScrollPos() {
      return new Point(this.scrollPos);
   }

   public void setScrollPosX(int scrollPos) {
      if (this.nextScrollPos == null) {
         this.nextScrollPos = new Point(scrollPos, this.scrollPos.y);
      } else {
         this.nextScrollPos.x = scrollPos;
      }

   }

   public void setScrollPosY(int scrollPos) {
      if (this.nextScrollPos == null) {
         this.nextScrollPos = new Point(this.scrollPos.x, scrollPos);
      } else {
         this.nextScrollPos.y = scrollPos;
      }

   }

   public Dimension getContentSize() {
      return this.contentSize;
   }

   public Dimension getScrollSize() {
      return this.scrollSize;
   }

   public boolean isScrollingX() {
      return this.contentSize.width > this.scrollSize.width;
   }

   public boolean isScrollingY() {
      return this.contentSize.height > this.scrollSize.height;
   }

   protected void clampScrollPos(Dimension scrollSize, Dimension contentSize) {
      if (this.scrollPos.x > contentSize.width - scrollSize.width) {
         this.scrollPos.x = contentSize.width - scrollSize.width;
      }

      if (this.scrollPos.x < 0) {
         this.scrollPos.x = 0;
      }

      if (this.scrollPos.y > contentSize.height - scrollSize.height) {
         this.scrollPos.y = contentSize.height - scrollSize.height;
      }

      if (this.scrollPos.y < 0) {
         this.scrollPos.y = 0;
      }

   }

   public final int getHeight(int height) {
      return this.getScrollHeight(this.tempContext, height);
   }

   public abstract void fillEmptySpace(Context var1, Rectangle var2);
}
