package com.lukflug.panelstudio.theme;

public final class ThemeTuple {
   public final ITheme theme;
   public final int logicalLevel;
   public final int graphicalLevel;

   public ThemeTuple(ITheme theme, int logicalLevel, int graphicalLevel) {
      this.theme = theme;
      this.logicalLevel = logicalLevel;
      this.graphicalLevel = graphicalLevel;
   }

   public ThemeTuple(ThemeTuple previous, int logicalDiff, int graphicalDiff) {
      this.theme = previous.theme;
      this.logicalLevel = previous.logicalLevel + logicalDiff;
      this.graphicalLevel = previous.graphicalLevel + graphicalDiff;
   }

   public IContainerRenderer getContainerRenderer(boolean horizontal) {
      return this.theme.getContainerRenderer(this.logicalLevel, this.graphicalLevel, horizontal);
   }

   public <T> IPanelRenderer<T> getPanelRenderer(Class<T> type) {
      return this.theme.getPanelRenderer(type, this.logicalLevel, this.graphicalLevel);
   }

   public <T> IScrollBarRenderer<T> getScrollBarRenderer(Class<T> type) {
      return this.theme.getScrollBarRenderer(type, this.logicalLevel, this.graphicalLevel);
   }

   public <T> IEmptySpaceRenderer<T> getEmptySpaceRenderer(Class<T> type, boolean container) {
      return this.theme.getEmptySpaceRenderer(type, this.logicalLevel, this.graphicalLevel, container);
   }

   public <T> IButtonRenderer<T> getButtonRenderer(Class<T> type, boolean container) {
      return this.theme.getButtonRenderer(type, this.logicalLevel, this.graphicalLevel, container);
   }

   public IButtonRenderer<Void> getSmallButtonRenderer(int symbol, boolean container) {
      return this.theme.getSmallButtonRenderer(symbol, this.logicalLevel, this.graphicalLevel, container);
   }

   public IButtonRenderer<String> getKeybindRenderer(boolean container) {
      return this.theme.getKeybindRenderer(this.logicalLevel, this.graphicalLevel, container);
   }

   public ISliderRenderer getSliderRenderer(boolean container) {
      return this.theme.getSliderRenderer(this.logicalLevel, this.graphicalLevel, container);
   }

   public IRadioRenderer getRadioRenderer(boolean container) {
      return this.theme.getRadioRenderer(this.logicalLevel, this.graphicalLevel, container);
   }

   public ITextFieldRenderer getTextRenderer(boolean embed, boolean container) {
      return this.theme.getTextRenderer(embed, this.logicalLevel, this.graphicalLevel, container);
   }

   public ISwitchRenderer<Boolean> getToggleSwitchRenderer(boolean container) {
      return this.theme.getToggleSwitchRenderer(this.logicalLevel, this.graphicalLevel, container);
   }

   public ISwitchRenderer<String> getCycleSwitchRenderer(boolean container) {
      return this.theme.getCycleSwitchRenderer(this.logicalLevel, this.graphicalLevel, container);
   }
}
