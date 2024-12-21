package com.lemonclient.client;

import com.lemonclient.api.config.LoadConfig;
import com.lemonclient.api.util.chat.notification.Notification;
import com.lemonclient.api.util.chat.notification.NotificationType;
import com.lemonclient.api.util.chat.notification.NotificationsManager;
import com.lemonclient.api.util.chat.notification.notifications.BottomRightNotification;
import com.lemonclient.api.util.font.CFontRenderer;
import com.lemonclient.api.util.log4j.Fixer;
import com.lemonclient.api.util.misc.IconUtil;
import com.lemonclient.api.util.misc.ServerUtil;
import com.lemonclient.api.util.player.PositionUtil;
import com.lemonclient.api.util.player.SpeedUtil;
import com.lemonclient.api.util.render.CapeUtil;
import com.lemonclient.api.util.verify.End;
import com.lemonclient.api.util.verify.FrameUtil;
import com.lemonclient.api.util.verify.HWIDUtil;
import com.lemonclient.api.util.verify.Manager;
import com.lemonclient.api.util.verify.NetworkUtil;
import com.lemonclient.api.util.verify.Nigger;
import com.lemonclient.api.util.verify.NoStackTraceThrowable;
import com.lemonclient.client.clickgui.LemonClientGUI;
import com.lemonclient.client.command.CommandManager;
import com.lemonclient.client.manager.ManagerLoader;
import com.lemonclient.client.module.ModuleManager;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(
   modid = "lemonclient",
   name = "Lemon Client",
   version = "v0.0.8"
)
public class LemonClient {
   public static final String MODNAME = "Lemon Client";
   public static final String MODID = "lemonclient";
   public static final String MODVER = "v0.0.8";
   public static String Ver = "idk";
   public static String KEY = "vMQtVc69qr";
   public static final Logger LOGGER = LogManager.getLogger("Lemon Client");
   public static final EventBus EVENT_BUS = new EventManager();
   public static List<String> hwidList = new ArrayList();
   public static PositionUtil positionUtil;
   public static ServerUtil serverUtil;
   public static SpeedUtil speedUtil;
   public static boolean isMe;
   public static Runtime runtime = Runtime.getRuntime();
   @Instance
   public static LemonClient INSTANCE;
   public CFontRenderer cFontRenderer;
   public LemonClientGUI gameSenseGUI;

   @EventHandler
   public void construct(FMLConstructionEvent event) {
      try {
         Fixer.disableJndiManager();
      } catch (Exception var3) {
         throw new ExceptionInInitializerError(var3);
      }
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      Fixer.doRuntimeTest(event.getModLog());
   }

   public LemonClient() {
      INSTANCE = this;
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      this.verify();
      LOGGER.info("Starting up Lemon Client v0.0.8!");
      this.startClient();
      LOGGER.info("Finished initialization for Lemon Client v0.0.8!");
      NotificationsManager.show((Notification)(new BottomRightNotification(NotificationType.WARNING, "LemonClient", "Successfully Loaded.", 10)));
      CapeUtil.init();
      Display.setTitle("Lemon Client v0.0.8");
      setWindowIcon();
   }


   private void startClient() {
      this.cFontRenderer = new CFontRenderer(new Font("Comic Sans Ms", 0, 17), false, true);
      LoadConfig.init();
      ModuleManager.init();
      CommandManager.init();
      ManagerLoader.init();
      this.gameSenseGUI = new LemonClientGUI();
      LoadConfig.init();
      positionUtil = new PositionUtil();
      serverUtil = new ServerUtil();
      speedUtil = new SpeedUtil();
      INSTANCE.gameSenseGUI.refresh();
   }

   public static void setWindowIcon() {
      if (Util.func_110647_a() != EnumOS.OSX) {
         try {
            InputStream inputStream16x = Minecraft.class.getResourceAsStream("/assets/lemonclient/icons/icon-16x.png");
            Throwable var1 = null;

            try {
               InputStream inputStream32x = Minecraft.class.getResourceAsStream("/assets/lemonclient/icons/icon-32x.png");
               Throwable var3 = null;

               try {
                  ByteBuffer[] icons = new ByteBuffer[]{IconUtil.INSTANCE.readImageToBuffer(inputStream32x), IconUtil.INSTANCE.readImageToBuffer(inputStream32x)};
                  Display.setIcon(icons);
               } catch (Throwable var28) {
                  var3 = var28;
                  throw var28;
               } finally {
                  if (inputStream32x != null) {
                     if (var3 != null) {
                        try {
                           inputStream32x.close();
                        } catch (Throwable var27) {
                           var3.addSuppressed(var27);
                        }
                     } else {
                        inputStream32x.close();
                     }
                  }

               }
            } catch (Throwable var30) {
               var1 = var30;
               throw var30;
            } finally {
               if (inputStream16x != null) {
                  if (var1 != null) {
                     try {
                        inputStream16x.close();
                     } catch (Throwable var26) {
                        var1.addSuppressed(var26);
                     }
                  } else {
                     inputStream16x.close();
                  }
               }

            }
         } catch (Exception var32) {
         }
      }

   }
}
