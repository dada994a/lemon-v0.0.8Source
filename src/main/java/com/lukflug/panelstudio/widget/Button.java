package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.component.FocusableComponent;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.IButtonRenderer;
import java.util.function.Supplier;

public class Button<T> extends FocusableComponent {
   protected Supplier<T> state;
   protected IButtonRenderer<T> renderer;

   public Button(ILabeled label, Supplier<T> state, IButtonRenderer<T> renderer) {
      super(label);
      this.renderer = renderer;
      this.state = state;
   }

   public void render(Context context) {
      super.render(context);
      this.renderer.renderButton(context, this.getTitle(), this.hasFocus(context), this.state.get());
   }

   protected int getHeight() {
      return this.renderer.getDefaultHeight();
   }
}
