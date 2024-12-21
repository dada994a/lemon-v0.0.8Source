package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.Command;
import com.lemonclient.client.command.CommandManager;
import java.util.Iterator;

@Command.Declaration(
   name = "Commands",
   syntax = "commands",
   alias = {"commands", "cmd", "command", "commandlist", "help"}
)
public class CmdListCommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      Iterator var4 = CommandManager.getCommands().iterator();

      while(var4.hasNext()) {
         Command command1 = (Command)var4.next();
         MessageBus.sendMessage(command1.getName() + ": \"" + command1.getSyntax() + "\"!", true);
      }

   }
}
