package com.lukflug.panelstudio.base;

import com.lukflug.panelstudio.popup.IPopupDisplayer;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public final class Context {
   private final IInterface inter;
   private final Dimension size;
   private final Point position;
   private final boolean focus;
   private final boolean onTop;
   private boolean focusRequested = false;
   private boolean focusOverride = false;
   private Description description = null;
   private IPopupDisplayer popupDisplayer = null;

   public Context(Context context, int width, Point offset, boolean focus, boolean onTop) {
      this.inter = context.getInterface();
      this.size = new Dimension(width, 0);
      this.position = context.getPos();
      this.position.translate(offset.x, offset.y);
      this.focus = context.hasFocus() && focus;
      this.onTop = context.onTop() && onTop;
      this.popupDisplayer = context.getPopupDisplayer();
   }

   public Context(IInterface inter, int width, Point position, boolean focus, boolean onTop) {
      this.inter = inter;
      this.size = new Dimension(width, 0);
      this.position = new Point(position);
      this.focus = focus;
      this.onTop = onTop;
   }

   public IInterface getInterface() {
      return this.inter;
   }

   public Dimension getSize() {
      return new Dimension(this.size);
   }

   public void setHeight(int height) {
      this.size.height = height;
   }

   public Point getPos() {
      return new Point(this.position);
   }

   public boolean hasFocus() {
      return this.focus;
   }

   public boolean onTop() {
      return this.onTop;
   }

   public void requestFocus() {
      if (!this.focusOverride) {
         this.focusRequested = true;
      }

   }

   public void releaseFocus() {
      this.focusRequested = false;
      this.focusOverride = true;
   }

   public boolean foucsRequested() {
      return this.focusRequested && !this.focusOverride;
   }

   public boolean focusReleased() {
      return this.focusOverride;
   }

   public boolean isHovered() {
      return (new Rectangle(this.position, this.size)).contains(this.inter.getMouse()) && this.onTop;
   }

   public boolean isClicked(int button) {
      return this.isHovered() && this.inter.getButton(button);
   }

   public Rectangle getRect() {
      return new Rectangle(this.position, this.size);
   }

   public Description getDescription() {
      return this.description;
   }

   public void setDescription(Description description) {
      this.description = description;
   }

   public IPopupDisplayer getPopupDisplayer() {
      return this.popupDisplayer;
   }

   public void setPopupDisplayer(IPopupDisplayer popupDisplayer) {
      this.popupDisplayer = popupDisplayer;
   }
}
