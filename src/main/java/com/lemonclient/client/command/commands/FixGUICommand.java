package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.clickgui.LemonClientGUI;
import com.lemonclient.client.command.Command;

@Command.Declaration(
   name = "FixGUI",
   syntax = "fixgui",
   alias = {"fixgui", "gui", "resetgui"}
)
public class FixGUICommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      LemonClient.INSTANCE.gameSenseGUI = new LemonClientGUI();
      MessageBus.sendCommandMessage("ClickGUI positions reset!", true);
   }
}
