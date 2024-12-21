package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import java.awt.Rectangle;

public interface ISwitchRenderer<T> extends IButtonRenderer<T> {
   Rectangle getOnField(Context var1);

   Rectangle getOffField(Context var1);
}
