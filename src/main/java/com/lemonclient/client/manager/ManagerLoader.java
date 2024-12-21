package com.lemonclient.client.manager;

import com.lemonclient.client.LemonClient;
import com.lemonclient.client.manager.managers.ClientEventManager;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.client.manager.managers.TotemPopManager;
import java.util.ArrayList;
import java.util.List;
import me.zero.alpine.listener.Listenable;
import net.minecraftforge.common.MinecraftForge;

public class ManagerLoader {
   private static final List<Manager> managers = new ArrayList();

   public static void init() {
      register(ClientEventManager.INSTANCE);
      register(PlayerPacketManager.INSTANCE);
      register(TotemPopManager.INSTANCE);
   }

   private static void register(Manager manager) {
      managers.add(manager);
      LemonClient.EVENT_BUS.subscribe((Listenable)manager);
      MinecraftForge.EVENT_BUS.register(manager);
   }
}
