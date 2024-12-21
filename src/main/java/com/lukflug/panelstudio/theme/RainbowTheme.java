package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.setting.ILabeled;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.function.IntSupplier;

public class RainbowTheme extends ThemeBase {
   protected IBoolean ignoreDisabled;
   protected IBoolean buttonRainbow;
   protected IntSupplier rainbowGradient;
   protected int height;
   protected int padding;
   protected String separator;

   public RainbowTheme(IColorScheme scheme, IBoolean ignoreDisabled, IBoolean buttonRainbow, IntSupplier rainbowGradient, int height, int padding, String separator) {
      super(scheme);
      this.ignoreDisabled = ignoreDisabled;
      this.buttonRainbow = buttonRainbow;
      this.rainbowGradient = rainbowGradient;
      this.height = height;
      this.padding = padding;
      this.separator = separator;
      scheme.createSetting(this, "Title Color", "The color for panel titles.", false, true, new Color(64, 64, 64), false);
      scheme.createSetting(this, "Rainbow Color", "The rainbow base color.", false, true, new Color(255, 0, 0), false);
      scheme.createSetting(this, "Background Color", "The main color for disabled components.", false, true, new Color(64, 64, 64), false);
      scheme.createSetting(this, "Font Color", "The main color for text.", false, true, new Color(255, 255, 255), false);
      scheme.createSetting(this, "Highlight Color", "The color for highlighted text.", false, true, new Color(0, 0, 255), false);
   }

   protected void renderOverlay(Context context) {
      Color color = context.isHovered() ? new Color(0, 0, 0, 64) : new Color(0, 0, 0, 0);
      context.getInterface().fillRect(context.getRect(), color, color, color, color);
   }

   protected void renderRainbowRect(Rectangle rect, Context context, boolean focus) {
      Color source = this.getMainColor(focus, true);
      float[] hsb = Color.RGBtoHSB(source.getRed(), source.getGreen(), source.getBlue(), (float[])null);
      float currentHue = hsb[0];
      float targetHue = hsb[0];
      if (this.rainbowGradient.getAsInt() != 0) {
         targetHue += (float)rect.height / (float)this.rainbowGradient.getAsInt();
      } else {
         context.getInterface().fillRect(rect, source, source, source, source);
      }

      while(currentHue < targetHue) {
         float nextHue = (float)(Math.floor((double)(currentHue * 6.0F)) + 1.0D) / 6.0F;
         if (nextHue > targetHue) {
            nextHue = targetHue;
         }

         Color colorA = Color.getHSBColor(currentHue, hsb[1], hsb[2]);
         Color colorB = Color.getHSBColor(nextHue, hsb[1], hsb[2]);
         int top = Math.round((currentHue - hsb[0]) * (float)this.rainbowGradient.getAsInt());
         int bottom = Math.round((nextHue - hsb[0]) * (float)this.rainbowGradient.getAsInt());
         context.getInterface().fillRect(new Rectangle(rect.x, rect.y + top, rect.width, bottom - top), colorA, colorA, colorB, colorB);
         currentHue = nextHue;
      }

   }

