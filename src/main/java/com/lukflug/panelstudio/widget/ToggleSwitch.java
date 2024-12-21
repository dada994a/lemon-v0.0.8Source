package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.FocusableComponent;
import com.lukflug.panelstudio.setting.IBooleanSetting;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.ISwitchRenderer;

public class ToggleSwitch extends FocusableComponent {
   protected IToggleable toggle;
   protected ISwitchRenderer<Boolean> renderer;

   public ToggleSwitch(ILabeled label, IToggleable toggle, ISwitchRenderer<Boolean> renderer) {
      super(label);
      this.toggle = toggle;
      this.renderer = renderer;
      if (this.toggle == null) {
         this.toggle = new SimpleToggleable(false);
      }

   }

   public ToggleSwitch(IBooleanSetting setting, ISwitchRenderer<Boolean> renderer) {
      this(setting, setting, renderer);
   }

   public void render(Context context) {
      super.render(context);
      this.renderer.renderButton(context, this.getTitle(), this.hasFocus(context), this.toggle.isOn());
   }

   public void handleButton(Context context, int button) {
      super.handleButton(context, button);
      if (button == 0 && context.isClicked(button)) {
         if (this.renderer.getOnField(context).contains(context.getInterface().getMouse()) && !this.toggle.isOn()) {
            this.toggle.toggle();
         } else if (this.renderer.getOffField(context).contains(context.getInterface().getMouse()) && this.toggle.isOn()) {
            this.toggle.toggle();
         }
      }

   }

   protected int getHeight() {
      return this.renderer.getDefaultHeight();
   }
}
