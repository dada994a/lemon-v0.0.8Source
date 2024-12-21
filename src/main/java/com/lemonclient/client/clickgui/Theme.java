package com.lemonclient.client.clickgui;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.IButtonRenderer;
import com.lukflug.panelstudio.theme.IColorPickerRenderer;
import com.lukflug.panelstudio.theme.IColorScheme;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import com.lukflug.panelstudio.theme.IDescriptionRenderer;
import com.lukflug.panelstudio.theme.IEmptySpaceRenderer;
import com.lukflug.panelstudio.theme.IPanelRenderer;
import com.lukflug.panelstudio.theme.IRadioRenderer;
import com.lukflug.panelstudio.theme.IResizeBorderRenderer;
import com.lukflug.panelstudio.theme.IScrollBarRenderer;
import com.lukflug.panelstudio.theme.ISliderRenderer;
import com.lukflug.panelstudio.theme.ISwitchRenderer;
import com.lukflug.panelstudio.theme.ITextFieldRenderer;
import com.lukflug.panelstudio.theme.ITheme;
import com.lukflug.panelstudio.theme.StandardColorPicker;
import com.lukflug.panelstudio.theme.ThemeBase;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class Theme extends ThemeBase {
   protected IBoolean gradient;
   protected int height;
   protected int padding;
   protected int border;
   protected String separator;
   Color title;
   Color enable;
   Color disable;
   Color background;
   Color font;
   Color scrollBar;
   Color hgihlight;

   public Theme(IColorScheme scheme, Color title, Color enable, Color disable, Color background, Color font, Color scrollBar, Color hgihlight, IBoolean gradient, int height, int padding, int border, String separator) {
      super(scheme);
      this.title = title;
      this.enable = enable;
      this.disable = disable;
      this.background = background;
      this.font = font;
      this.scrollBar = scrollBar;
      this.hgihlight = hgihlight;
      this.gradient = gradient;
      this.height = height;
      this.padding = padding;
      this.border = border;
      this.separator = separator;
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
            Rectangle rect = new Rectangle(pos, new Dimension(inter.getFontWidth(Theme.this.height, text) + 2, Theme.this.height + 2));
            Color color = Theme.this.getBackgroundColor(true);
            inter.fillRect(rect, color, color, color, color);
            inter.drawString(new Point(pos.x + 1, pos.y + 1), Theme.this.height, text, Theme.this.getFontColor(true));
         }
      };
   }

   public IContainerRenderer getContainerRenderer(int logicalLevel, final int graphicalLevel, final boolean horizontal) {
      return new IContainerRenderer() {
         public void renderBackground(Context context, boolean focus) {
            Theme.this.renderBackground(context, focus, graphicalLevel);
         }

         public int getBorder() {
            return horizontal ? 0 : Theme.this.border;
         }

         public int getTop() {
            return horizontal ? 0 : Theme.this.border;
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
                  Theme.this.renderSmallButton(subContext, (String)null, 7, focus);
               } else {
                  Theme.this.renderSmallButton(subContext, (String)null, 5, focus);
               }
            }

         }
      };
   }

   public <T> IScrollBarRenderer<T> getScrollBarRenderer(Class<T> type, int logicalLevel, final int graphicalLevel) {
      return new IScrollBarRenderer<T>() {
         public int renderScrollBar(Context context, boolean focus, T state, boolean horizontal, int height, int position) {
            Theme.this.renderBackground(context, focus, graphicalLevel);
            Color color = ITheme.combineColors(Theme.this.scrollBar, Theme.this.getBackgroundColor(focus));
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
            Theme.this.renderBackground(context, focus, graphicalLevel);
         }
      };
   }

   public <T> IButtonRenderer<T> getButtonRenderer(final Class<T> type, int logicalLevel, final int graphicalLevel, final boolean container) {
      return new IButtonRenderer<T>() {
         public void renderButton(Context context, String title, boolean focus, T state) {
            boolean effFocus = container ? context.hasFocus() : focus;
            Color color;
            if (container && graphicalLevel <= 0) {
               color = Theme.this.title;
               Color colorB = Theme.this.gradient.isOn() ? Theme.this.getBackgroundColor(effFocus) : color;
               context.getInterface().fillRect(context.getRect(), color, color, colorB, colorB);
            } else {
               Theme.this.renderBackground(context, effFocus, graphicalLevel);
            }

            color = Theme.this.getFontColor(effFocus);
            if (type == Boolean.class && (Boolean)state) {
               color = Theme.this.getMainColor(effFocus, true);
            } else if (type == Color.class) {
               color = (Color)state;
            }

            if (graphicalLevel > 0) {
               Theme.this.renderOverlay(context);
            }

            if (type == String.class) {
               context.getInterface().drawString(new Point(context.getPos().x + Theme.this.padding, context.getPos().y + Theme.this.padding), Theme.this.height, title + Theme.this.separator + state, color);
            } else {
               context.getInterface().drawString(new Point(context.getPos().x + Theme.this.padding, context.getPos().y + Theme.this.padding), Theme.this.height, title, color);
            }

         }

         public int getDefaultHeight() {
            return Theme.this.getBaseHeight();
         }
      };
   }

   public IButtonRenderer<Void> getSmallButtonRenderer(final int symbol, final int logicalLevel, final int graphicalLevel, final boolean container) {
      return new IButtonRenderer<Void>() {
         public void renderButton(Context context, String title, boolean focus, Void state) {
            Theme.this.renderBackground(context, focus, graphicalLevel);
            Theme.this.renderOverlay(context);
            if (!container || logicalLevel <= 0) {
               Theme.this.renderSmallButton(context, title, symbol, focus);
            }

         }

         public int getDefaultHeight() {
            return Theme.this.getBaseHeight();
         }
      };
   }

   public IButtonRenderer<String> getKeybindRenderer(int logicalLevel, final int graphicalLevel, final boolean container) {
      return new IButtonRenderer<String>() {
         public void renderButton(Context context, String title, boolean focus, String state) {
            boolean effFocus = container ? context.hasFocus() : focus;
            Color color;
            if (container && graphicalLevel <= 0) {
               color = Theme.this.title;
               Color colorB = Theme.this.gradient.isOn() ? Theme.this.getBackgroundColor(effFocus) : color;
               context.getInterface().fillRect(context.getRect(), color, color, colorB, colorB);
            } else {
               Theme.this.renderBackground(context, effFocus, graphicalLevel);
            }

            color = Theme.this.getFontColor(effFocus);
            if (effFocus) {
               color = Theme.this.getMainColor(effFocus, true);
            }

            Theme.this.renderOverlay(context);
            context.getInterface().drawString(new Point(context.getPos().x + Theme.this.padding, context.getPos().y + Theme.this.padding), Theme.this.height, title + Theme.this.separator + (focus ? "..." : state), color);
         }

         public int getDefaultHeight() {
            return Theme.this.getBaseHeight();
         }
      };
   }

   public ISliderRenderer getSliderRenderer(int logicalLevel, final int graphicalLevel, final boolean container) {
      return new ISliderRenderer() {
         public void renderSlider(Context context, String title, String state, boolean focus, double value) {
            boolean effFocus = container ? context.hasFocus() : focus;
            Theme.this.renderBackground(context, effFocus, graphicalLevel);
            Color color = Theme.this.getFontColor(effFocus);
            Color colorA = Theme.this.getMainColor(effFocus, true);
            Rectangle rect = this.getSlideArea(context, title, state);
            int divider = (int)((double)rect.width * value);
            context.getInterface().fillRect(new Rectangle(rect.x, rect.y, divider, rect.height), colorA, colorA, colorA, colorA);
            Theme.this.renderOverlay(context);
            context.getInterface().drawString(new Point(context.getPos().x + Theme.this.padding, context.getPos().y + Theme.this.padding), Theme.this.height, title + Theme.this.separator + state, color);
         }

         public int getDefaultHeight() {
            return Theme.this.getBaseHeight();
         }
      };
   }

   public IRadioRenderer getRadioRenderer(int logicalLevel, final int graphicalLevel, boolean container) {
      return new IRadioRenderer() {
         public void renderItem(Context context, ILabeled[] items, boolean focus, int target, double state, boolean horizontal) {
            Theme.this.renderBackground(context, focus, graphicalLevel);

            for(int i = 0; i < items.length; ++i) {
               Rectangle rect = this.getItemRect(context, items, i, horizontal);
               Context subContext = new Context(context.getInterface(), rect.width, rect.getLocation(), context.hasFocus(), context.onTop());
               subContext.setHeight(rect.height);
               Theme.this.renderOverlay(subContext);
               context.getInterface().drawString(new Point(rect.x + Theme.this.padding, rect.y + Theme.this.padding), Theme.this.height, items[i].getDisplayName(), i == target ? Theme.this.getMainColor(focus, true) : Theme.this.getFontColor(focus));
            }

         }

         public int getDefaultHeight(ILabeled[] items, boolean horizontal) {
            return (horizontal ? 1 : items.length) * Theme.this.getBaseHeight();
         }
      };
   }

   public IResizeBorderRenderer getResizeRenderer() {
      return new IResizeBorderRenderer() {
         public void drawBorder(Context context, boolean focus) {
            Color color = Theme.this.getBackgroundColor(focus);
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
            Theme.this.renderBackground(context, effFocus, graphicalLevel);
            Color textColor = Theme.this.getFontColor(effFocus);
            Color highlightColor = Theme.this.hgihlight;
            Rectangle rect = this.getTextArea(context, title);
            int strlen = context.getInterface().getFontWidth(Theme.this.height, content.substring(0, position));
            context.getInterface().fillRect(rect, new Color(0, 0, 0, 64), new Color(0, 0, 0, 64), new Color(0, 0, 0, 64), new Color(0, 0, 0, 64));
            int maxPosition;
            if (boxPosition < position) {
               for(maxPosition = boxPosition; maxPosition < position && context.getInterface().getFontWidth(Theme.this.height, content.substring(0, maxPosition)) + rect.width - Theme.this.padding < strlen; ++maxPosition) {
               }

               if (boxPosition < maxPosition) {
                  boxPosition = maxPosition;
               }
            } else if (boxPosition > position) {
               boxPosition = position - 1;
            }

            for(maxPosition = content.length(); maxPosition > 0; --maxPosition) {
               if (context.getInterface().getFontWidth(Theme.this.height, content.substring(maxPosition)) >= rect.width - Theme.this.padding) {
                  ++maxPosition;
                  break;
               }
            }

            if (boxPosition > maxPosition) {
               boxPosition = maxPosition;
            } else if (boxPosition < 0) {
               boxPosition = 0;
            }

            int offset = context.getInterface().getFontWidth(Theme.this.height, content.substring(0, boxPosition));
            int x1 = rect.x + Theme.this.padding / 2 - offset + strlen;
            int x2 = rect.x + Theme.this.padding / 2 - offset;
            if (position < content.length()) {
               x2 += context.getInterface().getFontWidth(Theme.this.height, content.substring(0, position + 1));
            } else {
               x2 += context.getInterface().getFontWidth(Theme.this.height, content + "X");
            }

            Theme.this.renderOverlay(context);
            context.getInterface().drawString(new Point(context.getPos().x + Theme.this.padding, context.getPos().y + Theme.this.padding / 2), Theme.this.height, title + Theme.this.separator, textColor);
            context.getInterface().window(rect);
            if (select >= 0) {
               int x3 = rect.x + Theme.this.padding / 2 - offset + context.getInterface().getFontWidth(Theme.this.height, content.substring(0, select));
               context.getInterface().fillRect(new Rectangle(Math.min(x1, x3), rect.y + Theme.this.padding / 2, Math.abs(x3 - x1), Theme.this.height), highlightColor, highlightColor, highlightColor, highlightColor);
            }

            context.getInterface().drawString(new Point(rect.x + Theme.this.padding / 2 - offset, rect.y + Theme.this.padding / 2), Theme.this.height, content, textColor);
            if (System.currentTimeMillis() / 500L % 2L == 0L && focus) {
               if (insertMode) {
                  context.getInterface().fillRect(new Rectangle(x1, rect.y + Theme.this.padding / 2 + Theme.this.height, x2 - x1, 1), textColor, textColor, textColor, textColor);
               } else {
                  context.getInterface().fillRect(new Rectangle(x1, rect.y + Theme.this.padding / 2, 1, Theme.this.height), textColor, textColor, textColor, textColor);
               }
            }

            context.getInterface().restore();
            return boxPosition;
         }

         public int getDefaultHeight() {
            int height = Theme.this.getBaseHeight() - Theme.this.padding;
            if (height % 2 == 1) {
               ++height;
            }

            return height;
         }

         public Rectangle getTextArea(Context context, String title) {
            Rectangle rect = context.getRect();
            int length = Theme.this.padding + context.getInterface().getFontWidth(Theme.this.height, title + Theme.this.separator);
            return new Rectangle(rect.x + length, rect.y, rect.width - length, rect.height);
         }

         public int transformToCharPos(Context context, String title, String content, int boxPosition) {
            Rectangle rect = this.getTextArea(context, title);
            Point mouse = context.getInterface().getMouse();
            int offset = context.getInterface().getFontWidth(Theme.this.height, content.substring(0, boxPosition));
            if (rect.contains(mouse)) {
               for(int i = 1; i <= content.length(); ++i) {
                  if (rect.x + Theme.this.padding / 2 - offset + context.getInterface().getFontWidth(Theme.this.height, content.substring(0, i)) > mouse.x) {
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
            Theme.this.renderBackground(context, effFocus, graphicalLevel);
            Theme.this.renderOverlay(context);
            context.getInterface().drawString(new Point(context.getPos().x + Theme.this.padding, context.getPos().y + Theme.this.padding), Theme.this.height, title + Theme.this.separator + (state ? "On" : "Off"), Theme.this.getFontColor(focus));
            Color color = state ? Theme.this.enable : Theme.this.disable;
            Color fillColor = ITheme.combineColors(color, Theme.this.getBackgroundColor(effFocus));
            Rectangle rect = state ? this.getOnField(context) : this.getOffField(context);
            context.getInterface().fillRect(rect, fillColor, fillColor, fillColor, fillColor);
            rect = context.getRect();
            rect = new Rectangle(rect.x + rect.width - 2 * rect.height + 3 * Theme.this.padding, rect.y + Theme.this.padding, 2 * rect.height - 4 * Theme.this.padding, rect.height - 2 * Theme.this.padding);
            context.getInterface().drawRect(rect, color, color, color, color);
         }

         public int getDefaultHeight() {
            return Theme.this.getBaseHeight();
         }

         public Rectangle getOnField(Context context) {
            Rectangle rect = context.getRect();
            return new Rectangle(rect.x + rect.width - rect.height + Theme.this.padding, rect.y + Theme.this.padding, rect.height - 2 * Theme.this.padding, rect.height - 2 * Theme.this.padding);
         }

         public Rectangle getOffField(Context context) {
            Rectangle rect = context.getRect();
            return new Rectangle(rect.x + rect.width - 2 * rect.height + 3 * Theme.this.padding, rect.y + Theme.this.padding, rect.height - 2 * Theme.this.padding, rect.height - 2 * Theme.this.padding);
         }
      };
   }

   public ISwitchRenderer<String> getCycleSwitchRenderer(final int logicalLevel, final int graphicalLevel, final boolean container) {
      return new ISwitchRenderer<String>() {
         public void renderButton(Context context, String title, boolean focus, String state) {
            boolean effFocus = container ? context.hasFocus() : focus;
            Theme.this.renderBackground(context, effFocus, graphicalLevel);
            Context subContext = new Context(context, context.getSize().width - 2 * context.getSize().height, new Point(0, 0), true, true);
            subContext.setHeight(context.getSize().height);
            Theme.this.renderOverlay(subContext);
            Color textColor = Theme.this.getFontColor(effFocus);
            context.getInterface().drawString(new Point(context.getPos().x + Theme.this.padding, context.getPos().y + Theme.this.padding), Theme.this.height, title + Theme.this.separator + state, textColor);
            Rectangle rect = this.getOnField(context);
            subContext = new Context(context, rect.width, new Point(rect.x - context.getPos().x, 0), true, true);
            subContext.setHeight(rect.height);
            Theme.this.getSmallButtonRenderer(5, logicalLevel, graphicalLevel, container).renderButton(subContext, (String)null, effFocus, (Object)null);
            rect = this.getOffField(context);
            subContext = new Context(context, rect.width, new Point(rect.x - context.getPos().x, 0), true, true);
            subContext.setHeight(rect.height);
            Theme.this.getSmallButtonRenderer(4, logicalLevel, graphicalLevel, false).renderButton(subContext, (String)null, effFocus, (Object)null);
         }

         public int getDefaultHeight() {
            return Theme.this.getBaseHeight();
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
            return Theme.this.padding;
         }

         public int getBaseHeight() {
            return Theme.this.getBaseHeight();
         }
      };
   }

   public int getBaseHeight() {
      return this.height + 2 * this.padding;
   }

   public Color getMainColor(boolean focus, boolean active) {
      return active ? this.enable : new Color(0, 0, 0, 0);
   }

   public Color getBackgroundColor(boolean focus) {
      return this.background;
   }

   public Color getFontColor(boolean focus) {
      return this.font;
   }
}
