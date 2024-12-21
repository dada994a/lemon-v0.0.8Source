package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.HorizontalComponent;
import com.lukflug.panelstudio.container.HorizontalContainer;
import com.lukflug.panelstudio.container.VerticalContainer;
import com.lukflug.panelstudio.setting.INumberSetting;
import com.lukflug.panelstudio.setting.IStringSetting;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import com.lukflug.panelstudio.theme.ThemeTuple;

public class Spinner extends HorizontalContainer {
   public Spinner(final INumberSetting setting, ThemeTuple theme, boolean container, final boolean allowInput, ITextFieldKeys keys) {
      super(setting, new IContainerRenderer() {
      });
      final TextField textField = new TextField(new IStringSetting() {
         private String value = null;
         private long lastTime;

         public String getDisplayName() {
            return setting.getDisplayName();
         }

         public String getValue() {
            if (this.value != null && System.currentTimeMillis() - this.lastTime > 500L) {
               if (this.value.isEmpty()) {
                  this.value = "0";
               }

               if (this.value.endsWith(".")) {
                  this.value = this.value + '0';
               }

               double number = Double.parseDouble(this.value);
               if (number > setting.getMaximumValue()) {
                  number = setting.getMaximumValue();
               } else if (number < setting.getMinimumValue()) {
                  number = setting.getMinimumValue();
               }

               setting.setNumber(number);
               this.value = null;
            }

            return this.value == null ? setting.getSettingState() : this.value;
         }

         public void setValue(String string) {
            if (this.value == null) {
               this.lastTime = System.currentTimeMillis();
            }

            this.value = new String(string);
         }
      }, keys, 0, new SimpleToggleable(false), theme.getTextRenderer(true, container)) {
         public boolean allowCharacter(char character) {
            if (!allowInput) {
               return false;
            } else {
               return character >= '0' && character <= '9' || character == '.' && !this.setting.getSettingState().contains(".");
            }
         }
      };
      this.addComponent(new HorizontalComponent(textField, 0, 1));
      VerticalContainer buttons = new VerticalContainer(setting, new IContainerRenderer() {
      });
      buttons.addComponent(new Button<Void>(new Labeled((String)null, (String)null, () -> {
         return true;
      }), () -> {
         return null;
      }, theme.getSmallButtonRenderer(6, container)) {
         public void handleButton(Context context, int button) {
            super.handleButton(context, button);
            if (button == 0 && context.isClicked(button)) {
               double number = setting.getNumber();
               number += Math.pow(10.0D, (double)(-setting.getPrecision()));
               if (number <= setting.getMaximumValue()) {
                  setting.setNumber(number);
               }
            }

         }

         public int getHeight() {
            return textField.getHeight() / 2;
         }
      });
      buttons.addComponent(new Button<Void>(new Labeled((String)null, (String)null, () -> {
         return true;
      }), () -> {
         return null;
      }, theme.getSmallButtonRenderer(7, container)) {
         public void handleButton(Context context, int button) {
            super.handleButton(context, button);
            if (button == 0 && context.isClicked(button)) {
               double number = setting.getNumber();
               number -= Math.pow(10.0D, (double)(-setting.getPrecision()));
               if (number >= setting.getMinimumValue()) {
                  setting.setNumber(number);
               }
            }

         }

         public int getHeight() {
            return textField.getHeight() / 2;
         }
      });
      this.addComponent(new HorizontalComponent(buttons, textField.getHeight(), 0));
   }
}
