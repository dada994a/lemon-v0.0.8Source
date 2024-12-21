package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.IInterface;
import java.awt.Color;

public abstract class ThemeBase implements ITheme {
   protected final IColorScheme scheme;
   private Color overrideColor = null;

   public ThemeBase(IColorScheme scheme) {
      this.scheme = scheme;
   }

   public void loadAssets(IInterface inter) {
   }

   public void overrideMainColor(Color color) {
      this.overrideColor = color;
   }

   public void restoreMainColor() {
      this.overrideColor = null;
   }

   protected Color getColor(Color color) {
      return this.overrideColor == null ? color : this.overrideColor;
   }
}
