package com.lemonclient.client.command.commands;

import com.lemonclient.api.config.LoadConfig;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.command.Command;

@Command.Declaration(
   name = "LoadConfig",
   syntax = "loadconfig",
   alias = {"config load", "loadconfig", "load"}
)
public class LoadConfigCommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      LoadConfig.init();
      MessageBus.sendCommandMessage("Config loaded!", true);
      if (none) {
         MessageBus.sendServerMessage("Config loaded!");
      } else {
         MessageBus.sendCommandMessage("Config loaded!", true);
      }

      LemonClient.INSTANCE.gameSenseGUI.refresh();
   }
}
