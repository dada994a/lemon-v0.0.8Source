package com.lukflug.panelstudio.tabgui;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.FixedComponent;
import com.lukflug.panelstudio.container.IContainer;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import com.lukflug.panelstudio.setting.ICategory;
import com.lukflug.panelstudio.setting.IClient;
import com.lukflug.panelstudio.setting.ILabeled;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TabGUI extends TabItem<TabGUI.ChildTab, Void> {
   private final FixedComponent<TabGUI> fixedComponent;
   protected int width;
   protected IContainer<? super FixedComponent<Tab>> container;
   protected IPopupPositioner positioner;
   protected ITabGUIRenderer<Boolean> childRenderer;

   public TabGUI(ILabeled label, IClient client, ITabGUITheme theme, IContainer<? super FixedComponent<Tab>> container, Supplier<Animation> animation, IntPredicate up, IntPredicate down, IntPredicate enter, IntPredicate exit, Point position, String configName) {
      super(label, theme.getParentRenderer(), (Animation)animation.get(), up, down, enter, exit);
      this.width = theme.getTabWidth();
      this.container = container;
      this.positioner = theme.getPositioner();
      this.childRenderer = theme.getChildRenderer();
      AtomicInteger i = new AtomicInteger(0);
      this.contents = (List)client.getCategories().map((category) -> {
         return new TabItem.ContentItem(category.getDisplayName(), new TabGUI.ChildTab(category, (Animation)animation.get(), i.getAndIncrement()));
      }).collect(Collectors.toList());
      this.fixedComponent = new FixedComponent(this, position, this.width, (IToggleable)null, true, configName);
   }

   public FixedComponent<TabGUI> getWrappedComponent() {
      return this.fixedComponent;
   }

   protected boolean hasChildren() {
      Iterator var1 = this.contents.iterator();

      TabItem.ContentItem tab;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         tab = (TabItem.ContentItem)var1.next();
      } while(!((TabGUI.ChildTab)tab.content).visible.isOn());

      return true;
   }

   protected void handleSelect(Context context) {
      TabGUI.ChildTab tab = (TabGUI.ChildTab)((TabItem.ContentItem)this.contents.get((int)this.tabState.getTarget())).content;
      if (!tab.visible.isOn()) {
         tab.visible.toggle();
      }

   }

   protected void handleExit(Context context) {
      TabGUI.ChildTab tab = (TabGUI.ChildTab)((TabItem.ContentItem)this.contents.get((int)this.tabState.getTarget())).content;
      if (tab.visible.isOn()) {
         tab.visible.toggle();
      }

   }

   protected class ChildTab implements Supplier<Void> {
      public final FixedComponent<Tab> tab;
      public final IToggleable visible;

      public ChildTab(ICategory category, Animation animation, final int index) {
         this.tab = new FixedComponent<Tab>(new Tab(category, TabGUI.this.childRenderer, animation, TabGUI.this.up, TabGUI.this.down, TabGUI.this.enter), new Point(0, 0), TabGUI.this.width, (IToggleable)null, false, category.getDisplayName()) {
            public Point getPosition(IInterface inter) {
               Rectangle rect = new Rectangle(TabGUI.this.fixedComponent.getPosition(inter), new Dimension(this.width, TabGUI.this.getHeight()));
               Dimension dim = new Dimension(this.width, ((Tab)this.component).getHeight());
               return TabGUI.this.positioner.getPosition(inter, dim, TabGUI.this.renderer.getItemRect(inter, rect, TabGUI.this.contents.size(), (double)index), rect);
            }
         };
         this.visible = new SimpleToggleable(false);
         TabGUI.this.container.addComponent(this.tab, this.visible);
      }

      public Void get() {
         return null;
      }
   }
}
