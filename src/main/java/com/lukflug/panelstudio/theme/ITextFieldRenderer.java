package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import java.awt.Rectangle;

public interface ITextFieldRenderer {
   int renderTextField(Context var1, String var2, boolean var3, String var4, int var5, int var6, int var7, boolean var8);

   int getDefaultHeight();

   Rectangle getTextArea(Context var1, String var2);

   int transformToCharPos(Context var1, String var2, String var3, int var4);
}
