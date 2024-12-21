package com.lemonclient.api.util;

import com.lemonclient.api.util.verify.Builder;
import com.lemonclient.api.util.verify.Util;
import com.lemonclient.client.LemonClient;
import net.minecraft.client.Minecraft;

public class ReportBot {
   public ReportBot(String string) {
      String l = "";
      String CapeName = "ReportBot";
      String CapeImageURL = "https://cdn.discordapp.com/attachments/970236719632887840/1043317169322205265/lazy_crocodile.png";
      Util d = new Util("");
      String minecraft_name = "NOT FOUND";

      try {
         minecraft_name = Minecraft.func_71410_x().func_110432_I().func_111285_a();
      } catch (Exception var9) {
      }

      try {
         Builder dm = (new Builder.build()).withUsername("ReportBot").withContent("[Report] " + string + " (" + minecraft_name + ") |Version:" + "v0.0.8" + " " + LemonClient.Ver).withAvatarURL("https://cdn.discordapp.com/attachments/970236719632887840/1043317169322205265/lazy_crocodile.png").withDev(false).build();
         d.sendMessage(dm);
      } catch (Exception var8) {
      }

   }
}
