package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.IInterface;
import java.awt.Color;
import java.awt.Rectangle;

public interface ITheme {
   int NONE = 0;
   int CLOSE = 1;
   int MINIMIZE = 2;
   int ADD = 3;
   int LEFT = 4;
   int RIGHT = 5;
   int UP = 6;
   int DOWN = 7;

   void loadAssets(IInterface var1);

   IDescriptionRenderer getDescriptionRenderer();

   IContainerRenderer getContainerRenderer(int var1, int var2, boolean var3);

   <T> IPanelRenderer<T> getPanelRenderer(Class<T> var1, int var2, int var3);

   <T> IScrollBarRenderer<T> getScrollBarRenderer(Class<T> var1, int var2, int var3);

   <T> IEmptySpaceRenderer<T> getEmptySpaceRenderer(Class<T> var1, int var2, int var3, boolean var4);

   <T> IButtonRenderer<T> getButtonRenderer(Class<T> var1, int var2, int var3, boolean var4);

   IButtonRenderer<Void> getSmallButtonRenderer(int var1, int var2, int var3, boolean var4);

   IButtonRenderer<String> getKeybindRenderer(int var1, int var2, boolean var3);

   ISliderRenderer getSliderRenderer(int var1, int var2, boolean var3);

   IRadioRenderer getRadioRenderer(int var1, int var2, boolean var3);

   IResizeBorderRenderer getResizeRenderer();

   ITextFieldRenderer getTextRenderer(boolean var1, int var2, int var3, boolean var4);

   ISwitchRenderer<Boolean> getToggleSwitchRenderer(int var1, int var2, boolean var3);

   ISwitchRenderer<String> getCycleSwitchRenderer(int var1, int var2, boolean var3);

   IColorPickerRenderer getColorPickerRenderer();

   int getBaseHeight();

   Color getMainColor(boolean var1, boolean var2);

   Color getBackgroundColor(boolean var1);

   Color getFontColor(boolean var1);

   void overrideMainColor(Color var1);

   void restoreMainColor();

   static Color combineColors(Color main, Color opacity) {
      return new Color(main.getRed(), main.getGreen(), main.getBlue(), opacity.getAlpha());
   }

   static void drawRect(IInterface inter, Rectangle rect, Color color) {
      inter.fillRect(new Rectangle(rect.x, rect.y, 1, rect.height), color, color, color, color);
      inter.fillRect(new Rectangle(rect.x + 1, rect.y, rect.width - 2, 1), color, color, color, color);
      inter.fillRect(new Rectangle(rect.x + rect.width - 1, rect.y, 1, rect.height), color, color, color, color);
      inter.fillRect(new Rectangle(rect.x + 1, rect.y + rect.height - 1, rect.width - 2, 1), color, color, color, color);
   }
}
