package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.config.IPanelConfig;
import java.awt.Point;

public abstract class DraggableComponent<T extends IFixedComponent> implements IComponentProxy<T>, IFixedComponent {
   protected boolean dragging = false;
   protected Point attachPoint;

   public Point getPosition(IInterface inter) {
      Point point = ((IFixedComponent)this.getComponent()).getPosition(inter);
      if (this.dragging) {
         point.translate(inter.getMouse().x - this.attachPoint.x, inter.getMouse().y - this.attachPoint.y);
      }

      return point;
   }

   public void setPosition(IInterface inter, Point position) {
      ((IFixedComponent)this.getComponent()).setPosition(inter, position);
   }

   public int getWidth(IInterface inter) {
      return ((IFixedComponent)this.getComponent()).getWidth(inter);
   }

   public boolean savesState() {
      return ((IFixedComponent)this.getComponent()).savesState();
   }

   public void saveConfig(IInterface inter, IPanelConfig config) {
      ((IFixedComponent)this.getComponent()).saveConfig(inter, config);
   }

   public void loadConfig(IInterface inter, IPanelConfig config) {
      ((IFixedComponent)this.getComponent()).loadConfig(inter, config);
   }

   public String getConfigName() {
      return ((IFixedComponent)this.getComponent()).getConfigName();
   }

   public <S extends IComponent> ComponentProxy<S> getWrappedDragComponent(S dragComponent) {
      return new ComponentProxy<S>(dragComponent) {
         public void handleButton(Context context, int button) {
            super.handleButton(context, button);
            if (context.isClicked(button) && button == 0) {
               DraggableComponent.this.dragging = true;
               DraggableComponent.this.attachPoint = context.getInterface().getMouse();
            } else if (!context.getInterface().getButton(0) && DraggableComponent.this.dragging) {
               Point mouse = context.getInterface().getMouse();
               DraggableComponent.this.dragging = false;
               Point p = ((IFixedComponent)DraggableComponent.this.getComponent()).getPosition(context.getInterface());
               p.translate(mouse.x - DraggableComponent.this.attachPoint.x, mouse.y - DraggableComponent.this.attachPoint.y);
               ((IFixedComponent)DraggableComponent.this.getComponent()).setPosition(context.getInterface(), p);
            }

         }

         public void exit() {
            DraggableComponent.this.dragging = false;
            super.exit();
         }
      };
   }
}
