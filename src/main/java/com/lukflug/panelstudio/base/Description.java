package com.lukflug.panelstudio.base;

import java.awt.Rectangle;

public final class Description {
   private final Rectangle componentPos;
   private final Rectangle panelPos;
   private final String content;

   public Description(Rectangle position, String content) {
      this.componentPos = position;
      this.panelPos = position;
      this.content = content;
   }

   public Description(Description description, Rectangle position) {
      this.componentPos = description.componentPos;
      this.panelPos = position;
      this.content = description.content;
   }

   public Rectangle getComponentPos() {
      return this.componentPos;
   }

   public Rectangle getPanelPos() {
      return this.panelPos;
   }

   public String getContent() {
      return this.content;
   }
}
