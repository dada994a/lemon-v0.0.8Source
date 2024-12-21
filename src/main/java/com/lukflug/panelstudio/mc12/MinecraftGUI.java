package com.lukflug.panelstudio.mc12;

import com.lukflug.panelstudio.container.GUI;
import java.awt.Point;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public abstract class MinecraftGUI extends GuiScreen {
   private Point mouse = new Point();
   private boolean lButton = false;
   private boolean rButton = false;
   private long lastTime;

   public void enterGUI() {
      Minecraft.func_71410_x().func_147108_a(this);
   }

   public void exitGUI() {
      Minecraft.func_71410_x().func_147108_a((GuiScreen)null);
   }

   protected void renderGUI() {
      this.lastTime = System.currentTimeMillis();
      this.getInterface().begin(true);
      this.getGUI().render();
      this.getInterface().end(true);
   }

   public void func_73866_w_() {
      this.getGUI().enter();
   }

   public void func_146281_b() {
      this.getGUI().exit();
   }

   public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
      this.mouse = this.getInterface().screenToGui(new Point(Mouse.getX(), Mouse.getY()));
      this.renderGUI();
      int scroll = Mouse.getDWheel();
      if (scroll != 0) {
         if (scroll > 0) {
            this.getGUI().handleScroll(-this.getScrollSpeed());
         } else {
            this.getGUI().handleScroll(this.getScrollSpeed());
         }
      }

   }

   public void func_73864_a(int mouseX, int mouseY, int clickedButton) {
      this.mouse = this.getInterface().screenToGui(new Point(Mouse.getX(), Mouse.getY()));
      switch(clickedButton) {
      case 0:
         this.lButton = true;
         break;
      case 1:
         this.rButton = true;
      }

      this.getGUI().handleButton(clickedButton);
   }

   public void func_146286_b(int mouseX, int mouseY, int releaseButton) {
      this.mouse = this.getInterface().screenToGui(new Point(Mouse.getX(), Mouse.getY()));
      switch(releaseButton) {
      case 0:
         this.lButton = false;
         break;
      case 1:
         this.rButton = false;
      }

      this.getGUI().handleButton(releaseButton);
   }

   protected void func_73869_a(char typedChar, int keyCode) {
      if (keyCode == 1) {
         this.exitGUI();
      } else {
         this.getGUI().handleKey(keyCode);
         this.getGUI().handleChar(typedChar);
      }

   }

   public boolean func_73868_f() {
      return false;
   }

   protected abstract GUI getGUI();

   protected abstract MinecraftGUI.GUIInterface getInterface();

   protected abstract int getScrollSpeed();

   public abstract class GUIInterface extends GLInterface {
      public GUIInterface(boolean clipX) {
         super(clipX);
      }

      public long getTime() {
         return MinecraftGUI.this.lastTime;
      }

      public boolean getButton(int button) {
         switch(button) {
         case 0:
            return MinecraftGUI.this.lButton;
         case 1:
            return MinecraftGUI.this.rButton;
         default:
            return false;
         }
      }

      public Point getMouse() {
         return new Point(MinecraftGUI.this.mouse);
      }

      protected float getZLevel() {
         return MinecraftGUI.this.field_73735_i;
      }
   }
}
