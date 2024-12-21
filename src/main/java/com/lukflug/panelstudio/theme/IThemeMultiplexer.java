package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.IInterface;
import java.awt.Color;

@FunctionalInterface
public interface IThemeMultiplexer extends ITheme {
   default void loadAssets(IInterface inter) {
      this.getTheme().loadAssets(inter);
   }

   default IDescriptionRenderer getDescriptionRenderer() {
      IDescriptionRendererProxy proxy = () -> {
         return this.getTheme().getDescriptionRenderer();
      };
      return proxy;
   }

   default IContainerRenderer getContainerRenderer(int logicalLevel, int graphicalLevel, boolean horizontal) {
      IContainerRendererProxy proxy = () -> {
         return this.getTheme().getContainerRenderer(logicalLevel, graphicalLevel, horizontal);
      };
      return proxy;
   }

   default <T> IPanelRenderer<T> getPanelRenderer(Class<T> type, int logicalLevel, int graphicalLevel) {
      IPanelRendererProxy<T> proxy = () -> {
         return this.getTheme().getPanelRenderer(type, logicalLevel, graphicalLevel);
      };
      return proxy;
   }

   default <T> IScrollBarRenderer<T> getScrollBarRenderer(Class<T> type, int logicalLevel, int graphicalLevel) {
      IScrollBarRendererProxy<T> proxy = () -> {
         return this.getTheme().getScrollBarRenderer(type, logicalLevel, graphicalLevel);
      };
      return proxy;
   }

   default <T> IEmptySpaceRenderer<T> getEmptySpaceRenderer(Class<T> type, int logicalLevel, int graphicalLevel, boolean container) {
      IEmptySpaceRendererProxy<T> proxy = () -> {
         return this.getTheme().getEmptySpaceRenderer(type, logicalLevel, graphicalLevel, container);
      };
      return proxy;
   }

   default <T> IButtonRenderer<T> getButtonRenderer(Class<T> type, int logicalLevel, int graphicalLevel, boolean container) {
      IButtonRendererProxy<T> proxy = () -> {
         return this.getTheme().getButtonRenderer(type, logicalLevel, graphicalLevel, container);
      };
      return proxy;
   }

   default IButtonRenderer<Void> getSmallButtonRenderer(int symbol, int logicalLevel, int graphicalLevel, boolean container) {
      IButtonRendererProxy<Void> proxy = () -> {
         return this.getTheme().getSmallButtonRenderer(symbol, logicalLevel, graphicalLevel, container);
      };
      return proxy;
   }

   default IButtonRenderer<String> getKeybindRenderer(int logicalLevel, int graphicalLevel, boolean container) {
      IButtonRendererProxy<String> proxy = () -> {
         return this.getTheme().getKeybindRenderer(logicalLevel, graphicalLevel, container);
      };
      return proxy;
   }

   default ISliderRenderer getSliderRenderer(int logicalLevel, int graphicalLevel, boolean container) {
      ISliderRendererProxy proxy = () -> {
         return this.getTheme().getSliderRenderer(logicalLevel, graphicalLevel, container);
      };
      return proxy;
   }

   default IRadioRenderer getRadioRenderer(int logicalLevel, int graphicalLevel, boolean container) {
      IRadioRendererProxy proxy = () -> {
         return this.getTheme().getRadioRenderer(logicalLevel, graphicalLevel, container);
      };
      return proxy;
   }

   default IResizeBorderRenderer getResizeRenderer() {
      IResizeBorderRendererProxy proxy = () -> {
         return this.getTheme().getResizeRenderer();
      };
      return proxy;
   }

   default ITextFieldRenderer getTextRenderer(boolean embed, int logicalLevel, int graphicalLevel, boolean container) {
      ITextFieldRendererProxy proxy = () -> {
         return this.getTheme().getTextRenderer(embed, logicalLevel, graphicalLevel, container);
      };
      return proxy;
   }

   default ISwitchRenderer<Boolean> getToggleSwitchRenderer(int logicalLevel, int graphicalLevel, boolean container) {
      ISwitchRendererProxy<Boolean> proxy = () -> {
         return this.getTheme().getToggleSwitchRenderer(logicalLevel, graphicalLevel, container);
      };
      return proxy;
   }

   default ISwitchRenderer<String> getCycleSwitchRenderer(int logicalLevel, int graphicalLevel, boolean container) {
      ISwitchRendererProxy<String> proxy = () -> {
         return this.getTheme().getCycleSwitchRenderer(logicalLevel, graphicalLevel, container);
      };
      return proxy;
   }

   default IColorPickerRenderer getColorPickerRenderer() {
      IColorPickerRendererProxy proxy = () -> {
         return this.getTheme().getColorPickerRenderer();
      };
      return proxy;
   }

   default int getBaseHeight() {
      return this.getTheme().getBaseHeight();
   }

   default Color getMainColor(boolean focus, boolean active) {
      return this.getTheme().getMainColor(focus, active);
   }

   default Color getBackgroundColor(boolean focus) {
      return this.getTheme().getBackgroundColor(focus);
   }

   default Color getFontColor(boolean focus) {
      return this.getTheme().getFontColor(focus);
   }

   default void overrideMainColor(Color color) {
      this.getTheme().overrideMainColor(color);
   }

   default void restoreMainColor() {
      this.getTheme().restoreMainColor();
   }

   ITheme getTheme();
}
