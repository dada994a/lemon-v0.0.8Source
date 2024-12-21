package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.setting.ILabeled;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class Windows31Theme extends ThemeBase {
   protected int height;
   protected int padding;
   protected int scroll;
   protected String separator;

   public Windows31Theme(IColorScheme scheme, int height, int padding, int scroll, String separator) {
      super(scheme);
      this.height = height;
      this.padding = padding;
      this.separator = separator;
      this.scroll = scroll;
      scheme.createSetting(this, "Title Color", "The color for panel titles.", false, true, new Color(0, 0, 168), false);
      scheme.createSetting(this, "Background Color", "The color for the background.", false, true, new Color(252, 252, 252), false);
      scheme.createSetting(this, "Button Color", "The main color for buttons.", false, true, new Color(192, 196, 200), false);
      scheme.createSetting(this, "Shadow Color", "The color for button shadows.", false, true, new Color(132, 136, 140), false);
      scheme.createSetting(this, "Font Color", "The main color for text.", false, true, new Color(0, 0, 0), false);
   }

   protected void drawButtonBase(IInterface inter, Rectangle rect, boolean focus, boolean clicked, boolean small) {
      Color c1 = this.scheme.getColor("Shadow Color");
      Color c2 = this.getMainColor(focus, false);
      Color c3 = this.getBackgroundColor(focus);
      if (clicked) {
         inter.fillRect(new Rectangle(rect.x, rect.y, 1, rect.height), c1, c1, c1, c1);
         inter.fillRect(new Rectangle(rect.x, rect.y, rect.width, 1), c1, c1, c1, c1);
         inter.fillRect(new Rectangle(rect.x + 1, rect.y + 1, rect.width - 1, rect.height - 1), c3, c3, c3, c3);
      } else {
         inter.fillRect(new Rectangle(rect.x + rect.width - 1, rect.y, 1, rect.height), c1, c1, c1, c1);
         inter.fillRect(new Rectangle(rect.x, rect.y + rect.height - 1, rect.width, 1), c1, c1, c1, c1);
         inter.fillRect(new Rectangle(rect.x + rect.width - 2, rect.y + 1, 1, rect.height - 1), c1, c1, c1, c1);
         inter.fillRect(new Rectangle(rect.x + 1, rect.y + rect.height - 2, rect.width - 1, 1), c1, c1, c1, c1);
         if (small) {
            inter.fillRect(new Rectangle(rect.x + 1, rect.y + 1, rect.width - 3, rect.height - 3), c3, c3, c3, c3);
         } else {
            inter.fillRect(new Rectangle(rect.x + 2, rect.y + 2, rect.width - 4, rect.height - 4), c3, c3, c3, c3);
         }

         inter.fillRect(new Rectangle(rect.x, rect.y, rect.width - 1, 1), c2, c2, c2, c2);
         inter.fillRect(new Rectangle(rect.x, rect.y, 1, rect.height - 1), c2, c2, c2, c2);
         if (!small) {
            inter.fillRect(new Rectangle(rect.x + 1, rect.y + 1, rect.width - 3, 1), c2, c2, c2, c2);
            inter.fillRect(new Rectangle(rect.x + 1, rect.y + 1, 1, rect.height - 3), c2, c2, c2, c2);
         }
      }

   }

   protected void drawButton(IInterface inter, Rectangle rect, boolean focus, boolean clicked, boolean small) {
      Color c0 = this.getFontColor(focus);
      if (small) {
         ITheme.drawRect(inter, rect, c0);
      } else {
         inter.fillRect(new Rectangle(rect.x, rect.y + 1, 1, rect.height - 2), c0, c0, c0, c0);
         inter.fillRect(new Rectangle(rect.x + 1, rect.y, rect.width - 2, 1), c0, c0, c0, c0);
         inter.fillRect(new Rectangle(rect.x + rect.width - 1, rect.y + 1, 1, rect.height - 2), c0, c0, c0, c0);
         inter.fillRect(new Rectangle(rect.x + 1, rect.y + rect.height - 1, rect.width - 2, 1), c0, c0, c0, c0);
      }

      if (focus && !small) {
         ITheme.drawRect(inter, new Rectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2), c0);
         this.drawButtonBase(inter, new Rectangle(rect.x + 2, rect.y + 2, rect.width - 4, rect.height - 4), focus, clicked, small);
      } else {
         this.drawButtonBase(inter, new Rectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2), focus, clicked, small);
      }

   }

   public IDescriptionRenderer getDescriptionRenderer() {
      return new IDescriptionRenderer() {
         public void renderDescription(IInterface inter, Point pos, String text) {
            Rectangle rect = new Rectangle(pos, new Dimension(inter.getFontWidth(Windows31Theme.this.height, text) + 4, Windows31Theme.this.height + 4));
            Color color = Windows31Theme.this.getMainColor(true, false);
            inter.fillRect(rect, color, color, color, color);
            inter.drawString(new Point(pos.x + 2, pos.y + 2), Windows31Theme.this.height, text, Windows31Theme.this.getFontColor(true));
            ITheme.drawRect(inter, rect, Windows31Theme.this.getMainColor(true, true));
         }
      };
   }

   public IContainerRenderer getContainerRenderer(int logicalLevel, int graphicalLevel, boolean horizontal) {
      return new IContainerRenderer() {
         public int getBorder() {
            return 1;
         }

         public int getLeft() {
            return 1;
         }

         public int getRight() {
            return 1;
         }

         public int getTop() {
            return 1;
         }

         public int getBottom() {
            return 1;
         }
      };
   }

   public <T> IPanelRenderer<T> getPanelRenderer(Class<T> type, int logicalLevel, int graphicalLevel) {
      return new IPanelRenderer<T>() {
         public void renderBackground(Context context, boolean focus) {
            Rectangle rect = context.getRect();
            Color c = Windows31Theme.this.getMainColor(focus, false);
            context.getInterface().fillRect(new Rectangle(rect.x + 3, rect.y + 3, rect.width - 6, rect.height - 6), c, c, c, c);
         }

         public int getBorder() {
            return 1;
         }

         public int getLeft() {
            return 4;
         }

         public int getRight() {
            return 4;
         }

         public int getTop() {
            return 4;
         }

         public int getBottom() {
            return 4;
         }

         public void renderPanelOverlay(Context context, boolean focus, T state, boolean open) {
            Rectangle rect = context.getRect();
            ITheme.drawRect(context.getInterface(), rect, Windows31Theme.this.getFontColor(focus));
            ITheme.drawRect(context.getInterface(), new Rectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2), Windows31Theme.this.getMainColor(focus, focus));
            ITheme.drawRect(context.getInterface(), new Rectangle(rect.x + 2, rect.y + 2, rect.width - 4, rect.height - 4), Windows31Theme.this.getMainColor(focus, focus));
         }

         public void renderTitleOverlay(Context context, boolean focus, T state, boolean open) {
         }
      };
   }

   public <T> IScrollBarRenderer<T> getScrollBarRenderer(Class<T> type, int logicalLevel, int graphicalLevel) {
      return new IScrollBarRenderer<T>() {
         public int renderScrollBar(Context context, boolean focus, T state, boolean horizontal, int height, int position) {
            Color color = Windows31Theme.this.getBackgroundColor(focus);
            context.getInterface().fillRect(context.getRect(), color, color, color, color);
            int d = horizontal ? context.getSize().height : context.getSize().width;
            int x = context.getPos().x + (horizontal ? (int)((double)position / (double)(height - context.getSize().width) * (double)(context.getSize().width - 2 * d)) : 0);
            int var10001 = horizontal ? 0 : (int)((double)position / (double)(height - context.getSize().height) * (double)(context.getSize().height - 2 * d));
            int y = context.getPos().y + var10001;
            Rectangle rect = new Rectangle(x, y, d * (horizontal ? 2 : 1), d * (horizontal ? 1 : 2));
            Windows31Theme.this.drawButton(context.getInterface(), rect, focus, context.isClicked(0) && rect.contains(context.getInterface().getMouse()), true);
            return horizontal ? (int)Math.round((double)(context.getInterface().getMouse().x - context.getPos().x - d) / (double)(context.getSize().width - 2 * d) * (double)(height - context.getSize().width)) : (int)Math.round((double)(context.getInterface().getMouse().y - context.getPos().y - d) / (double)(context.getSize().height - 2 * d) * (double)(height - context.getSize().height));
         }

         public int getThickness() {
            return Windows31Theme.this.scroll;
         }
      };
   }

   public <T> IEmptySpaceRenderer<T> getEmptySpaceRenderer(Class<T> type, int logicalLevel, int graphicalLevel, final boolean container) {
      return new IEmptySpaceRenderer<T>() {
         public void renderSpace(Context context, boolean focus, T state) {
            Color color;
            if (container) {
               color = Windows31Theme.this.getMainColor(focus, false);
            } else {
               color = Windows31Theme.this.getBackgroundColor(focus);
            }

            context.getInterface().fillRect(context.getRect(), color, color, color, color);
         }
      };
   }

   public <T> IButtonRenderer<T> getButtonRenderer(final Class<T> type, int logicalLevel, int graphicalLevel, final boolean container) {
      return new IButtonRenderer<T>() {
         public void renderButton(Context context, String title, boolean focus, T state) {
            boolean effFocus = container ? context.hasFocus() : focus;
            boolean active = type == Boolean.class ? (Boolean)state : effFocus;
            if (!container && type == Boolean.class) {
               ITheme.drawRect(context.getInterface(), new Rectangle(context.getPos().x, context.getPos().y, Windows31Theme.this.height, Windows31Theme.this.height), Windows31Theme.this.getFontColor(effFocus));
               if ((Boolean)state) {
                  context.getInterface().drawLine(context.getPos(), new Point(context.getPos().x + Windows31Theme.this.height - 1, context.getPos().y + Windows31Theme.this.height - 1), Windows31Theme.this.getFontColor(effFocus), Windows31Theme.this.getFontColor(effFocus));
                  context.getInterface().drawLine(new Point(context.getPos().x + Windows31Theme.this.height - 1, context.getPos().y + 1), new Point(context.getPos().x, context.getPos().y + Windows31Theme.this.height), Windows31Theme.this.getFontColor(effFocus), Windows31Theme.this.getFontColor(effFocus));
               }

               context.getInterface().drawString(new Point(context.getPos().x + Windows31Theme.this.height + Windows31Theme.this.padding, context.getPos().y), Windows31Theme.this.height, title, Windows31Theme.this.getFontColor(effFocus));
            } else {
               Color color;
               if (container) {
                  color = Windows31Theme.this.getMainColor(effFocus, active);
                  context.getInterface().fillRect(context.getRect(), color, color, color, color);
                  Color lineColor = Windows31Theme.this.getFontColor(effFocus);
                  context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y + context.getSize().height - 1, context.getSize().width, 1), lineColor, lineColor, lineColor, lineColor);
               } else {
                  Windows31Theme.this.drawButton(context.getInterface(), context.getRect(), effFocus, context.isClicked(0), false);
               }

               color = container && active ? Windows31Theme.this.getMainColor(effFocus, false) : Windows31Theme.this.getFontColor(effFocus);
               String string = title;
               if (type == String.class) {
                  string = title + Windows31Theme.this.separator + state;
               } else if (type == Color.class) {
                  color = (Color)state;
               }

               context.getInterface().drawString(new Point(context.getPos().x + context.getSize().width / 2 - context.getInterface().getFontWidth(Windows31Theme.this.height, string) / 2, context.getPos().y + (container ? 0 : 3) + Windows31Theme.this.padding), Windows31Theme.this.height, string, color);
            }
         }

         public int getDefaultHeight() {
            if (!container && type == Boolean.class) {
               return Windows31Theme.this.height;
            } else {
               return container ? Windows31Theme.this.getBaseHeight() : Windows31Theme.this.getBaseHeight() + 6;
            }
         }
      };
   }

   public IButtonRenderer<Void> getSmallButtonRenderer(final int symbol, int logicalLevel, int graphicalLevel, boolean container) {
      return new IButtonRenderer<Void>() {
         public void renderButton(Context context, String title, boolean focus, Void state) {
            Windows31Theme.this.drawButton(context.getInterface(), context.getRect(), focus, context.isClicked(0), true);
            Point[] points = new Point[3];
            int padding = context.getSize().height <= 12 ? 4 : 6;
            Rectangle rect = new Rectangle(context.getPos().x + padding / 2, context.getPos().y + padding / 2, context.getSize().height - 2 * (padding / 2), context.getSize().height - 2 * (padding / 2));
            if (title == null) {
               rect.x += context.getSize().width / 2 - context.getSize().height / 2;
            }

            Color color = Windows31Theme.this.getFontColor(focus);
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
               context.getInterface().drawString(new Point(context.getPos().x + (symbol == 0 ? padding : context.getSize().height), context.getPos().y + padding), Windows31Theme.this.height, title, Windows31Theme.this.getFontColor(focus));
            }

         }

         public int getDefaultHeight() {
            return Windows31Theme.this.getBaseHeight();
         }
      };
   }

   public IButtonRenderer<String> getKeybindRenderer(int logicalLevel, int graphicalLevel, boolean container) {
      return this.getButtonRenderer(String.class, logicalLevel, graphicalLevel, container);
   }

   public ISliderRenderer getSliderRenderer(int logicalLevel, int graphicalLevel, final boolean container) {
      return new ISliderRenderer() {
         public void renderSlider(Context context, String title, String state, boolean focus, double value) {
            boolean effFocus = container ? context.hasFocus() : focus;
            Color colorA = Windows31Theme.this.getMainColor(effFocus, true);
            if (container && effFocus) {
               context.getInterface().fillRect(context.getRect(), colorA, colorA, colorA, colorA);
            }

            Rectangle rect = this.getSlideArea(context, title, state);
            Color colorB = Windows31Theme.this.getBackgroundColor(effFocus);
            context.getInterface().fillRect(rect, colorB, colorB, colorB, colorB);
            ITheme.drawRect(context.getInterface(), rect, Windows31Theme.this.getFontColor(effFocus));
            int divider = (int)((double)(rect.width - rect.height) * value);
            Rectangle buttonRect = new Rectangle(rect.x + divider, rect.y, rect.height, rect.height);
            boolean clicked = context.isClicked(0) && buttonRect.contains(context.getInterface().getMouse());
            Windows31Theme.this.drawButton(context.getInterface(), buttonRect, effFocus, clicked, true);
            Color color = container && effFocus ? Windows31Theme.this.getMainColor(effFocus, false) : Windows31Theme.this.getFontColor(effFocus);
            String string = title + Windows31Theme.this.separator + state;
            context.getInterface().drawString(new Point(context.getPos().x + Windows31Theme.this.padding, context.getPos().y + Windows31Theme.this.padding), Windows31Theme.this.height, string, color);
         }

         public Rectangle getSlideArea(Context context, String title, String state) {
            return container ? context.getRect() : new Rectangle(context.getPos().x, context.getPos().y + context.getSize().height - Windows31Theme.this.height, context.getSize().width, Windows31Theme.this.height);
         }

         public int getDefaultHeight() {
            return Windows31Theme.this.getBaseHeight() + Windows31Theme.this.height;
         }
      };
   }

   public IRadioRenderer getRadioRenderer(int logicalLevel, int graphicalLevel, boolean container) {
      return new IRadioRenderer() {
         public void renderItem(Context context, ILabeled[] items, boolean focus, int target, double state, boolean horizontal) {
            for(int i = 0; i < items.length; ++i) {
               Rectangle rect = this.getItemRect(context, items, i, horizontal);
               Color color = Windows31Theme.this.getMainColor(focus, true);
               if (i == target) {
                  context.getInterface().fillRect(rect, color, color, color, color);
               }

               context.getInterface().drawString(new Point(rect.x + Windows31Theme.this.padding, rect.y + Windows31Theme.this.padding), Windows31Theme.this.height, items[i].getDisplayName(), i == target ? Windows31Theme.this.getMainColor(focus, false) : Windows31Theme.this.getFontColor(focus));
            }

         }

         public int getDefaultHeight(ILabeled[] items, boolean horizontal) {
            return (horizontal ? 1 : items.length) * Windows31Theme.this.getBaseHeight();
         }
      };
   }

   public ITextFieldRenderer getTextRenderer(boolean embed, int logicalLevel, int graphicalLevel, final boolean container) {
      return new ITextFieldRenderer() {
         public int renderTextField(Context context, String title, boolean focus, String content, int position, int select, int boxPosition, boolean insertMode) {
            boolean effFocus = container ? context.hasFocus() || focus : focus;
            Color textColor = Windows31Theme.this.getFontColor(effFocus);
            Color titleColor = container && effFocus ? Windows31Theme.this.getMainColor(effFocus, false) : textColor;
            Color highlightColor = Windows31Theme.this.getMainColor(effFocus, true);
            Rectangle rect = this.getTextArea(context, title);
            int strlen = context.getInterface().getFontWidth(Windows31Theme.this.height, content.substring(0, position));
            if (container && effFocus) {
               context.getInterface().fillRect(context.getRect(), highlightColor, highlightColor, highlightColor, highlightColor);
               context.getInterface().fillRect(rect, titleColor, titleColor, titleColor, titleColor);
            }

            int maxPosition;
            if (boxPosition < position) {
               for(maxPosition = boxPosition; maxPosition < position && context.getInterface().getFontWidth(Windows31Theme.this.height, content.substring(0, maxPosition)) + rect.width - Windows31Theme.this.padding < strlen; ++maxPosition) {
               }

               if (boxPosition < maxPosition) {
                  boxPosition = maxPosition;
               }
            } else if (boxPosition > position) {
               boxPosition = position - 1;
            }

            for(maxPosition = content.length(); maxPosition > 0; --maxPosition) {
               if (context.getInterface().getFontWidth(Windows31Theme.this.height, content.substring(maxPosition)) >= rect.width - Windows31Theme.this.padding) {
                  ++maxPosition;
                  break;
               }
            }

            if (boxPosition > maxPosition) {
               boxPosition = maxPosition;
            } else if (boxPosition < 0) {
               boxPosition = 0;
            }

            int offset = context.getInterface().getFontWidth(Windows31Theme.this.height, content.substring(0, boxPosition));
            int x1 = rect.x + Windows31Theme.this.padding / 2 - offset + strlen;
            int x2 = rect.x + Windows31Theme.this.padding / 2 - offset;
            if (position < content.length()) {
               x2 += context.getInterface().getFontWidth(Windows31Theme.this.height, content.substring(0, position + 1));
            } else {
               x2 += context.getInterface().getFontWidth(Windows31Theme.this.height, content + "X");
            }

            context.getInterface().drawString(new Point(context.getPos().x + Windows31Theme.this.padding, context.getPos().y + Windows31Theme.this.padding), Windows31Theme.this.height, title + Windows31Theme.this.separator, titleColor);
            context.getInterface().window(rect);
            if (select >= 0) {
               int x3 = rect.x + Windows31Theme.this.padding / 2 - offset + context.getInterface().getFontWidth(Windows31Theme.this.height, content.substring(0, select));
               context.getInterface().fillRect(new Rectangle(Math.min(x1, x3), rect.y + Windows31Theme.this.padding, Math.abs(x3 - x1), Windows31Theme.this.height), highlightColor, highlightColor, highlightColor, highlightColor);
               context.getInterface().drawString(new Point(rect.x + Windows31Theme.this.padding / 2 - offset, rect.y + Windows31Theme.this.padding), Windows31Theme.this.height, content.substring(0, Math.min(position, select)), textColor);
               context.getInterface().drawString(new Point(Math.min(x1, x3), rect.y + Windows31Theme.this.padding), Windows31Theme.this.height, content.substring(Math.min(position, select), Math.max(position, select)), Windows31Theme.this.getMainColor(effFocus, false));
               context.getInterface().drawString(new Point(Math.max(x1, x3), rect.y + Windows31Theme.this.padding), Windows31Theme.this.height, content.substring(Math.max(position, select)), textColor);
            } else {
               context.getInterface().drawString(new Point(rect.x + Windows31Theme.this.padding / 2 - offset, rect.y + Windows31Theme.this.padding), Windows31Theme.this.height, content, textColor);
            }

            if (System.currentTimeMillis() / 500L % 2L == 0L && focus) {
               if (insertMode) {
                  context.getInterface().fillRect(new Rectangle(x1, rect.y + Windows31Theme.this.padding + Windows31Theme.this.height, x2 - x1, 1), textColor, textColor, textColor, textColor);
               } else {
                  context.getInterface().fillRect(new Rectangle(x1, rect.y + Windows31Theme.this.padding, 1, Windows31Theme.this.height), textColor, textColor, textColor, textColor);
               }
            }

            ITheme.drawRect(context.getInterface(), rect, textColor);
            context.getInterface().restore();
            return boxPosition;
         }

         public int getDefaultHeight() {
            int height = Windows31Theme.this.getBaseHeight();
            if (height % 2 == 1) {
               ++height;
            }

            return height;
         }

         public Rectangle getTextArea(Context context, String title) {
            Rectangle rect = context.getRect();
            int length = Windows31Theme.this.padding + context.getInterface().getFontWidth(Windows31Theme.this.height, title + Windows31Theme.this.separator);
            return new Rectangle(rect.x + length, rect.y, rect.width - length, rect.height);
         }

         public int transformToCharPos(Context context, String title, String content, int boxPosition) {
            Rectangle rect = this.getTextArea(context, title);
            Point mouse = context.getInterface().getMouse();
            int offset = context.getInterface().getFontWidth(Windows31Theme.this.height, content.substring(0, boxPosition));
            if (rect.contains(mouse)) {
               for(int i = 1; i <= content.length(); ++i) {
                  if (rect.x + Windows31Theme.this.padding / 2 - offset + context.getInterface().getFontWidth(Windows31Theme.this.height, content.substring(0, i)) > mouse.x) {
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

   public IResizeBorderRenderer getResizeRenderer() {
      return new IResizeBorderRenderer() {
         public void drawBorder(Context context, boolean focus) {
            Color color = Windows31Theme.this.getBackgroundColor(focus);
            Rectangle rect = context.getRect();
            context.getInterface().fillRect(new Rectangle(rect.x, rect.y, rect.width, this.getBorder()), color, color, color, color);
            context.getInterface().fillRect(new Rectangle(rect.x, rect.y + rect.height - this.getBorder(), rect.width, this.getBorder()), color, color, color, color);
            context.getInterface().fillRect(new Rectangle(rect.x, rect.y + this.getBorder(), this.getBorder(), rect.height - 2 * this.getBorder()), color, color, color, color);
            context.getInterface().fillRect(new Rectangle(rect.x + rect.width - this.getBorder(), rect.y + this.getBorder(), this.getBorder(), rect.height - 2 * this.getBorder()), color, color, color, color);
            Color borderColor = Windows31Theme.this.getFontColor(focus);
            ITheme.drawRect(context.getInterface(), rect, borderColor);
            ITheme.drawRect(context.getInterface(), new Rectangle(rect.x, rect.y + this.getBorder(), rect.width, rect.height - 2 * this.getBorder()), borderColor);
            ITheme.drawRect(context.getInterface(), new Rectangle(rect.x + this.getBorder(), rect.y, rect.width - 2 * this.getBorder(), rect.height), borderColor);
         }

         public int getBorder() {
            return 4;
         }
      };
   }

   public ISwitchRenderer<Boolean> getToggleSwitchRenderer(int logicalLevel, int graphicalLevel, final boolean container) {
      return new ISwitchRenderer<Boolean>() {
         public void renderButton(Context context, String title, boolean focus, Boolean state) {
            boolean effFocus = container ? context.hasFocus() : focus;
            Color colorA = Windows31Theme.this.getMainColor(effFocus, true);
            if (container && effFocus) {
               context.getInterface().fillRect(context.getRect(), colorA, colorA, colorA, colorA);
            }

            context.getInterface().drawString(new Point(context.getPos().x + Windows31Theme.this.padding, context.getPos().y + Windows31Theme.this.padding), Windows31Theme.this.height, title + Windows31Theme.this.separator + (state ? "On" : "Off"), Windows31Theme.this.getFontColor(focus));
            Rectangle rect = new Rectangle(context.getPos().x + context.getSize().width - 2 * context.getSize().height, context.getPos().y, 2 * context.getSize().height, context.getSize().height);
            Color colorB = Windows31Theme.this.getMainColor(effFocus, state);
            context.getInterface().fillRect(rect, colorB, colorB, colorB, colorB);
            ITheme.drawRect(context.getInterface(), rect, Windows31Theme.this.getFontColor(effFocus));
            Rectangle field = state ? this.getOnField(context) : this.getOffField(context);
            Windows31Theme.this.drawButton(context.getInterface(), field, focus, context.isClicked(0) && field.contains(context.getInterface().getMouse()), true);
         }

         public int getDefaultHeight() {
            return Windows31Theme.this.getBaseHeight();
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

   public ISwitchRenderer<String> getCycleSwitchRenderer(final int logicalLevel, final int graphicalLevel, final boolean container) {
      return new ISwitchRenderer<String>() {
         public void renderButton(Context context, String title, boolean focus, String state) {
            boolean effFocus = container ? context.hasFocus() : focus;
            Color colorA = Windows31Theme.this.getMainColor(effFocus, true);
            if (container && effFocus) {
               context.getInterface().fillRect(context.getRect(), colorA, colorA, colorA, colorA);
            }

            Context subContext = new Context(context, context.getSize().width - 2 * context.getSize().height, new Point(0, 0), true, true);
            subContext.setHeight(context.getSize().height);
            Color textColor = container && effFocus ? Windows31Theme.this.getMainColor(effFocus, false) : Windows31Theme.this.getFontColor(effFocus);
            context.getInterface().drawString(new Point(context.getPos().x + Windows31Theme.this.padding, context.getPos().y + Windows31Theme.this.padding), Windows31Theme.this.height, title + Windows31Theme.this.separator + state, textColor);
            Rectangle rect = this.getOnField(context);
            subContext = new Context(context, rect.width, new Point(rect.x - context.getPos().x, 0), true, true);
            subContext.setHeight(rect.height);
            Windows31Theme.this.getSmallButtonRenderer(5, logicalLevel, graphicalLevel, container).renderButton(subContext, (String)null, effFocus, (Object)null);
            rect = this.getOffField(context);
            subContext = new Context(context, rect.width, new Point(rect.x - context.getPos().x, 0), true, true);
            subContext.setHeight(rect.height);
            Windows31Theme.this.getSmallButtonRenderer(4, logicalLevel, graphicalLevel, false).renderButton(subContext, (String)null, effFocus, (Object)null);
         }

         public int getDefaultHeight() {
            return Windows31Theme.this.getBaseHeight();
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
            return Windows31Theme.this.padding;
         }

         public int getBaseHeight() {
            return Windows31Theme.this.getBaseHeight();
         }
      };
   }

   public int getBaseHeight() {
      return this.height + 2 * this.padding;
   }

   public Color getMainColor(boolean focus, boolean active) {
      return active ? this.getColor(this.scheme.getColor("Title Color")) : this.scheme.getColor("Background Color");
   }

   public Color getBackgroundColor(boolean focus) {
      return this.scheme.getColor("Button Color");
   }

   public Color getFontColor(boolean focus) {
      return this.scheme.getColor("Font Color");
   }
}
