package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.client.command.Command;

@Command.Declaration(
   name = "Ignore",
   syntax = "ignore list/add/del [player]",
   alias = {"ignore", "ignores"}
)
public class IgnoreCommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      String main = message[0];
      String string;
      if (main.equalsIgnoreCase("list")) {
         string = "Ignores: " + SocialManager.getIgnoresByName() + ".";
      } else {
         String value = message[1];
         if (main.equalsIgnoreCase("add")) {
            if (SocialManager.isOnIgnoreList(value)) {
               string = value + " is already on your ignore.";
            } else {
               SocialManager.addIgnore(value);
               string = "Added ignore: " + value + ".";
            }
         } else {
            if (!main.equalsIgnoreCase("del") || !SocialManager.isOnIgnoreList(value)) {
               return;
            }

            if (SocialManager.isOnIgnoreList(value)) {
               SocialManager.delIgnore(value);
               string = "Deleted ignore: " + value + ".";
            } else {
               string = value + " isn't your ignore.";
            }
         }
      }

      if (none) {
         MessageBus.sendServerMessage(string);
      } else {
         MessageBus.sendCommandMessage(string, true);
      }

   }
}