   protected void renderSmallButton(Context context, String title, int symbol, boolean focus) {
      Point[] points = new Point[3];
      int padding = context.getSize().height <= 12 ? (context.getSize().height <= 8 ? 2 : 4) : 6;
      Rectangle rect = new Rectangle(context.getPos().x + padding / 2, context.getPos().y + padding / 2, context.getSize().height - 2 * (padding / 2), context.getSize().height - 2 * (padding / 2));
      if (title == null) {
         rect.x += context.getSize().width / 2 - context.getSize().height / 2;
      }

      Color color = this.getFontColor(focus);
      switch(symbol) {
      case 1:
         context.getInterface().drawLine(new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), color, color);
         context.getInterface().drawLine(new Point(rect.x, rect.y + rect.height), new Point(rect.x + rect.width, rect.y), color, color);
         break;
      case 2:
         context.getInterface().fillRect(new Rectangle(rect.x, rect.y + rect.height - 2, rect.width, 2), color, color, color, color);
         break;
      case 3:
         if (rect.width % 2 == 1) {
            --rect.width;
         }

         if (rect.height % 2 == 1) {
            --rect.height;
         }

         context.getInterface().fillRect(new Rectangle(rect.x + rect.width / 2 - 1, rect.y, 2, rect.height), color, color, color, color);
         context.getInterface().fillRect(new Rectangle(rect.x, rect.y + rect.height / 2 - 1, rect.width, 2), color, color, color, color);
         break;
      case 4:
         if (rect.height % 2 == 1) {
            --rect.height;
         }

         points[2] = new Point(rect.x + rect.width, rect.y);
         points[0] = new Point(rect.x + rect.width, rect.y + rect.height);
         points[1] = new Point(rect.x, rect.y + rect.height / 2);
         break;
      case 5:
         if (rect.height % 2 == 1) {
            --rect.height;
         }

         points[0] = new Point(rect.x, rect.y);
         points[2] = new Point(rect.x, rect.y + rect.height);
         points[1] = new Point(rect.x + rect.width, rect.y + rect.height / 2);
         break;
      case 6:
         if (rect.width % 2 == 1) {
            --rect.width;
         }

         points[0] = new Point(rect.x, rect.y + rect.height);
         points[2] = new Point(rect.x + rect.width, rect.y + rect.height);
         points[1] = new Point(rect.x + rect.width / 2, rect.y);
         break;
      case 7:
         if (rect.width % 2 == 1) {
            --rect.width;
         }

         points[2] = new Point(rect.x, rect.y);
         points[0] = new Point(rect.x + rect.width, rect.y);
         points[1] = new Point(rect.x + rect.width / 2, rect.y + rect.height);
      }

      if (symbol >= 4 && symbol <= 7) {
         context.getInterface().drawLine(points[0], points[1], color, color);
         context.getInterface().drawLine(points[1], points[2], color, color);
      }

