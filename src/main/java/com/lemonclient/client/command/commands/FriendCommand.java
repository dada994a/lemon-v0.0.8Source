package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.client.command.Command;

@Command.Declaration(
   name = "Friend",
   syntax = "friend list/add/del [player]",
   alias = {"friend", "friends", "f"}
)
public class FriendCommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      String main = message[0];
      String string;
      if (main.equalsIgnoreCase("list")) {
         string = "Friends: " + SocialManager.getFriendsByName() + ".";
      } else {
         String value = message[1];
         if (main.equalsIgnoreCase("add")) {
            if (SocialManager.isOnFriendList(value)) {
               string = value + " is already your friend.";
            } else {
               SocialManager.addFriend(value);
               string = "Added friend: " + value + ".";
            }
         } else {
            if (!main.equalsIgnoreCase("del") || !SocialManager.isOnFriendList(value)) {
               return;
            }

            if (SocialManager.isOnFriendList(value)) {
               SocialManager.delFriend(value);
               string = "Deleted friend: " + value + ".";
            } else {
               string = value + " isn't your friend.";
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
