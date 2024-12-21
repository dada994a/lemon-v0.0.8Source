package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.Command;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import java.util.Iterator;
import org.lwjgl.input.Keyboard;

@Command.Declaration(
   name = "Bind",
   syntax = "bind [module] key",
   alias = {"bind", "b", "setbind", "key"}
)
public class BindCommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      String main = message[0];
      String value = message[1].toUpperCase();
      Iterator var6 = ModuleManager.getModules().iterator();

      while(var6.hasNext()) {
         Module module = (Module)var6.next();
         if (module.getName().equalsIgnoreCase(main)) {
            if (value.equalsIgnoreCase("none")) {
               module.setBind(0);
               MessageBus.sendCommandMessage("Module " + module.getName() + " bind set to: " + value + "!", true);
            } else if (value.length() == 1) {
               int key = Keyboard.getKeyIndex(value);
               module.setBind(key);
               MessageBus.sendCommandMessage("Module " + module.getName() + " bind set to: " + value + "!", true);
            } else if (value.length() > 1) {
               MessageBus.sendCommandMessage(this.getSyntax(), true);
            }
         }
      }

   }
}