      if (title != null) {
         context.getInterface().drawString(new Point(context.getPos().x + (symbol == 0 ? padding : context.getSize().height), context.getPos().y + padding), this.height, title, this.getFontColor(focus));
      }

   }

   public IDescriptionRenderer getDescriptionRenderer() {
      return new IDescriptionRenderer() {
         public void renderDescription(IInterface inter, Point pos, String text) {
            Rectangle rect = new Rectangle(pos, new Dimension(inter.getFontWidth(RainbowTheme.this.height, text) + 2, RainbowTheme.this.height + 2));
            Color color = RainbowTheme.this.getBackgroundColor(true);
            inter.fillRect(rect, color, color, color, color);
            inter.drawString(new Point(pos.x + 1, pos.y + 1), RainbowTheme.this.height, text, RainbowTheme.this.getFontColor(true));
         }
      };
   }

   public IContainerRenderer getContainerRenderer(int logicalLevel, final int graphicalLevel, boolean horizontal) {
      return new IContainerRenderer() {
         public void renderBackground(Context context, boolean focus) {
            if (graphicalLevel == 0 && !RainbowTheme.this.buttonRainbow.isOn()) {
               RainbowTheme.this.renderRainbowRect(context.getRect(), context, focus);
            }

         }
      };
   }

   public <T> IPanelRenderer<T> getPanelRenderer(Class<T> type, int logicalLevel, final int graphicalLevel) {
      return new IPanelRenderer<T>() {
         public int getBorder() {
            return graphicalLevel == 0 ? 1 : 0;
         }

         public void renderPanelOverlay(Context context, boolean focus, T state, boolean open) {
         }

         public void renderTitleOverlay(Context context, boolean focus, T state, boolean open) {
            if (graphicalLevel <= 0) {
               Color color = RainbowTheme.this.getFontColor(focus);
               context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y + context.getSize().height, context.getSize().width, 1), color, color, color, color);
            } else {
               Rectangle rect = context.getRect();
               rect = new Rectangle(rect.width - rect.height, 0, rect.height, rect.height);
               if (rect.width % 2 != 0) {
                  --rect.width;
                  --rect.height;
                  ++rect.x;
               }

               Context subContext = new Context(context, rect.width, rect.getLocation(), true, true);
               subContext.setHeight(rect.height);
               if (open) {
                  RainbowTheme.this.renderSmallButton(subContext, (String)null, 7, focus);
               } else {
                  RainbowTheme.this.renderSmallButton(subContext, (String)null, 5, focus);
               }
            }

         }
      };
   }

   public <T> IScrollBarRenderer<T> getScrollBarRenderer(Class<T> type, int logicalLevel, final int graphicalLevel) {
      return new IScrollBarRenderer<T>() {
         public int renderScrollBar(Context context, boolean focus, T state, boolean horizontal, int height, int position) {
            Color color = RainbowTheme.this.getBackgroundColor(focus);
            if (graphicalLevel == 0 || RainbowTheme.this.buttonRainbow.isOn()) {
               RainbowTheme.this.renderRainbowRect(context.getRect(), context, focus);
            }

            int a;
            int b;
            if (horizontal) {
               a = (int)((double)position / (double)height * (double)context.getSize().width);
               b = (int)((double)(position + context.getSize().width) / (double)height * (double)context.getSize().width);
               context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y, a, context.getSize().height), color, color, color, color);
               context.getInterface().fillRect(new Rectangle(context.getPos().x + b, context.getPos().y, context.getSize().width - b, context.getSize().height), color, color, color, color);
            } else {
               a = (int)((double)position / (double)height * (double)context.getSize().height);
               b = (int)((double)(position + context.getSize().height) / (double)height * (double)context.getSize().height);
               context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y, context.getSize().width, a), color, color, color, color);
               context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y + b, context.getSize().width, context.getSize().height - b), color, color, color, color);
            }

            return horizontal ? (int)((double)((context.getInterface().getMouse().x - context.getPos().x) * height) / (double)context.getSize().width - (double)context.getSize().width / 2.0D) : (int)((double)((context.getInterface().getMouse().y - context.getPos().y) * height) / (double)context.getSize().height - (double)context.getSize().height / 2.0D);
         }

         public int getThickness() {
            return 4;
         }
      };
   }

   public <T> IEmptySpaceRenderer<T> getEmptySpaceRenderer(Class<T> type, int logicalLevel, int graphicalLevel, boolean container) {
      return new IEmptySpaceRenderer<T>() {
         public void renderSpace(Context context, boolean focus, T state) {
            Color color = RainbowTheme.this.getBackgroundColor(focus);
            context.getInterface().fillRect(context.getRect(), color, color, color, color);
         }
      };
   }

   public <T> IButtonRenderer<T> getButtonRenderer(final Class<T> type, final int logicalLevel, final int graphicalLevel, final boolean container) {
      return new IButtonRenderer<T>() {
         public void renderButton(Context context, String title, boolean focus, Object state) {
            boolean effFocus = container ? context.hasFocus() : focus;
            boolean active = container && graphicalLevel != 0;
            if (type == Boolean.class) {
               active = (Boolean)state || RainbowTheme.this.ignoreDisabled.isOn() && container;
            }

            if (!active) {
               Color color = RainbowTheme.this.getBackgroundColor(effFocus);
               context.getInterface().fillRect(context.getRect(), color, color, color, color);
            } else if (graphicalLevel == 0 || RainbowTheme.this.buttonRainbow.isOn()) {
               RainbowTheme.this.renderRainbowRect(context.getRect(), context, effFocus);
            }

            RainbowTheme.this.renderOverlay(context);
            String text = (logicalLevel >= 2 ? "> " : "") + title + (type == String.class ? RainbowTheme.this.separator + state : "");
            context.getInterface().drawString(new Point(context.getPos().x + RainbowTheme.this.padding, context.getPos().y + RainbowTheme.this.padding), RainbowTheme.this.height, text, RainbowTheme.this.getFontColor(effFocus));
         }

         public int getDefaultHeight() {
            return RainbowTheme.this.getBaseHeight();
         }
      };
   }

   public IButtonRenderer<Void> getSmallButtonRenderer(final int symbol, final int logicalLevel, final int graphicalLevel, final boolean container) {
      return new IButtonRenderer<Void>() {
         public void renderButton(Context context, String title, boolean focus, Void state) {
            if (graphicalLevel == 0 || RainbowTheme.this.buttonRainbow.isOn()) {
               RainbowTheme.this.renderRainbowRect(context.getRect(), context, focus);
            }

            RainbowTheme.this.renderOverlay(context);
            if (!container || logicalLevel <= 0) {
               RainbowTheme.this.renderSmallButton(context, title, symbol, focus);
            }

         }

         public int getDefaultHeight() {
            return RainbowTheme.this.getBaseHeight();
         }
      };
   }

   public IButtonRenderer<String> getKeybindRenderer(int logicalLevel, int graphicalLevel, boolean container) {
      return this.getButtonRenderer(String.class, logicalLevel, graphicalLevel, container);
   }

   public ISliderRenderer getSliderRenderer(final int logicalLevel, final int graphicalLevel, final boolean container) {
      return new ISliderRenderer() {
         public void renderSlider(Context context, String title, String state, boolean focus, double value) {
            boolean effFocus = container ? context.hasFocus() : focus;
            if (graphicalLevel == 0 || RainbowTheme.this.buttonRainbow.isOn()) {
               RainbowTheme.this.renderRainbowRect(context.getRect(), context, effFocus);
            }

            int divider = (int)((double)context.getSize().width * value);
            Color color = RainbowTheme.this.getBackgroundColor(effFocus);
            context.getInterface().fillRect(new Rectangle(context.getPos().x + divider, context.getPos().y, context.getSize().width - divider, context.getSize().height), color, color, color, color);
            RainbowTheme.this.renderOverlay(context);
            String text = (logicalLevel >= 2 ? "> " : "") + title + RainbowTheme.this.separator + state;
            context.getInterface().drawString(new Point(context.getPos().x + RainbowTheme.this.padding, context.getPos().y + RainbowTheme.this.padding), RainbowTheme.this.height, text, RainbowTheme.this.getFontColor(effFocus));
         }

         public int getDefaultHeight() {
            return RainbowTheme.this.getBaseHeight();
         }
      };
   }

   public IRadioRenderer getRadioRenderer(int logicalLevel, final int graphicalLevel, boolean container) {
      return new IRadioRenderer() {
         public void renderItem(Context context, ILabeled[] items, boolean focus, int target, double state, boolean horizontal) {
            if (graphicalLevel == 0 || RainbowTheme.this.buttonRainbow.isOn()) {
               RainbowTheme.this.renderRainbowRect(context.getRect(), context, focus);
            }

            for(int i = 0; i < items.length; ++i) {
               Rectangle rect = this.getItemRect(context, items, i, horizontal);
               Context subContext = new Context(context.getInterface(), rect.width, rect.getLocation(), context.hasFocus(), context.onTop());
               subContext.setHeight(rect.height);
               if (i != target) {
                  Color color = RainbowTheme.this.getBackgroundColor(focus);
                  context.getInterface().fillRect(subContext.getRect(), color, color, color, color);
               }

               RainbowTheme.this.renderOverlay(subContext);
               context.getInterface().drawString(new Point(rect.x + RainbowTheme.this.padding, rect.y + RainbowTheme.this.padding), RainbowTheme.this.height, items[i].getDisplayName(), RainbowTheme.this.getFontColor(focus));
            }

         }

         public int getDefaultHeight(ILabeled[] items, boolean horizontal) {
            return (horizontal ? 1 : items.length) * RainbowTheme.this.getBaseHeight();
         }
      };
   }

   public IResizeBorderRenderer getResizeRenderer() {
      return new IResizeBorderRenderer() {
         public void drawBorder(Context context, boolean focus) {
            Color color = RainbowTheme.this.getBackgroundColor(focus);
            Rectangle rect = context.getRect();
            context.getInterface().fillRect(new Rectangle(rect.x, rect.y, rect.width, this.getBorder()), color, color, color, color);
            context.getInterface().fillRect(new Rectangle(rect.x, rect.y + rect.height - this.getBorder(), rect.width, this.getBorder()), color, color, color, color);
            context.getInterface().fillRect(new Rectangle(rect.x, rect.y + this.getBorder(), this.getBorder(), rect.height - 2 * this.getBorder()), color, color, color, color);
            context.getInterface().fillRect(new Rectangle(rect.x + rect.width - this.getBorder(), rect.y + this.getBorder(), this.getBorder(), rect.height - 2 * this.getBorder()), color, color, color, color);
         }

         public int getBorder() {
            return 2;
         }
      };
   }

   public ITextFieldRenderer getTextRenderer(boolean embed, int logicalLevel, final int graphicalLevel, final boolean container) {
      return new ITextFieldRenderer() {
         public int renderTextField(Context context, String title, boolean focus, String content, int position, int select, int boxPosition, boolean insertMode) {
            boolean effFocus = container ? context.hasFocus() : focus;
            if (graphicalLevel == 0 || RainbowTheme.this.buttonRainbow.isOn()) {
               RainbowTheme.this.renderRainbowRect(context.getRect(), context, effFocus);
            }

            Color textColor = RainbowTheme.this.getFontColor(effFocus);
            Color highlightColor = RainbowTheme.this.scheme.getColor("Highlight Color");
            Rectangle rect = this.getTextArea(context, title);
            int strlen = context.getInterface().getFontWidth(RainbowTheme.this.height, content.substring(0, position));
            context.getInterface().fillRect(rect, new Color(0, 0, 0, 64), new Color(0, 0, 0, 64), new Color(0, 0, 0, 64), new Color(0, 0, 0, 64));
            ITheme.drawRect(context.getInterface(), rect, new Color(0, 0, 0, 64));
            int maxPosition;
            if (boxPosition < position) {
               for(maxPosition = boxPosition; maxPosition < position && context.getInterface().getFontWidth(RainbowTheme.this.height, content.substring(0, maxPosition)) + rect.width - RainbowTheme.this.padding < strlen; ++maxPosition) {
               }

               if (boxPosition < maxPosition) {
                  boxPosition = maxPosition;
               }
            } else if (boxPosition > position) {
               boxPosition = position - 1;
            }

            for(maxPosition = content.length(); maxPosition > 0; --maxPosition) {
               if (context.getInterface().getFontWidth(RainbowTheme.this.height, content.substring(maxPosition)) >= rect.width - RainbowTheme.this.padding) {
                  ++maxPosition;
                  break;
               }
            }

            if (boxPosition > maxPosition) {
               boxPosition = maxPosition;
            } else if (boxPosition < 0) {
               boxPosition = 0;
            }

            int offset = context.getInterface().getFontWidth(RainbowTheme.this.height, content.substring(0, boxPosition));
            int x1 = rect.x + RainbowTheme.this.padding / 2 - offset + strlen;
            int x2 = rect.x + RainbowTheme.this.padding / 2 - offset;
            if (position < content.length()) {
               x2 += context.getInterface().getFontWidth(RainbowTheme.this.height, content.substring(0, position + 1));
            } else {
               x2 += context.getInterface().getFontWidth(RainbowTheme.this.height, content + "X");
            }

            RainbowTheme.this.renderOverlay(context);
            context.getInterface().drawString(new Point(context.getPos().x + RainbowTheme.this.padding, context.getPos().y + RainbowTheme.this.padding / 2), RainbowTheme.this.height, title + RainbowTheme.this.separator, textColor);
            context.getInterface().window(rect);
            if (select >= 0) {
               int x3 = rect.x + RainbowTheme.this.padding / 2 - offset + context.getInterface().getFontWidth(RainbowTheme.this.height, content.substring(0, select));
               context.getInterface().fillRect(new Rectangle(Math.min(x1, x3), rect.y + RainbowTheme.this.padding / 2, Math.abs(x3 - x1), RainbowTheme.this.height), highlightColor, highlightColor, highlightColor, highlightColor);
            }

            context.getInterface().drawString(new Point(rect.x + RainbowTheme.this.padding / 2 - offset, rect.y + RainbowTheme.this.padding / 2), RainbowTheme.this.height, content, textColor);
            if (System.currentTimeMillis() / 500L % 2L == 0L && focus) {
               if (insertMode) {
                  context.getInterface().fillRect(new Rectangle(x1, rect.y + RainbowTheme.this.padding / 2 + RainbowTheme.this.height, x2 - x1, 1), textColor, textColor, textColor, textColor);
               } else {
                  context.getInterface().fillRect(new Rectangle(x1, rect.y + RainbowTheme.this.padding / 2, 1, RainbowTheme.this.height), textColor, textColor, textColor, textColor);
               }
            }

            context.getInterface().restore();
            return boxPosition;
         }

         public int getDefaultHeight() {
            int height = RainbowTheme.this.getBaseHeight() - RainbowTheme.this.padding;
            if (height % 2 == 1) {
               ++height;
            }

            return height;
         }

         public Rectangle getTextArea(Context context, String title) {
            Rectangle rect = context.getRect();
            int length = RainbowTheme.this.padding + context.getInterface().getFontWidth(RainbowTheme.this.height, title + RainbowTheme.this.separator);
            return new Rectangle(rect.x + length, rect.y, rect.width - length, rect.height);
         }

         public int transformToCharPos(Context context, String title, String content, int boxPosition) {
            Rectangle rect = this.getTextArea(context, title);
            Point mouse = context.getInterface().getMouse();
            int offset = context.getInterface().getFontWidth(RainbowTheme.this.height, content.substring(0, boxPosition));
            if (rect.contains(mouse)) {
               for(int i = 1; i <= content.length(); ++i) {
                  if (rect.x + RainbowTheme.this.padding / 2 - offset + context.getInterface().getFontWidth(RainbowTheme.this.height, content.substring(0, i)) > mouse.x) {
                     return i - 1;
                  }
               }

               return content.length();
            } else {
               return -1;
            }
         }
      };
   }

   public ISwitchRenderer<Boolean> getToggleSwitchRenderer(int logicalLevel, final int graphicalLevel, final boolean container) {
      return new ISwitchRenderer<Boolean>() {
         public void renderButton(Context context, String title, boolean focus, Boolean state) {
            boolean effFocus = container ? context.hasFocus() : focus;
            if (graphicalLevel == 0 || RainbowTheme.this.buttonRainbow.isOn()) {
               RainbowTheme.this.renderRainbowRect(context.getRect(), context, effFocus);
            }

            Color color = RainbowTheme.this.getBackgroundColor(effFocus);
            if (graphicalLevel <= 0 && container) {
               context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y + context.getSize().height - 1, context.getSize().width, 1), color, color, color, color);
            }

            RainbowTheme.this.renderOverlay(context);
            context.getInterface().drawString(new Point(context.getPos().x + RainbowTheme.this.padding, context.getPos().y + RainbowTheme.this.padding), RainbowTheme.this.height, title + RainbowTheme.this.separator + (state ? "On" : "Off"), RainbowTheme.this.getFontColor(focus));
            Rectangle rect = state ? this.getOnField(context) : this.getOffField(context);
            context.getInterface().fillRect(rect, color, color, color, color);
            rect = context.getRect();
            rect = new Rectangle(rect.x + rect.width - 2 * rect.height + 3 * RainbowTheme.this.padding, rect.y + RainbowTheme.this.padding, 2 * rect.height - 4 * RainbowTheme.this.padding, rect.height - 2 * RainbowTheme.this.padding);
            ITheme.drawRect(context.getInterface(), rect, color);
         }

         public int getDefaultHeight() {
            return RainbowTheme.this.getBaseHeight();
         }

         public Rectangle getOnField(Context context) {
            Rectangle rect = context.getRect();
            return new Rectangle(rect.x + rect.width - rect.height + RainbowTheme.this.padding, rect.y + RainbowTheme.this.padding, rect.height - 2 * RainbowTheme.this.padding, rect.height - 2 * RainbowTheme.this.padding);
         }

         public Rectangle getOffField(Context context) {
            Rectangle rect = context.getRect();
            return new Rectangle(rect.x + rect.width - 2 * rect.height + 3 * RainbowTheme.this.padding, rect.y + RainbowTheme.this.padding, rect.height - 2 * RainbowTheme.this.padding, rect.height - 2 * RainbowTheme.this.padding);
         }
      };
   }

   public ISwitchRenderer<String> getCycleSwitchRenderer(final int logicalLevel, final int graphicalLevel, final boolean container) {
      return new ISwitchRenderer<String>() {
         public void renderButton(Context context, String title, boolean focus, String state) {
            boolean effFocus = container ? context.hasFocus() : focus;
            if (graphicalLevel == 0 || RainbowTheme.this.buttonRainbow.isOn()) {
               RainbowTheme.this.renderRainbowRect(context.getRect(), context, effFocus);
            }

            Context subContext = new Context(context, context.getSize().width - 2 * context.getSize().height, new Point(0, 0), true, true);
            subContext.setHeight(context.getSize().height);
            RainbowTheme.this.renderOverlay(subContext);
            Color textColor = RainbowTheme.this.getFontColor(effFocus);
            context.getInterface().drawString(new Point(context.getPos().x + RainbowTheme.this.padding, context.getPos().y + RainbowTheme.this.padding), RainbowTheme.this.height, title + RainbowTheme.this.separator + state, textColor);
            Rectangle rect = this.getOnField(context);
            subContext = new Context(context, rect.width, new Point(rect.x - context.getPos().x, 0), true, true);
            subContext.setHeight(rect.height);
            RainbowTheme.this.getSmallButtonRenderer(5, logicalLevel, graphicalLevel, container).renderButton(subContext, (String)null, effFocus, (Object)null);
            rect = this.getOffField(context);
            subContext = new Context(context, rect.width, new Point(rect.x - context.getPos().x, 0), true, true);
            subContext.setHeight(rect.height);
            RainbowTheme.this.getSmallButtonRenderer(4, logicalLevel, graphicalLevel, false).renderButton(subContext, (String)null, effFocus, (Object)null);
         }

         public int getDefaultHeight() {
            return RainbowTheme.this.getBaseHeight();
         }

         public Rectangle getOnField(Context context) {
            Rectangle rect = context.getRect();
            return new Rectangle(rect.x + rect.width - rect.height, rect.y, rect.height, rect.height);
         }

         public Rectangle getOffField(Context context) {
            Rectangle rect = context.getRect();
            return new Rectangle(rect.x + rect.width - 2 * rect.height, rect.y, rect.height, rect.height);
         }
      };
   }

   public IColorPickerRenderer getColorPickerRenderer() {
      return new StandardColorPicker() {
         public int getPadding() {
            return RainbowTheme.this.padding;
         }

         public int getBaseHeight() {
            return RainbowTheme.this.getBaseHeight();
         }
      };
   }

   public int getBaseHeight() {
      return this.height + 2 * this.padding;
   }

   public Color getMainColor(boolean focus, boolean active) {
      return active ? this.scheme.getColor("Rainbow Color") : this.scheme.getColor("Background Color");
   }

   public Color getBackgroundColor(boolean focus) {
      return this.scheme.getColor("Background Color");
   }

   public Color getFontColor(boolean focus) {
      return this.scheme.getColor("Font Color");
   }
}
