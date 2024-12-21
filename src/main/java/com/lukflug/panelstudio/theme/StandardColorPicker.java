package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

public abstract class StandardColorPicker implements IColorPickerRenderer {
   public void renderPicker(Context context, boolean focus, Color color) {
      float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[])null);
      Color colorA = Color.getHSBColor(hsb[0], 0.0F, 1.0F);
      Color colorB = Color.getHSBColor(hsb[0], 1.0F, 1.0F);
      context.getInterface().fillRect(context.getRect(), colorA, colorB, colorB, colorA);
      Color colorC = new Color(0, 0, 0, 0);
      Color colorD = new Color(0, 0, 0);
      context.getInterface().fillRect(context.getRect(), colorC, colorC, colorD, colorD);
      Point p = new Point(Math.round((float)context.getPos().x + hsb[1] * (float)(context.getSize().width - 1)), Math.round((float)context.getPos().y + (1.0F - hsb[2]) * (float)(context.getSize().height - 1)));
      this.renderCursor(context, p, color);
   }

   public Color transformPoint(Context context, Color color, Point point) {
      float hue = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[])null)[0];
      float saturation = (float)(point.x - context.getPos().x) / (float)(context.getSize().width - 1);
      float brightness = 1.0F + (float)(context.getPos().y - point.y) / (float)(context.getSize().height - 1);
      if (saturation > 1.0F) {
         saturation = 1.0F;
      } else if (saturation < 0.0F) {
         saturation = 0.0F;
      }

      if (brightness > 1.0F) {
         brightness = 1.0F;
      } else if (brightness < 0.0F) {
         brightness = 0.0F;
      }

      Color value = Color.getHSBColor(hue, saturation, brightness);
      return ITheme.combineColors(value, color);
   }

   public int getDefaultHeight(int width) {
      return Math.min(width, 8 * this.getBaseHeight());
   }

   protected void renderCursor(Context context, Point p, Color color) {
      Color fontColor = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
      context.getInterface().fillRect(new Rectangle(p.x, p.y - this.getPadding(), 1, 2 * this.getPadding() + 1), fontColor, fontColor, fontColor, fontColor);
      context.getInterface().fillRect(new Rectangle(p.x - this.getPadding(), p.y, 2 * this.getPadding() + 1, 1), fontColor, fontColor, fontColor, fontColor);
   }

   public abstract int getPadding();

   public abstract int getBaseHeight();
}
