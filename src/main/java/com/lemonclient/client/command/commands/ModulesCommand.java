package com.lemonclient.client.command.commands;

import com.lemonclient.client.command.Command;
import com.lemonclient.client.command.CommandManager;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;

@Command.Declaration(
   name = "Modules",
   syntax = "modules (click to toggle)",
   alias = {"modules", "module", "modulelist", "mod", "mods"}
)
public class ModulesCommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      TextComponentString msg = new TextComponentString("§7Modules: §f ");
      Collection<Module> modules = ModuleManager.getModules();
      int size = modules.size();
      int index = 0;

      for(Iterator var8 = modules.iterator(); var8.hasNext(); ++index) {
         Module module = (Module)var8.next();
         msg.func_150257_a((new TextComponentString((module.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.RED) + module.getName() + "§7" + (index == size - 1 ? "" : ", "))).func_150255_a((new Style()).func_150209_a(new HoverEvent(Action.SHOW_TEXT, new TextComponentString(module.getCategory().name()))).func_150241_a(new ClickEvent(net.minecraft.util.text.event.ClickEvent.Action.RUN_COMMAND, CommandManager.getCommandPrefix() + "toggle " + module.getName()))));
      }

      msg.func_150257_a(new TextComponentString(ChatFormatting.GRAY + "!"));
      mc.field_71456_v.func_146158_b().func_146227_a(msg);
   }
}
