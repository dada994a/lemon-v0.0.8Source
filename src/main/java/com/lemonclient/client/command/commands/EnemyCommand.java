package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.client.command.Command;

@Command.Declaration(
   name = "Enemy",
   syntax = "enemy list/add/del [player]",
   alias = {"enemy", "enemies", "e"}
)
public class EnemyCommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      String main = message[0];
      if (main.equalsIgnoreCase("list")) {
         MessageBus.sendCommandMessage("Enemies: " + SocialManager.getEnemiesByName() + ".", true);
      } else {
         String value = message[1];
         if (main.equalsIgnoreCase("add") && !SocialManager.isOnEnemyList(value)) {
            if (SocialManager.isOnEnemyList(value)) {
               MessageBus.sendCommandMessage(value + " is already your enemy.", true);
            } else {
               SocialManager.addEnemy(value);
               MessageBus.sendCommandMessage("Added enemy: " + value, true);
            }
         } else if (main.equalsIgnoreCase("del") && SocialManager.isOnEnemyList(value)) {
            if (SocialManager.isOnEnemyList(value)) {
               SocialManager.delEnemy(value);
               MessageBus.sendCommandMessage("Deleted enemy: " + value, true);
            } else {
               MessageBus.sendCommandMessage(value + " isn't your enemy.", true);
            }
         }

      }
   }
}
