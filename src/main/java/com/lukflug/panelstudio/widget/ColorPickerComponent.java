package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.setting.IColorSetting;
import com.lukflug.panelstudio.theme.ThemeTuple;

public class ColorPickerComponent extends ColorComponent {
   public ColorPickerComponent(IColorSetting setting, ThemeTuple theme) {
      super(setting, theme);
   }

   public void populate(ThemeTuple theme) {
      this.addComponent(new ToggleButton(new ColorComponent.RainbowToggle(), theme.getButtonRenderer(Boolean.class, false)));
      this.addComponent(new ColorPicker(this.setting, theme.theme.getColorPickerRenderer()));
      this.addComponent(new NumberSlider(new ColorComponent.ColorNumber(0, () -> {
         return true;
      }), theme.getSliderRenderer(false)));
      this.addComponent(new NumberSlider(new ColorComponent.ColorNumber(3, () -> {
         return true;
      }), theme.getSliderRenderer(false)));
   }
}
