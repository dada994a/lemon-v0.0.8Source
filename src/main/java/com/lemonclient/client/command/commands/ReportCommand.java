package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.ReportBot;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.Command;
import java.util.Objects;

@Command.Declaration(
   name = "Report",
   syntax = "report [report]",
   alias = {"report"}
)
public class ReportCommand extends Command {
   ReportBot reportBot;

   public void onCommand(String command, String[] message, boolean none) {
      if (!Objects.equals(message[0], "")) {
         this.reportBot = new ReportBot(message[0]);
         MessageBus.sendClientPrefixMessage("Reported " + message[0] + "!", Notification.Type.SUCCESS);
      }
   }
}
