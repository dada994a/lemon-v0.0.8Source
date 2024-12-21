package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.container.VerticalContainer;
import com.lukflug.panelstudio.setting.IBooleanSetting;
import com.lukflug.panelstudio.setting.IColorSetting;
import com.lukflug.panelstudio.setting.INumberSetting;
import com.lukflug.panelstudio.theme.ITheme;
import com.lukflug.panelstudio.theme.ThemeTuple;
import java.awt.Color;

public abstract class ColorComponent extends VerticalContainer {
   protected IColorSetting setting;
   protected ITheme theme;

   public ColorComponent(IColorSetting setting, ThemeTuple theme) {
      super(setting, theme.getContainerRenderer(false));
      this.setting = setting;
      this.theme = theme.theme;
      this.populate(new ThemeTuple(theme, 0, 1));
   }

   public void render(Context context) {
      this.theme.overrideMainColor(this.setting.getValue());
      super.render(context);
      this.theme.restoreMainColor();
   }

   public abstract void populate(ThemeTuple var1);

   protected final class ColorNumber implements INumberSetting {
      private final int value;
      private final IBoolean model;

      public ColorNumber(int value, IBoolean model) {
         this.value = value;
         this.model = model;
      }

      public String getDisplayName() {
         switch(this.value) {
         case 0:
            return this.model.isOn() ? "Hue" : "Red";
         case 1:
            return this.model.isOn() ? "Saturation" : "Green";
         case 2:
            return this.model.isOn() ? "Brightness" : "Blue";
         case 3:
            return this.model.isOn() ? "Opacity" : "Alpha";
         default:
            return "";
         }
      }

      public IBoolean isVisible() {
         return () -> {
            return this.value != 3 || ColorComponent.this.setting.hasAlpha();
         };
      }

      public double getNumber() {
         Color c = ColorComponent.this.setting.getColor();
         if (this.value < 3) {
            if (this.model.isOn()) {
               return (double)Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), (float[])null)[this.value] * this.getMaximumValue();
            }

            switch(this.value) {
            case 0:
               return (double)c.getRed();
            case 1:
               return (double)c.getGreen();
            case 2:
               return (double)c.getBlue();
            }
         }

         return (double)c.getAlpha() * this.getMaximumValue() / 255.0D;
      }

      public void setNumber(double value) {
         Color c = ColorComponent.this.setting.getColor();
         float[] color = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), (float[])null);
         switch(this.value) {
         case 0:
            if (this.model.isOn()) {
               c = Color.getHSBColor((float)value / 360.0F, color[1], color[2]);
            } else {
               c = new Color((int)Math.round(value), c.getGreen(), c.getBlue());
            }

            if (ColorComponent.this.setting.hasAlpha()) {
               ColorComponent.this.setting.setValue(new Color(c.getRed(), c.getGreen(), c.getBlue(), ColorComponent.this.setting.getColor().getAlpha()));
            } else {
               ColorComponent.this.setting.setValue(c);
            }
            break;
         case 1:
            if (this.model.isOn()) {
               c = Color.getHSBColor(color[0], (float)value / 100.0F, color[2]);
            } else {
               c = new Color(c.getRed(), (int)Math.round(value), c.getBlue());
            }

            if (ColorComponent.this.setting.hasAlpha()) {
               ColorComponent.this.setting.setValue(new Color(c.getRed(), c.getGreen(), c.getBlue(), ColorComponent.this.setting.getColor().getAlpha()));
            } else {
               ColorComponent.this.setting.setValue(c);
            }
            break;
         case 2:
            if (this.model.isOn()) {
               c = Color.getHSBColor(color[0], color[1], (float)value / 100.0F);
            } else {
               c = new Color(c.getRed(), c.getGreen(), (int)Math.round(value));
            }

            if (ColorComponent.this.setting.hasAlpha()) {
               ColorComponent.this.setting.setValue(new Color(c.getRed(), c.getGreen(), c.getBlue(), ColorComponent.this.setting.getColor().getAlpha()));
            } else {
               ColorComponent.this.setting.setValue(c);
            }
            break;
         case 3:
            ColorComponent.this.setting.setValue(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)Math.round(value / this.getMaximumValue() * 255.0D)));
         }

      }

      public double getMaximumValue() {
         int max = 100;
         if (!this.model.isOn()) {
            max = 255;
         } else if (this.value == 0) {
            max = 360;
         }

         return (double)max;
      }

      public double getMinimumValue() {
         return 0.0D;
      }

      public int getPrecision() {
         return 0;
      }
   }

   protected final class RainbowToggle implements IBooleanSetting {
      public String getDisplayName() {
         return "Rainbow";
      }

      public IBoolean isVisible() {
         return () -> {
            return ColorComponent.this.setting.allowsRainbow();
         };
      }

      public boolean isOn() {
         return ColorComponent.this.setting.getRainbow();
      }

      public void toggle() {
         ColorComponent.this.setting.setRainbow(!ColorComponent.this.setting.getRainbow());
      }
   }
}
