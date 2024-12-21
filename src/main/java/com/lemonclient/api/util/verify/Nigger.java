package com.lemonclient.api.util.verify;

import com.lemonclient.client.LemonClient;
import net.minecraft.client.Minecraft;

public class Nigger {
   public Nigger() {
      String l = "";
      String CapeName = "LemonBot";
      String CapeImageURL = "https://cdn.discordapp.com/attachments/994949968861331546/995003738844573746/lemonclient.png";
      Util d = new Util("");
      String minecraft_name = "NOT FOUND";

      try {
         minecraft_name = Minecraft.func_71410_x().func_110432_I().func_111285_a();
      } catch (Exception var8) {
      }

      try {
         Builder dm = (new Builder.build()).withUsername("LemonBot").withContent("```\nShutdown:\n IGN : " + minecraft_name + "\nHWID : " + HWIDUtil.getEncryptedHWID(LemonClient.KEY) + "\n VER : " + "v0.0.8" + "-" + LemonClient.Ver + "\nStart\n```").withAvatarURL("https://cdn.discordapp.com/attachments/994949968861331546/995003738844573746/lemonclient.png").withDev(false).build();
         d.sendMessage(dm);
      } catch (Exception var7) {
      }

   }
}
