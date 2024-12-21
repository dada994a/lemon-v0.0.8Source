package com.lemonclient.client.module.modules.gui;

import com.lemonclient.api.event.events.EntityUseTotemEvent;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.misc.ColorUtil;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.modules.movement.SpeedPlus;
import com.lemonclient.client.module.modules.qwq.AutoEz;
import com.lemonclient.mixin.mixins.accessor.AccessorCPacketCustomPayload;
import io.netty.buffer.Unpooled;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.lwjgl.opengl.Display;

@Module.Declaration(
   name = "Colors",
   category = Category.GUI,
   enabled = true,
   drawn = false,
   priority = 10000
)
public class ColorMain extends Module {
   public static ColorMain INSTANCE;
   public ColorSetting enabledColor = this.registerColor("Main Color", new GSColor(255, 0, 0, 255));
   public DoubleSetting rainbowSpeed = this.registerDouble("Rainbow Speed", 1.0D, 0.1D, 10.0D);
   public ModeSetting rainbowMode = this.registerMode("Rainbow Mode", Arrays.asList("Normal", "Sin", "Tan", "Sec", "CoTan", "CoSec"), "Normal");
   public BooleanSetting customFont = this.registerBoolean("Custom Font", true);
   public BooleanSetting textFont = this.registerBoolean("Custom Text", false);
   public BooleanSetting highlightSelf = this.registerBoolean("Highlight SelfName", false);
   public ModeSetting selfColor;
   public ModeSetting friendColor;
   public ModeSetting enemyColor;
   public ModeSetting chatModuleColor;
   public ModeSetting chatEnableColor;
   public ModeSetting chatDisableColor;
   public ColorSetting Title;
   public ColorSetting Enabled;
   public ColorSetting Disabled;
   public ColorSetting Background;
   public ColorSetting Font;
   public ColorSetting ScrollBar;
   public ColorSetting Highlight;
   public ModeSetting colorModel;
   Color title;
   Color enable;
   Color disable;
   Color background;
   Color font;
   Color scrollBar;
   Color highlight;
   public boolean sneaking;
   public double velocityBoost;
   public List<BlockPos> breakList;
   HashMap<EntityPlayer, BlockPos> list;
   BlockPos lastBreak;
   @EventHandler
   private final Listener<PacketEvent.PostSend> postSendListener;
   @EventHandler
   private final Listener<PacketEvent.Send> packetSend;
   @EventHandler
   private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener;
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener;
   @EventHandler
   public Listener<EntityUseTotemEvent> listListener;

