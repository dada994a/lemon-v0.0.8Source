package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.command.Command;

@Command.Declaration(
   name = "RefreshGUI",
   syntax = "refreshgui",
   alias = {"refreshgui", "freshgui", "fresh"}
)
public class RefreshGUICommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      LemonClient.INSTANCE.gameSenseGUI.refresh();
      MessageBus.sendCommandMessage("Refreshed ClickGUI!", true);
   }
}
