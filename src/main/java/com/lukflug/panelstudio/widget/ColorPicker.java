package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.component.FocusableComponent;
import com.lukflug.panelstudio.setting.IColorSetting;
import com.lukflug.panelstudio.theme.IColorPickerRenderer;

public class ColorPicker extends FocusableComponent {
   protected IColorSetting setting;
   protected IColorPickerRenderer renderer;
   protected boolean dragging = false;

   public ColorPicker(IColorSetting setting, IColorPickerRenderer renderer) {
      super(setting);
      this.setting = setting;
      this.renderer = renderer;
   }

   public void render(Context context) {
      super.render(context);
      if (this.dragging && context.getInterface().getButton(0)) {
         this.setting.setValue(this.renderer.transformPoint(context, this.setting.getColor(), context.getInterface().getMouse()));
      }

      this.renderer.renderPicker(context, this.hasFocus(context), this.setting.getColor());
   }

   public void handleButton(Context context, int button) {
      super.handleButton(context, button);
      if (button == 0 && context.isClicked(button)) {
         this.dragging = true;
      } else if (!context.getInterface().getButton(0)) {
         this.dragging = false;
      }

   }

   public void getHeight(Context context) {
      context.setHeight(this.renderer.getDefaultHeight(context.getSize().width));
   }

   public void exit() {
      super.exit();
      this.dragging = false;
   }

   protected int getHeight() {
      return 0;
   }
}
