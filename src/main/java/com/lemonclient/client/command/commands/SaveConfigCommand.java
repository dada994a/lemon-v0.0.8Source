package com.lemonclient.client.command.commands;

import com.lemonclient.api.config.SaveConfig;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.Command;

@Command.Declaration(
   name = "SaveConfig",
   syntax = "saveconfig",
   alias = {"config save", "saveconfig", "save"}
)
public class SaveConfigCommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      SaveConfig.init();
      MessageBus.sendCommandMessage("Config saved!", true);
   }
}
