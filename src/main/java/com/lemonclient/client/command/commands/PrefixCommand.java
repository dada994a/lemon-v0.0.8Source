package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.Command;
import com.lemonclient.client.command.CommandManager;

@Command.Declaration(
   name = "Prefix",
   syntax = "prefix value (no letters or numbers)",
   alias = {"prefix", "setprefix", "cmdprefix", "commandprefix"}
)
public class PrefixCommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      String main = message[0].toUpperCase().replaceAll("[a-zA-Z0-9]", (String)null);
      int size = message[0].length();
      if (size == 1) {
         CommandManager.setCommandPrefix(main);
         MessageBus.sendCommandMessage("Prefix set: \"" + main + "\"!", true);
      } else if (size != 1) {
         MessageBus.sendCommandMessage(this.getSyntax(), true);
      }

   }
}
