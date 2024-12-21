package com.lukflug.panelstudio.config;

import java.awt.Dimension;
import java.awt.Point;

public interface IPanelConfig {
   void savePositon(Point var1);

   void saveSize(Dimension var1);

   Point loadPosition();

   Dimension loadSize();

   void saveState(boolean var1);

   boolean loadState();
}
