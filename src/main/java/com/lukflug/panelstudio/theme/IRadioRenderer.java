package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.setting.ILabeled;
import java.awt.Rectangle;

public interface IRadioRenderer {
   void renderItem(Context var1, ILabeled[] var2, boolean var3, int var4, double var5, boolean var7);

   int getDefaultHeight(ILabeled[] var1, boolean var2);

   default Rectangle getItemRect(Context context, ILabeled[] items, int index, boolean horizontal) {
      Rectangle rect = context.getRect();
      int start;
      int end;
      if (horizontal) {
         start = (int)Math.round((double)rect.width / (double)items.length * (double)index);
         end = (int)Math.round((double)rect.width / (double)items.length * (double)(index + 1));
         return new Rectangle(rect.x + start, rect.y, end - start, rect.height);
      } else {
         start = (int)Math.round((double)rect.height / (double)items.length * (double)index);
         end = (int)Math.round((double)rect.height / (double)items.length * (double)(index + 1));
         return new Rectangle(rect.x, rect.y + start, rect.width, end - start);
      }
   }
}
