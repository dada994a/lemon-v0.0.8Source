package com.lukflug.panelstudio.layout;

import com.lukflug.panelstudio.base.AnimatedToggleable;
import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.ComponentProxy;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.container.VerticalContainer;
import com.lukflug.panelstudio.popup.IPopup;
import com.lukflug.panelstudio.popup.PopupTuple;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.RendererTuple;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.Button;
import com.lukflug.panelstudio.widget.ClosableComponent;
import java.util.function.Supplier;

public class ChildUtil {
   protected int width;
   protected Supplier<Animation> animation;
   protected PopupTuple popupType;

   public ChildUtil(int width, Supplier<Animation> animation, PopupTuple popupType) {
      this.width = width;
      this.animation = animation;
      this.popupType = popupType;
   }

   protected <T> void addContainer(ILabeled label, IComponent title, IComponent container, Supplier<T> state, Class<T> stateClass, VerticalContainer parent, IComponentAdder gui, ThemeTuple theme, ChildUtil.ChildMode mode) {
      boolean drawTitle = mode == ChildUtil.ChildMode.DRAG_POPUP;
      switch(mode) {
      case DOWN:
         parent.addComponent(new ClosableComponent(title, container, state, new AnimatedToggleable(new SimpleToggleable(false), (Animation)this.animation.get()), theme.getPanelRenderer(stateClass), false));
         break;
      case POPUP:
      case DRAG_POPUP:
         final IToggleable toggle = new SimpleToggleable(false);
         Button<T> button = new Button(new Labeled(label.getDisplayName(), label.getDescription(), () -> {
            return drawTitle && label.isVisible().isOn();
         }), state, theme.getButtonRenderer(stateClass, true));
         final Object popup;
         if (this.popupType.dynamicPopup) {
            popup = ClosableComponent.createDynamicPopup(button, container, state, (Animation)this.animation.get(), new RendererTuple(stateClass, theme), this.popupType.popupSize, toggle, this.width);
         } else {
            popup = ClosableComponent.createStaticPopup(button, container, state, (Animation)this.animation.get(), new RendererTuple(stateClass, theme), this.popupType.popupSize, toggle, () -> {
               return this.width;
            }, false, "", false);
         }

         parent.addComponent(new ComponentProxy<IComponent>(title) {
            public void handleButton(Context context, int button) {
               super.handleButton(context, button);
               if (button == 1 && context.isClicked(button)) {
                  context.getPopupDisplayer().displayPopup((IPopup)popup, context.getRect(), toggle, ChildUtil.this.popupType.popupPos);
                  context.releaseFocus();
               }

            }
         });
         gui.addPopup((IFixedComponent)popup);
      }

   }

   public static enum ChildMode {
      DOWN,
      POPUP,
      DRAG_POPUP;
   }
}
