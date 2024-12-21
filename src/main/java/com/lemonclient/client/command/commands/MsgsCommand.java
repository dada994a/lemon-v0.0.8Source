package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.Command;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;

@Command.Declaration(
   name = "Msgs",
   syntax = "msgs [module]",
   alias = {"msgs", "togglemsgs", "showmsgs", "messages"}
)
public class MsgsCommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      String main = message[0];
      Module module = ModuleManager.getModule(main);
      if (module == null) {
         MessageBus.sendCommandMessage(this.getSyntax(), true);
      } else {
         if (module.isToggleMsg()) {
            module.setToggleMsg(false);
            MessageBus.sendCommandMessage("Module " + module.getName() + " message toggle set to: FALSE!", true);
         } else {
            module.setToggleMsg(true);
            MessageBus.sendCommandMessage("Module " + module.getName() + " message toggle set to: TRUE!", true);
         }

      }
   }
}
