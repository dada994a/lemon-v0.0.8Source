package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import java.awt.Color;
import java.awt.Point;

public interface IColorPickerRenderer {
   void renderPicker(Context var1, boolean var2, Color var3);

   Color transformPoint(Context var1, Color var2, Point var3);

   int getDefaultHeight(int var1);
}
