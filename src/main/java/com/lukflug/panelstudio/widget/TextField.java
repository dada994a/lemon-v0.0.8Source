package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.component.FocusableComponent;
import com.lukflug.panelstudio.setting.IStringSetting;
import com.lukflug.panelstudio.theme.ITextFieldRenderer;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public abstract class TextField extends FocusableComponent {
   protected IStringSetting setting;
   protected ITextFieldKeys keys;
   private int position;
   private int select = -1;
   protected int boxPosition = 0;
   protected IToggleable insertMode;
   protected ITextFieldRenderer renderer;

   public TextField(IStringSetting setting, ITextFieldKeys keys, int position, IToggleable insertMode, ITextFieldRenderer renderer) {
      super(setting);
      this.setting = setting;
      this.keys = keys;
      this.position = position;
      this.insertMode = insertMode;
      this.renderer = renderer;
   }

   public void render(Context context) {
      super.render(context);
      this.boxPosition = this.renderer.renderTextField(context, this.getTitle(), this.hasFocus(context), this.setting.getValue(), this.getPosition(), this.getSelect(), this.boxPosition, this.insertMode.isOn());
   }

   public void handleButton(Context context, int button) {
      super.handleButton(context, button);
      if (button == 0 && context.isClicked(button)) {
         int pos = this.renderer.transformToCharPos(context, this.getTitle(), this.setting.getValue(), this.boxPosition);
         if (pos >= 0) {
            this.setPosition(context.getInterface(), pos);
         }

         this.unselect();
      } else if (!this.hasFocus(context)) {
         this.unselect();
      }

   }

   public void handleKey(Context context, int scancode) {
      super.handleKey(context, scancode);
      if (this.hasFocus(context)) {
         int pos = this.getPosition();
         int sel = this.getSelect();
         String s = this.setting.getValue();
         int temp;
         if (!this.keys.isBackspaceKey(scancode) || pos <= 0 && sel < 0) {
            if (this.keys.isDeleteKey(scancode) && (pos < this.setting.getValue().length() || sel >= 0)) {
               if (sel < 0) {
                  this.setting.setValue(s.substring(0, pos) + s.substring(pos + 1));
               } else {
                  if (pos > sel) {
                     temp = sel;
                     sel = pos;
                     pos = temp;
                     this.setPosition(context.getInterface(), temp);
                  }

                  this.setting.setValue(s.substring(0, pos) + s.substring(sel));
               }

               this.unselect();
            } else if (this.keys.isInsertKey(scancode)) {
               this.insertMode.toggle();
            } else if (this.keys.isLeftKey(scancode)) {
               if (sel >= 0 && !context.getInterface().getModifier(0)) {
                  this.setPosition(context.getInterface(), Math.min(pos, sel));
               } else {
                  this.setPosition(context.getInterface(), pos - 1);
               }
            } else if (this.keys.isRightKey(scancode)) {
               if (sel >= 0 && !context.getInterface().getModifier(0)) {
                  this.setPosition(context.getInterface(), Math.max(pos, sel));
               } else {
                  this.setPosition(context.getInterface(), this.getPosition() + 1);
               }
            } else if (this.keys.isHomeKey(scancode)) {
               this.setPosition(context.getInterface(), 0);
            } else if (this.keys.isEndKey(scancode)) {
               this.setPosition(context.getInterface(), this.setting.getValue().length());
            } else {
               StringSelection selection;
               if (context.getInterface().getModifier(1) && this.keys.isCopyKey(scancode) && sel >= 0) {
                  selection = new StringSelection(s.substring(Math.min(pos, sel), Math.max(pos, sel)));
                  Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
               } else if (context.getInterface().getModifier(1) && this.keys.isPasteKey(scancode)) {
                  try {
                     Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents((Object)null);
                     if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        String selection = (String)t.getTransferData(DataFlavor.stringFlavor);
                        if (sel < 0) {
                           this.setting.setValue(s.substring(0, pos) + selection + s.substring(pos));
                        } else {
                           if (pos > sel) {
                              int temp = sel;
                              sel = pos;
                              pos = temp;
                              this.setPosition(context.getInterface(), temp);
                           }

                           this.setting.setValue(s.substring(0, pos) + selection + s.substring(sel));
                        }

                        this.position = pos + selection.length();
                        this.select = pos;
                     }
                  } catch (IOException var9) {
                  } catch (UnsupportedFlavorException var10) {
                  }
               } else if (context.getInterface().getModifier(1) && this.keys.isCutKey(scancode) && sel >= 0) {
                  selection = new StringSelection(s.substring(Math.min(pos, sel), Math.max(pos, sel)));
                  Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                  if (pos > sel) {
                     int temp = sel;
                     sel = pos;
                     pos = temp;
                     this.setPosition(context.getInterface(), temp);
                  }

                  this.setting.setValue(s.substring(0, pos) + s.substring(sel));
               } else if (context.getInterface().getModifier(1) && this.keys.isAllKey(scancode)) {
                  this.select = 0;
                  this.position = s.length();
               }
            }
         } else {
            if (sel < 0) {
               this.setPosition(context.getInterface(), pos - 1);
               this.setting.setValue(s.substring(0, pos - 1) + s.substring(pos));
            } else {
               if (pos > sel) {
                  temp = sel;
                  sel = pos;
                  pos = temp;
                  this.setPosition(context.getInterface(), temp);
               }

               this.setting.setValue(s.substring(0, pos) + s.substring(sel));
            }

            this.unselect();
         }
      }

   }

   public void handleChar(Context context, char character) {
      super.handleChar(context, character);
      if (this.hasFocus(context) && this.allowCharacter(character)) {
         int pos = this.getPosition();
         int sel = this.getSelect();
         String s = this.setting.getValue();
         if (sel < 0) {
            if (this.insertMode.isOn() && pos < s.length()) {
               this.setting.setValue(s.substring(0, pos) + character + s.substring(pos + 1));
            } else {
               this.setting.setValue(s.substring(0, pos) + character + s.substring(pos));
            }
         } else {
            if (pos > sel) {
               int temp = sel;
               sel = pos;
               pos = temp;
            }

            this.setting.setValue(s.substring(0, pos) + character + s.substring(sel));
            this.unselect();
         }

         this.position = pos + 1;
      }

   }

   public void releaseFocus() {
      super.releaseFocus();
      this.unselect();
   }

   public void exit() {
      super.exit();
      this.unselect();
   }

   protected int getHeight() {
      return this.renderer.getDefaultHeight();
   }

   protected int getPosition() {
      if (this.position < 0) {
         this.position = 0;
      } else if (this.position > this.setting.getValue().length()) {
         this.position = this.setting.getValue().length();
      }

      return this.position;
   }

   protected void setPosition(IInterface inter, int position) {
      if (inter.getModifier(0)) {
         if (this.select < 0) {
            this.select = this.position;
         }
      } else {
         this.select = -1;
      }

      this.position = position;
   }

   protected int getSelect() {
      if (this.select > this.setting.getValue().length()) {
         this.select = this.setting.getValue().length();
      }

      if (this.select == this.getPosition()) {
         this.select = -1;
      }

      return this.select;
   }

   protected void unselect() {
      this.select = -1;
   }

   public abstract boolean allowCharacter(char var1);
}
