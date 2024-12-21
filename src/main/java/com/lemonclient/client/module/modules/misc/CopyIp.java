package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;

@Module.Declaration(
   name = "Copy IP",
   category = Category.Misc
)
public class CopyIp extends Module {
   String server;

   public void onEnable() {
      try {
         this.server = mc.func_147104_D().field_78845_b;
      } catch (Exception var4) {
         this.server = "Singleplayer";
      }

      String myString = this.server;
      StringSelection stringSelection = new StringSelection(myString);
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(stringSelection, (ClipboardOwner)null);
      MessageBus.sendClientPrefixMessage("Copied '" + this.server + "' to clipboard.", Notification.Type.INFO);
      this.disable();
   }
}
