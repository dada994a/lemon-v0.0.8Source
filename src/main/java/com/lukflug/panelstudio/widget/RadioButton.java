package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.component.FocusableComponent;
import com.lukflug.panelstudio.setting.AnimatedEnum;
import com.lukflug.panelstudio.setting.IEnumSetting;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.IRadioRenderer;

public abstract class RadioButton extends FocusableComponent {
   protected IEnumSetting setting;
   protected IRadioRenderer renderer;
   protected AnimatedEnum animation;
   protected final boolean horizontal;

   public RadioButton(IEnumSetting setting, IRadioRenderer renderer, Animation animation, boolean horizontal) {
      super(setting);
      this.setting = setting;
      this.renderer = renderer;
      this.animation = new AnimatedEnum(setting, animation);
      this.horizontal = horizontal;
   }

   public void render(Context context) {
      super.render(context);
      ILabeled[] values = IEnumSetting.getVisibleValues(this.setting);
      String compare = this.setting.getValueName();
      int value = -1;

      for(int i = 0; i < values.length; ++i) {
         if (values[i].getDisplayName().equals(compare)) {
            value = i;
            break;
         }
      }

      this.renderer.renderItem(context, values, this.hasFocus(context), value, this.animation.getValue(), this.horizontal);
   }

   public void handleButton(Context context, int button) {
      super.handleButton(context, button);
      if (button == 0 && context.isClicked(button)) {
         int index = 0;
         ILabeled[] values = this.setting.getAllowedValues();
         ILabeled[] visibleValues = IEnumSetting.getVisibleValues(this.setting);

         for(int i = 0; i < values.length; ++i) {
            if (values[i].isVisible().isOn()) {
               if (this.renderer.getItemRect(context, visibleValues, index, this.horizontal).contains(context.getInterface().getMouse())) {
                  this.setting.setValueIndex(i);
                  return;
               }

               ++index;
            }
         }
      }

   }

   public void handleKey(Context context, int key) {
      super.handleKey(context, key);
      if (context.hasFocus()) {
         if (this.isUpKey(key)) {
            this.setting.decrement();
         } else if (this.isDownKey(key)) {
            this.setting.increment();
         }
      }

   }

   protected int getHeight() {
      return this.renderer.getDefaultHeight(IEnumSetting.getVisibleValues(this.setting), this.horizontal);
   }

   protected abstract boolean isUpKey(int var1);

   protected abstract boolean isDownKey(int var1);
}
