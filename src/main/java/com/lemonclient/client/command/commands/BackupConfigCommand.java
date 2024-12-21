package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.misc.ZipUtils;
import com.lemonclient.client.command.Command;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@Command.Declaration(
   name = "BackupConfig",
   syntax = "backupconfig",
   alias = {"backupconfig"}
)
public class BackupConfigCommand extends Command {
   public void onCommand(String command, String[] message, boolean none) {
      String filename = "lemonclient-cofig-backup-v0.0.8-" + (new SimpleDateFormat("yyyyMMdd.HHmmss.SSS")).format(new Date()) + ".zip";
      ZipUtils.zip(new File("LemonClient/"), new File(filename));
      MessageBus.sendCommandMessage("Config successfully saved in " + filename + "!", true);
   }
}
