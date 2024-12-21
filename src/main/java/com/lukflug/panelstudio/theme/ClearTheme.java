package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.setting.ILabeled;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class ClearTheme extends ThemeBase {
   protected IBoolean gradient;
   protected int height;
   protected int padding;
   protected int border;
   protected String separator;

   public ClearTheme(IColorScheme scheme, IBoolean gradient, int height, int padding, int border, String separator) {
      super(scheme);
      this.gradient = gradient;
      this.height = height;
      this.padding = padding;
      this.border = border;
      this.separator = separator;
      scheme.createSetting(this, "Title Color", "The color for panel titles.", false, true, new Color(90, 145, 240), false);
      scheme.createSetting(this, "Enabled Color", "The main color for enabled components.", false, true, new Color(90, 145, 240), false);
      scheme.createSetting(this, "Disabled Color", "The main color for disabled switches.", false, true, new Color(64, 64, 64), false);
      scheme.createSetting(this, "Background Color", "The background color.", true, true, new Color(195, 195, 195, 150), false);
      scheme.createSetting(this, "Font Color", "The main color for text.", false, true, new Color(255, 255, 255), false);
      scheme.createSetting(this, "Scroll Bar Color", "The color for the scroll bar.", false, true, new Color(90, 145, 240), false);
      scheme.createSetting(this, "Highlight Color", "The color for highlighted text.", false, true, new Color(0, 0, 255), false);
   }

   protected void renderOverlay(Context context) {
      Color color = context.isHovered() ? new Color(0, 0, 0, 64) : new Color(0, 0, 0, 0);
      context.getInterface().fillRect(context.getRect(), color, color, color, color);
   }

   protected void renderBackground(Context context, boolean focus, int graphicalLevel) {
      if (graphicalLevel == 0) {
         Color color = this.getBackgroundColor(focus);
         context.getInterface().fillRect(context.getRect(), color, color, color, color);
      }

   }

   protected void renderSmallButton(Context context, String title, int symbol, boolean focus) {
      Point[] points = new Point[3];
      int padding = context.getSize().height <= 8 ? 2 : 4;
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
         points[1] = new Point(rect.x + rect.width, rect.y + rect.height);
         points[0] = new Point(rect.x, rect.y + rect.height / 2);
         break;
      case 5:
         if (rect.height % 2 == 1) {
            --rect.height;
         }

         points[0] = new Point(rect.x, rect.y);
         points[1] = new Point(rect.x, rect.y + rect.height);
         points[2] = new Point(rect.x + rect.width, rect.y + rect.height / 2);
         break;
      case 6:
         if (rect.width % 2 == 1) {
            --rect.width;
         }

         points[0] = new Point(rect.x, rect.y + rect.height);
         points[1] = new Point(rect.x + rect.width, rect.y + rect.height);
         points[2] = new Point(rect.x + rect.width / 2, rect.y);
         break;
      case 7:
         if (rect.width % 2 == 1) {
            --rect.width;
         }

         points[2] = new Point(rect.x, rect.y);
         points[1] = new Point(rect.x + rect.width, rect.y);
         points[0] = new Point(rect.x + rect.width / 2, rect.y + rect.height);
      }

      if (symbol >= 4 && symbol <= 7) {
         context.getInterface().fillTriangle(points[0], points[1], points[2], color, color, color);
      }

      if (title != null) {
         context.getInterface().drawString(new Point(context.getPos().x + (symbol == 0 ? padding : context.getSize().height), context.getPos().y + padding), this.height, title, this.getFontColor(focus));
      }

   }

   public IDescriptionRenderer getDescriptionRenderer() {
      return new IDescriptionRenderer() {
         public void renderDescription(IInterface inter, Point pos, String text) {
            Rectangle rect = new Rectangle(pos, new Dimension(inter.getFontWidth(ClearTheme.this.height, text) + 2, ClearTheme.this.height + 2));
            Color color = ClearTheme.this.getBackgroundColor(true);
            inter.fillRect(rect, color, color, color, color);
            inter.drawString(new Point(pos.x + 1, pos.y + 1), ClearTheme.this.height, text, ClearTheme.this.getFontColor(true));
         }
      };
   }

   public IContainerRenderer getContainerRenderer(int logicalLevel, final int graphicalLevel, final boolean horizontal) {
      return new IContainerRenderer() {
         public void renderBackground(Context context, boolean focus) {
            ClearTheme.this.renderBackground(context, focus, graphicalLevel);
         }

         public int getBorder() {
            return horizontal ? 0 : ClearTheme.this.border;
         }

         public int getTop() {
            return horizontal ? 0 : ClearTheme.this.border;
         }
      };
   }

   public <T> IPanelRenderer<T> getPanelRenderer(Class<T> type, int logicalLevel, final int graphicalLevel) {
      return new IPanelRenderer<T>() {
         public void renderPanelOverlay(Context context, boolean focus, T state, boolean open) {
         }

         public void renderTitleOverlay(Context context, boolean focus, T state, boolean open) {
            if (graphicalLevel > 0) {
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
                  ClearTheme.this.renderSmallButton(subContext, (String)null, 7, focus);
               } else {
                  ClearTheme.this.renderSmallButton(subContext, (String)null, 5, focus);
               }
            }

         }
      };
   }

   public <T> IScrollBarRenderer<T> getScrollBarRenderer(Class<T> type, int logicalLevel, final int graphicalLevel) {
      return new IScrollBarRenderer<T>() {
         public int renderScrollBar(Context context, boolean focus, T state, boolean horizontal, int height, int position) {
            ClearTheme.this.renderBackground(context, focus, graphicalLevel);
            Color color = ITheme.combineColors(ClearTheme.this.scheme.getColor("Scroll Bar Color"), ClearTheme.this.getBackgroundColor(focus));
            int a;
            int b;
            if (horizontal) {
               a = (int)((double)position / (double)height * (double)context.getSize().width);
               b = (int)((double)(position + context.getSize().width) / (double)height * (double)context.getSize().width);
               context.getInterface().fillRect(new Rectangle(context.getPos().x + a + 1, context.getPos().y + 1, b - a - 2, 2), color, color, color, color);
               context.getInterface().drawRect(new Rectangle(context.getPos().x + a + 1, context.getPos().y + 1, b - a - 2, 2), color, color, color, color);
            } else {
               a = (int)((double)position / (double)height * (double)context.getSize().height);
               b = (int)((double)(position + context.getSize().height) / (double)height * (double)context.getSize().height);
               context.getInterface().fillRect(new Rectangle(context.getPos().x + 1, context.getPos().y + a + 1, 2, b - a - 2), color, color, color, color);
               context.getInterface().drawRect(new Rectangle(context.getPos().x + 1, context.getPos().y + a + 1, 2, b - a - 2), color, color, color, color);
            }

            return horizontal ? (int)((double)((context.getInterface().getMouse().x - context.getPos().x) * height) / (double)context.getSize().width - (double)context.getSize().width / 2.0D) : (int)((double)((context.getInterface().getMouse().y - context.getPos().y) * height) / (double)context.getSize().height - (double)context.getSize().height / 2.0D);
         }

         public int getThickness() {
            return 4;
         }
      };
   }

   public <T> IEmptySpaceRenderer<T> getEmptySpaceRenderer(Class<T> type, int logicalLevel, final int graphicalLevel, boolean container) {
      return new IEmptySpaceRenderer<T>() {
         public void renderSpace(Context context, boolean focus, T state) {
            ClearTheme.this.renderBackground(context, focus, graphicalLevel);
         }
      };
   }

   public <T> IButtonRenderer<T> getButtonRenderer(final Class<T> type, int logicalLevel, final int graphicalLevel, final boolean container) {
      return new IButtonRenderer<T>() {
         public void renderButton(Context context, String title, boolean focus, T state) {
            boolean effFocus = container ? context.hasFocus() : focus;
            Color color;
            if (container && graphicalLevel <= 0) {
               color = ClearTheme.this.getColor(ClearTheme.this.scheme.getColor("Title Color"));
               Color colorB = ClearTheme.this.gradient.isOn() ? ClearTheme.this.getBackgroundColor(effFocus) : color;
               context.getInterface().fillRect(context.getRect(), color, color, colorB, colorB);
            } else {
               ClearTheme.this.renderBackground(context, effFocus, graphicalLevel);
            }

            color = ClearTheme.this.getFontColor(effFocus);
            if (type == Boolean.class && (Boolean)state) {
               color = ClearTheme.this.getMainColor(effFocus, true);
            } else if (type == Color.class) {
               color = (Color)state;
            }

            if (graphicalLevel > 0) {
               ClearTheme.this.renderOverlay(context);
            }

            if (type == String.class) {
               context.getInterface().drawString(new Point(context.getPos().x + ClearTheme.this.padding, context.getPos().y + ClearTheme.this.padding), ClearTheme.this.height, title + ClearTheme.this.separator + state, color);
            } else {
               context.getInterface().drawString(new Point(context.getPos().x + ClearTheme.this.padding, context.getPos().y + ClearTheme.this.padding), ClearTheme.this.height, title, color);
            }

         }

         public int getDefaultHeight() {
            return ClearTheme.this.getBaseHeight();
         }
      };
   }

   public IButtonRenderer<Void> getSmallButtonRenderer(final int symbol, final int logicalLevel, final int graphicalLevel, final boolean container) {
      return new IButtonRenderer<Void>() {
         public void renderButton(Context context, String title, boolean focus, Void state) {
            ClearTheme.this.renderBackground(context, focus, graphicalLevel);
            ClearTheme.this.renderOverlay(context);
            if (!container || logicalLevel <= 0) {
               ClearTheme.this.renderSmallButton(context, title, symbol, focus);
            }

         }

         public int getDefaultHeight() {
            return ClearTheme.this.getBaseHeight();
         }
      };
   }

   public IButtonRenderer<String> getKeybindRenderer(int logicalLevel, final int graphicalLevel, final boolean container) {
      return new IButtonRenderer<String>() {
         public void renderButton(Context context, String title, boolean focus, String state) {
            boolean effFocus = container ? context.hasFocus() : focus;
            Color color;
            if (container && graphicalLevel <= 0) {
               color = ClearTheme.this.getColor(ClearTheme.this.scheme.getColor("Title Color"));
               Color colorB = ClearTheme.this.gradient.isOn() ? ClearTheme.this.getBackgroundColor(effFocus) : color;
               context.getInterface().fillRect(context.getRect(), color, color, colorB, colorB);
            } else {
               ClearTheme.this.renderBackground(context, effFocus, graphicalLevel);
            }

            color = ClearTheme.this.getFontColor(effFocus);
            if (effFocus) {
               color = ClearTheme.this.getMainColor(effFocus, true);
            }

            ClearTheme.this.renderOverlay(context);
            context.getInterface().drawString(new Point(context.getPos().x + ClearTheme.this.padding, context.getPos().y + ClearTheme.this.padding), ClearTheme.this.height, title + ClearTheme.this.separator + (focus ? "..." : state), color);
         }

         public int getDefaultHeight() {
            return ClearTheme.this.getBaseHeight();
         }
      };
   }

   public ISliderRenderer getSliderRenderer(int logicalLevel, final int graphicalLevel, final boolean container) {
      return new ISliderRenderer() {
         public void renderSlider(Context context, String title, String state, boolean focus, double value) {
            boolean effFocus = container ? context.hasFocus() : focus;
            ClearTheme.this.renderBackground(context, effFocus, graphicalLevel);
            Color color = ClearTheme.this.getFontColor(effFocus);
            Color colorA = ClearTheme.this.getMainColor(effFocus, true);
            Rectangle rect = this.getSlideArea(context, title, state);
            int divider = (int)((double)rect.width * value);
            context.getInterface().fillRect(new Rectangle(rect.x, rect.y, divider, rect.height), colorA, colorA, colorA, colorA);
            ClearTheme.this.renderOverlay(context);
            context.getInterface().drawString(new Point(context.getPos().x + ClearTheme.this.padding, context.getPos().y + ClearTheme.this.padding), ClearTheme.this.height, title + ClearTheme.this.separator + state, color);
         }

         public int getDefaultHeight() {
            return ClearTheme.this.getBaseHeight();
         }
      };
   }

   public IRadioRenderer getRadioRenderer(int logicalLevel, final int graphicalLevel, boolean container) {
      return new IRadioRenderer() {
         public void renderItem(Context context, ILabeled[] items, boolean focus, int target, double state, boolean horizontal) {
            ClearTheme.this.renderBackground(context, focus, graphicalLevel);

            for(int i = 0; i < items.length; ++i) {
               Rectangle rect = this.getItemRect(context, items, i, horizontal);
               Context subContext = new Context(context.getInterface(), rect.width, rect.getLocation(), context.hasFocus(), context.onTop());
               subContext.setHeight(rect.height);
               ClearTheme.this.renderOverlay(subContext);
               context.getInterface().drawString(new Point(rect.x + ClearTheme.this.padding, rect.y + ClearTheme.this.padding), ClearTheme.this.height, items[i].getDisplayName(), i == target ? ClearTheme.this.getMainColor(focus, true) : ClearTheme.this.getFontColor(focus));
            }

         }

         public int getDefaultHeight(ILabeled[] items, boolean horizontal) {
            return (horizontal ? 1 : items.length) * ClearTheme.this.getBaseHeight();
         }
      };
   }

   public IResizeBorderRenderer getResizeRenderer() {
      return new IResizeBorderRenderer() {
         public void drawBorder(Context context, boolean focus) {
            Color color = ClearTheme.this.getBackgroundColor(focus);
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
            ClearTheme.this.renderBackground(context, effFocus, graphicalLevel);
            Color textColor = ClearTheme.this.getFontColor(effFocus);
            Color highlightColor = ClearTheme.this.scheme.getColor("Highlight Color");
            Rectangle rect = this.getTextArea(context, title);
            int strlen = context.getInterface().getFontWidth(ClearTheme.this.height, content.substring(0, position));
            context.getInterface().fillRect(rect, new Color(0, 0, 0, 64), new Color(0, 0, 0, 64), new Color(0, 0, 0, 64), new Color(0, 0, 0, 64));
            int maxPosition;
            if (boxPosition < position) {
               for(maxPosition = boxPosition; maxPosition < position && context.getInterface().getFontWidth(ClearTheme.this.height, content.substring(0, maxPosition)) + rect.width - ClearTheme.this.padding < strlen; ++maxPosition) {
               }

               if (boxPosition < maxPosition) {
                  boxPosition = maxPosition;
               }
            } else if (boxPosition > position) {
               boxPosition = position - 1;
            }

            for(maxPosition = content.length(); maxPosition > 0; --maxPosition) {
               if (context.getInterface().getFontWidth(ClearTheme.this.height, content.substring(maxPosition)) >= rect.width - ClearTheme.this.padding) {
                  ++maxPosition;
                  break;
               }
            }

            if (boxPosition > maxPosition) {
               boxPosition = maxPosition;
            } else if (boxPosition < 0) {
               boxPosition = 0;
            }

            int offset = context.getInterface().getFontWidth(ClearTheme.this.height, content.substring(0, boxPosition));
            int x1 = rect.x + ClearTheme.this.padding / 2 - offset + strlen;
            int x2 = rect.x + ClearTheme.this.padding / 2 - offset;
            if (position < content.length()) {
               x2 += context.getInterface().getFontWidth(ClearTheme.this.height, content.substring(0, position + 1));
            } else {
               x2 += context.getInterface().getFontWidth(ClearTheme.this.height, content + "X");
            }

            ClearTheme.this.renderOverlay(context);
            context.getInterface().drawString(new Point(context.getPos().x + ClearTheme.this.padding, context.getPos().y + ClearTheme.this.padding / 2), ClearTheme.this.height, title + ClearTheme.this.separator, textColor);
            context.getInterface().window(rect);
            if (select >= 0) {
               int x3 = rect.x + ClearTheme.this.padding / 2 - offset + context.getInterface().getFontWidth(ClearTheme.this.height, content.substring(0, select));
               context.getInterface().fillRect(new Rectangle(Math.min(x1, x3), rect.y + ClearTheme.this.padding / 2, Math.abs(x3 - x1), ClearTheme.this.height), highlightColor, highlightColor, highlightColor, highlightColor);
            }

            context.getInterface().drawString(new Point(rect.x + ClearTheme.this.padding / 2 - offset, rect.y + ClearTheme.this.padding / 2), ClearTheme.this.height, content, textColor);
            if (System.currentTimeMillis() / 500L % 2L == 0L && focus) {
               if (insertMode) {
                  context.getInterface().fillRect(new Rectangle(x1, rect.y + ClearTheme.this.padding / 2 + ClearTheme.this.height, x2 - x1, 1), textColor, textColor, textColor, textColor);
               } else {
                  context.getInterface().fillRect(new Rectangle(x1, rect.y + ClearTheme.this.padding / 2, 1, ClearTheme.this.height), textColor, textColor, textColor, textColor);
               }
            }

            context.getInterface().restore();
            return boxPosition;
         }

         public int getDefaultHeight() {
            int height = ClearTheme.this.getBaseHeight() - ClearTheme.this.padding;
            if (height % 2 == 1) {
               ++height;
            }

            return height;
         }

         public Rectangle getTextArea(Context context, String title) {
            Rectangle rect = context.getRect();
            int length = ClearTheme.this.padding + context.getInterface().getFontWidth(ClearTheme.this.height, title + ClearTheme.this.separator);
            return new Rectangle(rect.x + length, rect.y, rect.width - length, rect.height);
         }

         public int transformToCharPos(Context context, String title, String content, int boxPosition) {
            Rectangle rect = this.getTextArea(context, title);
            Point mouse = context.getInterface().getMouse();
            int offset = context.getInterface().getFontWidth(ClearTheme.this.height, content.substring(0, boxPosition));
            if (rect.contains(mouse)) {
               for(int i = 1; i <= content.length(); ++i) {
                  if (rect.x + ClearTheme.this.padding / 2 - offset + context.getInterface().getFontWidth(ClearTheme.this.height, content.substring(0, i)) > mouse.x) {
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
            ClearTheme.this.renderBackground(context, effFocus, graphicalLevel);
            ClearTheme.this.renderOverlay(context);
            context.getInterface().drawString(new Point(context.getPos().x + ClearTheme.this.padding, context.getPos().y + ClearTheme.this.padding), ClearTheme.this.height, title + ClearTheme.this.separator + (state ? "On" : "Off"), ClearTheme.this.getFontColor(focus));
            Color color = state ? ClearTheme.this.scheme.getColor("Enabled Color") : ClearTheme.this.scheme.getColor("Disabled Color");
            Color fillColor = ITheme.combineColors(color, ClearTheme.this.getBackgroundColor(effFocus));
            Rectangle rect = state ? this.getOnField(context) : this.getOffField(context);
            context.getInterface().fillRect(rect, fillColor, fillColor, fillColor, fillColor);
            rect = context.getRect();
            rect = new Rectangle(rect.x + rect.width - 2 * rect.height + 3 * ClearTheme.this.padding, rect.y + ClearTheme.this.padding, 2 * rect.height - 4 * ClearTheme.this.padding, rect.height - 2 * ClearTheme.this.padding);
            context.getInterface().drawRect(rect, color, color, color, color);
         }

         public int getDefaultHeight() {
            return ClearTheme.this.getBaseHeight();
         }

         public Rectangle getOnField(Context context) {
            Rectangle rect = context.getRect();
            return new Rectangle(rect.x + rect.width - rect.height + ClearTheme.this.padding, rect.y + ClearTheme.this.padding, rect.height - 2 * ClearTheme.this.padding, rect.height - 2 * ClearTheme.this.padding);
         }

         public Rectangle getOffField(Context context) {
            Rectangle rect = context.getRect();
            return new Rectangle(rect.x + rect.width - 2 * rect.height + 3 * ClearTheme.this.padding, rect.y + ClearTheme.this.padding, rect.height - 2 * ClearTheme.this.padding, rect.height - 2 * ClearTheme.this.padding);
         }
      };
   }

   public ISwitchRenderer<String> getCycleSwitchRenderer(final int logicalLevel, final int graphicalLevel, final boolean container) {
      return new ISwitchRenderer<String>() {
         public void renderButton(Context context, String title, boolean focus, String state) {
            boolean effFocus = container ? context.hasFocus() : focus;
            ClearTheme.this.renderBackground(context, effFocus, graphicalLevel);
            Context subContext = new Context(context, context.getSize().width - 2 * context.getSize().height, new Point(0, 0), true, true);
            subContext.setHeight(context.getSize().height);
            ClearTheme.this.renderOverlay(subContext);
            Color textColor = ClearTheme.this.getFontColor(effFocus);
            context.getInterface().drawString(new Point(context.getPos().x + ClearTheme.this.padding, context.getPos().y + ClearTheme.this.padding), ClearTheme.this.height, title + ClearTheme.this.separator + state, textColor);
            Rectangle rect = this.getOnField(context);
            subContext = new Context(context, rect.width, new Point(rect.x - context.getPos().x, 0), true, true);
            subContext.setHeight(rect.height);
            ClearTheme.this.getSmallButtonRenderer(5, logicalLevel, graphicalLevel, container).renderButton(subContext, (String)null, effFocus, (Object)null);
            rect = this.getOffField(context);
            subContext = new Context(context, rect.width, new Point(rect.x - context.getPos().x, 0), true, true);
            subContext.setHeight(rect.height);
            ClearTheme.this.getSmallButtonRenderer(4, logicalLevel, graphicalLevel, false).renderButton(subContext, (String)null, effFocus, (Object)null);
         }

         public int getDefaultHeight() {
            return ClearTheme.this.getBaseHeight();
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
            return ClearTheme.this.padding;
         }

         public int getBaseHeight() {
            return ClearTheme.this.getBaseHeight();
         }
      };
   }

   public int getBaseHeight() {
      return this.height + 2 * this.padding;
   }

   public Color getMainColor(boolean focus, boolean active) {
      return active ? this.getColor(this.scheme.getColor("Enabled Color")) : new Color(0, 0, 0, 0);
   }

   public Color getBackgroundColor(boolean focus) {
      return this.scheme.getColor("Background Color");
   }

   public Color getFontColor(boolean focus) {
      return this.scheme.getColor("Font Color");
   }
}
