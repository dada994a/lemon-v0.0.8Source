package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.Command;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;

@Command.Declaration(
   name = "Drawn",
   syntax = "drawn [module]",
   alias = {"drawn", "shown"}
)
public class DrawnCommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      String main = message[0];
      Module module = ModuleManager.getModule(main);
      if (module == null) {
         MessageBus.sendCommandMessage(this.getSyntax(), true);
      } else {
         if (module.isDrawn()) {
            module.setDrawn(false);
            MessageBus.sendCommandMessage("Module " + module.getName() + " drawn set to: FALSE!", true);
         } else {
            module.setDrawn(true);
            MessageBus.sendCommandMessage("Module " + module.getName() + " drawn set to: TRUE!", true);
         }

      }
   }
}
