package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.config.IPanelConfig;
import java.awt.Point;

public class FixedComponent<T extends IComponent> extends ComponentProxy<T> implements IFixedComponent {
   protected Point position;
   protected int width;
   protected IToggleable state;
   protected boolean savesState;
   protected String configName;

   public FixedComponent(T component, Point position, int width, IToggleable state, boolean savesState, String configName) {
      super(component);
      this.position = position;
      this.width = width;
      this.state = state;
      this.savesState = savesState;
      this.configName = configName;
   }

   public Point getPosition(IInterface inter) {
      return new Point(this.position);
   }

   public void setPosition(IInterface inter, Point position) {
      this.position = new Point(position);
   }

   public int getWidth(IInterface inter) {
      return this.width;
   }

   public boolean savesState() {
      return this.savesState;
   }

   public void saveConfig(IInterface inter, IPanelConfig config) {
      config.savePositon(this.position);
      if (this.state != null) {
         config.saveState(this.state.isOn());
      }

   }

   public void loadConfig(IInterface inter, IPanelConfig config) {
      this.position = config.loadPosition();
      if (this.state != null && this.state.isOn() != config.loadState()) {
         this.state.toggle();
      }

   }

   public String getConfigName() {
      return this.configName;
   }
}
