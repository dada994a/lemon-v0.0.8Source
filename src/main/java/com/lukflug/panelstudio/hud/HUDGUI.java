package com.lukflug.panelstudio.hud;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.container.GUI;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import com.lukflug.panelstudio.theme.IDescriptionRenderer;
import com.lukflug.panelstudio.theme.ITheme;

public class HUDGUI extends GUI {
   protected IToggleable guiVisibility;
   protected IToggleable hudVisibility;

   public HUDGUI(IInterface inter, IDescriptionRenderer descriptionRenderer, IPopupPositioner descriptionPosition, IToggleable guiVisibility, IToggleable hudVisibility) {
      super(inter, descriptionRenderer, descriptionPosition);
      this.guiVisibility = guiVisibility;
      this.hudVisibility = hudVisibility;
   }

   public boolean addComponent(IFixedComponent component) {
      return this.container.addComponent(component, this.guiVisibility);
   }

   public boolean addComponent(IFixedComponent component, IBoolean visible) {
      return this.container.addComponent(component, () -> {
         return this.guiVisibility.isOn() && visible.isOn();
      });
   }

   public boolean addHUDComponent(IFixedComponent component, IBoolean visible) {
      return this.container.addComponent(component, visible);
   }

   public boolean addHUDComponent(IFixedComponent component, IToggleable state, Animation animation, ITheme theme, int border) {
      return this.container.addComponent(new HUDPanel(component, state, animation, theme, this.hudVisibility, border), () -> {
         return true;
      });
   }

   public IToggleable getGUIVisibility() {
      return this.guiVisibility;
   }

   public IToggleable getHUDVisibility() {
      return this.hudVisibility;
   }
}
