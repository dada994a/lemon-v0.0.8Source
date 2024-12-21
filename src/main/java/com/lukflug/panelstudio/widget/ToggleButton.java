package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.FocusableComponent;
import com.lukflug.panelstudio.setting.IBooleanSetting;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.IButtonRenderer;

public class ToggleButton extends FocusableComponent {
   protected IToggleable toggle;
   protected IButtonRenderer<Boolean> renderer;

   public ToggleButton(ILabeled label, IToggleable toggle, IButtonRenderer<Boolean> renderer) {
      super(label);
      this.toggle = toggle;
      this.renderer = renderer;
      if (this.toggle == null) {
         this.toggle = new SimpleToggleable(false);
      }

   }

   public ToggleButton(IBooleanSetting setting, IButtonRenderer<Boolean> renderer) {
      this(setting, setting, renderer);
   }

   public void render(Context context) {
      super.render(context);
      this.renderer.renderButton(context, this.getTitle(), this.hasFocus(context), this.toggle.isOn());
   }

   public void handleButton(Context context, int button) {
      super.handleButton(context, button);
      if (button == 0 && context.isClicked(button)) {
         this.toggle.toggle();
      }

   }

   protected int getHeight() {
      return this.renderer.getDefaultHeight();
   }
}