   public ColorMain() {
      this.selfColor = this.registerMode("Self Color", ColorUtil.colors, "Blue");
      this.friendColor = this.registerMode("Friend Color", ColorUtil.colors, "Green");
      this.enemyColor = this.registerMode("Enemy Color", ColorUtil.colors, "Red");
      this.chatModuleColor = this.registerMode("Msg Module", ColorUtil.colors, "Aqua");
      this.chatEnableColor = this.registerMode("Msg Enable", ColorUtil.colors, "Green");
      this.chatDisableColor = this.registerMode("Msg Disable", ColorUtil.colors, "Red");
      this.Title = this.registerColor("Title Color", new GSColor(90, 145, 240));
      this.Enabled = this.registerColor("Enabled Color", new GSColor(90, 145, 240));
      this.Disabled = this.registerColor("Disabled", new GSColor(64, 64, 64));
      this.Background = this.registerColor("BackGround Color", new GSColor(195, 195, 195, 150), true);
      this.Font = this.registerColor("Font Color", new GSColor(255, 255, 255));
      this.ScrollBar = this.registerColor("ScrollBar Color", new GSColor(90, 145, 240));
      this.Highlight = this.registerColor("Highlight Color", new GSColor(0, 0, 240));
      this.colorModel = this.registerMode("Color Model", Arrays.asList("RGB", "HSB"), "HSB");
      this.breakList = new ArrayList();
      this.list = new HashMap();
      this.postSendListener = new Listener((event) -> {
         if (event.getPacket() instanceof CPacketEntityAction) {
            if (((CPacketEntityAction)event.getPacket()).func_180764_b() == Action.START_SNEAKING) {
               this.sneaking = true;
            }

            if (((CPacketEntityAction)event.getPacket()).func_180764_b() == Action.STOP_SNEAKING) {
               this.sneaking = false;
            }
         }

      }, new Predicate[0]);
      this.packetSend = new Listener((event) -> {
         if (event.getPacket() instanceof FMLProxyPacket && !mc.func_71356_B()) {
            event.cancel();
         }

         if (event.getPacket() instanceof CPacketCustomPayload) {
            CPacketCustomPayload packetx = (CPacketCustomPayload)event.getPacket();
            if (packetx.func_149559_c().equalsIgnoreCase("MC|Brand")) {
               ((AccessorCPacketCustomPayload)packetx).setData((new PacketBuffer(Unpooled.buffer())).func_180714_a("vanilla"));
            }
         }

         if (event.getPacket() instanceof CPacketPlayerDigging) {
            CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
            if (packet.func_180762_c() == net.minecraft.network.play.client.CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
               this.lastBreak = packet.func_179715_a();
            }
         }

      }, new Predicate[0]);
      this.onUpdateWalkingPlayerEventListener = new Listener((event) -> {
         if (mc.field_71441_e != null && mc.field_71439_g != null) {
            LemonClient.speedUtil.update();
            LemonClient.positionUtil.updatePosition();
         }
      }, new Predicate[0]);
      this.receiveListener = new Listener((event) -> {
         if (mc.field_71441_e != null && mc.field_71439_g != null && !EntityUtil.isDead(mc.field_71439_g)) {
            if (event.getPacket() instanceof SPacketChat) {
               String message = ((SPacketChat)event.getPacket()).func_148915_c().func_150260_c();
               Matcher matcher = Pattern.compile("<.*?> ").matcher(message);
               String username = "";
               if (matcher.find()) {
                  username = matcher.group();
                  username = username.substring(1, username.length() - 2);
               } else if (message.contains(":")) {
                  int spaceIndex = message.indexOf(" ");
                  if (spaceIndex != -1) {
                     username = message.substring(0, spaceIndex);
                  }
               }

               username = cleanColor(username);
               if (SocialManager.isIgnore(username)) {
                  event.cancel();
               }
            }

            if (event.getPacket() instanceof SPacketBlockBreakAnim) {
               SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim)event.getPacket();
               BlockPos blockPos = packet.func_179821_b();
               EntityPlayer entityPlayer = (EntityPlayer)mc.field_71441_e.func_73045_a(packet.func_148845_c());
               if (entityPlayer == null) {
                  return;
               }

               this.list.put(entityPlayer, blockPos);
            }

            if (event.getPacket() instanceof SPacketEntityVelocity) {
               SPacketEntityVelocity packetx = (SPacketEntityVelocity)event.getPacket();
               Entity entity = mc.field_71441_e.func_73045_a(packetx.field_149417_a);
               if (entity != null && entity == mc.field_71439_g) {
                  this.velocityBoost = (Boolean)SpeedPlus.INSTANCE.sum.getValue() ? this.velocityBoost + Math.hypot((double)((float)packetx.field_149415_b / 8000.0F), (double)((float)packetx.field_149414_d / 8000.0F)) : Math.max(this.velocityBoost, Math.hypot((double)((float)packetx.field_149415_b / 8000.0F), (double)((float)packetx.field_149414_d / 8000.0F)));
               }
            }

         }
      }, new Predicate[0]);
      this.listListener = new Listener((event) -> {
         if (event.getEntity() == mc.field_71439_g && mc.field_71462_r instanceof GuiContainer && !(mc.field_71462_r instanceof GuiInventory)) {
            mc.field_71439_g.func_71053_j();
         }

      }, new Predicate[0]);
      INSTANCE = this;
   }

   public void onDisable() {
      this.enable();
   }

   public void fast() {
      if (this.title != this.Title.getColor() || this.enable != this.Enabled.getColor() || this.disable != this.Disabled.getColor() || this.background != this.Background.getColor() || this.font != this.Font.getColor() || this.scrollBar != this.ScrollBar.getColor() || this.highlight != this.Highlight.getColor()) {
         this.title = this.Title.getColor();
         this.enable = this.Enabled.getColor();
         this.disable = this.Disabled.getColor();
         this.background = this.Background.getColor();
         this.font = this.Font.getColor();
         this.scrollBar = this.ScrollBar.getColor();
         this.highlight = this.Highlight.getColor();
         LemonClient.INSTANCE.gameSenseGUI.refresh();
      }

      if (!(Boolean)AutoEz.INSTANCE.hi.getValue()) {
         AutoEz.INSTANCE.hi.setValue(true);
      }

      this.breakList = new ArrayList();
      this.breakList.add(this.lastBreak);
      List<EntityPlayer> playerList = mc.field_71441_e.field_73010_i;
      Iterator var2 = playerList.iterator();

      while(var2.hasNext()) {
         EntityPlayer player = (EntityPlayer)var2.next();
         if (this.list.containsKey(player)) {
            BlockPos pos = (BlockPos)this.list.get(player);
            this.breakList.add(pos);
         }
      }

   }

   public void onUpdate() {
      if (!Display.getTitle().equals("Lemon Client v0.0.8")) {
         Display.setTitle("Lemon Client v0.0.8");
         LemonClient.setWindowIcon();
      }

      if (!SpeedPlus.INSTANCE.isEnabled() && MotionUtil.moving(mc.field_71439_g)) {
         this.velocityBoost = 0.0D;
      }

   }

   public String highlight(String string) {
      if (string != null && this.isEnabled()) {
         String username = mc.func_110432_I().func_111285_a();
         return string.replace(username, this.getSelfColor() + username).replace(username.toLowerCase(), this.getSelfColor() + username.toLowerCase()).replace(username.toUpperCase(), this.getSelfColor() + username.toUpperCase());
      } else {
         return string;
      }
   }

   public static String cleanColor(String input) {
      return input.replaceAll("(?i)\\u00A7.", "");
   }

   public TextFormatting getSelfColor() {
      return ColorUtil.settingToTextFormatting(this.selfColor);
   }

   public TextFormatting getFriendColor() {
      return ColorUtil.settingToTextFormatting(this.friendColor);
   }

   public TextFormatting getEnemyColor() {
      return ColorUtil.settingToTextFormatting(this.enemyColor);
   }

   public TextFormatting getModuleColor() {
      return ColorUtil.settingToTextFormatting(this.chatModuleColor);
   }

   public TextFormatting getEnabledColor() {
      return ColorUtil.settingToTextFormatting(this.chatEnableColor);
   }

   public TextFormatting getDisabledColor() {
      return ColorUtil.settingToTextFormatting(this.chatDisableColor);
   }

   public GSColor getFriendGSColor() {
      return new GSColor(ColorUtil.settingToColor(this.friendColor));
   }

   public GSColor getEnemyGSColor() {
      return new GSColor(ColorUtil.settingToColor(this.enemyColor));
   }
}
