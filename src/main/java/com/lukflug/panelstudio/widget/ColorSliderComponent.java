package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.setting.IBooleanSetting;
import com.lukflug.panelstudio.setting.IColorSetting;
import com.lukflug.panelstudio.setting.INumberSetting;
import com.lukflug.panelstudio.theme.ThemeTuple;

public class ColorSliderComponent extends ColorComponent {
   public ColorSliderComponent(IColorSetting setting, ThemeTuple theme) {
      super(setting, theme);
   }

   public void populate(ThemeTuple theme) {
      this.addComponent(this.getRainbowComponent(theme, new ColorComponent.RainbowToggle()));
      this.addComponent(this.getColorComponent(theme, 0, new ColorComponent.ColorNumber(0, () -> {
         return this.setting.hasHSBModel();
      })));
      this.addComponent(this.getColorComponent(theme, 1, new ColorComponent.ColorNumber(1, () -> {
         return this.setting.hasHSBModel();
      })));
      this.addComponent(this.getColorComponent(theme, 2, new ColorComponent.ColorNumber(2, () -> {
         return this.setting.hasHSBModel();
      })));
      this.addComponent(this.getColorComponent(theme, 3, new ColorComponent.ColorNumber(3, () -> {
         return this.setting.hasHSBModel();
      })));
   }

   public IComponent getRainbowComponent(ThemeTuple theme, IBooleanSetting toggle) {
      return new ToggleButton(toggle, theme.getButtonRenderer(Boolean.class, false));
   }

   public IComponent getColorComponent(ThemeTuple theme, int value, INumberSetting number) {
      return new NumberSlider(number, theme.getSliderRenderer(false));
   }
}
