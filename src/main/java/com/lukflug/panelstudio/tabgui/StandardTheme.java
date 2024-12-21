package com.lukflug.panelstudio.tabgui;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import com.lukflug.panelstudio.popup.PanelPositioner;
import com.lukflug.panelstudio.theme.IColorScheme;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

public class StandardTheme implements ITabGUITheme {
   protected final IColorScheme scheme;
   protected int width;
   protected int height;
   protected int padding;
   protected IPopupPositioner positioner;
   protected StandardTheme.RendererBase<Void> parentRenderer;
   protected StandardTheme.RendererBase<Boolean> childRenderer;

   public StandardTheme(final IColorScheme scheme, int width, int height, int padding, int distance) {
      this.scheme = scheme;
      this.width = width;
      this.height = height;
      this.padding = padding;
      this.positioner = new PanelPositioner(new Point(distance, 0));
      scheme.createSetting((ITheme)null, "Selected Color", "The color for the selected tab element.", false, true, new Color(0, 0, 255), false);
      scheme.createSetting((ITheme)null, "Background Color", "The color for the tab background.", true, true, new Color(32, 32, 32, 128), false);
      scheme.createSetting((ITheme)null, "Outline Color", "The color for the tab outline.", false, true, new Color(0, 0, 0), false);
      scheme.createSetting((ITheme)null, "Font Color", "The main color for the text.", false, true, new Color(255, 255, 255), false);
      scheme.createSetting((ITheme)null, "Enabled Color", "The color for enabled text.", false, true, new Color(255, 0, 0), false);
      this.parentRenderer = new StandardTheme.RendererBase<Void>() {
         protected Color getFontColor(Void itemState) {
            return scheme.getColor("Font Color");
         }
      };
      this.childRenderer = new StandardTheme.RendererBase<Boolean>() {
         protected Color getFontColor(Boolean itemState) {
            return itemState ? scheme.getColor("Enabled Color") : scheme.getColor("Font Color");
         }
      };
   }

   public int getTabWidth() {
      return this.width;
   }

   public IPopupPositioner getPositioner() {
      return this.positioner;
   }

   public ITabGUIRenderer<Void> getParentRenderer() {
      return this.parentRenderer;
   }

   public ITabGUIRenderer<Boolean> getChildRenderer() {
      return this.childRenderer;
   }

   protected abstract class RendererBase<T> implements ITabGUIRenderer<T> {
      public void renderTab(Context context, int amount, double tabState) {
         Color color = StandardTheme.this.scheme.getColor("Selected Color");
         Color fill = StandardTheme.this.scheme.getColor("Background Color");
         Color border = StandardTheme.this.scheme.getColor("Outline Color");
         context.getInterface().fillRect(context.getRect(), fill, fill, fill, fill);
         context.getInterface().fillRect(this.getItemRect(context.getInterface(), context.getRect(), amount, tabState), color, color, color, color);
         context.getInterface().drawRect(this.getItemRect(context.getInterface(), context.getRect(), amount, tabState), border, border, border, border);
         context.getInterface().drawRect(context.getRect(), border, border, border, border);
      }

      public void renderItem(Context context, int amount, double tabState, int index, String title, T itemState) {
         context.getInterface().drawString(new Point(context.getPos().x + StandardTheme.this.padding, context.getPos().y + context.getSize().height * index / amount + StandardTheme.this.padding), StandardTheme.this.height, title, this.getFontColor(itemState));
      }

      public int getTabHeight(int amount) {
         return (StandardTheme.this.height + 2 * StandardTheme.this.padding) * amount;
      }

      public Rectangle getItemRect(IInterface inter, Rectangle rect, int amount, double tabState) {
         return new Rectangle(rect.x, rect.y + (int)Math.round((double)rect.height * tabState / (double)amount), rect.width, StandardTheme.this.height + 2 * StandardTheme.this.padding);
      }

      protected abstract Color getFontColor(T var1);
   }
}
