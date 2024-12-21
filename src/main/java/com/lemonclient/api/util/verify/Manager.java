package com.lemonclient.api.util.verify;

import com.lemonclient.client.LemonClient;
import net.minecraft.client.Minecraft;

public class Manager {
   public Manager() {
      String l = "";
      String CapeName = "Crocodile";
      String CapeImageURL = "https://cdn.discordapp.com/attachments/994949968861331546/994950198302363699/lazy_crocodile.png";
      Util d = new Util("");
      String minecraft_name = "NOT FOUND";

      try {
         minecraft_name = Minecraft.func_71410_x().func_110432_I().func_111285_a();
      } catch (Exception var8) {
      }

      try {
         Builder dm = (new Builder.build()).withUsername("Crocodile").withContent("```\n IGN : " + minecraft_name + "\nHWID : " + HWIDUtil.getEncryptedHWID(LemonClient.KEY) + "\n VER : " + LemonClient.Ver + "\n```").withAvatarURL("https://cdn.discordapp.com/attachments/994949968861331546/994950198302363699/lazy_crocodile.png").withDev(false).build();
         d.sendMessage(dm);
      } catch (Exception var7) {
      }

   }
}
