package com.lemonclient.client.command.commands;

import com.lemonclient.api.setting.SettingsManager;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.Command;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;

@Command.Declaration(
   name = "Set",
   syntax = "set [module] [setting] value (no color support)",
   alias = {"set", "setmodule", "changesetting", "setting"}
)
public class SetCommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      String main = message[0];
      Module module = ModuleManager.getModule(main);
      String[] string = new String[1];
      if (module == null) {
         string[0] = this.getSyntax();
      } else {
         SettingsManager.getSettingsForModule(module).stream().filter((setting) -> {
            return setting.getConfigName().equalsIgnoreCase(message[1]);
         }).forEach((setting) -> {
            if (setting instanceof BooleanSetting) {
               if (!message[2].equalsIgnoreCase("true") && !message[2].equalsIgnoreCase("false")) {
                  string[0] = this.getSyntax();
               } else {
                  setting.setValue(Boolean.parseBoolean(message[2]));
                  string[0] = module.getName() + " " + setting.getConfigName() + " set to: " + setting.getValue() + "!";
               }
            } else if (setting instanceof IntegerSetting) {
               if (Integer.parseInt(message[2]) > ((IntegerSetting)setting).getMax()) {
                  setting.setValue(((IntegerSetting)setting).getMax());
               }

               if (Integer.parseInt(message[2]) < ((IntegerSetting)setting).getMin()) {
                  setting.setValue(((IntegerSetting)setting).getMin());
               }

               if (Integer.parseInt(message[2]) < ((IntegerSetting)setting).getMax() && Integer.parseInt(message[2]) > ((IntegerSetting)setting).getMin()) {
                  setting.setValue(Integer.parseInt(message[2]));
               }

               string[0] = module.getName() + " " + setting.getConfigName() + " set to: " + setting.getValue() + "!";
            } else if (setting instanceof DoubleSetting) {
               if (Double.parseDouble(message[2]) > ((DoubleSetting)setting).getMax()) {
                  setting.setValue(((DoubleSetting)setting).getMax());
               }

               if (Double.parseDouble(message[2]) < ((DoubleSetting)setting).getMin()) {
                  setting.setValue(((DoubleSetting)setting).getMin());
               }

               if (Double.parseDouble(message[2]) < ((DoubleSetting)setting).getMax() && Double.parseDouble(message[2]) > ((DoubleSetting)setting).getMin()) {
                  setting.setValue(Double.parseDouble(message[2]));
               }

               string[0] = module.getName() + " " + setting.getConfigName() + " set to: " + setting.getValue() + "!";
            } else if (setting instanceof ModeSetting) {
               if (!((ModeSetting)setting).getModes().contains(message[2])) {
                  string[0] = this.getSyntax();
               } else {
                  setting.setValue(message[2]);
                  string[0] = module.getName() + " " + setting.getConfigName() + " set to: " + setting.getValue() + "!";
               }
            } else {
               string[0] = this.getSyntax();
            }

         });
         if (none) {
            MessageBus.sendServerMessage(string[0]);
         } else {
            MessageBus.sendCommandMessage(string[0], true);
         }

      }
   }
}
