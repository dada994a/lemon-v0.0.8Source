package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.Command;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import java.util.Iterator;

@Command.Declaration(
   name = "DisableAll",
   syntax = "disableall",
   alias = {"disableall", "stop"}
)
public class DisableAllCommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      int count = 0;
      Iterator var5 = ModuleManager.getModules().iterator();

      while(var5.hasNext()) {
         Module module = (Module)var5.next();
         if (module.isEnabled()) {
            module.disable();
            ++count;
         }
      }

      MessageBus.sendCommandMessage("Disabled " + count + " modules!", true);
   }
}
