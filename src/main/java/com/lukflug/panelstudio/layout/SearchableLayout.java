package com.lukflug.panelstudio.layout;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.component.HorizontalComponent;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.container.HorizontalContainer;
import com.lukflug.panelstudio.container.IContainer;
import com.lukflug.panelstudio.container.VerticalContainer;
import com.lukflug.panelstudio.popup.PopupTuple;
import com.lukflug.panelstudio.setting.IBooleanSetting;
import com.lukflug.panelstudio.setting.IClient;
import com.lukflug.panelstudio.setting.IEnumSetting;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.IModule;
import com.lukflug.panelstudio.setting.ISetting;
import com.lukflug.panelstudio.theme.ITheme;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.Button;
import com.lukflug.panelstudio.widget.ITextFieldKeys;
import com.lukflug.panelstudio.widget.ScrollBarComponent;
import com.lukflug.panelstudio.widget.SearchableRadioButton;
import java.awt.Point;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SearchableLayout implements ILayout, IScrollSize {
   protected ILabeled titleLabel;
   protected ILabeled searchLabel;
   protected Point position;
   protected int width;
   protected Supplier<Animation> animation;
   protected String enabledButton;
   protected int weight;
   protected ChildUtil.ChildMode colorType;
   protected ChildUtil util;
   protected Comparator<IModule> comparator;
   protected IntPredicate charFilter;
   protected ITextFieldKeys keys;

   public SearchableLayout(ILabeled titleLabel, ILabeled searchLabel, Point position, int width, int popupWidth, Supplier<Animation> animation, String enabledButton, int weight, ChildUtil.ChildMode colorType, PopupTuple popupType, Comparator<IModule> comparator, IntPredicate charFilter, ITextFieldKeys keys) {
      this.titleLabel = titleLabel;
      this.searchLabel = searchLabel;
      this.position = position;
      this.width = width;
      this.animation = animation;
      this.enabledButton = enabledButton;
      this.weight = weight;
      this.colorType = colorType;
      this.comparator = comparator;
      this.charFilter = charFilter;
      this.keys = keys;
      this.util = new ChildUtil(popupWidth, animation, popupType);
   }

   public void populateGUI(IComponentAdder gui, IComponentGenerator components, IClient client, ITheme theme) {
      Button<Void> title = new Button(this.titleLabel, () -> {
         return null;
      }, theme.getButtonRenderer(Void.class, 0, 0, true));
      HorizontalContainer window = new HorizontalContainer(this.titleLabel, theme.getContainerRenderer(0, 0, true));
      Supplier<Stream<IModule>> modules = () -> {
         return client.getCategories().flatMap((cat) -> {
            return cat.getModules();
         }).sorted(this.comparator);
      };
      IEnumSetting modSelect = this.addContainer(this.searchLabel, ((Stream)modules.get()).map((mod) -> {
         return mod;
      }), window, new ThemeTuple(theme, 0, 1), false, (button) -> {
         return this.wrapColumn(button, new ThemeTuple(theme, 0, 1), 1);
      }, () -> {
         return true;
      });
      gui.addComponent(title, window, new ThemeTuple(theme, 0, 0), this.position, this.width, this.animation);
      ((Stream)modules.get()).forEach((module) -> {
         VerticalContainer container = new VerticalContainer(module, theme.getContainerRenderer(1, 1, false));
         window.addComponent(this.wrapColumn(container, new ThemeTuple(theme, 1, 1), this.weight), () -> {
            return modSelect.getValueName() == module.getDisplayName();
         });
         if (module.isEnabled() != null) {
            container.addComponent(components.getComponent(new IBooleanSetting() {
               public String getDisplayName() {
                  return SearchableLayout.this.enabledButton;
               }

               public void toggle() {
                  module.isEnabled().toggle();
               }

               public boolean isOn() {
                  return module.isEnabled().isOn();
               }
            }, this.animation, gui, new ThemeTuple(theme, 1, 2), 2, false));
         }

         module.getSettings().forEach((setting) -> {
            this.addSettingsComponent(setting, container, gui, components, new ThemeTuple(theme, 2, 2));
         });
      });
   }

   protected <T> void addSettingsComponent(ISetting<T> setting, VerticalContainer container, IComponentAdder gui, IComponentGenerator components, ThemeTuple theme) {
      int colorLevel = this.colorType == ChildUtil.ChildMode.DOWN ? theme.graphicalLevel : 0;
      boolean isContainer = setting.getSubSettings() != null;
      IComponent component = components.getComponent(setting, this.animation, gui, theme, colorLevel, isContainer);
      VerticalContainer colorContainer;
      if (component instanceof VerticalContainer) {
         colorContainer = (VerticalContainer)component;
         Button<T> button = new Button(setting, () -> {
            return setting.getSettingState();
         }, theme.getButtonRenderer(setting.getSettingClass(), this.colorType == ChildUtil.ChildMode.DOWN));
         this.util.addContainer(setting, button, colorContainer, () -> {
            return setting.getSettingState();
         }, setting.getSettingClass(), container, gui, new ThemeTuple(theme.theme, theme.logicalLevel, colorLevel), this.colorType);
         if (setting.getSubSettings() != null) {
            setting.getSubSettings().forEach((subSetting) -> {
               this.addSettingsComponent(subSetting, colorContainer, gui, components, new ThemeTuple(theme.theme, theme.logicalLevel + 1, colorLevel + 1));
            });
         }
      } else if (setting.getSubSettings() != null) {
         colorContainer = new VerticalContainer(setting, theme.getContainerRenderer(false));
         this.util.addContainer(setting, component, colorContainer, () -> {
            return setting.getSettingState();
         }, setting.getSettingClass(), container, gui, theme, ChildUtil.ChildMode.DOWN);
         setting.getSubSettings().forEach((subSetting) -> {
            this.addSettingsComponent(subSetting, colorContainer, gui, components, new ThemeTuple(theme, 1, 1));
         });
      } else {
         container.addComponent(component);
      }

   }

   protected <T extends IComponent> IEnumSetting addContainer(final ILabeled label, final Stream<ILabeled> labels, IContainer<T> window, ThemeTuple theme, final boolean horizontal, Function<SearchableRadioButton, T> container, IBoolean visible) {
      IEnumSetting setting = new IEnumSetting() {
         private int state = 0;
         private ILabeled[] array = (ILabeled[])labels.toArray((x$0) -> {
            return new ILabeled[x$0];
         });

         public String getDisplayName() {
            return label.getDisplayName();
         }

         public String getDescription() {
            return label.getDescription();
         }

         public IBoolean isVisible() {
            return label.isVisible();
         }

         public void increment() {
            this.state = (this.state + 1) % this.array.length;
         }

         public void decrement() {
            --this.state;
            if (this.state < 0) {
               this.state = this.array.length - 1;
            }

         }

         public String getValueName() {
            return this.array[this.state].getDisplayName();
         }

         public void setValueIndex(int index) {
            this.state = index;
         }

         public int getValueIndex() {
            return this.state;
         }

         public ILabeled[] getAllowedValues() {
            return this.array;
         }
      };
      SearchableRadioButton button = new SearchableRadioButton(setting, theme, true, this.keys) {
         protected Animation getAnimation() {
            return (Animation)SearchableLayout.this.animation.get();
         }

         public boolean allowCharacter(char character) {
            return SearchableLayout.this.charFilter.test(character);
         }

         protected boolean isUpKey(int key) {
            return horizontal ? SearchableLayout.this.isLeftKey(key) : SearchableLayout.this.isUpKey(key);
         }

         protected boolean isDownKey(int key) {
            return horizontal ? SearchableLayout.this.isRightKey(key) : SearchableLayout.this.isDownKey(key);
         }
      };
      window.addComponent((IComponent)container.apply(button), visible);
      return setting;
   }

   protected HorizontalComponent<ScrollBarComponent<Void, IComponent>> wrapColumn(IComponent button, ThemeTuple theme, int weight) {
      return new HorizontalComponent(new ScrollBarComponent<Void, IComponent>(button, theme.getScrollBarRenderer(Void.class), theme.getEmptySpaceRenderer(Void.class, false), theme.getEmptySpaceRenderer(Void.class, true)) {
         public int getScrollHeight(Context context, int componentHeight) {
            return SearchableLayout.this.getScrollHeight(context, componentHeight);
         }

         protected Void getState() {
            return null;
         }
      }, 0, weight);
   }

   protected boolean isUpKey(int key) {
      return false;
   }

   protected boolean isDownKey(int key) {
      return false;
   }

   protected boolean isLeftKey(int key) {
      return false;
   }

   protected boolean isRightKey(int key) {
      return false;
   }
}
