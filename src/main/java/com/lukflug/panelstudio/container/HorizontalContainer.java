package com.lukflug.panelstudio.container;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.component.IHorizontalComponent;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;

public class HorizontalContainer extends Container<IHorizontalComponent> {
   public HorizontalContainer(ILabeled label, IContainerRenderer renderer) {
      super(label, renderer);
   }

   protected void doContextSensitiveLoop(Context context, Container.ContextSensitiveConsumer<IHorizontalComponent> function) {
      AtomicInteger availableWidth = new AtomicInteger(context.getSize().width - this.renderer.getLeft() - this.renderer.getRight() + this.renderer.getBorder());
      AtomicInteger totalWeight = new AtomicInteger(0);
      this.doContextlessLoop((component) -> {
         availableWidth.addAndGet(-component.getWidth(context.getInterface()) - this.renderer.getBorder());
         totalWeight.addAndGet(component.getWeight());
      });
      double weightFactor = (double)availableWidth.get() / (double)totalWeight.get();
      AtomicInteger x = new AtomicInteger(this.renderer.getLeft());
      AtomicInteger spentWeight = new AtomicInteger(0);
      AtomicInteger height = new AtomicInteger(0);
      this.doContextlessLoop((component) -> {
         int start = (int)Math.round((double)spentWeight.get() * weightFactor);
         int end = (int)Math.round((double)(spentWeight.get() + component.getWeight()) * weightFactor);
         int componentWidth = component.getWidth(context.getInterface()) + end - start;
         int componentPosition = x.get() + start;
         Context subContext = this.getSubContext(context, componentPosition, componentWidth);
         function.accept(subContext, component);
         if (subContext.focusReleased()) {
            context.releaseFocus();
         } else if (subContext.foucsRequested()) {
            context.requestFocus();
         }

         x.addAndGet(component.getWidth(context.getInterface()) + this.renderer.getBorder());
         spentWeight.addAndGet(component.getWeight());
         if (subContext.getSize().height > height.get()) {
            height.set(subContext.getSize().height);
         }

      });
      context.setHeight(height.get());
   }

   protected Context getSubContext(Context context, int posx, int width) {
      return new Context(context, width, new Point(posx, this.renderer.getTop()), context.hasFocus(), true);
   }
}
