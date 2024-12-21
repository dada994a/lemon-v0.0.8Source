package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.IInterface;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class OptimizedTheme implements ITheme {
   private final ITheme theme;
   private IDescriptionRenderer descriptionRenderer = null;
   private final Map<OptimizedTheme.ParameterTuple<Void, Boolean>, IContainerRenderer> containerRenderer = new HashMap();
   private final Map<OptimizedTheme.ParameterTuple<Class<?>, Void>, IPanelRenderer<?>> panelRenderer = new HashMap();
   private final Map<OptimizedTheme.ParameterTuple<Class<?>, Void>, IScrollBarRenderer<?>> scrollBarRenderer = new HashMap();
   private final Map<OptimizedTheme.ParameterTuple<Class<?>, Boolean>, IEmptySpaceRenderer<?>> emptySpaceRenderer = new HashMap();
   private final Map<OptimizedTheme.ParameterTuple<Class<?>, Boolean>, IButtonRenderer<?>> buttonRenderer = new HashMap();
   private final Map<OptimizedTheme.ParameterTuple<Integer, Boolean>, IButtonRenderer<Void>> smallButtonRenderer = new HashMap();
   private final Map<OptimizedTheme.ParameterTuple<Void, Boolean>, IButtonRenderer<String>> keybindRenderer = new HashMap();
   private final Map<OptimizedTheme.ParameterTuple<Void, Boolean>, ISliderRenderer> sliderRenderer = new HashMap();
   private final Map<OptimizedTheme.ParameterTuple<Void, Boolean>, IRadioRenderer> radioRenderer = new HashMap();
   private IResizeBorderRenderer resizeRenderer = null;
   private final Map<OptimizedTheme.ParameterTuple<Boolean, Boolean>, ITextFieldRenderer> textRenderer = new HashMap();
   private final Map<OptimizedTheme.ParameterTuple<Void, Boolean>, ISwitchRenderer<Boolean>> toggleSwitchRenderer = new HashMap();
   private final Map<OptimizedTheme.ParameterTuple<Void, Boolean>, ISwitchRenderer<String>> cycleSwitchRenderer = new HashMap();
   private IColorPickerRenderer colorPickerRenderer = null;

   public OptimizedTheme(ITheme theme) {
      this.theme = theme;
   }

   public void loadAssets(IInterface inter) {
      this.theme.loadAssets(inter);
   }

   public IDescriptionRenderer getDescriptionRenderer() {
      if (this.descriptionRenderer == null) {
         this.descriptionRenderer = this.theme.getDescriptionRenderer();
      }

      return this.descriptionRenderer;
   }

   public IContainerRenderer getContainerRenderer(int logicalLevel, int graphicalLevel, boolean horizontal) {
      return (IContainerRenderer)getRenderer(this.containerRenderer, () -> {
         return this.theme.getContainerRenderer(logicalLevel, graphicalLevel, horizontal);
      }, (Object)null, logicalLevel, graphicalLevel, horizontal);
   }

   public <T> IPanelRenderer<T> getPanelRenderer(Class<T> type, int logicalLevel, int graphicalLevel) {
      return (IPanelRenderer)getRenderer(this.panelRenderer, () -> {
         return this.theme.getPanelRenderer(type, logicalLevel, graphicalLevel);
      }, type, logicalLevel, graphicalLevel, (Object)null);
   }

   public <T> IScrollBarRenderer<T> getScrollBarRenderer(Class<T> type, int logicalLevel, int graphicalLevel) {
      return (IScrollBarRenderer)getRenderer(this.scrollBarRenderer, () -> {
         return this.theme.getScrollBarRenderer(type, logicalLevel, graphicalLevel);
      }, type, logicalLevel, graphicalLevel, (Object)null);
   }

   public <T> IEmptySpaceRenderer<T> getEmptySpaceRenderer(Class<T> type, int logicalLevel, int graphicalLevel, boolean container) {
      return (IEmptySpaceRenderer)getRenderer(this.emptySpaceRenderer, () -> {
         return this.theme.getEmptySpaceRenderer(type, logicalLevel, graphicalLevel, container);
      }, type, logicalLevel, graphicalLevel, container);
   }

   public <T> IButtonRenderer<T> getButtonRenderer(Class<T> type, int logicalLevel, int graphicalLevel, boolean container) {
      return (IButtonRenderer)getRenderer(this.buttonRenderer, () -> {
         return this.theme.getButtonRenderer(type, logicalLevel, graphicalLevel, container);
      }, type, logicalLevel, graphicalLevel, container);
   }

   public IButtonRenderer<Void> getSmallButtonRenderer(int symbol, int logicalLevel, int graphicalLevel, boolean container) {
      return (IButtonRenderer)getRenderer(this.smallButtonRenderer, () -> {
         return this.theme.getSmallButtonRenderer(symbol, logicalLevel, graphicalLevel, container);
      }, symbol, logicalLevel, graphicalLevel, container);
   }

   public IButtonRenderer<String> getKeybindRenderer(int logicalLevel, int graphicalLevel, boolean container) {
      return (IButtonRenderer)getRenderer(this.keybindRenderer, () -> {
         return this.theme.getKeybindRenderer(logicalLevel, graphicalLevel, container);
      }, (Object)null, logicalLevel, graphicalLevel, container);
   }

   public ISliderRenderer getSliderRenderer(int logicalLevel, int graphicalLevel, boolean container) {
      return (ISliderRenderer)getRenderer(this.sliderRenderer, () -> {
         return this.theme.getSliderRenderer(logicalLevel, graphicalLevel, container);
      }, (Object)null, logicalLevel, graphicalLevel, container);
   }

   public IRadioRenderer getRadioRenderer(int logicalLevel, int graphicalLevel, boolean container) {
      return (IRadioRenderer)getRenderer(this.radioRenderer, () -> {
         return this.theme.getRadioRenderer(logicalLevel, graphicalLevel, container);
      }, (Object)null, logicalLevel, graphicalLevel, container);
   }

   public IResizeBorderRenderer getResizeRenderer() {
      if (this.resizeRenderer == null) {
         this.resizeRenderer = this.theme.getResizeRenderer();
      }

      return this.resizeRenderer;
   }

   public ITextFieldRenderer getTextRenderer(boolean embed, int logicalLevel, int graphicalLevel, boolean container) {
      return (ITextFieldRenderer)getRenderer(this.textRenderer, () -> {
         return this.theme.getTextRenderer(embed, logicalLevel, graphicalLevel, container);
      }, embed, logicalLevel, graphicalLevel, container);
   }

   public ISwitchRenderer<Boolean> getToggleSwitchRenderer(int logicalLevel, int graphicalLevel, boolean container) {
      return (ISwitchRenderer)getRenderer(this.toggleSwitchRenderer, () -> {
         return this.theme.getToggleSwitchRenderer(logicalLevel, graphicalLevel, container);
      }, (Object)null, logicalLevel, graphicalLevel, container);
   }

   public ISwitchRenderer<String> getCycleSwitchRenderer(int logicalLevel, int graphicalLevel, boolean container) {
      return (ISwitchRenderer)getRenderer(this.cycleSwitchRenderer, () -> {
         return this.theme.getCycleSwitchRenderer(logicalLevel, graphicalLevel, container);
      }, (Object)null, logicalLevel, graphicalLevel, container);
   }

   public IColorPickerRenderer getColorPickerRenderer() {
      if (this.colorPickerRenderer == null) {
         this.colorPickerRenderer = this.theme.getColorPickerRenderer();
      }

      return this.colorPickerRenderer;
   }

   public int getBaseHeight() {
      return this.theme.getBaseHeight();
   }

   public Color getMainColor(boolean focus, boolean active) {
      return this.theme.getMainColor(focus, active);
   }

   public Color getBackgroundColor(boolean focus) {
      return this.theme.getBackgroundColor(focus);
   }

   public Color getFontColor(boolean focus) {
      return this.theme.getFontColor(focus);
   }

   public void overrideMainColor(Color color) {
      this.theme.overrideMainColor(color);
   }

   public void restoreMainColor() {
      this.theme.restoreMainColor();
   }

   private static <S, T, U> U getRenderer(Map<OptimizedTheme.ParameterTuple<S, T>, U> table, Supplier<U> init, S type, int logicalLevel, int graphicalLevel, T container) {
      OptimizedTheme.ParameterTuple<S, T> key = new OptimizedTheme.ParameterTuple(type, logicalLevel, graphicalLevel, container);
      U value = table.getOrDefault(key, (Object)null);
      if (value == null) {
         table.put(key, value = init.get());
      }

      return value;
   }

   private static class ParameterTuple<S, T> {
      private final S type;
      private final int logicalLevel;
      private final int graphicalLevel;
      private final T container;

      public ParameterTuple(S type, int logicalLevel, int graphicalLevel, T container) {
         this.type = type;
         this.logicalLevel = logicalLevel;
         this.graphicalLevel = graphicalLevel;
         this.container = container;
      }

      public int hashCode() {
         return this.toString().hashCode();
      }

      public boolean equals(Object o) {
         return o instanceof OptimizedTheme.ParameterTuple ? this.toString().equals(o.toString()) : false;
      }

      public String toString() {
         return "(" + this.type + "," + this.logicalLevel + "," + this.graphicalLevel + "," + this.container + ")";
      }
   }
}
