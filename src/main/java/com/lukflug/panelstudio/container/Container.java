package com.lukflug.panelstudio.container;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.Description;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.component.ComponentBase;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public abstract class Container<T extends IComponent> extends ComponentBase implements IContainer<T> {
   protected List<Container<T>.ComponentState> components = new ArrayList();
   protected IContainerRenderer renderer;
   private boolean visible;

   public Container(ILabeled label, IContainerRenderer renderer) {
      super(label);
      this.renderer = renderer;
   }

   public boolean addComponent(T component) {
      if (this.getComponentState(component) == null) {
         this.components.add(new Container.ComponentState(component, this.getDefaultVisibility()));
         return true;
      } else {
         return false;
      }
   }

   public boolean addComponent(T component, IBoolean visible) {
      if (this.getComponentState(component) == null) {
         this.components.add(new Container.ComponentState(component, visible));
         return true;
      } else {
         return false;
      }
   }

   public boolean removeComponent(T component) {
      Container<T>.ComponentState state = this.getComponentState(component);
      if (state != null) {
         this.components.remove(state);
         if (state.lastVisible) {
            state.component.exit();
         }

         return true;
      } else {
         return false;
      }
   }

   public void render(Context context) {
      this.getHeight(context);
      if (this.renderer != null) {
         this.renderer.renderBackground(context, context.hasFocus());
      }

      this.doContextSensitiveLoop(context, (subContext, component) -> {
         component.render(subContext);
         if (subContext.isHovered() && subContext.getDescription() != null) {
            context.setDescription(new Description(subContext.getDescription(), subContext.getRect()));
         }

      });
      if (context.getDescription() == null && this.label.getDescription() != null) {
         context.setDescription(new Description(context.getRect(), this.label.getDescription()));
      }

   }

   public void handleButton(Context context, int button) {
      this.doContextSensitiveLoop(context, (subContext, component) -> {
         component.handleButton(subContext, button);
      });
   }

   public void handleKey(Context context, int scancode) {
      this.doContextSensitiveLoop(context, (subContext, component) -> {
         component.handleKey(subContext, scancode);
      });
   }

   public void handleChar(Context context, char character) {
      this.doContextSensitiveLoop(context, (subContext, component) -> {
         component.handleChar(subContext, character);
      });
   }

   public void handleScroll(Context context, int diff) {
      this.doContextSensitiveLoop(context, (subContext, component) -> {
         component.handleScroll(subContext, diff);
      });
   }

   public void getHeight(Context context) {
      this.doContextSensitiveLoop(context, (subContext, component) -> {
         component.getHeight(subContext);
      });
   }

   public void enter() {
      this.visible = true;
      this.doContextlessLoop((component) -> {
      });
   }

   public void exit() {
      this.visible = false;
      this.doContextlessLoop((component) -> {
      });
   }

   public void releaseFocus() {
      this.doContextlessLoop(IComponent::releaseFocus);
   }

   protected int getHeight() {
      return 0;
   }

   protected Container<T>.ComponentState getComponentState(T component) {
      Iterator var2 = this.components.iterator();

      Container.ComponentState state;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         state = (Container.ComponentState)var2.next();
      } while(state.component != component);

      return state;
   }

   protected void doContextlessLoop(Consumer<T> function) {
      List<Container<T>.ComponentState> components = new ArrayList();
      Iterator var3 = this.components.iterator();

      Container.ComponentState state;
      while(var3.hasNext()) {
         state = (Container.ComponentState)var3.next();
         components.add(state);
      }

      var3 = components.iterator();

      while(var3.hasNext()) {
         state = (Container.ComponentState)var3.next();
         state.update();
      }

      var3 = components.iterator();

      while(var3.hasNext()) {
         state = (Container.ComponentState)var3.next();
         if (state.lastVisible()) {
            function.accept(state.component);
         }
      }

   }

   protected abstract void doContextSensitiveLoop(Context var1, Container.ContextSensitiveConsumer<T> var2);

   protected IBoolean getDefaultVisibility() {
      return () -> {
         return true;
      };
   }

   @FunctionalInterface
   protected interface ContextSensitiveConsumer<T extends IComponent> {
      void accept(Context var1, T var2);
   }

   protected final class ComponentState {
      public final T component;
      public final IBoolean externalVisibility;
      private boolean lastVisible = false;

      public ComponentState(T component, IBoolean externalVisibility) {
         this.component = component;
         this.externalVisibility = externalVisibility;
         this.update();
      }

      public void update() {
         if ((this.component.isVisible() && this.externalVisibility.isOn() && Container.this.visible) != this.lastVisible) {
            if (this.lastVisible) {
               this.lastVisible = false;
               this.component.exit();
            } else {
               this.lastVisible = true;
               this.component.enter();
            }
         }

      }

      public boolean lastVisible() {
         return this.lastVisible;
      }
   }
}
