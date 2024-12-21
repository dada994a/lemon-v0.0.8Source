package com.lukflug.panelstudio.container;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;

public class VerticalContainer extends Container<IComponent> {
   public VerticalContainer(ILabeled label, IContainerRenderer renderer) {
      super(label, renderer);
   }

   protected void doContextSensitiveLoop(Context context, Container.ContextSensitiveConsumer<IComponent> function) {
      AtomicInteger posy = new AtomicInteger(this.renderer.getTop());
      this.doContextlessLoop((component) -> {
         Context subContext = this.getSubContext(context, posy.get());
         function.accept(subContext, component);
         if (subContext.focusReleased()) {
            context.releaseFocus();
         } else if (subContext.foucsRequested()) {
            context.requestFocus();
         }

         posy.addAndGet(subContext.getSize().height + this.renderer.getBorder());
      });
      context.setHeight(posy.get() - this.renderer.getBorder() + this.renderer.getBottom());
   }

   protected Context getSubContext(Context context, int posy) {
      return new Context(context, context.getSize().width - this.renderer.getLeft() - this.renderer.getRight(), new Point(this.renderer.getLeft(), posy), this.hasFocus(context), true);
   }

   protected boolean hasFocus(Context context) {
      return context.hasFocus();
   }
}
