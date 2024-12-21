package com.lukflug.panelstudio.tabgui;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.component.ComponentBase;
import com.lukflug.panelstudio.setting.ILabeled;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.function.Supplier;

public abstract class TabItem<S extends Supplier<T>, T> extends ComponentBase {
   protected ITabGUIRenderer<T> renderer;
   protected List<TabItem.ContentItem<S, T>> contents;
   protected Animation tabState;
   protected final IntPredicate up;
   protected final IntPredicate down;
   protected final IntPredicate enter;
   protected final IntPredicate exit;

   public TabItem(ILabeled label, ITabGUIRenderer<T> renderer, Animation animation, IntPredicate up, IntPredicate down, IntPredicate enter, IntPredicate exit) {
      super(label);
      this.renderer = renderer;
      this.tabState = animation;
      this.up = up;
      this.down = down;
      this.enter = enter;
      this.exit = exit;
   }

   public void render(Context context) {
      super.render(context);
      this.renderer.renderTab(context, this.contents.size(), this.tabState.getValue());

      for(int i = 0; i < this.contents.size(); ++i) {
         this.renderer.renderItem(context, this.contents.size(), this.tabState.getValue(), i, ((TabItem.ContentItem)this.contents.get(i)).name, ((TabItem.ContentItem)this.contents.get(i)).content.get());
      }

   }

   public void handleKey(Context context, int key) {
      super.handleKey(context, key);
      if (!this.hasChildren()) {
         int nextState;
         if (this.up.test(key)) {
            nextState = (int)this.tabState.getTarget() - 1;
            if (nextState < 0) {
               nextState = this.contents.size() - 1;
            }

            this.tabState.setValue((double)nextState);
         } else if (this.down.test(key)) {
            nextState = (int)this.tabState.getTarget() + 1;
            if (nextState >= this.contents.size()) {
               nextState = 0;
            }

            this.tabState.setValue((double)nextState);
         } else if (this.enter.test(key)) {
            this.handleSelect(context);
         }
      }

      if (this.exit.test(key)) {
         this.handleExit(context);
      }

   }

   public void releaseFocus() {
   }

   protected int getHeight() {
      return this.renderer.getTabHeight(this.contents.size());
   }

   protected boolean hasChildren() {
      return false;
   }

   protected abstract void handleSelect(Context var1);

   protected abstract void handleExit(Context var1);

   protected static final class ContentItem<S extends Supplier<T>, T> {
      public final String name;
      public final S content;

      public ContentItem(String name, S content) {
         this.name = name;
         this.content = content;
      }
   }
}
