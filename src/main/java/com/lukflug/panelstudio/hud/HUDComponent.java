package com.lukflug.panelstudio.hud;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.Description;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.config.IPanelConfig;
import com.lukflug.panelstudio.setting.ILabeled;
import java.awt.Dimension;
import java.awt.Point;

public abstract class HUDComponent implements IFixedComponent {
   protected String title;
   protected IBoolean visible;
   protected String description;
   protected Point position;
   protected String configName;

   public HUDComponent(ILabeled label, Point position, String configName) {
      this.title = label.getDisplayName();
      this.position = position;
      this.description = label.getDescription();
      this.configName = configName;
   }

   public String getTitle() {
      return this.title;
   }

   public void render(Context context) {
      context.setHeight(this.getSize(context.getInterface()).height);
      if (this.description != null) {
         context.setDescription(new Description(context.getRect(), this.description));
      }

   }

   public void handleButton(Context context, int button) {
      context.setHeight(this.getSize(context.getInterface()).height);
   }

   public void handleKey(Context context, int scancode) {
      context.setHeight(this.getSize(context.getInterface()).height);
   }

   public void handleChar(Context context, char character) {
      context.setHeight(this.getSize(context.getInterface()).height);
   }

   public void handleScroll(Context context, int diff) {
      context.setHeight(this.getSize(context.getInterface()).height);
   }

   public void getHeight(Context context) {
      context.setHeight(this.getSize(context.getInterface()).height);
   }

   public void enter() {
   }

   public void exit() {
   }

   public void releaseFocus() {
   }

   public boolean isVisible() {
      return true;
   }

   public Point getPosition(IInterface inter) {
      return new Point(this.position);
   }

   public void setPosition(IInterface inter, Point position) {
      this.position = new Point(position);
   }

   public int getWidth(IInterface inter) {
      return this.getSize(inter).width;
   }

   public boolean savesState() {
      return true;
   }

   public void saveConfig(IInterface inter, IPanelConfig config) {
      config.savePositon(this.position);
   }

   public void loadConfig(IInterface inter, IPanelConfig config) {
      this.position = config.loadPosition();
   }

   public String getConfigName() {
      return this.configName;
   }

   public abstract Dimension getSize(IInterface var1);
}
