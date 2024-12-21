package com.lukflug.panelstudio.hud;

import com.lukflug.panelstudio.base.AnimatedToggleable;
import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.component.ComponentProxy;
import com.lukflug.panelstudio.component.DraggableComponent;
import com.lukflug.panelstudio.component.IComponentProxy;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.config.IPanelConfig;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.IButtonRenderer;
import com.lukflug.panelstudio.theme.IButtonRendererProxy;
import com.lukflug.panelstudio.theme.IPanelRenderer;
import com.lukflug.panelstudio.theme.IPanelRendererProxy;
import com.lukflug.panelstudio.theme.ITheme;
import com.lukflug.panelstudio.widget.ClosableComponent;
import com.lukflug.panelstudio.widget.ToggleButton;
import java.awt.Point;

public class HUDPanel<T extends IFixedComponent> extends DraggableComponent<HUDPanel<T>.HUDPanelComponent> {
   protected T component;
   protected HUDPanel<T>.HUDPanelComponent panel;
   protected IBoolean renderState;

   public HUDPanel(T component, IToggleable state, Animation animation, ITheme theme, IBoolean renderState, int border) {
      this.component = component;
      this.panel = new HUDPanel.HUDPanelComponent(state, animation, theme, renderState, border);
      this.renderState = renderState;
   }

   public HUDPanel<T>.HUDPanelComponent getComponent() {
      return this.panel;
   }

   public void handleButton(Context context, int button) {
      if (this.renderState.isOn()) {
         super.handleButton(context, button);
      } else {
         super.getHeight(context);
      }

   }

   public void handleScroll(Context context, int diff) {
      if (this.renderState.isOn()) {
         super.handleScroll(context, diff);
      } else {
         super.getHeight(context);
      }

   }

   protected class HUDPanelComponent implements IFixedComponent, IComponentProxy<ComponentProxy<ClosableComponent<ToggleButton, ComponentProxy<T>>>> {
      protected ComponentProxy<ClosableComponent<ToggleButton, ComponentProxy<T>>> closable;
      protected IButtonRenderer<Boolean> titleRenderer;
      protected IPanelRenderer<Boolean> panelRenderer;
      protected int border;

      public HUDPanelComponent(final IToggleable state, Animation animation, ITheme theme, final IBoolean renderState, final int border) {
         this.border = border;
         this.panelRenderer = theme.getPanelRenderer(Boolean.class, 0, 0);
         this.titleRenderer = theme.getButtonRenderer(Boolean.class, 0, 0, true);
         this.closable = HUDPanel.this.getWrappedDragComponent(new ClosableComponent(new ToggleButton(new Labeled(HUDPanel.this.component.getTitle(), (String)null, () -> {
            return HUDPanel.this.component.isVisible();
         }), new IToggleable() {
            public boolean isOn() {
               return state.isOn();
            }

            public void toggle() {
            }
         }, new IButtonRendererProxy<Boolean>() {
            public void renderButton(Context context, String title, boolean focus, Boolean state) {
               if (renderState.isOn()) {
                  IButtonRendererProxy.super.renderButton(context, title, focus, state);
               }

            }

            public IButtonRenderer<Boolean> getRenderer() {
               return HUDPanelComponent.this.titleRenderer;
            }
         }), new ComponentProxy<T>(HUDPanel.this.component) {
            public int getHeight(int height) {
               return height + 2 * border;
            }

            public Context getContext(Context context) {
               return new Context(context, context.getSize().width - 2 * border, new Point(border, border), context.hasFocus(), context.onTop());
            }
         }, () -> {
            return state.isOn();
         }, new AnimatedToggleable(state, animation), new IPanelRendererProxy<Boolean>() {
            public void renderBackground(Context context, boolean focus) {
               if (renderState.isOn()) {
                  IPanelRendererProxy.super.renderBackground(context, focus);
               }

            }

            public void renderPanelOverlay(Context context, boolean focus, Boolean state, boolean open) {
               if (renderState.isOn()) {
                  IPanelRendererProxy.super.renderPanelOverlay(context, focus, state, open);
               }

            }

            public void renderTitleOverlay(Context context, boolean focus, Boolean state, boolean open) {
               if (renderState.isOn()) {
                  IPanelRendererProxy.super.renderTitleOverlay(context, focus, state, open);
               }

            }

            public IPanelRenderer<Boolean> getRenderer() {
               return HUDPanelComponent.this.panelRenderer;
            }
         }, false));
      }

      public ComponentProxy<ClosableComponent<ToggleButton, ComponentProxy<T>>> getComponent() {
         return this.closable;
      }

      public Point getPosition(IInterface inter) {
         Point pos = HUDPanel.this.component.getPosition(inter);
         pos.translate(-this.panelRenderer.getLeft() - this.border, -this.panelRenderer.getTop() - this.titleRenderer.getDefaultHeight() - this.panelRenderer.getBorder() - this.border);
         return pos;
      }

      public void setPosition(IInterface inter, Point position) {
         position.translate(this.panelRenderer.getLeft() + this.border, this.panelRenderer.getTop() + this.titleRenderer.getDefaultHeight() + this.panelRenderer.getBorder() + this.border);
         HUDPanel.this.component.setPosition(inter, position);
      }

      public int getWidth(IInterface inter) {
         return HUDPanel.this.component.getWidth(inter) + this.panelRenderer.getLeft() + this.panelRenderer.getRight() + 2 * this.border;
      }

      public boolean savesState() {
         return HUDPanel.this.component.savesState();
      }

      public void saveConfig(IInterface inter, IPanelConfig config) {
         HUDPanel.this.component.saveConfig(inter, config);
      }

      public void loadConfig(IInterface inter, IPanelConfig config) {
         HUDPanel.this.component.loadConfig(inter, config);
      }

      public String getConfigName() {
         return HUDPanel.this.component.getConfigName();
      }
   }
}
