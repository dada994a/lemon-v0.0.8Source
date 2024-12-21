package com.lukflug.panelstudio.layout;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.setting.IKeybindSetting;
import com.lukflug.panelstudio.setting.IStringSetting;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.ITextFieldKeys;
import com.lukflug.panelstudio.widget.KeybindComponent;
import com.lukflug.panelstudio.widget.TextField;
import java.util.function.IntPredicate;
import java.util.function.Supplier;

public class ComponentGenerator implements IComponentGenerator {
   protected final IntPredicate keybindKey;
   protected final IntPredicate charFilter;
   protected final ITextFieldKeys keys;

   public ComponentGenerator(IntPredicate keybindKey, IntPredicate charFilter, ITextFieldKeys keys) {
      this.keybindKey = keybindKey;
      this.charFilter = charFilter;
      this.keys = keys;
   }

   public IComponent getKeybindComponent(IKeybindSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
      return new KeybindComponent(setting, theme.getKeybindRenderer(isContainer)) {
         public int transformKey(int scancode) {
            return ComponentGenerator.this.keybindKey.test(scancode) ? 0 : scancode;
         }
      };
   }

   public IComponent getStringComponent(IStringSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
      return new TextField(setting, this.keys, 0, new SimpleToggleable(false), theme.getTextRenderer(false, isContainer)) {
         public boolean allowCharacter(char character) {
            return ComponentGenerator.this.charFilter.test(character);
         }
      };
   }
}
