package com.lukflug.panelstudio.container;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.Description;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.config.IConfigList;
import com.lukflug.panelstudio.config.IPanelConfig;
import com.lukflug.panelstudio.popup.IPopup;
import com.lukflug.panelstudio.popup.IPopupDisplayer;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class FixedContainer extends Container<IFixedComponent> implements IPopupDisplayer {
   protected boolean clip;
   protected List<FixedContainer.PopupPair> popups = new ArrayList();

   public FixedContainer(ILabeled label, IContainerRenderer renderer, boolean clip) {
      super(label, renderer);
      this.clip = clip;
   }

   public void displayPopup(IPopup popup, Rectangle rect, IToggleable visible, IPopupPositioner positioner) {
      this.popups.add(new FixedContainer.PopupPair(popup, rect, visible, positioner));
   }

   public void render(Context context) {
      context.setHeight(this.getHeight());
      if (this.clip) {
         context.getInterface().window(context.getRect());
      }

      if (this.renderer != null) {
         this.renderer.renderBackground(context, context.hasFocus());
      }

      AtomicReference<IFixedComponent> highest = new AtomicReference((Object)null);
      AtomicReference<IFixedComponent> first = new AtomicReference((Object)null);
      this.doContextlessLoop((component) -> {
         if (first.get() == null) {
            first.set(component);
         }

         Context subContext = this.getSubContext(context, component, true, true);
         component.getHeight(subContext);
         if (subContext.isHovered() && highest.get() == null) {
            highest.set(component);
         }

      });
      AtomicBoolean highestReached = new AtomicBoolean(false);
      if (highest.get() == null) {
         highestReached.set(true);
      }

      AtomicReference<IFixedComponent> focusComponent = new AtomicReference((Object)null);
      super.doContextlessLoop((component) -> {
         if (component == highest.get()) {
            highestReached.set(true);
         }

         Context subContext = this.getSubContext(context, component, component == first.get(), highestReached.get());
         component.render(subContext);
         if (subContext.focusReleased()) {
            context.releaseFocus();
         } else if (subContext.foucsRequested()) {
            focusComponent.set(component);
            context.requestFocus();
         }

         if (subContext.isHovered() && subContext.getDescription() != null) {
            context.setDescription(new Description(subContext.getDescription(), subContext.getRect()));
         }

         Iterator var8 = this.popups.iterator();

         while(var8.hasNext()) {
            FixedContainer.PopupPair popup = (FixedContainer.PopupPair)var8.next();
            popup.popup.setPosition(context.getInterface(), popup.rect, subContext.getRect(), popup.positioner);
            if (!popup.visible.isOn()) {
               popup.visible.toggle();
            }

            if (popup.popup instanceof IFixedComponent) {
               focusComponent.set((IFixedComponent)popup.popup);
            }
         }

         this.popups.clear();
      });
      if (focusComponent.get() != null && this.removeComponent((IComponent)focusComponent.get())) {
         this.addComponent((IComponent)focusComponent.get());
      }

      if (context.getDescription() == null && this.label.getDescription() != null) {
         context.setDescription(new Description(context.getRect(), this.label.getDescription()));
      }

      if (this.clip) {
         context.getInterface().restore();
      }

   }

   protected void doContextlessLoop(Consumer<IFixedComponent> function) {
      List<Container<IFixedComponent>.ComponentState> components = new ArrayList();
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

      for(int i = components.size() - 1; i >= 0; --i) {
         state = (Container.ComponentState)components.get(i);
         if (state.lastVisible()) {
            function.accept(state.component);
         }
      }

   }

   protected void doContextSensitiveLoop(Context context, Container.ContextSensitiveConsumer<IFixedComponent> function) {
      context.setHeight(this.getHeight());
      AtomicBoolean highest = new AtomicBoolean(true);
      AtomicBoolean first = new AtomicBoolean(true);
      AtomicReference<IFixedComponent> focusComponent = new AtomicReference((Object)null);
      this.doContextlessLoop((component) -> {
         Context subContext = this.getSubContext(context, component, first.get(), highest.get());
         first.set(false);
         function.accept(subContext, component);
         if (subContext.focusReleased()) {
            context.releaseFocus();
         } else if (subContext.foucsRequested()) {
            focusComponent.set(component);
            context.requestFocus();
         }

         if (subContext.isHovered()) {
            highest.set(false);
         }

         Iterator var8 = this.popups.iterator();

         while(var8.hasNext()) {
            FixedContainer.PopupPair popup = (FixedContainer.PopupPair)var8.next();
            popup.popup.setPosition(context.getInterface(), popup.rect, subContext.getRect(), popup.positioner);
            if (!popup.visible.isOn()) {
               popup.visible.toggle();
            }

            if (popup.popup instanceof IFixedComponent) {
               focusComponent.set((IFixedComponent)popup.popup);
            }
         }

         this.popups.clear();
      });
      if (focusComponent.get() != null) {
         Container<IFixedComponent>.ComponentState focusState = (Container.ComponentState)this.components.stream().filter((state) -> {
            return state.component == focusComponent.get();
         }).findFirst().orElse((Object)null);
         if (focusState != null) {
            this.components.remove(focusState);
            this.components.add(focusState);
         }
      }

   }

   protected Context getSubContext(Context context, IFixedComponent component, boolean focus, boolean highest) {
      Context subContext = new Context(context, component.getWidth(context.getInterface()), component.getPosition(context.getInterface()), context.hasFocus() && focus, highest);
      subContext.setPopupDisplayer(this);
      return subContext;
   }

   public void saveConfig(IInterface inter, IConfigList config) {
      config.begin(false);
      Iterator var3 = this.components.iterator();

      while(var3.hasNext()) {
         Container<IFixedComponent>.ComponentState state = (Container.ComponentState)var3.next();
         if (((IFixedComponent)state.component).savesState()) {
            IPanelConfig cf = config.addPanel(((IFixedComponent)state.component).getConfigName());
            if (cf != null) {
               ((IFixedComponent)state.component).saveConfig(inter, cf);
            }
         }
      }

      config.end(false);
   }

   public void loadConfig(IInterface inter, IConfigList config) {
      config.begin(true);
      Iterator var3 = this.components.iterator();

      while(var3.hasNext()) {
         Container<IFixedComponent>.ComponentState state = (Container.ComponentState)var3.next();
         if (((IFixedComponent)state.component).savesState()) {
            IPanelConfig cf = config.getPanel(((IFixedComponent)state.component).getConfigName());
            if (cf != null) {
               ((IFixedComponent)state.component).loadConfig(inter, cf);
            }
         }
      }

      config.end(true);
   }

   protected final class PopupPair {
      public final IPopup popup;
      public final Rectangle rect;
      public final IToggleable visible;
      public final IPopupPositioner positioner;

      public PopupPair(IPopup popup, Rectangle rect, IToggleable visible, IPopupPositioner positioner) {
         this.popup = popup;
         this.rect = rect;
         this.visible = visible;
         this.positioner = positioner;
      }
   }
}
