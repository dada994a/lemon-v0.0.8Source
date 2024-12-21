package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.container.VerticalContainer;
import com.lukflug.panelstudio.setting.IEnumSetting;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.IStringSetting;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import com.lukflug.panelstudio.theme.ThemeTuple;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public abstract class SearchableRadioButton extends VerticalContainer {
   protected boolean transferFocus = false;

   public SearchableRadioButton(final IEnumSetting setting, ThemeTuple theme, boolean container, ITextFieldKeys keys) {
      super(setting, new IContainerRenderer() {
      });
      final AtomicReference<String> searchTerm = new AtomicReference("");
      TextField textField = new TextField(new IStringSetting() {
         public String getDisplayName() {
            return setting.getDisplayName();
         }

         public String getValue() {
            return (String)searchTerm.get();
         }

         public void setValue(String string) {
            searchTerm.set(string);
         }
      }, keys, 0, new SimpleToggleable(false), theme.getTextRenderer(true, container)) {
         public void handleButton(Context context, int button) {
            super.handleButton(context, button);
            if (this.hasFocus(context)) {
               SearchableRadioButton.this.transferFocus = true;
            }

         }

         public boolean allowCharacter(char character) {
            return SearchableRadioButton.this.allowCharacter(character);
         }
      };
      this.addComponent(textField);
      RadioButton content = new RadioButton(new IEnumSetting() {
         ILabeled[] values = (ILabeled[])Arrays.stream(setting.getAllowedValues()).map((value) -> {
            return new Labeled(value.getDisplayName(), value.getDescription(), () -> {
               return !value.isVisible().isOn() ? false : value.getDisplayName().toUpperCase().contains(((String)searchTerm.get()).toUpperCase());
            });
         }).toArray((x$0) -> {
            return new ILabeled[x$0];
         });

         public String getDisplayName() {
            return setting.getDisplayName();
         }

         public String getDescription() {
            return setting.getDescription();
         }

         public IBoolean isVisible() {
            return setting.isVisible();
         }

         public void increment() {
            setting.increment();
         }

         public void decrement() {
            setting.decrement();
         }

         public String getValueName() {
            return setting.getValueName();
         }

         public void setValueIndex(int index) {
            setting.setValueIndex(index);
         }

         public ILabeled[] getAllowedValues() {
            return this.values;
         }
      }, theme.getRadioRenderer(container), this.getAnimation(), false) {
         protected boolean isUpKey(int key) {
            return SearchableRadioButton.this.isUpKey(key);
         }

         protected boolean isDownKey(int key) {
            return SearchableRadioButton.this.isDownKey(key);
         }
      };
      this.addComponent(content);
   }

   protected abstract Animation getAnimation();

   public abstract boolean allowCharacter(char var1);

   protected abstract boolean isUpKey(int var1);

   protected abstract boolean isDownKey(int var1);
}
