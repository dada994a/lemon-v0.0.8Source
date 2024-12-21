package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.font.CFontRenderer;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.command.Command;
import java.awt.Font;

@Command.Declaration(
   name = "Font",
   syntax = "font [name] (use _ for spaces) size antiAlias (true/false) metrics (true/false)",
   alias = {"font", "setfont", "customfont", "fonts", "chatfont"}
)
public class FontCommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      String main = message[0].replace("_", " ");
      int value = Integer.parseInt(message[1]);
      if (value >= 21 || value <= 15) {
         value = 18;
      }

      LemonClient.INSTANCE.cFontRenderer = new CFontRenderer(new Font(main, 0, value), Boolean.parseBoolean(message[2]), Boolean.parseBoolean(message[3]));
      LemonClient.INSTANCE.cFontRenderer.setFontName(main);
      LemonClient.INSTANCE.cFontRenderer.setFontSize(value);
      MessageBus.sendCommandMessage("Font set to: " + main.toUpperCase() + ", size " + value + "!", true);
   }
}
