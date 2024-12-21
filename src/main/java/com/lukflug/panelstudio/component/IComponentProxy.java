package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.Context;
import java.util.function.Consumer;

@FunctionalInterface
public interface IComponentProxy<T extends IComponent> extends IComponent {
   default String getTitle() {
      return this.getComponent().getTitle();
   }

   default void render(Context context) {
      IComponent var10002 = this.getComponent();
      this.doOperation(context, var10002::render);
   }

   default void handleButton(Context context, int button) {
      this.doOperation(context, (subContext) -> {
         this.getComponent().handleButton(subContext, button);
      });
   }

   default void handleKey(Context context, int scancode) {
      this.doOperation(context, (subContext) -> {
         this.getComponent().handleKey(subContext, scancode);
      });
   }

   default void handleChar(Context context, char character) {
      this.doOperation(context, (subContext) -> {
         this.getComponent().handleChar(subContext, character);
      });
   }

   default void handleScroll(Context context, int diff) {
      this.doOperation(context, (subContext) -> {
         this.getComponent().handleScroll(subContext, diff);
      });
   }

   default void getHeight(Context context) {
      IComponent var10002 = this.getComponent();
      this.doOperation(context, var10002::getHeight);
   }

   default void enter() {
      this.getComponent().enter();
   }

   default void exit() {
      this.getComponent().exit();
   }

   default void releaseFocus() {
      this.getComponent().releaseFocus();
   }

   default boolean isVisible() {
      return this.getComponent().isVisible();
   }

   T getComponent();

   default Context doOperation(Context context, Consumer<Context> operation) {
      Context subContext = this.getContext(context);
      operation.accept(subContext);
      if (subContext != context) {
         if (subContext.focusReleased()) {
            context.releaseFocus();
         } else if (subContext.foucsRequested()) {
            context.requestFocus();
         }

         context.setHeight(this.getHeight(subContext.getSize().height));
         if (subContext.getDescription() != null) {
            context.setDescription(subContext.getDescription());
         }
      }

      return subContext;
   }

   default int getHeight(int height) {
      return height;
   }

   default Context getContext(Context context) {
      return context;
   }
}
