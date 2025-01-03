package com.lukflug.panelstudio.theme;

public class RendererTuple<T> {
   public final IPanelRenderer<T> panelRenderer;
   public final IScrollBarRenderer<T> scrollRenderer;
   public final IEmptySpaceRenderer<T> cornerRenderer;
   public final IEmptySpaceRenderer<T> emptyRenderer;

   public RendererTuple(Class<T> type, ThemeTuple theme) {
      this.panelRenderer = theme.getPanelRenderer(type);
      this.scrollRenderer = theme.getScrollBarRenderer(type);
      this.cornerRenderer = theme.getEmptySpaceRenderer(type, false);
      this.emptyRenderer = theme.getEmptySpaceRenderer(type, true);
   }
}
