package com.lukflug.panelstudio.layout;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.container.VerticalContainer;
import com.lukflug.panelstudio.popup.PopupTuple;
import com.lukflug.panelstudio.setting.IClient;
import com.lukflug.panelstudio.setting.ISetting;
import com.lukflug.panelstudio.theme.ITheme;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.Button;
import com.lukflug.panelstudio.widget.ToggleButton;
import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class PanelLayout implements ILayout {
   protected int width;
   protected Point start;
   protected int skipX;
   protected int skipY;
   protected Supplier<Animation> animation;
   protected IntFunction<ChildUtil.ChildMode> layoutType;
   protected IntFunction<ChildUtil.ChildMode> colorType;
   protected ChildUtil util;

   public PanelLayout(int width, Point start, int skipX, int skipY, Supplier<Animation> animation, IntFunction<ChildUtil.ChildMode> layoutType, IntFunction<ChildUtil.ChildMode> colorType, PopupTuple popupType) {
      this.width = width;
      this.start = start;
      this.skipX = skipX;
      this.skipY = skipY;
      this.animation = animation;
      this.layoutType = layoutType;
      this.colorType = colorType;
      this.util = new ChildUtil(width, animation, popupType);
   }

   public void populateGUI(IComponentAdder gui, IComponentGenerator components, IClient client, ITheme theme) {
      Point pos = this.start;
      AtomicInteger skipY = new AtomicInteger(this.skipY);
      client.getCategories().forEach((category) -> {
         Button<Void> categoryTitle = new Button(category, () -> {
            return null;
         }, theme.getButtonRenderer(Void.class, 0, 0, true));
         VerticalContainer categoryContent = new VerticalContainer(category, theme.getContainerRenderer(0, 0, false));
         gui.addComponent(categoryTitle, categoryContent, new ThemeTuple(theme, 0, 0), new Point(pos), this.width, this.animation);
         pos.translate(this.skipX, skipY.get());
         skipY.set(-skipY.get());
         category.getModules().forEach((module) -> {
            ChildUtil.ChildMode mode = (ChildUtil.ChildMode)this.layoutType.apply(0);
            int graphicalLevel = mode == ChildUtil.ChildMode.DOWN ? 1 : 0;
            Object moduleTitle;
            if (module.isEnabled() == null) {
               moduleTitle = new Button(module, () -> {
                  return null;
               }, theme.getButtonRenderer(Void.class, 1, 1, mode == ChildUtil.ChildMode.DOWN));
            } else {
               moduleTitle = new ToggleButton(module, module.isEnabled(), theme.getButtonRenderer(Boolean.class, 1, 1, mode == ChildUtil.ChildMode.DOWN));
            }

            VerticalContainer moduleContainer = new VerticalContainer(module, theme.getContainerRenderer(1, graphicalLevel, false));
            if (module.isEnabled() == null) {
               this.util.addContainer(module, (IComponent)moduleTitle, moduleContainer, () -> {
                  return null;
               }, Void.class, categoryContent, gui, new ThemeTuple(theme, 1, graphicalLevel), (ChildUtil.ChildMode)this.layoutType.apply(0));
            } else {
               this.util.addContainer(module, (IComponent)moduleTitle, moduleContainer, () -> {
                  return module.isEnabled();
               }, IBoolean.class, categoryContent, gui, new ThemeTuple(theme, 1, graphicalLevel), (ChildUtil.ChildMode)this.layoutType.apply(0));
            }

            module.getSettings().forEach((setting) -> {
               this.addSettingsComponent(setting, moduleContainer, gui, components, new ThemeTuple(theme, 2, graphicalLevel + 1));
            });
         });
      });
   }

   protected <T> void addSettingsComponent(ISetting<T> setting, VerticalContainer container, IComponentAdder gui, IComponentGenerator components, ThemeTuple theme) {
      int nextLevel = this.layoutType.apply(theme.logicalLevel - 1) == ChildUtil.ChildMode.DOWN ? theme.graphicalLevel : 0;
      int colorLevel = this.colorType.apply(theme.logicalLevel - 1) == ChildUtil.ChildMode.DOWN ? theme.graphicalLevel : 0;
      boolean isContainer = setting.getSubSettings() != null && this.layoutType.apply(theme.logicalLevel - 1) == ChildUtil.ChildMode.DOWN;
      IComponent component = components.getComponent(setting, this.animation, gui, theme, colorLevel, isContainer);
      VerticalContainer colorContainer;
      if (component instanceof VerticalContainer) {
         colorContainer = (VerticalContainer)component;
         Button<T> button = new Button(setting, () -> {
            return setting.getSettingState();
         }, theme.getButtonRenderer(setting.getSettingClass(), this.colorType.apply(theme.logicalLevel - 1) == ChildUtil.ChildMode.DOWN));
         this.util.addContainer(setting, button, colorContainer, () -> {
            return setting.getSettingState();
         }, setting.getSettingClass(), container, gui, new ThemeTuple(theme.theme, theme.logicalLevel, colorLevel), (ChildUtil.ChildMode)this.colorType.apply(theme.logicalLevel - 1));
         if (setting.getSubSettings() != null) {
            setting.getSubSettings().forEach((subSetting) -> {
               this.addSettingsComponent(subSetting, colorContainer, gui, components, new ThemeTuple(theme.theme, theme.logicalLevel + 1, colorLevel + 1));
            });
         }
      } else if (setting.getSubSettings() != null) {
         colorContainer = new VerticalContainer(setting, theme.theme.getContainerRenderer(theme.logicalLevel, nextLevel, false));
         this.util.addContainer(setting, component, colorContainer, () -> {
            return setting.getSettingState();
         }, setting.getSettingClass(), container, gui, new ThemeTuple(theme.theme, theme.logicalLevel, nextLevel), (ChildUtil.ChildMode)this.layoutType.apply(theme.logicalLevel - 1));
         setting.getSubSettings().forEach((subSetting) -> {
            this.addSettingsComponent(subSetting, colorContainer, gui, components, new ThemeTuple(theme.theme, theme.logicalLevel + 1, nextLevel + 1));
         });
      } else {
         container.addComponent(component);
      }

   }
}
