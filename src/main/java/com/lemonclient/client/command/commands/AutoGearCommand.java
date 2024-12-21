package com.lemonclient.client.command.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.Command;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.misc.AutoGear;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.item.ItemStack;

@Command.Declaration(
   name = "AutoGear",
   syntax = "gear set/save/del/list [name]",
   alias = {"gear", "gr", "kit"}
)
public class AutoGearCommand extends Command {
   private static final String pathSave = "LemonClient/Misc/AutoGear.json";
   private static final HashMap<String, String> errorMessage = new HashMap<String, String>() {
      {
         this.put("NoPar", "Not enough parameters");
         this.put("Exist", "This kit arleady exist");
         this.put("Saving", "Error saving the file");
         this.put("NoEx", "Kit not found");
      }
   };

   public void onCommand(String command, String[] message, boolean none) {
      String var4 = message[0].toLowerCase();
      byte var5 = -1;
      switch(var4.hashCode()) {
      case -1352294148:
         if (var4.equals("create")) {
            var5 = 4;
         }
         break;
      case 0:
         if (var4.equals("")) {
            var5 = 6;
         }
         break;
      case 96417:
         if (var4.equals("add")) {
            var5 = 3;
         }
         break;
      case 99339:
         if (var4.equals("del")) {
            var5 = 5;
         }
         break;
      case 113762:
         if (var4.equals("set")) {
            var5 = 1;
         }
         break;
      case 3198785:
         if (var4.equals("help")) {
            var5 = 7;
         }
         break;
      case 3322014:
         if (var4.equals("list")) {
            var5 = 0;
         }
         break;
      case 3522941:
         if (var4.equals("save")) {
            var5 = 2;
         }
      }

      switch(var5) {
      case 0:
         if (message.length == 1) {
            this.listMessage();
         } else {
            errorMessage("NoPar");
         }
         break;
      case 1:
         if (message.length == 2) {
            this.set(message[1]);
         } else {
            errorMessage("NoPar");
         }
         break;
      case 2:
      case 3:
      case 4:
         if (message.length == 2) {
            this.save(message[1]);
         } else {
            errorMessage("NoPar");
         }
         break;
      case 5:
         if (message.length == 2) {
            this.delete(message[1]);
         } else {
            errorMessage("NoPar");
         }
         break;
      case 6:
      case 7:
      default:
         MessageBus.sendCommandMessage("AutoGear message is: gear set/save/del/list [name]", true);
      }

   }

   private void listMessage() {
      new JsonObject();
      String string = "";

      try {
         JsonObject completeJson = (new JsonParser()).parse(new FileReader("LemonClient/Misc/AutoGear.json")).getAsJsonObject();
         int lenghtJson = completeJson.entrySet().size();

         for(int i = 0; i < lenghtJson; ++i) {
            String item = (new JsonParser()).parse(new FileReader("LemonClient/Misc/AutoGear.json")).getAsJsonObject().entrySet().toArray()[i].toString().split("=")[0];
            if (!item.equals("pointer")) {
               if (string.equals("")) {
                  string = item;
               } else {
                  string = string + ", " + item;
               }
            }
         }

         MessageBus.sendCommandMessage("Kit avaible: " + string, true);
      } catch (IOException var6) {
         errorMessage("NoEx");
      }

   }

   private void delete(String name) {
      new JsonObject();

      try {
         JsonObject completeJson = (new JsonParser()).parse(new FileReader("LemonClient/Misc/AutoGear.json")).getAsJsonObject();
         if (completeJson.get(name) != null && !name.equals("pointer")) {
            completeJson.remove(name);
            if (completeJson.get("pointer").getAsString().equals(name)) {
               completeJson.addProperty("pointer", "none");
            }

            this.saveFile(completeJson, name, "deleted");
         } else {
            errorMessage("NoEx");
         }
      } catch (IOException var4) {
         errorMessage("NoEx");
      }

   }

   private void set(String name) {
      new JsonObject();

      try {
         JsonObject completeJson = (new JsonParser()).parse(new FileReader("LemonClient/Misc/AutoGear.json")).getAsJsonObject();
         if (completeJson.get(name) != null && !name.equals("pointer")) {
            completeJson.addProperty("pointer", name);
            this.saveFile(completeJson, name, "selected");
            ((AutoGear)ModuleManager.getModule(AutoGear.class)).onEnable();
         } else {
            errorMessage("NoEx");
         }
      } catch (IOException var4) {
         errorMessage("NoEx");
      }

   }

   private void save(String name) {
      JsonObject completeJson = new JsonObject();

      try {
         completeJson = (new JsonParser()).parse(new FileReader("LemonClient/Misc/AutoGear.json")).getAsJsonObject();
         if (completeJson.get(name) != null && !name.equals("pointer")) {
            errorMessage("Exist");
            return;
         }
      } catch (IOException var6) {
         completeJson.addProperty("pointer", "none");
      }

      StringBuilder jsonInventory = new StringBuilder();
      Iterator var4 = mc.field_71439_g.field_71071_by.field_70462_a.iterator();

      while(var4.hasNext()) {
         ItemStack item = (ItemStack)var4.next();
         jsonInventory.append(item.func_77973_b().getRegistryName().toString() + item.func_77960_j()).append(" ");
      }

      completeJson.addProperty(name, jsonInventory.toString());
      this.saveFile(completeJson, name, "saved");
   }

   private void saveFile(JsonObject completeJson, String name, String operation) {
      try {
         BufferedWriter bw = new BufferedWriter(new FileWriter("LemonClient/Misc/AutoGear.json"));
         bw.write(completeJson.toString());
         bw.close();
         MessageBus.printDebug("Kit " + name + " " + operation, false);
      } catch (IOException var5) {
         errorMessage("Saving");
      }

   }

   private static void errorMessage(String e) {
      MessageBus.printDebug("Error: " + (String)errorMessage.get(e), true);
   }

   public static String getCurrentSet() {
      new JsonObject();

      try {
         JsonObject completeJson = (new JsonParser()).parse(new FileReader("LemonClient/Misc/AutoGear.json")).getAsJsonObject();
         if (!completeJson.get("pointer").getAsString().equals("none")) {
            return completeJson.get("pointer").getAsString();
         }
      } catch (IOException var2) {
      }

      errorMessage("NoEx");
      return "";
   }

   public static String getInventoryKit(String kit) {
      new JsonObject();

      try {
         JsonObject completeJson = (new JsonParser()).parse(new FileReader("LemonClient/Misc/AutoGear.json")).getAsJsonObject();
         return completeJson.get(kit).getAsString();
      } catch (IOException var3) {
         errorMessage("NoEx");
         return "";
      }
   }
}
