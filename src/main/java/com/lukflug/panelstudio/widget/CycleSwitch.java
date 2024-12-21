package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.component.FocusableComponent;
import com.lukflug.panelstudio.setting.IEnumSetting;
import com.lukflug.panelstudio.theme.ISwitchRenderer;

public class CycleSwitch extends FocusableComponent {
   protected IEnumSetting setting;
   protected ISwitchRenderer<String> renderer;

   public CycleSwitch(IEnumSetting setting, ISwitchRenderer<String> renderer) {
      super(setting);
      this.setting = setting;
      this.renderer = renderer;
   }

   public void render(Context context) {
      super.render(context);
      this.renderer.renderButton(context, this.getTitle(), this.hasFocus(context), this.setting.getValueName());
   }

   public void handleButton(Context context, int button) {
      super.handleButton(context, button);
      if (button == 0 && context.isClicked(button)) {
         if (this.renderer.getOnField(context).contains(context.getInterface().getMouse())) {
            this.setting.increment();
         } else if (this.renderer.getOffField(context).contains(context.getInterface().getMouse())) {
            this.setting.decrement();
         }
      }

   }

   protected int getHeight() {
      return this.renderer.getDefaultHeight();
   }
}
