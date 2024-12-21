package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.Command;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;

@Command.Declaration(
   name = "Toggle",
   syntax = "toggle [module]",
   alias = {"toggle", "t", "enable", "disable"}
)
public class ToggleCommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      String main = message[0];
      Module module = ModuleManager.getModule(main);
      String string;
      if (module == null) {
         string = this.getSyntax();
      } else {
         module.toggle();
         if (module.isEnabled()) {
            string = "Module " + module.getName() + " set to: ENABLED!";
         } else {
            string = "Module " + module.getName() + " set to: DISABLED!";
         }
      }

      if (none) {
         MessageBus.sendServerMessage(string);
      } else {
         MessageBus.sendCommandMessage(string, true);
      }

   }
}
