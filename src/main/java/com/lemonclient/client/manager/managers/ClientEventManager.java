package com.lemonclient.client.manager.managers;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.PlayerJoinEvent;
import com.lemonclient.api.event.events.PlayerLeaveEvent;
import com.lemonclient.api.event.events.Render2DEvent;
import com.lemonclient.api.event.events.Render3DEvent;
import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.event.events.SendMessageEvent;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.chat.NotificationManager;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.player.NameUtil;
import com.lemonclient.api.util.render.RenderUtil;
import com.lemonclient.api.util.world.TimerUtils;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.PeekCmd;
import com.lemonclient.client.command.CommandManager;
import com.lemonclient.client.manager.Manager;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.dev.AntiPush;
import com.lemonclient.client.module.modules.misc.ShulkerBypass;
import com.lemonclient.client.module.modules.qwq.AntiUnicdoe;
import com.lemonclient.mixin.mixins.accessor.AccessorCPacketCustomPayload;
import com.mojang.realmsclient.gui.ChatFormatting;
import io.netty.buffer.Unpooled;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketPlayerListItem.Action;
import net.minecraft.network.play.server.SPacketPlayerListItem.AddPlayerData;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public enum ClientEventManager implements Manager {
   INSTANCE;

   final String LAG_MESSAGE = "āȁ́Ё\u0601܁ࠁँਁଁก༁ခᄁሁጁᐁᔁᘁᜁ᠁ᤁᨁᬁᰁᴁḁἁ ℁∁⌁␁━✁⠁⤁⨁⬁Ⰱⴁ⸁⼁、\u3101㈁㌁㐁㔁㘁㜁㠁㤁㨁㬁㰁㴁㸁㼁䀁䄁䈁䌁䐁䔁䘁䜁䠁䤁䨁䬁䰁䴁丁企倁儁刁匁吁唁嘁圁堁夁威嬁封崁币弁态愁戁持搁攁昁朁栁椁樁欁氁洁渁漁瀁焁爁猁琁甁瘁省码礁稁笁簁紁縁缁老脁舁茁萁蔁蘁蜁蠁褁訁謁谁贁踁輁送鄁鈁錁鐁锁阁霁頁餁騁鬁鰁鴁鸁鼁ꀁꄁꈁꌁꐁꔁꘁ꜁ꠁ꤁ꨁꬁ각괁긁꼁뀁넁눁댁됁딁똁뜁렁뤁먁묁밁봁";
   final Set<Character> lagMessageSet = new HashSet();
   @EventHandler
   private final Listener<PacketEvent.Send> packetSend = new Listener((event) -> {
      if (event.getPacket() instanceof FMLProxyPacket && !Minecraft.func_71410_x().func_71356_B()) {
         event.cancel();
      }

      if (event.getPacket() instanceof CPacketCustomPayload) {
         CPacketCustomPayload packet = (CPacketCustomPayload)event.getPacket();
         if (packet.func_149559_c().equalsIgnoreCase("MC|Brand")) {
            ((AccessorCPacketCustomPayload)packet).setData((new PacketBuffer(Unpooled.buffer())).func_180714_a("vanilla"));
         }
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if (event.getPacket() instanceof SPacketPlayerListItem) {
         SPacketPlayerListItem packet = (SPacketPlayerListItem)event.getPacket();
         Iterator var3;
         AddPlayerData playerData;
         if (packet.func_179768_b() == Action.ADD_PLAYER) {
            var3 = packet.func_179767_a().iterator();

            while(var3.hasNext()) {
               playerData = (AddPlayerData)var3.next();
               if (playerData.func_179962_a().getId() != this.getMinecraft().field_71449_j.func_148256_e().getId()) {
                  (new Thread(() -> {
                     String name = NameUtil.resolveName(playerData.func_179962_a().getId().toString());
                     if (name != null && this.getPlayer() != null && this.getPlayer().field_70173_aa >= 1000) {
                        LemonClient.EVENT_BUS.post(new PlayerJoinEvent(name));
                     }

                  })).start();
               }
            }
         }

         if (packet.func_179768_b() == Action.REMOVE_PLAYER) {
            var3 = packet.func_179767_a().iterator();

            while(var3.hasNext()) {
               playerData = (AddPlayerData)var3.next();
               if (playerData.func_179962_a().getId() != this.getMinecraft().field_71449_j.func_148256_e().getId()) {
                  (new Thread(() -> {
                     String name = NameUtil.resolveName(playerData.func_179962_a().getId().toString());
                     if (name != null && this.getPlayer() != null && this.getPlayer().field_70173_aa >= 1000) {
                        LemonClient.EVENT_BUS.post(new PlayerLeaveEvent(name));
                     }

                  })).start();
               }
            }
         }
      }

      if (event.getPacket() instanceof SPacketTimeUpdate) {
         LemonClient.serverUtil.update();
      }

   }, new Predicate[0]);

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onRenderGameOverlayEvent(Text event) {
      if (event.getType().equals(ElementType.TEXT)) {
         ScaledResolution resolution = new ScaledResolution(Minecraft.func_71410_x());
         Render2DEvent render2DEvent = new Render2DEvent(event.getPartialTicks(), resolution);
         Iterator var4 = ModuleManager.getModules().iterator();

         while(var4.hasNext()) {
            Module module = (Module)var4.next();
            if (module.isEnabled()) {
               this.getProfiler().func_76320_a(module.getName());
               module.onRender2D(render2DEvent);
               this.getProfiler().func_76319_b();
               GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      }

      LemonClient.EVENT_BUS.post(event);
   }

   @SubscribeEvent
   public void onChatReceived(ClientChatReceivedEvent event) {
      int count;
      if (this.lagMessageSet.isEmpty()) {
         for(count = 0; count < "āȁ́Ё\u0601܁ࠁँਁଁก༁ခᄁሁጁᐁᔁᘁᜁ᠁ᤁᨁᬁᰁᴁḁἁ ℁∁⌁␁━✁⠁⤁⨁⬁Ⰱⴁ⸁⼁、\u3101㈁㌁㐁㔁㘁㜁㠁㤁㨁㬁㰁㴁㸁㼁䀁䄁䈁䌁䐁䔁䘁䜁䠁䤁䨁䬁䰁䴁丁企倁儁刁匁吁唁嘁圁堁夁威嬁封崁币弁态愁戁持搁攁昁朁栁椁樁欁氁洁渁漁瀁焁爁猁琁甁瘁省码礁稁笁簁紁縁缁老脁舁茁萁蔁蘁蜁蠁褁訁謁谁贁踁輁送鄁鈁錁鐁锁阁霁頁餁騁鬁鰁鴁鸁鼁ꀁꄁꈁꌁꐁꔁꘁ꜁ꠁ꤁ꨁꬁ각괁긁꼁뀁넁눁댁됁딁똁뜁렁뤁먁묁밁봁".length(); ++count) {
            this.lagMessageSet.add("āȁ́Ё\u0601܁ࠁँਁଁก༁ခᄁሁጁᐁᔁᘁᜁ᠁ᤁᨁᬁᰁᴁḁἁ ℁∁⌁␁━✁⠁⤁⨁⬁Ⰱⴁ⸁⼁、\u3101㈁㌁㐁㔁㘁㜁㠁㤁㨁㬁㰁㴁㸁㼁䀁䄁䈁䌁䐁䔁䘁䜁䠁䤁䨁䬁䰁䴁丁企倁儁刁匁吁唁嘁圁堁夁威嬁封崁币弁态愁戁持搁攁昁朁栁椁樁欁氁洁渁漁瀁焁爁猁琁甁瘁省码礁稁笁簁紁縁缁老脁舁茁萁蔁蘁蜁蠁褁訁謁谁贁踁輁送鄁鈁錁鐁锁阁霁頁餁騁鬁鰁鴁鸁鼁ꀁꄁꈁꌁꐁꔁꘁ꜁ꠁ꤁ꨁꬁ각괁긁꼁뀁넁눁댁됁딁똁뜁렁뤁먁묁밁봁".charAt(count));
         }
      }

      if (!event.getMessage().func_150254_d().contains("{") && !event.getMessage().func_150254_d().contains("}")) {
         if (ModuleManager.isModuleEnabled(AntiUnicdoe.class)) {
            count = 0;
            String text = event.getMessage().func_150254_d();

            for(int i = 0; i < text.length(); ++i) {
               if (this.lagMessageSet.contains(text.charAt(i))) {
                  ++count;
               }
            }

            if (count >= 25) {
               event.setCanceled(true);
               TextComponentString string = new TextComponentString("(lag message)");
               Minecraft.func_71410_x().field_71439_g.func_145747_a(string);
               return;
            }
         }

         LemonClient.EVENT_BUS.post(event);
      } else {
         event.setCanceled(true);
         TextComponentString string = new TextComponentString(event.getMessage().func_150254_d().replace("{", "").replace("}", "").replace("$", "").replace("ldap", ""));
         Minecraft.func_71410_x().field_71439_g.func_145747_a(string);
      }
   }

   @SubscribeEvent
   public void onAttackEntity(AttackEntityEvent event) {
      LemonClient.EVENT_BUS.post(event);
   }

   @SubscribeEvent
   public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
      LemonClient.EVENT_BUS.post(event);
   }

   @SubscribeEvent
   public void onLivingEntityUseItemFinish(Finish event) {
      LemonClient.EVENT_BUS.post(event);
   }

   @SubscribeEvent
   public void onInputUpdate(InputUpdateEvent event) {
      LemonClient.EVENT_BUS.post(event);
   }

   @SubscribeEvent
   public void onLivingDeath(LivingDeathEvent event) {
      LemonClient.EVENT_BUS.post(event);
   }

   @SubscribeEvent
   public void onPlayerPush(PlayerSPPushOutOfBlocksEvent event) {
      if (ModuleManager.isModuleEnabled(AntiPush.class)) {
         event.setCanceled(true);
      }

      LemonClient.EVENT_BUS.post(event);
   }

   @SubscribeEvent
   public void onEntitySpawn(EntityJoinWorldEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof EntityItem) {
         PeekCmd.drop = (EntityItem)entity;
         PeekCmd.metadataTicks = 0;
      }

   }

   @SubscribeEvent
   public void onWorldLoad(Load event) {
      LemonClient.EVENT_BUS.post(event);
   }

   @SubscribeEvent
   public void onWorldUnload(Unload event) {
      LemonClient.EVENT_BUS.post(event);
   }

   @SubscribeEvent
   public void onGuiOpen(GuiOpenEvent event) {
      LemonClient.EVENT_BUS.post(event);
   }

   @SubscribeEvent
   public void onFogColor(FogColors event) {
      LemonClient.EVENT_BUS.post(event);
   }

   @SubscribeEvent
   public void onFogDensity(FogDensity event) {
      LemonClient.EVENT_BUS.post(event);
   }

   @SubscribeEvent
   public void onFov(FOVModifier event) {
      LemonClient.EVENT_BUS.post(event);
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (ModuleManager.isModuleEnabled("Peek") && ShulkerBypass.shulkers) {
         if (event.phase == Phase.END) {
            if (PeekCmd.guiTicks > -1) {
               ++PeekCmd.guiTicks;
            }

            if (PeekCmd.metadataTicks > -1) {
               ++PeekCmd.metadataTicks;
            }
         }

         if (PeekCmd.metadataTicks >= ShulkerBypass.delay) {
            PeekCmd.metadataTicks = -1;
            if (PeekCmd.drop.func_92059_d().func_77973_b() instanceof ItemShulkerBox) {
               MessageBus.sendClientDeleteMessage("New shulker found. use /peek to view its content " + TextFormatting.GREEN + "(" + PeekCmd.drop.func_92059_d().func_82833_r() + ")", Notification.Type.INFO, "Peek", 3);
               PeekCmd.shulker = PeekCmd.drop.func_92059_d();
            }
         }

         if (PeekCmd.guiTicks == 20) {
            PeekCmd.guiTicks = -1;
            Minecraft.func_71410_x().field_71439_g.func_71007_a(PeekCmd.toOpen);
         }
      }

      if (this.getMinecraft().field_71439_g != null && this.getMinecraft().field_71441_e != null) {
         int timerSpeed = (int)TimerUtils.getTimer();
         Iterator var3 = ModuleManager.getModules().iterator();

         while(var3.hasNext()) {
            Module module = (Module)var3.next();

            try {
               if (module.isEnabled()) {
                  ++module.onTickTimer;
                  if (module.onTickTimer >= timerSpeed) {
                     module.onTick();
                     module.onTickTimer = 0;
                  }
               }
            } catch (Exception var10) {
               if (this.getWorld() != null && this.getPlayer() != null) {
                  MessageBus.sendClientPrefixMessage("Disabled " + module.getName() + " due to " + var10, Notification.Type.ERROR);
               }

               module.setEnabled(false);
               StackTraceElement[] var6 = var10.getStackTrace();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  StackTraceElement stack = var6[var8];
                  System.out.println(stack.toString());
               }
            }
         }
      }

      LemonClient.EVENT_BUS.post(event);
   }

   @SubscribeEvent
   public void onUpdate(LivingUpdateEvent event) {
      if (this.getMinecraft().field_71439_g != null && this.getMinecraft().field_71441_e != null) {
         if (event.getEntity().func_130014_f_().field_72995_K && event.getEntityLiving() == this.getPlayer()) {
            int timerSpeed = (int)TimerUtils.getTimer();
            Iterator var3 = ModuleManager.getModules().iterator();

            while(var3.hasNext()) {
               Module module = (Module)var3.next();

               try {
                  if (module.isEnabled()) {
                     ++module.onUpdateTimer;
                     if (module.onUpdateTimer >= timerSpeed) {
                        module.onUpdate();
                        module.onUpdateTimer = 0;
                     }
                  }
               } catch (Exception var10) {
                  if (this.getWorld() != null && this.getPlayer() != null) {
                     MessageBus.sendClientPrefixMessage("Disabled " + module.getName() + " due to " + var10, Notification.Type.ERROR);
                  }

                  module.setEnabled(false);
                  StackTraceElement[] var6 = var10.getStackTrace();
                  int var7 = var6.length;

                  for(int var8 = 0; var8 < var7; ++var8) {
                     StackTraceElement stack = var6[var8];
                     System.out.println(stack.toString());
                  }
               }
            }

            LemonClient.EVENT_BUS.post(event);
         }

      }
   }

   @SubscribeEvent
   public void onWorldRender(RenderWorldLastEvent event) {
      if (!event.isCanceled()) {
         if (this.getMinecraft().field_71439_g != null && this.getMinecraft().field_71441_e != null) {
            this.getProfiler().func_76320_a("lemonclient");
            this.getProfiler().func_76320_a("setup");
            RenderUtil.prepare();
            RenderEvent event1 = new RenderEvent(event.getPartialTicks());
            this.getProfiler().func_76319_b();
            Iterator var3 = ModuleManager.getModules().iterator();

            while(var3.hasNext()) {
               Module module = (Module)var3.next();
               if (module.isEnabled()) {
                  this.getProfiler().func_76320_a(module.getName());
                  module.onWorldRender(event1);
                  this.getProfiler().func_76319_b();
               }
            }

            this.getProfiler().func_76320_a("release");
            RenderUtil.release();
            this.getProfiler().func_76319_b();
            this.getProfiler().func_76319_b();
         }
      }
   }

   @SubscribeEvent
   public void onRender3D(RenderWorldLastEvent event) {
      if (!event.isCanceled()) {
         if (this.getMinecraft().field_71439_g != null && this.getMinecraft().field_71441_e != null) {
            this.getProfiler().func_76320_a("lemonclient");
            this.getProfiler().func_76320_a("setup");
            GlStateManager.func_179090_x();
            GlStateManager.func_179147_l();
            GlStateManager.func_179118_c();
            GlStateManager.func_179120_a(770, 771, 1, 0);
            GlStateManager.func_179103_j(7425);
            GlStateManager.func_179097_i();
            GlStateManager.func_187441_d(1.0F);
            Render3DEvent event2 = new Render3DEvent(event.getPartialTicks());
            this.getProfiler().func_76319_b();
            Iterator var3 = ModuleManager.getModules().iterator();

            while(var3.hasNext()) {
               Module module = (Module)var3.next();
               if (module.isEnabled()) {
                  this.getProfiler().func_76320_a(module.getName());
                  module.onRender3D(event2);
                  this.getProfiler().func_76319_b();
               }
            }

            this.getProfiler().func_76320_a("release");
            GlStateManager.func_187441_d(1.0F);
            GlStateManager.func_179103_j(7424);
            GlStateManager.func_179084_k();
            GlStateManager.func_179141_d();
            GlStateManager.func_179098_w();
            GlStateManager.func_179126_j();
            GlStateManager.func_179089_o();
            GlStateManager.func_179089_o();
            GlStateManager.func_179132_a(true);
            GlStateManager.func_179098_w();
            GlStateManager.func_179147_l();
            GlStateManager.func_179126_j();
            this.getProfiler().func_76319_b();
            this.getProfiler().func_76319_b();
         }
      }
   }

   @SubscribeEvent
   public void onRender(Post event) {
      if (this.getMinecraft().field_71439_g != null && this.getMinecraft().field_71441_e != null) {
         if (event.getType() == ElementType.HOTBAR) {
            Iterator var2 = ModuleManager.getModules().iterator();

            while(var2.hasNext()) {
               Module module = (Module)var2.next();
               if (module.isEnabled()) {
                  module.onRender();
                  NotificationManager.draw();
               }
            }

            LemonClient.INSTANCE.gameSenseGUI.render();
         }

         LemonClient.EVENT_BUS.post(event);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL,
      receiveCanceled = true
   )
   public void onKeyInput(KeyInputEvent event) {
      if (Keyboard.getEventKeyState() && Keyboard.getEventKey() != 0) {
         EntityPlayerSP player = this.getPlayer();
         if (player != null && !player.func_70093_af()) {
            String prefix = CommandManager.getCommandPrefix();
            char typedChar = Keyboard.getEventCharacter();
            if (prefix.length() == 1 && prefix.charAt(0) == typedChar) {
               this.getMinecraft().func_147108_a(new GuiChat(prefix));
            }
         }

         int key = Keyboard.getEventKey();
         if (key != 0) {
            Iterator var7 = ModuleManager.getModules().iterator();

            while(var7.hasNext()) {
               Module module = (Module)var7.next();
               if (module.getBind() == key) {
                  module.toggle();
               }
            }
         }

         LemonClient.INSTANCE.gameSenseGUI.handleKeyEvent(Keyboard.getEventKey());
      }
   }

   @SubscribeEvent
   public void onMouseInput(MouseInputEvent event) {
      if (Mouse.getEventButtonState()) {
         LemonClient.EVENT_BUS.post(event);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onChatSent(ClientChatEvent event) {
      if (event.getMessage().startsWith(CommandManager.getCommandPrefix())) {
         event.setCanceled(true);

         try {
            this.getMinecraft().field_71456_v.func_146158_b().func_146239_a(event.getMessage());
            CommandManager.callCommand(event.getMessage().substring(1), false);
         } catch (Exception var3) {
            var3.printStackTrace();
            MessageBus.sendCommandMessage(ChatFormatting.DARK_RED + "Error: " + var3.getMessage(), true);
         }
      } else {
         SendMessageEvent eventNow = new SendMessageEvent(event.getMessage());
         LemonClient.EVENT_BUS.post(eventNow);
         if (eventNow.isCancelled()) {
            event.setCanceled(true);
         }
      }

   }

   @SubscribeEvent
   public void init(ClientTickEvent event) {
      this.fastest();
   }

   @SubscribeEvent
   public void init(ServerTickEvent event) {
      this.fastest();
   }

   @SubscribeEvent
   public void init(PlayerTickEvent event) {
      this.fastest();
   }

   @SubscribeEvent
   public void init(WorldTickEvent event) {
      this.fastest();
   }

   public void fastest() {
      if (this.getMinecraft().field_71439_g != null && this.getMinecraft().field_71441_e != null) {
         int timerSpeed = (int)TimerUtils.getTimer();
         Iterator var2 = ModuleManager.getModules().iterator();

         while(var2.hasNext()) {
            Module module = (Module)var2.next();

            try {
               if (module.isEnabled()) {
                  ++module.fastTimer;
                  if (module.fastTimer >= timerSpeed) {
                     module.fast();
                     module.fastTimer = 0;
                  }
               }
            } catch (Exception var9) {
               if (this.getWorld() != null && this.getPlayer() != null) {
                  MessageBus.sendClientPrefixMessage("Disabled " + module.getName() + " due to " + var9, Notification.Type.ERROR);
               }

               module.setEnabled(false);
               StackTraceElement[] var5 = var9.getStackTrace();
               int var6 = var5.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  StackTraceElement stack = var5[var7];
                  System.out.println(stack.toString());
               }
            }
         }
      }

   }
}
