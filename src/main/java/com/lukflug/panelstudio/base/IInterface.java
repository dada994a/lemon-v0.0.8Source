package com.lukflug.panelstudio.base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public interface IInterface {
   int LBUTTON = 0;
   int RBUTTON = 1;
   int SHIFT = 0;
   int CTRL = 1;
   int ALT = 2;
   int SUPER = 3;

   long getTime();

   Point getMouse();

   boolean getButton(int var1);

   boolean getModifier(int var1);

   void drawString(Point var1, int var2, String var3, Color var4);

   int getFontWidth(int var1, String var2);

   void fillTriangle(Point var1, Point var2, Point var3, Color var4, Color var5, Color var6);

   void drawLine(Point var1, Point var2, Color var3, Color var4);

   void fillRect(Rectangle var1, Color var2, Color var3, Color var4, Color var5);

   void drawRect(Rectangle var1, Color var2, Color var3, Color var4, Color var5);

   int loadImage(String var1);

   default void drawImage(Rectangle r, int rotation, boolean parity, int image) {
      this.drawImage(r, rotation, parity, image, new Color(255, 255, 255));
   }

   void drawImage(Rectangle var1, int var2, boolean var3, int var4, Color var5);

   Dimension getWindowSize();

   void window(Rectangle var1);

   void restore();
}
