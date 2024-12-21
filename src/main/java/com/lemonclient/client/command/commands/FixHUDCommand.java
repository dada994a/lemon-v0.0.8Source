package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.Command;
import com.lemonclient.client.module.HUDModule;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import java.util.Iterator;

@Command.Declaration(
   name = "FixHUD",
   syntax = "fixhud",
   alias = {"fixhud", "hud", "resethud"}
)
public class FixHUDCommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      Iterator var4 = ModuleManager.getModules().iterator();

      while(var4.hasNext()) {
         Module module = (Module)var4.next();
         if (module instanceof HUDModule) {
            ((HUDModule)module).resetPosition();
         }
      }

      MessageBus.sendCommandMessage("HUD positions reset!", true);
   }
}
