package com.lemonclient.client.command;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.commands.AutoGearCommand;
import com.lemonclient.client.command.commands.BackupConfigCommand;
import com.lemonclient.client.command.commands.BindCommand;
import com.lemonclient.client.command.commands.CmdListCommand;
import com.lemonclient.client.command.commands.DisableAllCommand;
import com.lemonclient.client.command.commands.DrawnCommand;
import com.lemonclient.client.command.commands.EnemyCommand;
import com.lemonclient.client.command.commands.FixGUICommand;
import com.lemonclient.client.command.commands.FixHUDCommand;
import com.lemonclient.client.command.commands.FontCommand;
import com.lemonclient.client.command.commands.FriendCommand;
import com.lemonclient.client.command.commands.IgnoreCommand;
import com.lemonclient.client.command.commands.LoadCapeCommand;
import com.lemonclient.client.command.commands.LoadConfigCommand;
import com.lemonclient.client.command.commands.ModulesCommand;
import com.lemonclient.client.command.commands.MsgsCommand;
import com.lemonclient.client.command.commands.OpenFolderCommand;
import com.lemonclient.client.command.commands.PrefixCommand;
import com.lemonclient.client.command.commands.RefreshGUICommand;
import com.lemonclient.client.command.commands.ReportCommand;
import com.lemonclient.client.command.commands.SaveConfigCommand;
import com.lemonclient.client.command.commands.SetCommand;
import com.lemonclient.client.command.commands.ToggleCommand;
import java.util.ArrayList;

public class CommandManager {
   private static String commandPrefix = "-";
   public static final ArrayList<Command> commands = new ArrayList();
   public static boolean isValidCommand = false;

   public static void init() {
      addCommand(new AutoGearCommand());
      addCommand(new BackupConfigCommand());
      addCommand(new BindCommand());
      addCommand(new CmdListCommand());
      addCommand(new DisableAllCommand());
      addCommand(new DrawnCommand());
      addCommand(new EnemyCommand());
      addCommand(new FixGUICommand());
      addCommand(new FixHUDCommand());
      addCommand(new FontCommand());
      addCommand(new FriendCommand());
      addCommand(new IgnoreCommand());
      addCommand(new LoadCapeCommand());
      addCommand(new LoadConfigCommand());
      addCommand(new ModulesCommand());
      addCommand(new MsgsCommand());
      addCommand(new OpenFolderCommand());
      addCommand(new PrefixCommand());
      addCommand(new RefreshGUICommand());
      addCommand(new ReportCommand());
      addCommand(new SaveConfigCommand());
      addCommand(new SetCommand());
      addCommand(new ToggleCommand());
   }

   public static void addCommand(Command command) {
      commands.add(command);
   }

   public static ArrayList<Command> getCommands() {
      return commands;
   }

   public static String getCommandPrefix() {
      return commandPrefix;
   }

   public static void setCommandPrefix(String prefix) {
      commandPrefix = prefix;
   }

   public static void callCommand(String input, boolean none) {
      String[] split = input.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
      String command1 = split[0];
      String args = input.substring(command1.length()).trim();
      isValidCommand = false;
      commands.forEach((command) -> {
         String[] var4 = command.getAlias();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String string = var4[var6];
            if (string.equalsIgnoreCase(command1)) {
               isValidCommand = true;

               try {
                  command.onCommand(args, args.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"), none);
               } catch (Exception var9) {
                  MessageBus.sendCommandMessage(command.getSyntax(), true);
               }
            }
         }

      });
      if (!isValidCommand) {
         MessageBus.sendCommandMessage("Error! Invalid command!", true);
      }

   }
}
