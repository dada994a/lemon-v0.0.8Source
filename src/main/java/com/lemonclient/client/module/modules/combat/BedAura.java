package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.event.Phase;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerPacket;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.PredictUtil;
import com.lemonclient.api.util.player.RotationUtil;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.util.render.RenderUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.combat.DamageUtil;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.lemonclient.client.module.modules.qwq.AutoEz;
import com.lemonclient.mixin.mixins.accessor.AccessorCPacketVehicleMove;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBed;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Module.Declaration(
   name = "BedAura",
   category = Category.Combat,
   priority = 999
)
public class BedAura extends Module {
   ModeSetting page = this.registerMode("Page", Arrays.asList("Target", "General", "Delay", "Base", "Calc", "SlowFacePlace", "Switch", "Render"), "General");
   BooleanSetting predict = this.registerBoolean("Predict", true, () -> {
      return ((String)this.page.getValue()).equals("Target");
   });
   BooleanSetting selfPredict = this.registerBoolean("Predict Self", true, () -> {
      return ((String)this.page.getValue()).equals("Target");
   });
   DoubleSetting resetRotate = this.registerDouble("Reset Yaw Difference", 15.0D, 0.0D, 180.0D, () -> {
      return ((String)this.page.getValue()).equals("Target");
   });
   BooleanSetting detect = this.registerBoolean("Detect Ping", false, () -> {
      return ((String)this.page.getValue()).equals("Target");
   });
   IntegerSetting startTick = this.registerInteger("Start Tick", 2, 0, 30, () -> {
      return ((String)this.page.getValue()).equals("Target");
   });
   IntegerSetting addTick = this.registerInteger("Add Tick", 4, 0, 10, () -> {
      return ((String)this.page.getValue()).equals("Target");
   });
   IntegerSetting tickPredict = this.registerInteger("Max Predict Ticks", 10, 0, 30, () -> {
      return ((String)this.page.getValue()).equals("Target") && !(Boolean)this.detect.getValue();
   });
   BooleanSetting calculateYPredict = this.registerBoolean("Calculate Y Predict", true, () -> {
      return ((String)this.page.getValue()).equals("Target");
   });
   IntegerSetting startDecrease = this.registerInteger("Start Decrease", 39, 0, 200, () -> {
      return (Boolean)this.calculateYPredict.getValue() && ((String)this.page.getValue()).equals("Target");
   });
   IntegerSetting exponentStartDecrease = this.registerInteger("Exponent Start", 2, 1, 5, () -> {
      return (Boolean)this.calculateYPredict.getValue() && ((String)this.page.getValue()).equals("Target");
   });
   IntegerSetting decreaseY = this.registerInteger("Decrease Y", 2, 1, 5, () -> {
      return (Boolean)this.calculateYPredict.getValue() && ((String)this.page.getValue()).equals("Target");
   });
   IntegerSetting exponentDecreaseY = this.registerInteger("Exponent Decrease Y", 1, 1, 3, () -> {
      return (Boolean)this.calculateYPredict.getValue() && ((String)this.page.getValue()).equals("Target");
   });
   BooleanSetting splitXZ = this.registerBoolean("Split XZ", true, () -> {
      return ((String)this.page.getValue()).equals("Target");
   });
   BooleanSetting manualOutHole = this.registerBoolean("Manual Out Hole", false, () -> {
      return ((String)this.page.getValue()).equals("Target");
   });
   BooleanSetting aboveHoleManual = this.registerBoolean("Above Hole Manual", false, () -> {
      return (Boolean)this.manualOutHole.getValue() && ((String)this.page.getValue()).equals("Target");
   });
   BooleanSetting stairPredict = this.registerBoolean("Stair Predict", false, () -> {
      return ((String)this.page.getValue()).equals("Target");
   });
   IntegerSetting nStair = this.registerInteger("N Stair", 2, 1, 4, () -> {
      return (Boolean)this.stairPredict.getValue() && ((String)this.page.getValue()).equals("Target");
   });
   DoubleSetting speedActivationStair = this.registerDouble("Speed Activation Stair", 0.3D, 0.0D, 1.0D, () -> {
      return (Boolean)this.stairPredict.getValue() && ((String)this.page.getValue()).equals("Target");
   });
   ModeSetting targetMode = this.registerMode("Target", Arrays.asList("Nearest", "Damage", "Health", "Smart"), "Nearest", () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   DoubleSetting smartHealth = this.registerDouble("Smart Health", 16.0D, 0.0D, 36.0D, () -> {
      return ((String)this.page.getValue()).equals("General") && ((String)this.targetMode.getValue()).equals("Smart");
   });
   BooleanSetting monster = this.registerBoolean("Monsters", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting neutral = this.registerBoolean("Neutrals", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting animal = this.registerBoolean("Animals", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   ModeSetting mode = this.registerMode("Mode", Arrays.asList("PlaceBreak", "BreakPlace", "Test"), "PlaceBreak", () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting debug = this.registerBoolean("Debug", false, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting packetPlace = this.registerBoolean("Packet Place", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting placeSwing = this.registerBoolean("Place Swing", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting breakSwing = this.registerBoolean("Break Swing", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting packetSwing = this.registerBoolean("Packet Swing", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting highVersion = this.registerBoolean("1.13", true, () -> {
      return ((String)this.page.getValue()).equals("Base");
   });
   BooleanSetting placeInAir = this.registerBoolean("Place In Air", true, () -> {
      return ((String)this.page.getValue()).equals("Base");
   });
   BooleanSetting base = this.registerBoolean("Place Base", true, () -> {
      return ((String)this.page.getValue()).equals("Base") && !(Boolean)this.highVersion.getValue();
   });
   BooleanSetting allPossible = this.registerBoolean("Calc All Possible", true, () -> {
      return ((String)this.page.getValue()).equals("Base") && (Boolean)this.base.getValue() && !(Boolean)this.highVersion.getValue();
   });
   BooleanSetting detectBreak = this.registerBoolean("Detect Break", true, () -> {
      return ((String)this.page.getValue()).equals("Base") && (Boolean)this.base.getValue() && !(Boolean)this.highVersion.getValue();
   });
   BooleanSetting packetBase = this.registerBoolean("Packet Base Place", true, () -> {
      return ((String)this.page.getValue()).equals("Base") && (Boolean)this.base.getValue() && !(Boolean)this.highVersion.getValue();
   });
   BooleanSetting baseSwing = this.registerBoolean("Base Swing", true, () -> {
      return ((String)this.page.getValue()).equals("Base") && (Boolean)this.base.getValue() && !(Boolean)this.highVersion.getValue();
   });
   DoubleSetting toggleDmg = this.registerDouble("Toggle Damage", 8.0D, 0.0D, 36.0D, () -> {
      return ((String)this.page.getValue()).equals("Base") && (Boolean)this.base.getValue() && !(Boolean)this.highVersion.getValue();
   });
   IntegerSetting baseDelay = this.registerInteger("Base Delay", 0, 0, 1000, () -> {
      return ((String)this.page.getValue()).equals("Base") && (Boolean)this.base.getValue() && !(Boolean)this.highVersion.getValue();
   });
   DoubleSetting baseMinDmg = this.registerDouble("Base MinDmg", 8.0D, 0.0D, 36.0D, () -> {
      return ((String)this.page.getValue()).equals("Base") && (Boolean)this.base.getValue() && !(Boolean)this.highVersion.getValue();
   });
   DoubleSetting maxY = this.registerDouble("Max Y", 1.0D, 0.0D, 3.0D, () -> {
      return ((String)this.page.getValue()).equals("Base") && (Boolean)this.base.getValue() && !(Boolean)this.highVersion.getValue();
   });
   DoubleSetting maxSpeed = this.registerDouble("Max Target Speed", 10.0D, 0.0D, 50.0D, () -> {
      return ((String)this.page.getValue()).equals("Base") && (Boolean)this.base.getValue() && !(Boolean)this.highVersion.getValue();
   });
   IntegerSetting calcDelay = this.registerInteger("Calc Delay", 0, 0, 1000, () -> {
      return ((String)this.page.getValue()).equals("Delay");
   });
   IntegerSetting updateDelay = this.registerInteger("Update Delay", 0, 0, 1000, () -> {
      return ((String)this.page.getValue()).equals("Delay");
   });
   IntegerSetting placeDelay = this.registerInteger("Place Delay", 0, 0, 1000, () -> {
      return ((String)this.page.getValue()).equals("Delay");
   });
   IntegerSetting breakDelay = this.registerInteger("Break Delay", 0, 0, 1000, () -> {
      return ((String)this.page.getValue()).equals("Delay");
   });
   IntegerSetting stuckPlaceDelay = this.registerInteger("Stuck Place Delay", 0, 0, 1000, () -> {
      return ((String)this.page.getValue()).equals("Delay") && ((String)this.mode.getValue()).equals("Test");
   });
   IntegerSetting stuckBreakDelay = this.registerInteger("Stuck Break Delay", 0, 0, 1000, () -> {
      return ((String)this.page.getValue()).equals("Delay") && ((String)this.mode.getValue()).equals("Test");
   });
   DoubleSetting range = this.registerDouble("Place Range", 5.0D, 0.0D, 10.0D, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   DoubleSetting yRange = this.registerDouble("Y Range", 2.5D, 0.0D, 10.0D, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   IntegerSetting enemyRange = this.registerInteger("Enemy Range", 10, 0, 16, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   IntegerSetting maxEnemies = this.registerInteger("Max Calc Enemies", 5, 0, 25, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   ModeSetting handMode = this.registerMode("Hand", Arrays.asList("Main", "Off", "Auto"), "Auto", () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   BooleanSetting autorotate = this.registerBoolean("Auto Rotate", true, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   BooleanSetting pause = this.registerBoolean("Pause While Burrow", false, () -> {
      return ((String)this.page.getValue()).equals("Calc") && (Boolean)this.autorotate.getValue();
   });
   BooleanSetting pitch = this.registerBoolean("Pitch Down", true, () -> {
      return ((String)this.page.getValue()).equals("Calc") && (Boolean)this.autorotate.getValue();
   });
   DoubleSetting minDmg = this.registerDouble("Min Damage", 8.0D, 0.0D, 36.0D, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   BooleanSetting ignore = this.registerBoolean("Ignore Self Dmg", false, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   DoubleSetting maxSelfDmg = this.registerDouble("Max Self Dmg", 10.0D, 1.0D, 36.0D, () -> {
      return ((String)this.page.getValue()).equals("Calc") && !(Boolean)this.ignore.getValue();
   });
   BooleanSetting suicide = this.registerBoolean("Anti Suicide", true, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   DoubleSetting balance = this.registerDouble("Health Balance", 2.5D, 0.0D, 10.0D, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   IntegerSetting facePlaceValue = this.registerInteger("FacePlace HP", 8, 0, 36, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   DoubleSetting fpMinDmg = this.registerDouble("FP Min Damage", 1.0D, 0.0D, 36.0D, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   BooleanSetting forcePlace = this.registerBoolean("Force Place", false, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   BooleanSetting autoSwitch = this.registerBoolean("Auto Switch", true, () -> {
      return ((String)this.page.getValue()).equals("Switch");
   });
   BooleanSetting update = this.registerBoolean("Update", true, () -> {
      return ((String)this.page.getValue()).equals("Switch");
   });
   BooleanSetting silentSwitch = this.registerBoolean("Switch Back", true, () -> {
      return ((String)this.page.getValue()).equals("Switch") && (Boolean)this.autoSwitch.getValue();
   });
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true, () -> {
      return ((String)this.page.getValue()).equals("Switch");
   });
   BooleanSetting refill = this.registerBoolean("Refill Beds", true, () -> {
      return ((String)this.page.getValue()).equals("Switch");
   });
   ModeSetting clickMode = this.registerMode("Click Mode", Arrays.asList("Quick", "Swap", "Pickup"), "Quick", () -> {
      return ((String)this.page.getValue()).equals("Switch") && (Boolean)this.refill.getValue();
   });
   ModeSetting refillMode = this.registerMode("Refill Mode", Arrays.asList("All", "Only"), "All", () -> {
      return ((String)this.page.getValue()).equals("Switch") && (Boolean)this.refill.getValue();
   });
   IntegerSetting slotS = this.registerInteger("Slot", 1, 1, 9, () -> {
      return ((String)this.page.getValue()).equals("Switch") && (Boolean)this.refill.getValue();
   });
   BooleanSetting force = this.registerBoolean("Force Refill", false, () -> {
      return ((String)this.page.getValue()).equals("Switch") && (Boolean)this.refill.getValue();
   });
   BooleanSetting slowFP = this.registerBoolean("Slow Face Place", true, () -> {
      return ((String)this.page.getValue()).equals("SlowFacePlace");
   });
   IntegerSetting slowPlaceDelay = this.registerInteger("SlowFP Place Delay", 500, 0, 1000, () -> {
      return (Boolean)this.slowFP.getValue() && ((String)this.page.getValue()).equals("SlowFacePlace");
   });
   IntegerSetting slowBreakDelay = this.registerInteger("SlowFP Break Delay", 500, 0, 1000, () -> {
      return (Boolean)this.slowFP.getValue() && ((String)this.page.getValue()).equals("SlowFacePlace");
   });
   DoubleSetting slowMinDmg = this.registerDouble("SlowFP Min Dmg", 0.05D, 0.0D, 36.0D, () -> {
      return (Boolean)this.slowFP.getValue() && ((String)this.page.getValue()).equals("SlowFacePlace");
   });
   BooleanSetting showDamage = this.registerBoolean("Render Dmg", true, () -> {
      return ((String)this.page.getValue()).equals("Render");
   });
   BooleanSetting showSelfDamage = this.registerBoolean("Self Dmg", true, () -> {
      return ((String)this.page.getValue()).equals("Render") && (Boolean)this.showDamage.getValue();
   });
   ColorSetting color = this.registerColor("Hand Color", new GSColor(255, 0, 0, 50), () -> {
      return ((String)this.page.getValue()).equals("Render");
   });
   ColorSetting color2 = this.registerColor("Base Color", new GSColor(0, 255, 0, 50), () -> {
      return ((String)this.page.getValue()).equals("Render");
   });
   BooleanSetting gradient = this.registerBoolean("Gradient", true, () -> {
      return ((String)this.page.getValue()).equals("Render");
   });
   IntegerSetting alpha = this.registerInteger("Alpha", 60, 0, 255, () -> {
      return ((String)this.page.getValue()).equals("Render");
   });
   BooleanSetting outline = this.registerBoolean("Outline", true, () -> {
      return ((String)this.page.getValue()).equals("Render");
   });
   IntegerSetting outAlpha = this.registerInteger("Outline Alpha", 120, 0, 255, () -> {
      return ((String)this.page.getValue()).equals("Render") && (Boolean)this.outline.getValue();
   });
   IntegerSetting width = this.registerInteger("Width", 1, 1, 10, () -> {
      return ((String)this.page.getValue()).equals("Render") && (Boolean)this.outline.getValue();
   });
   BooleanSetting anime = this.registerBoolean("Animation", true, () -> {
      return ((String)this.page.getValue()).equals("Render");
   });
   DoubleSetting movingPlaceSpeed = this.registerDouble("Moving Speed", 0.1D, 0.01D, 0.5D, () -> {
      return ((String)this.page.getValue()).equals("Render") && (Boolean)this.anime.getValue();
   });
   BooleanSetting reset = this.registerBoolean("Reset", true, () -> {
      return ((String)this.page.getValue()).equals("Render") && (Boolean)this.anime.getValue();
   });
   BooleanSetting fade = this.registerBoolean("Fade", true, () -> {
      return ((String)this.page.getValue()).equals("Render");
   });
   IntegerSetting fadeAlpha = this.registerInteger("Fade Alpha", 50, 0, 255, () -> {
      return ((String)this.page.getValue()).equals("Render") && (Boolean)this.fade.getValue();
   });
   IntegerSetting lifeTime = this.registerInteger("Fade Time", 3000, 0, 5000, () -> {
      return ((String)this.page.getValue()).equals("Render") && (Boolean)this.fade.getValue();
   });
   BooleanSetting renderTest = this.registerBoolean("Render Test", false, () -> {
      return ((String)this.page.getValue()).equals("Render");
   });
   ModeSetting hudDisplay = this.registerMode("HUD", Arrays.asList("Target", "Damage", "Both", "None"), "None", () -> {
      return ((String)this.page.getValue()).equals("Render");
   });
   BooleanSetting hudSelfDamage = this.registerBoolean("Show Self Damage", false, () -> {
      return ((String)this.page.getValue()).equals("Render") && (((String)this.hudDisplay.getValue()).equals("Damage") || ((String)this.hudDisplay.getValue()).equals("Both"));
   });
   BedAura.managerClassRenderBlocks managerClassRenderBlocks = new BedAura.managerClassRenderBlocks();
   HashMap<EntityPlayer, BedAura.MoveRotation> playerSpeed = new HashMap();
   BedAura.EntityInfo target = null;
   BlockPos headPos;
   BlockPos basePos;
   boolean canBasePlace;
   boolean burrow;
   float damage;
   float selfDamage;
   String face;
   Vec3d movingPlaceNow = new Vec3d(-1.0D, -1.0D, -1.0D);
   Vec3d movingPosNow = new Vec3d(-1.0D, -1.0D, -1.0D);
   BlockPos lastBestPlace = null;
   BlockPos lastBestPos = null;
   Timing basetiming = new Timing();
   Timing calctiming = new Timing();
   Timing placetiming = new Timing();
   Timing breaktiming = new Timing();
   Timing updatetiming = new Timing();
   EnumHand hand;
   int slot;
   int maxPredict;
   Vec2f rotation;
   BlockPos[] sides = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1)};
   int nowSlot;
   @EventHandler
   private final Listener<PacketEvent.Send> postSendListener = new Listener((event) -> {
      if (event.getPacket() instanceof CPacketHeldItemChange) {
         this.nowSlot = ((CPacketHeldItemChange)event.getPacket()).func_149614_c();
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.Send> sendListener = new Listener((event) -> {
      if (this.rotation != null) {
         if (event.getPacket() instanceof Rotation) {
            ((Rotation)event.getPacket()).field_149476_e = this.rotation.field_189982_i;
            if ((Boolean)this.pitch.getValue()) {
               ((Rotation)event.getPacket()).field_149473_f = 90.0F;
            }
         }

         if (event.getPacket() instanceof PositionRotation) {
            ((PositionRotation)event.getPacket()).field_149476_e = this.rotation.field_189982_i;
            if ((Boolean)this.pitch.getValue()) {
               ((PositionRotation)event.getPacket()).field_149473_f = 90.0F;
            }
         }

         if (event.getPacket() instanceof CPacketVehicleMove) {
            ((AccessorCPacketVehicleMove)event.getPacket()).setYaw(this.rotation.field_189982_i);
         }
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener((event) -> {
      if (this.rotation != null && event.getPhase() == Phase.PRE) {
         PlayerPacket packet = new PlayerPacket(this, new Vec2f(this.rotation.field_189982_i, (Boolean)this.pitch.getValue() ? 90.0F : PlayerPacketManager.INSTANCE.getServerSideRotation().field_189983_j));
         PlayerPacketManager.INSTANCE.addPacket(packet);
      }
   }, new Predicate[0]);

   public void onUpdate() {
      if (mc.field_71439_g != null && mc.field_71441_e != null && !EntityUtil.isDead(mc.field_71439_g) && !this.inNether()) {
         Iterator var1 = mc.field_71441_e.field_73010_i.iterator();

         while(var1.hasNext()) {
            EntityPlayer player = (EntityPlayer)var1.next();
            if (!(mc.field_71439_g.func_70068_e(player) > (double)((Integer)this.enemyRange.getValue() * (Integer)this.enemyRange.getValue()))) {
               double lastYaw = 512.0D;
               int tick = (Integer)this.startTick.getValue();
               if (this.playerSpeed.get(player) != null) {
                  BedAura.MoveRotation info = (BedAura.MoveRotation)this.playerSpeed.get(player);
                  lastYaw = info.yaw;
                  tick = info.tick + (Integer)this.addTick.getValue();
               }

               if (tick > this.maxPredict) {
                  tick = this.maxPredict;
               }

               this.playerSpeed.put(player, new BedAura.MoveRotation(player, lastYaw, tick));
            }
         }

         this.calc();
      } else {
         this.target = null;
         this.headPos = this.basePos = null;
         this.damage = this.selfDamage = 0.0F;
         this.rotation = null;
      }
   }

   public void fast() {
      if (mc.field_71439_g != null && mc.field_71441_e != null && !EntityUtil.isDead(mc.field_71439_g) && !this.inNether()) {
         if (this.updatetiming.passedMs((long)(Integer)this.updateDelay.getValue())) {
            this.updatetiming.reset();
            if (!(Boolean)this.pause.getValue()) {
               this.burrow = false;
            } else {
               BlockPos pos = PlayerUtil.getPlayerFloorPos();
               this.burrow = this.isBurrow(pos) && !this.isBurrow(pos.func_177984_a());
            }

            this.maxPredict = (Boolean)this.detect.getValue() ? (int)(mc.func_147104_D() == null ? 0L : mc.func_147104_D().field_78844_e * 2L / 50L) : (Integer)this.tickPredict.getValue();
            this.managerClassRenderBlocks.update((Integer)this.lifeTime.getValue());
            if ((Boolean)this.base.getValue() && this.basetiming.passedMs((long)(Integer)this.baseDelay.getValue())) {
               this.canBasePlace = true;
               this.basetiming.reset();
            }
         }

         this.bedaura();
      }
   }

   private boolean isBurrow(BlockPos pos) {
      if (!mc.field_71439_g.field_70121_D.func_72326_a(mc.field_71441_e.func_180495_p(pos).func_185918_c(mc.field_71441_e, pos))) {
         return false;
      } else {
         Block block = BlockUtil.getBlock(pos);
         return block == Blocks.field_150343_Z || block == Blocks.field_150357_h || block == Blocks.field_150477_bB;
      }
   }

   private void bedaura() {
      if (!(Boolean)this.renderTest.getValue() && this.headPos != null && this.basePos != null) {
         if (this.isBed(this.headPos) || this.isBed(this.basePos)) {
            this.breakBed(false);
         }

         if (!((String)this.mode.getValue()).equals("PlaceBreak") && this.target.defaultPlayer != null) {
            if (((String)this.mode.getValue()).equals("BreakPlace")) {
               this.breakBed(false);
               this.place(false);
            } else if (this.stuck(this.target)) {
               if ((Boolean)this.debug.getValue()) {
                  MessageBus.sendDeleteMessage("true", "ba", 1);
               }

               this.breakBed(true);
               this.place(true);
            } else {
               if ((Boolean)this.debug.getValue()) {
                  MessageBus.sendDeleteMessage("false", "ba", 1);
               }

               this.place(false);
               this.breakBed(false);
            }
         } else {
            this.place(false);
            this.breakBed(false);
         }

      }
   }

   private void calc() {
      if (this.calctiming.passedMs((long)(Integer)this.calcDelay.getValue())) {
         this.calctiming.reset();
         this.target = null;
         this.headPos = this.basePos = null;
         this.damage = this.selfDamage = 0.0F;
         this.rotation = null;
         boolean offhand = !((String)this.handMode.getValue()).equals("Main") && mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151104_aV;
         if (!offhand && !((String)this.handMode.getValue()).equals("Off")) {
            if ((Boolean)this.refill.getValue()) {
               this.refill_bed();
            }

            this.slot = BurrowUtil.findHotbarBlock(ItemBed.class);
            if (this.slot == -1) {
               return;
            }
         }

         this.hand = offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
         BedAura.EntityInfo self = new BedAura.EntityInfo(mc.field_71439_g, (Boolean)this.selfPredict.getValue());
         BedAura.PlaceInfo placeInfo = this.getPlaceInfo(self, this.findBlocksExcluding((Boolean)this.highVersion.getValue() || (Boolean)this.base.getValue() && this.canBasePlace));
         if (placeInfo == null) {
            List<Entity> entityList = new ArrayList();
            Iterator var5 = mc.field_71441_e.field_72996_f.iterator();

            while(true) {
               if (!var5.hasNext()) {
                  BedAura.EntityInfo entity = this.getNearestEntity(entityList);
                  if (entity == null) {
                     if ((Boolean)this.reset.getValue()) {
                        this.lastBestPlace = null;
                        this.lastBestPos = null;
                        this.movingPlaceNow = new Vec3d(-1.0D, -1.0D, -1.0D);
                        this.movingPosNow = new Vec3d(-1.0D, -1.0D, -1.0D);
                     }

                     return;
                  }

                  placeInfo = this.calculatePlacement(entity, self, this.findBlocksExcluding((Boolean)this.highVersion.getValue()));
                  this.target = placeInfo.target;
                  break;
               }

               Entity entity = (Entity)var5.next();
               if (!(mc.field_71439_g.func_70032_d(entity) > (float)(Integer)this.enemyRange.getValue()) && !EntityUtil.isDead(entity)) {
                  if ((Boolean)this.monster.getValue() && EntityUtil.isMobAggressive(entity)) {
                     entityList.add(entity);
                  }

                  if ((Boolean)this.neutral.getValue() && EntityUtil.isNeutralMob(entity)) {
                     entityList.add(entity);
                  }

                  if ((Boolean)this.animal.getValue() && EntityUtil.isPassive(entity)) {
                     entityList.add(entity);
                  }
               }
            }
         } else {
            this.target = placeInfo.target;
            if (ModuleManager.isModuleEnabled("AutoEz")) {
               AutoEz.INSTANCE.addTargetedPlayer(this.target.defaultPlayer.func_70005_c_());
            }

            if ((Boolean)this.base.getValue() && placeInfo.basePos != null) {
               this.canBasePlace = false;
               int obbySlot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
               int oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
               boolean same = obbySlot == oldSlot;
               if (!same) {
                  this.switchTo(obbySlot);
                  BurrowUtil.placeBlock(placeInfo.basePos, EnumHand.MAIN_HAND, false, (Boolean)this.packetBase.getValue(), false, (Boolean)this.baseSwing.getValue());
                  this.switchTo(oldSlot);
               } else {
                  BurrowUtil.placeBlock(placeInfo.basePos, EnumHand.MAIN_HAND, false, (Boolean)this.packetBase.getValue(), false, (Boolean)this.baseSwing.getValue());
               }
            }
         }

         BlockPos bedPos = placeInfo.placePos;
         if (bedPos == null) {
            return;
         }

         this.damage = placeInfo.damage;
         this.selfDamage = placeInfo.selfDamage;
         this.headPos = bedPos;
         switch(RotationUtil.getFacing((double)PlayerPacketManager.INSTANCE.getServerSideRotation().field_189982_i)) {
         case SOUTH:
            this.face = "SOUTH";
            this.rotation = new Vec2f(0.0F, 90.0F);
            bedPos = new BlockPos(this.headPos.field_177962_a, this.headPos.field_177960_b, this.headPos.field_177961_c - 1);
            break;
         case WEST:
            this.face = "WEST";
            this.rotation = new Vec2f(90.0F, 90.0F);
            bedPos = new BlockPos(this.headPos.field_177962_a + 1, this.headPos.field_177960_b, this.headPos.field_177961_c);
            break;
         case NORTH:
            this.face = "NORTH";
            this.rotation = new Vec2f(180.0F, 90.0F);
            bedPos = new BlockPos(this.headPos.field_177962_a, this.headPos.field_177960_b, this.headPos.field_177961_c + 1);
            break;
         case EAST:
            this.face = "EAST";
            this.rotation = new Vec2f(-90.0F, 90.0F);
            bedPos = new BlockPos(this.headPos.field_177962_a - 1, this.headPos.field_177960_b, this.headPos.field_177961_c);
         }

         if (!this.block(bedPos, true, true)) {
            if (!(Boolean)this.autorotate.getValue() || this.burrow) {
               this.target = null;
               this.headPos = this.basePos = null;
               this.damage = this.selfDamage = 0.0F;
               this.rotation = null;
               return;
            }

            if (this.block(this.headPos.func_177974_f(), true, true)) {
               this.face = "WEST";
               this.rotation = new Vec2f(90.0F, 90.0F);
               bedPos = new BlockPos(this.headPos.field_177962_a + 1, this.headPos.field_177960_b, this.headPos.field_177961_c);
            } else if (this.block(this.headPos.func_177978_c(), true, true)) {
               this.face = "SOUTH";
               this.rotation = new Vec2f(0.0F, 90.0F);
               bedPos = new BlockPos(this.headPos.field_177962_a, this.headPos.field_177960_b, this.headPos.field_177961_c - 1);
            } else if (this.block(this.headPos.func_177976_e(), true, true)) {
               this.face = "EAST";
               this.rotation = new Vec2f(-90.0F, 90.0F);
               bedPos = new BlockPos(this.headPos.field_177962_a - 1, this.headPos.field_177960_b, this.headPos.field_177961_c);
            } else {
               if (!this.block(this.headPos.func_177968_d(), true, true)) {
                  this.target = null;
                  this.headPos = this.basePos = null;
                  this.damage = this.selfDamage = 0.0F;
                  this.rotation = null;
                  return;
               }

               this.face = "NORTH";
               this.rotation = new Vec2f(180.0F, 90.0F);
               bedPos = new BlockPos(this.headPos.field_177962_a, this.headPos.field_177960_b, this.headPos.field_177961_c + 1);
            }
         }

         this.headPos = this.headPos.func_177984_a();
         this.basePos = bedPos.func_177984_a();
      }

   }

   private void place(boolean stuck) {
      if (this.placetiming.passedMs((long)this.getPlaceDelay(stuck))) {
         BlockPos neighbour = this.basePos.func_177977_b();
         EnumFacing opposite = EnumFacing.DOWN.func_176734_d();
         Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
         if (BlockUtil.blackList.contains(mc.field_71441_e.func_180495_p(neighbour).func_177230_c()) && !ColorMain.INSTANCE.sneaking) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
         }

         this.run(() -> {
            if ((Boolean)this.packetPlace.getValue()) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(neighbour, EnumFacing.UP, this.hand, 0.5F, 1.0F, 0.5F));
            } else {
               mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, neighbour, EnumFacing.UP, hitVec, this.hand);
            }

         }, this.slot);
         if ((Boolean)this.placeSwing.getValue()) {
            this.swing(this.hand);
         }

         this.placetiming.reset();
      }

   }

   private void run(Runnable runnable, int slot) {
      if (this.hand == EnumHand.OFF_HAND) {
         runnable.run();
      } else {
         if (slot != this.nowSlot) {
            if ((Boolean)this.autoSwitch.getValue()) {
               int oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
               this.switchTo(slot);
               runnable.run();
               if ((Boolean)this.silentSwitch.getValue()) {
                  this.switchTo(oldSlot);
               }
            }
         } else {
            runnable.run();
         }

      }
   }

   private void breakBed(boolean stuck) {
      if (this.breaktiming.passedMs((long)this.getBreakDelay(stuck))) {
         EnumFacing side = EnumFacing.UP;
         Vec3d facing = this.getHitVecOffset(side);
         if (((ColorMain)ModuleManager.getModule(ColorMain.class)).sneaking) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
         }

         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(this.basePos, side, this.hand, (float)facing.field_72450_a, (float)facing.field_72448_b, (float)facing.field_72449_c));
         if (this.isBed(this.headPos) && !this.isBed(this.basePos)) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(this.headPos, side, this.hand, (float)facing.field_72450_a, (float)facing.field_72448_b, (float)facing.field_72449_c));
         }

         if ((Boolean)this.breakSwing.getValue()) {
            this.swing(this.hand);
         }

         this.breaktiming.reset();
      }

   }

   private BedAura.PlaceInfo getPlaceInfo(BedAura.EntityInfo self, List<BlockPos> posList) {
      BedAura.PlaceInfo placeInfo = null;
      List<EntityPlayer> playerList = PlayerUtil.getNearPlayers((double)(Integer)this.enemyRange.getValue(), (Integer)this.maxEnemies.getValue());
      String var5 = (String)this.targetMode.getValue();
      byte var6 = -1;
      switch(var5.hashCode()) {
      case -2137395588:
         if (var5.equals("Health")) {
            var6 = 2;
         }
         break;
      case -804534210:
         if (var5.equals("Nearest")) {
            var6 = 0;
         }
         break;
      case 79996329:
         if (var5.equals("Smart")) {
            var6 = 3;
         }
         break;
      case 2039707535:
         if (var5.equals("Damage")) {
            var6 = 1;
         }
      }

      Iterator var8;
      EntityPlayer player;
      BedAura.EntityInfo player;
      EntityPlayer entityPlayer;
      Iterator var21;
      switch(var6) {
      case 0:
         EntityPlayer entityPlayer = (EntityPlayer)playerList.stream().min(Comparator.comparing((p) -> {
            return mc.field_71439_g.func_70032_d(p);
         })).orElse((Object)null);
         if (entityPlayer != null) {
            BedAura.EntityInfo player = new BedAura.EntityInfo(entityPlayer, (Boolean)this.predict.getValue());
            placeInfo = this.calculateBestPlacement(player, self, posList);
         }
         break;
      case 1:
         BedAura.PlaceInfo best = null;
         var8 = playerList.iterator();

         while(true) {
            BedAura.PlaceInfo info;
            do {
               do {
                  if (!var8.hasNext()) {
                     placeInfo = best;
                     return placeInfo;
                  }

                  player = (EntityPlayer)var8.next();
               } while(player == null);

               player = new BedAura.EntityInfo(player, (Boolean)this.predict.getValue());
               info = this.calculateBestPlacement(player, self, posList);
            } while(best != null && !(info.damage > best.damage));

            best = info;
         }
      case 2:
         double health = 37.0D;
         player = null;
         var21 = playerList.iterator();

         while(true) {
            do {
               if (!var21.hasNext()) {
                  if (player != null) {
                     placeInfo = this.calculateBestPlacement(new BedAura.EntityInfo(player, (Boolean)this.predict.getValue()), self, posList);
                  }

                  return placeInfo;
               }

               entityPlayer = (EntityPlayer)var21.next();
            } while(player != null && !(health > (double)(entityPlayer.func_110143_aJ() + entityPlayer.func_110139_bj())));

            player = entityPlayer;
            health = (double)(entityPlayer.func_110143_aJ() + entityPlayer.func_110139_bj());
         }
      case 3:
         List<EntityPlayer> players = new ArrayList();
         var8 = playerList.iterator();

         while(var8.hasNext()) {
            player = (EntityPlayer)var8.next();
            if ((Double)this.smartHealth.getValue() >= (double)(player.func_110143_aJ() + player.func_110139_bj())) {
               players.add(player);
            }
         }

         EntityPlayer target = (EntityPlayer)players.stream().min(Comparator.comparing((p) -> {
            return p.func_110143_aJ() + p.func_110139_bj();
         })).orElse((Object)null);
         BedAura.PlaceInfo best = null;
         if (target != null) {
            player = new BedAura.EntityInfo(target, (Boolean)this.predict.getValue());
            best = this.calculateBestPlacement(player, self, posList);
         }

         if (best == null) {
            var21 = playerList.iterator();

            label87:
            while(true) {
               BedAura.PlaceInfo info;
               do {
                  do {
                     if (!var21.hasNext()) {
                        break label87;
                     }

                     entityPlayer = (EntityPlayer)var21.next();
                  } while(entityPlayer == null);

                  BedAura.EntityInfo player = new BedAura.EntityInfo(entityPlayer, (Boolean)this.predict.getValue());
                  info = this.calculateBestPlacement(player, self, posList);
               } while(best != null && !(info.damage > best.damage));

               best = info;
            }
         }

         placeInfo = best;
      }

      return placeInfo;
   }

   private List<BlockPos> findBlocksExcluding(boolean calcWithOutBase) {
      return (List)EntityUtil.getSphere(PlayerUtil.getEyesPos(), (Double)this.range.getValue() + 1.0D, (Double)this.yRange.getValue(), false, false, 0).stream().filter((pos) -> {
         return this.canPlaceBed(pos, !calcWithOutBase);
      }).collect(Collectors.toList());
   }

   private BedAura.PlaceInfo calculateBestPlacement(BedAura.EntityInfo target, BedAura.EntityInfo self, List<BlockPos> blocks) {
      BedAura.PlaceInfo best = new BedAura.PlaceInfo(target, (BlockPos)null, (float)Math.min(Math.min((Double)this.minDmg.getValue(), (Double)this.slowMinDmg.getValue()), this.fpMinDmg.getMin()), -1.0F, (BlockPos)null);
      if (target != null && self != null) {
         Iterator var5 = blocks.iterator();

         while(true) {
            BlockPos pos;
            BlockPos basePos;
            float targetDamage;
            float selfDamage;
            while(true) {
               double x;
               double y;
               double z;
               while(true) {
                  boolean canPlace;
                  do {
                     do {
                        while(true) {
                           if (!var5.hasNext()) {
                              return best;
                           }

                           pos = (BlockPos)var5.next();
                           basePos = null;
                           boolean air = BlockUtil.canReplace(pos);
                           canPlace = (Boolean)this.highVersion.getValue() || !air && !this.needBase(pos);
                           if (canPlace) {
                              break;
                           }

                           if ((Boolean)this.base.getValue() && (!((double)best.damage >= (Double)this.toggleDmg.getValue()) || best.basePos != null) && !((double)(pos.func_177956_o() + 1) > target.player.field_70163_u + (Double)this.maxY.getValue()) && BurrowUtil.findHotbarBlock(BlockObsidian.class) != -1 && !(LemonClient.speedUtil.getPlayerSpeed(target.defaultPlayer) > (Double)this.maxSpeed.getValue())) {
                              basePos = this.getBestBasePos(pos);
                              if (basePos == null) {
                                 continue;
                              }
                              break;
                           }
                        }

                        x = (double)pos.func_177958_n() + 0.5D;
                        y = (double)pos.func_177956_o() + 1.5625D;
                        z = (double)pos.func_177952_p() + 0.5D;
                        targetDamage = DamageUtil.calculateDamage(target.player, x, y, z, 5.0F, "Bed");
                     } while(targetDamage <= best.damage);
                  } while((double)targetDamage < (Double)this.baseMinDmg.getValue() && !canPlace);

                  if (target.hp > (double)(Integer)this.facePlaceValue.getValue()) {
                     if (!((double)targetDamage < (Double)this.minDmg.getValue()) || !((double)targetDamage < (Double)this.slowMinDmg.getValue()) && (Boolean)this.slowFP.getValue()) {
                        break;
                     }
                  } else {
                     if ((double)targetDamage < (Double)this.fpMinDmg.getValue()) {
                        continue;
                     }
                     break;
                  }
               }

               selfDamage = 0.0F;
               if (self.player.func_184812_l_()) {
                  break;
               }

               selfDamage = DamageUtil.calculateDamage(self.player, x, y, z, 5.0F, "Bed");
               if ((double)selfDamage + (Double)this.balance.getValue() > (Double)this.maxSelfDmg.getValue()) {
                  if ((double)targetDamage >= target.hp) {
                     if (!(Boolean)this.forcePlace.getValue()) {
                        continue;
                     }
                  } else if (!(Boolean)this.ignore.getValue()) {
                     continue;
                  }
               }

               if (!(Boolean)this.suicide.getValue() || !((double)selfDamage + (Double)this.balance.getValue() >= self.hp)) {
                  break;
               }
            }

            best = new BedAura.PlaceInfo(target, pos, targetDamage, selfDamage, basePos);
         }
      } else {
         return best;
      }
   }

   private BedAura.PlaceInfo calculatePlacement(BedAura.EntityInfo target, BedAura.EntityInfo self, List<BlockPos> poslist) {
      BedAura.PlaceInfo best = new BedAura.PlaceInfo(target, (BlockPos)null, (float)Math.min(Math.min((Double)this.minDmg.getValue(), (Double)this.slowMinDmg.getValue()), this.fpMinDmg.getMin()), -1.0F, (BlockPos)null);
      if (target != null && self != null) {
         Iterator var5 = poslist.iterator();

         while(true) {
            BlockPos pos;
            float targetDamage;
            float selfDamage;
            do {
               do {
                  if (!var5.hasNext()) {
                     return best;
                  }

                  pos = (BlockPos)var5.next();
                  double x = (double)pos.func_177958_n() + 0.5D;
                  double y = (double)pos.func_177956_o() + 1.5625D;
                  double z = (double)pos.func_177952_p() + 0.5D;
                  targetDamage = DamageUtil.calculateDamage(target.entity, x, y, z, 5.0F, "Bed");
                  selfDamage = DamageUtil.calculateDamage(self.player, x, y, z, 5.0F, "Bed");
               } while((double)targetDamage < (Double)this.minDmg.getValue() && ((double)targetDamage < (Double)this.slowMinDmg.getValue() || !(Boolean)this.slowFP.getValue()) && (double)targetDamage < (Double)this.fpMinDmg.getValue());
            } while(!self.player.func_184812_l_() && ((double)selfDamage + (Double)this.balance.getValue() > (Double)this.maxSelfDmg.getValue() && !(Boolean)this.ignore.getValue() || (Boolean)this.suicide.getValue() && (double)selfDamage + (Double)this.balance.getValue() >= self.hp));

            if (targetDamage > best.damage) {
               best = new BedAura.PlaceInfo(target, pos, targetDamage, selfDamage, (BlockPos)null);
            }
         }
      } else {
         return best;
      }
   }

   private boolean near(BedAura.EntityInfo player) {
      boolean below = (int)(player.defaultPlayer.field_70163_u + 0.5D) + 2 > this.headPos.field_177960_b;
      boolean near = (player.defaultPlayer.func_70011_f((double)this.headPos.func_177958_n() + 0.5D, (double)this.headPos.func_177956_o() + 0.25D, (double)this.headPos.func_177952_p() + 0.5D) < 2.5D || player.defaultPlayer.func_70011_f((double)this.basePos.func_177958_n() + 0.5D, (double)this.basePos.func_177956_o() + 0.25D, (double)this.basePos.func_177952_p() + 0.5D) < 2.5D) && (double)player.defaultPlayer.func_70032_d(mc.field_71439_g) <= 7.5D;
      boolean predictBelow = player.player.field_70163_u + 1.0D > (double)this.headPos.field_177960_b;
      boolean predictNear = (player.player.func_70011_f((double)this.headPos.func_177958_n() + 0.5D, (double)this.headPos.func_177956_o() + 0.25D, (double)this.headPos.func_177952_p() + 0.5D) < 2.5D || player.player.func_70011_f((double)this.basePos.func_177958_n() + 0.5D, (double)this.basePos.func_177956_o() + 0.25D, (double)this.basePos.func_177952_p() + 0.5D) < 2.5D) && (double)player.player.func_70032_d(mc.field_71439_g) <= 7.5D;
      return below && near || predictBelow && predictNear;
   }

   private boolean stuck(EntityPlayer player) {
      double distTraveledLastTickX = player.field_70165_t - player.field_70169_q;
      double distTraveledLastTickZ = player.field_70161_v - player.field_70166_s;
      double playerSpeed = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
      return player.field_70163_u - (double)((int)player.field_70163_u) > 0.3D || (double)player.field_70143_R > 0.4D || playerSpeed > 3.0D;
   }

   private boolean stuck(BedAura.EntityInfo target) {
      EntityPlayer player = target.defaultPlayer;
      EntityPlayer predict = target.player;
      boolean inAir = false;
      Vec3d[] var5 = new Vec3d[]{new Vec3d(0.25D, 0.0D, 0.25D), new Vec3d(0.25D, 0.0D, -0.25D), new Vec3d(-0.25D, 0.0D, 0.25D), new Vec3d(-0.25D, 0.0D, -0.25D)};
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Vec3d vec3d = var5[var7];
         BlockPos pos = new BlockPos(player.field_70165_t + vec3d.field_72450_a, player.field_70163_u + 0.7D, player.field_70161_v + vec3d.field_72449_c);
         pos = pos.func_177977_b();
         if (BlockUtil.canReplace(pos) || BlockUtil.getBlock(pos) == Blocks.field_150324_C) {
            inAir = true;
            break;
         }
      }

      double y = predict.field_70163_u - player.field_70163_u;
      return this.near(target) && (this.stuck(player) || this.stuck(predict) || inAir || y > 0.3D || y < -0.3D);
   }

   private int getPlaceDelay(boolean stuck) {
      if ((double)this.damage >= (Double)this.minDmg.getValue()) {
         return stuck ? (Integer)this.stuckPlaceDelay.getValue() : (Integer)this.placeDelay.getValue();
      } else {
         return (Integer)this.slowPlaceDelay.getValue();
      }
   }

   private int getBreakDelay(boolean stuck) {
      if ((double)this.damage >= (Double)this.minDmg.getValue()) {
         return stuck ? (Integer)this.stuckBreakDelay.getValue() : (Integer)this.breakDelay.getValue();
      } else {
         return (Integer)this.slowBreakDelay.getValue();
      }
   }

   private BedAura.EntityInfo getNearestEntity(List<Entity> list) {
      Entity entity = (Entity)list.stream().filter((target) -> {
         return target instanceof EntityLivingBase;
      }).min(Comparator.comparing((p) -> {
         return mc.field_71439_g.func_70032_d(p);
      })).orElse((Object)null);
      return entity == null ? null : new BedAura.EntityInfo((EntityLivingBase)entity);
   }

   private boolean canPlaceBed(BlockPos blockPos, boolean baseCheck) {
      if (!this.block(blockPos, !(Boolean)this.highVersion.getValue() || !(Boolean)this.allPossible.getValue() || baseCheck, false)) {
         return false;
      } else if ((Boolean)this.autorotate.getValue() && !this.burrow) {
         EnumFacing[] var8 = EnumFacing.field_82609_l;
         int var4 = var8.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            EnumFacing facing = var8[var5];
            if (facing != EnumFacing.UP && facing != EnumFacing.DOWN) {
               BlockPos pos = blockPos.func_177972_a(facing);
               if (this.block(pos, (Boolean)this.highVersion.getValue() && !(Boolean)this.placeInAir.getValue() || baseCheck, true)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         BlockPos pos = blockPos.func_177967_a(mc.field_71439_g.func_174811_aO(), -1);
         return this.block(pos, (Boolean)this.highVersion.getValue() && !(Boolean)this.placeInAir.getValue() || baseCheck, true) && this.inRange(pos.func_177984_a());
      }
   }

   private boolean canPlaceBase(BlockPos pos) {
      if ((Boolean)this.detectBreak.getValue() && ColorMain.INSTANCE.breakList.contains(pos)) {
         return false;
      } else if (!this.inRange(pos)) {
         return false;
      } else if (BurrowUtil.getBedFacing(pos) == null) {
         return false;
      } else {
         return this.space(pos.func_177984_a()) && !this.intersectsWithEntity(pos);
      }
   }

   private boolean needBase(BlockPos pos) {
      BlockPos[] var2 = this.sides;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         BlockPos side = var2[var4];
         BlockPos blockPos = pos.func_177971_a(side);
         if (this.space(blockPos.func_177984_a()) && !BlockUtil.canReplace(blockPos) && this.solid(pos)) {
            return false;
         }
      }

      return true;
   }

   private BlockPos getBestBasePos(BlockPos pos) {
      if (BlockUtil.canReplace(pos)) {
         return !this.inRange(pos) ? null : pos;
      } else {
         BlockPos bestPos = null;
         double bestRange = 1000.0D;
         if ((Boolean)this.autorotate.getValue() && !this.burrow) {
            BlockPos[] var10 = this.sides;
            int var6 = var10.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               BlockPos side = var10[var7];
               BlockPos base = pos.func_177971_a(side);
               if (this.canPlaceBase(base) && (bestPos == null || bestRange > mc.field_71439_g.func_174818_b(base))) {
                  bestRange = mc.field_71439_g.func_174818_b(base);
                  bestPos = base;
               }
            }

            return bestPos;
         } else {
            BlockPos base = pos.func_177972_a(mc.field_71439_g.func_174811_aO());
            return this.canPlaceBase(base) ? base : null;
         }
      }
   }

   private boolean intersectsWithEntity(BlockPos pos) {
      Iterator var2 = mc.field_71441_e.field_72996_f.iterator();

      Entity entity;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entity = (Entity)var2.next();
      } while(entity instanceof EntityItem || !(new AxisAlignedBB(pos)).func_72326_a(entity.func_174813_aQ()));

      return true;
   }

   private boolean block(BlockPos pos, boolean baseCheck, boolean rangeCheck) {
      if (!this.space(pos.func_177984_a())) {
         return false;
      } else {
         if (BlockUtil.canReplace(pos)) {
            if (baseCheck || !this.canPlaceBase(pos)) {
               return false;
            }
         } else if (!(Boolean)this.highVersion.getValue() && !this.solid(pos)) {
            return false;
         }

         return !rangeCheck || this.inRange(pos.func_177984_a());
      }
   }

   private boolean isBed(BlockPos pos) {
      Block block = mc.field_71441_e.func_180495_p(pos).func_177230_c();
      return block == Blocks.field_150324_C || block instanceof BlockBed;
   }

   private boolean space(BlockPos pos) {
      return mc.field_71441_e.func_175623_d(pos) || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150324_C;
   }

   private boolean solid(BlockPos pos) {
      return !BlockUtil.isBlockUnSolid(pos) && !(mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockBed) && mc.field_71441_e.func_180495_p(pos).isSideSolid(mc.field_71441_e, pos, EnumFacing.UP) && BlockUtil.getBlock(pos).field_149787_q;
   }

   private boolean inRange(BlockPos pos) {
      double x = (double)pos.field_177962_a - mc.field_71439_g.field_70165_t;
      double z = (double)pos.field_177961_c - mc.field_71439_g.field_70161_v;
      double y = (double)(pos.field_177960_b - PlayerUtil.getEyesPos().field_177960_b);
      double add = Math.sqrt(y * y) / 2.0D;
      return x * x + z * z <= ((Double)this.range.getValue() - add) * ((Double)this.range.getValue() - add) && y * y <= (Double)this.yRange.getValue() * (Double)this.yRange.getValue();
   }

   private static boolean isPos2(BlockPos pos1, BlockPos pos2) {
      if (pos1 != null && pos2 != null) {
         return pos1.field_177962_a == pos2.field_177962_a && pos1.field_177960_b == pos2.field_177960_b && pos1.field_177961_c == pos2.field_177961_c;
      } else {
         return false;
      }
   }

   public void refill_bed() {
      if (!(mc.field_71462_r instanceof GuiContainer) || mc.field_71462_r instanceof GuiInventory) {
         int airSlot = this.isSpace();
         if (airSlot != -1) {
            for(int i = 9; i < 36; ++i) {
               if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == Items.field_151104_aV) {
                  if (((String)this.clickMode.getValue()).equalsIgnoreCase("Quick")) {
                     if (mc.field_71439_g.field_71071_by.func_70301_a(airSlot).func_77973_b() != Items.field_190931_a) {
                        mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, airSlot + 36, 0, ClickType.QUICK_MOVE, mc.field_71439_g);
                     }

                     mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, i, 0, ClickType.QUICK_MOVE, mc.field_71439_g);
                  } else if (((String)this.clickMode.getValue()).equalsIgnoreCase("Swap")) {
                     mc.field_71442_b.func_187098_a(0, i, airSlot, ClickType.SWAP, mc.field_71439_g);
                  } else {
                     mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, i, 0, ClickType.PICKUP, mc.field_71439_g);
                     mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, airSlot + 36, 0, ClickType.PICKUP, mc.field_71439_g);
                  }
                  break;
               }
            }
         }
      }

   }

   private int isSpace() {
      int slot = -1;
      int i;
      if ((Boolean)this.force.getValue()) {
         if (((String)this.refillMode.getValue()).equals("Only")) {
            i = (Integer)this.slotS.getValue() - 1;
            if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() != Items.field_151104_aV) {
               slot = i;
            }
         } else {
            for(i = 0; i < 9; ++i) {
               if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() != Items.field_151104_aV) {
                  slot = i;
               }
            }
         }
      } else if (((String)this.refillMode.getValue()).equals("Only")) {
         i = (Integer)this.slotS.getValue() - 1;
         if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == Items.field_190931_a) {
            slot = i;
         }
      } else {
         for(i = 0; i < 9; ++i) {
            if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == Items.field_190931_a) {
               slot = i;
            }
         }
      }

      return slot;
   }

   private Vec3d getHitVecOffset(EnumFacing face) {
      Vec3i vec = face.func_176730_m();
      return new Vec3d((double)((float)vec.field_177962_a * 0.5F + 0.5F), (double)((float)vec.field_177960_b * 0.5F + 0.5F), (double)((float)vec.field_177961_c * 0.5F + 0.5F));
   }

   private void switchTo(int slot) {
      if (slot > -1 && slot < 9) {
         if ((Boolean)this.packetSwitch.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = slot;
         }

         if ((Boolean)this.update.getValue()) {
            mc.field_71442_b.func_78765_e();
         }

         mc.field_71439_g.field_71070_bA.func_75142_b();
      }

   }

   private void swing(EnumHand hand) {
      if ((Boolean)this.packetSwing.getValue()) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketAnimation(hand));
      } else {
         mc.field_71439_g.func_184609_a(hand);
      }

   }

   private boolean inNether() {
      return mc.field_71439_g.field_71093_bK == 0;
   }

   public void onEnable() {
      this.calctiming.reset();
      this.basetiming.reset();
      this.placetiming.reset();
      this.breaktiming.reset();
      this.updatetiming.reset();
      if ((Boolean)this.reset.getValue()) {
         this.lastBestPlace = null;
         this.lastBestPos = null;
         this.managerClassRenderBlocks.blocks.clear();
         this.movingPlaceNow = new Vec3d(-1.0D, -1.0D, -1.0D);
         this.movingPosNow = new Vec3d(-1.0D, -1.0D, -1.0D);
      }

   }

   public void onDisable() {
      this.headPos = null;
      this.basePos = null;
      if ((Boolean)this.reset.getValue()) {
         this.lastBestPlace = null;
         this.lastBestPos = null;
         this.managerClassRenderBlocks.blocks.clear();
         this.movingPlaceNow = new Vec3d(-1.0D, -1.0D, -1.0D);
         this.movingPosNow = new Vec3d(-1.0D, -1.0D, -1.0D);
      }

   }

   public void onWorldRender(RenderEvent event) {
      if (mc.field_71441_e != null) {
         this.managerClassRenderBlocks.render();
         if (this.headPos != null && this.basePos != null) {
            if (!(Boolean)this.anime.getValue()) {
               this.drawRender();
            } else {
               this.lastBestPlace = this.headPos;
               this.lastBestPos = this.basePos;
            }

            if ((Boolean)this.fade.getValue()) {
               this.managerClassRenderBlocks.addRender(this.headPos, this.basePos);
            }

            if ((Boolean)this.anime.getValue() && this.lastBestPlace != null && this.lastBestPos != null) {
               if (this.movingPlaceNow.field_72448_b == -1.0D && this.movingPlaceNow.field_72450_a == -1.0D && this.movingPlaceNow.field_72449_c == -1.0D) {
                  this.movingPlaceNow = new Vec3d((double)this.lastBestPlace.func_177958_n(), (double)this.lastBestPlace.func_177956_o(), (double)this.lastBestPlace.func_177952_p());
               }

               if (this.movingPosNow.field_72448_b == -1.0D && this.movingPosNow.field_72450_a == -1.0D && this.movingPosNow.field_72449_c == -1.0D) {
                  this.movingPosNow = new Vec3d((double)this.lastBestPos.func_177958_n(), (double)this.lastBestPos.func_177956_o(), (double)this.lastBestPos.func_177952_p());
               }

               this.movingPlaceNow = new Vec3d(this.movingPlaceNow.field_72450_a + ((double)this.lastBestPlace.func_177958_n() - this.movingPlaceNow.field_72450_a) * (double)((Double)this.movingPlaceSpeed.getValue()).floatValue(), this.movingPlaceNow.field_72448_b + ((double)this.lastBestPlace.func_177956_o() - this.movingPlaceNow.field_72448_b) * (double)((Double)this.movingPlaceSpeed.getValue()).floatValue(), this.movingPlaceNow.field_72449_c + ((double)this.lastBestPlace.func_177952_p() - this.movingPlaceNow.field_72449_c) * (double)((Double)this.movingPlaceSpeed.getValue()).floatValue());
               this.movingPosNow = new Vec3d(this.movingPosNow.field_72450_a + ((double)this.lastBestPos.func_177958_n() - this.movingPosNow.field_72450_a) * (double)((Double)this.movingPlaceSpeed.getValue()).floatValue(), this.movingPosNow.field_72448_b + ((double)this.lastBestPos.func_177956_o() - this.movingPosNow.field_72448_b) * (double)((Double)this.movingPlaceSpeed.getValue()).floatValue(), this.movingPosNow.field_72449_c + ((double)this.lastBestPos.func_177952_p() - this.movingPosNow.field_72449_c) * (double)((Double)this.movingPlaceSpeed.getValue()).floatValue());
               this.drawBoxMain(this.movingPlaceNow.field_72450_a, this.movingPlaceNow.field_72448_b, this.movingPlaceNow.field_72449_c, this.movingPosNow.field_72450_a, this.movingPosNow.field_72448_b, this.movingPosNow.field_72449_c);
               if (Math.abs(this.movingPlaceNow.field_72450_a - (double)this.lastBestPlace.func_177958_n()) <= 0.125D && Math.abs(this.movingPlaceNow.field_72448_b - (double)this.lastBestPlace.func_177956_o()) <= 0.125D && Math.abs(this.movingPlaceNow.field_72449_c - (double)this.lastBestPlace.func_177952_p()) <= 0.125D) {
                  this.lastBestPlace = null;
               }

               if (Math.abs(this.movingPosNow.field_72450_a - (double)this.lastBestPos.func_177958_n()) <= 0.125D && Math.abs(this.movingPosNow.field_72448_b - (double)this.lastBestPos.func_177956_o()) <= 0.125D && Math.abs(this.movingPosNow.field_72449_c - (double)this.lastBestPos.func_177952_p()) <= 0.125D) {
                  this.lastBestPos = null;
               }
            }

         }
      }
   }

   private void drawAnimationRender(AxisAlignedBB box1, AxisAlignedBB box2) {
      AxisAlignedBB nBox1 = new AxisAlignedBB(box1.field_72340_a, box1.field_72338_b, box1.field_72339_c, box1.field_72340_a + 1.0D, box1.field_72338_b + 0.5625D, box1.field_72339_c + 1.0D);
      AxisAlignedBB nBox2 = new AxisAlignedBB(box2.field_72340_a, box2.field_72338_b, box2.field_72339_c, box2.field_72340_a + 1.0D, box2.field_72338_b + 0.5625D, box2.field_72339_c + 1.0D);
      boolean x1 = box1.field_72340_a < box2.field_72340_a;
      boolean z2 = box1.field_72339_c > box2.field_72339_c;
      AxisAlignedBB box;
      if (x1) {
         if (z2) {
            box = new AxisAlignedBB(box1.field_72340_a, box1.field_72338_b, box2.field_72339_c, box2.field_72340_a + 1.0D, box2.field_72338_b + 0.5625D, box1.field_72339_c + 1.0D);
         } else {
            box = new AxisAlignedBB(box1.field_72340_a, box1.field_72338_b, box1.field_72339_c, box2.field_72340_a + 1.0D, box2.field_72338_b + 0.5625D, box2.field_72339_c + 1.0D);
         }
      } else if (z2) {
         box = new AxisAlignedBB(box2.field_72340_a, box1.field_72338_b, box2.field_72339_c, box1.field_72340_a + 1.0D, box2.field_72338_b + 0.5625D, box1.field_72339_c + 1.0D);
      } else {
         box = new AxisAlignedBB(box2.field_72340_a, box1.field_72338_b, box1.field_72339_c, box1.field_72340_a + 1.0D, box2.field_72338_b + 0.5625D, box2.field_72339_c + 1.0D);
      }

      if ((new GSColor(this.color.getValue(), (Integer)this.alpha.getValue())).equals(new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()))) {
         RenderUtil.drawBox(box, false, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 63);
         if ((Boolean)this.outline.getValue()) {
            RenderUtil.drawBoundingBox(box, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), 255));
         }
      } else {
         String var8 = this.face;
         byte var9 = -1;
         switch(var8.hashCode()) {
         case 2120701:
            if (var8.equals("EAST")) {
               var9 = 1;
            }
            break;
         case 2660783:
            if (var8.equals("WEST")) {
               var9 = 0;
            }
            break;
         case 74469605:
            if (var8.equals("NORTH")) {
               var9 = 3;
            }
            break;
         case 79090093:
            if (var8.equals("SOUTH")) {
               var9 = 2;
            }
         }

         switch(var9) {
         case 0:
            if ((Boolean)this.gradient.getValue()) {
               RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 0, 16);
               RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()), 0, 32);
               if ((Boolean)this.outline.getValue()) {
                  RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), (Integer)this.outAlpha.getValue()), 0, 16);
                  RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), (Integer)this.outAlpha.getValue()), 0, 32);
               }
            } else {
               RenderUtil.drawBox(nBox1, false, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 31);
               RenderUtil.drawBox(nBox2, false, 0.5625D, new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()), 47);
               if ((Boolean)this.outline.getValue()) {
                  RenderUtil.drawBoundingBox(nBox1, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), (Integer)this.outAlpha.getValue()));
                  RenderUtil.drawBoundingBox(nBox2, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), (Integer)this.outAlpha.getValue()));
               }
            }
            break;
         case 1:
            if ((Boolean)this.gradient.getValue()) {
               RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 0, 32);
               RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()), 0, 16);
               if ((Boolean)this.outline.getValue()) {
                  RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), (Integer)this.outAlpha.getValue()), 0, 32);
                  RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), (Integer)this.outAlpha.getValue()), 0, 16);
               }
            } else {
               RenderUtil.drawBox(nBox1, false, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 47);
               RenderUtil.drawBox(nBox2, false, 0.5625D, new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()), 31);
               if ((Boolean)this.outline.getValue()) {
                  RenderUtil.drawBoundingBox(nBox1, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), (Integer)this.outAlpha.getValue()));
                  RenderUtil.drawBoundingBox(nBox2, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), (Integer)this.outAlpha.getValue()));
               }
            }
            break;
         case 2:
            if ((Boolean)this.gradient.getValue()) {
               RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 0, 8);
               RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()), 0, 4);
               if ((Boolean)this.outline.getValue()) {
                  RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), (Integer)this.outAlpha.getValue()), 0, 8);
                  RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), (Integer)this.outAlpha.getValue()), 0, 4);
               }
            } else {
               RenderUtil.drawBox(nBox1, false, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 59);
               RenderUtil.drawBox(nBox2, false, 0.5625D, new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()), 55);
               if ((Boolean)this.outline.getValue()) {
                  RenderUtil.drawBoundingBox(nBox1, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), (Integer)this.outAlpha.getValue()));
                  RenderUtil.drawBoundingBox(nBox2, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), (Integer)this.outAlpha.getValue()));
               }
            }
            break;
         case 3:
            if ((Boolean)this.gradient.getValue()) {
               RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 0, 4);
               RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()), 0, 8);
               if ((Boolean)this.outline.getValue()) {
                  RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), (Integer)this.outAlpha.getValue()), 0, 4);
                  RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), (Integer)this.outAlpha.getValue()), 0, 8);
               }
            } else {
               RenderUtil.drawBox(nBox1, false, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 55);
               RenderUtil.drawBox(nBox2, false, 0.5625D, new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()), 59);
               if ((Boolean)this.outline.getValue()) {
                  RenderUtil.drawBoundingBox(nBox1, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), (Integer)this.outAlpha.getValue()));
                  RenderUtil.drawBoundingBox(nBox2, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), (Integer)this.outAlpha.getValue()));
               }
            }
         }
      }

      if ((Boolean)this.showDamage.getValue()) {
         String[] damageText = new String[]{String.format("%.1f", this.damage)};
         if ((Boolean)this.showSelfDamage.getValue()) {
            damageText = new String[]{String.format("%.1f", this.damage) + "/" + String.format("%.1f", this.selfDamage)};
         }

         RenderUtil.drawNametag(box1.field_72340_a + 0.5D, box1.field_72338_b + 0.28125D, box1.field_72339_c + 0.5D, damageText, new GSColor(255, 255, 255), 1, 0.02666666666666667D, 0.0D);
      }

   }

   private void drawFade(BlockPos render, BlockPos render2, int alpha, int outlineAlpha) {
      if (render != null && render2 != null && alpha >= 0 && outlineAlpha >= 0) {
         AxisAlignedBB box;
         if ((new GSColor(this.color.getValue(), alpha)).equals(new GSColor(this.color2.getValue(), alpha))) {
            if (render.field_177962_a - render2.field_177962_a >= 0 && render.field_177961_c - render2.field_177961_c >= 0) {
               box = new AxisAlignedBB((double)render2.field_177962_a, (double)render2.field_177960_b, (double)render2.field_177961_c, (double)(render.func_177958_n() + 1), (double)render.func_177956_o() + 0.5625D, (double)(render.func_177952_p() + 1));
            } else {
               box = new AxisAlignedBB((double)render.field_177962_a, (double)render.field_177960_b, (double)render.field_177961_c, (double)(render2.func_177958_n() + 1), (double)render2.func_177956_o() + 0.5625D, (double)(render2.func_177952_p() + 1));
            }

            RenderUtil.drawBox(box, false, 0.5625D, new GSColor(this.color.getValue(), alpha), 63);
            if ((Boolean)this.outline.getValue()) {
               RenderUtil.drawBoundingBox(box, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), outlineAlpha));
            }
         } else if ((Boolean)this.gradient.getValue()) {
            if (render.field_177962_a - render2.field_177962_a >= 0 && render.field_177961_c - render2.field_177961_c >= 0) {
               box = new AxisAlignedBB((double)render2.field_177962_a, (double)render2.field_177960_b, (double)render2.field_177961_c, (double)(render.func_177958_n() + 1), (double)render.func_177956_o() + 0.5625D, (double)(render.func_177952_p() + 1));
            } else {
               box = new AxisAlignedBB((double)render.field_177962_a, (double)render.field_177960_b, (double)render.field_177961_c, (double)(render2.func_177958_n() + 1), (double)render2.func_177956_o() + 0.5625D, (double)(render2.func_177952_p() + 1));
            }

            if (render.field_177962_a - render2.field_177962_a < 0) {
               RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color.getValue(), alpha), 0, 16);
               RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color2.getValue(), alpha), 0, 32);
               if ((Boolean)this.outline.getValue()) {
                  RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), outlineAlpha), 0, 16);
                  RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), outlineAlpha), 0, 32);
               }
            } else if (render.field_177961_c - render2.field_177961_c < 0) {
               RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color.getValue(), alpha), 0, 4);
               RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color2.getValue(), alpha), 0, 8);
               if ((Boolean)this.outline.getValue()) {
                  RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), outlineAlpha), 0, 4);
                  RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), outlineAlpha), 0, 8);
               }
            } else if (render.field_177961_c - render2.field_177961_c == 0) {
               RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color.getValue(), alpha), 0, 32);
               RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color2.getValue(), alpha), 0, 16);
               if ((Boolean)this.outline.getValue()) {
                  RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), outlineAlpha), 0, 32);
                  RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), outlineAlpha), 0, 16);
               }
            } else {
               RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color.getValue(), alpha), 0, 8);
               RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color2.getValue(), alpha), 0, 4);
               if ((Boolean)this.outline.getValue()) {
                  RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), outlineAlpha), 0, 8);
                  RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), outlineAlpha), 0, 4);
               }
            }
         } else if (render.field_177962_a - render2.field_177962_a < 0) {
            RenderUtil.drawBox(render, 0.5625D, new GSColor(this.color.getValue(), alpha), 31);
            RenderUtil.drawBox(render2, 0.5625D, new GSColor(this.color2.getValue(), alpha), 47);
            if ((Boolean)this.outline.getValue()) {
               RenderUtil.drawBoundingBoxDire((BlockPos)render, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), outlineAlpha), outlineAlpha, 31);
               RenderUtil.drawBoundingBoxDire((BlockPos)render2, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), outlineAlpha), outlineAlpha, 47);
            }
         } else if (render.field_177961_c - render2.field_177961_c < 0) {
            RenderUtil.drawBox(render, 0.5625D, new GSColor(this.color.getValue(), alpha), 55);
            RenderUtil.drawBox(render2, 0.5625D, new GSColor(this.color2.getValue(), alpha), 59);
            if ((Boolean)this.outline.getValue()) {
               RenderUtil.drawBoundingBoxDire((BlockPos)render, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), outlineAlpha), outlineAlpha, 55);
               RenderUtil.drawBoundingBoxDire((BlockPos)render2, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), outlineAlpha), outlineAlpha, 59);
            }
         } else if (render.field_177961_c - render2.field_177961_c == 0) {
            RenderUtil.drawBox(render, 0.5625D, new GSColor(this.color.getValue(), alpha), 47);
            RenderUtil.drawBox(render2, 0.5625D, new GSColor(this.color2.getValue(), alpha), 31);
            if ((Boolean)this.outline.getValue()) {
               RenderUtil.drawBoundingBoxDire((BlockPos)render, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), outlineAlpha), outlineAlpha, 47);
               RenderUtil.drawBoundingBoxDire((BlockPos)render2, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), outlineAlpha), outlineAlpha, 31);
            }
         } else {
            RenderUtil.drawBox(render, 0.5625D, new GSColor(this.color.getValue(), alpha), 59);
            RenderUtil.drawBox(render2, 0.5625D, new GSColor(this.color2.getValue(), alpha), 55);
            if ((Boolean)this.outline.getValue()) {
               RenderUtil.drawBoundingBoxDire((BlockPos)render, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), outlineAlpha), outlineAlpha, 59);
               RenderUtil.drawBoundingBoxDire((BlockPos)render2, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), outlineAlpha), outlineAlpha, 47);
            }
         }

      }
   }

   private void drawRender() {
      AxisAlignedBB box;
      if ((new GSColor(this.color.getValue(), (Integer)this.alpha.getValue())).equals(new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()))) {
         if (this.headPos.field_177962_a - this.basePos.field_177962_a >= 0 && this.headPos.field_177961_c - this.basePos.field_177961_c >= 0) {
            box = new AxisAlignedBB((double)this.basePos.field_177962_a, (double)this.basePos.field_177960_b, (double)this.basePos.field_177961_c, (double)(this.headPos.func_177958_n() + 1), (double)this.headPos.func_177956_o() + 0.5625D, (double)(this.headPos.func_177952_p() + 1));
         } else {
            box = new AxisAlignedBB((double)this.headPos.field_177962_a, (double)this.headPos.field_177960_b, (double)this.headPos.field_177961_c, (double)(this.basePos.func_177958_n() + 1), (double)this.basePos.func_177956_o() + 0.5625D, (double)(this.basePos.func_177952_p() + 1));
         }

         RenderUtil.drawBox(box, false, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 63);
         if ((Boolean)this.outline.getValue()) {
            RenderUtil.drawBoundingBox(box, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), 255));
         }
      } else if ((Boolean)this.gradient.getValue()) {
         if (this.headPos.field_177962_a - this.basePos.field_177962_a >= 0 && this.headPos.field_177961_c - this.basePos.field_177961_c >= 0) {
            box = new AxisAlignedBB((double)this.basePos.field_177962_a, (double)this.basePos.field_177960_b, (double)this.basePos.field_177961_c, (double)(this.headPos.func_177958_n() + 1), (double)this.headPos.func_177956_o() + 0.5625D, (double)(this.headPos.func_177952_p() + 1));
         } else {
            box = new AxisAlignedBB((double)this.headPos.field_177962_a, (double)this.headPos.field_177960_b, (double)this.headPos.field_177961_c, (double)(this.basePos.func_177958_n() + 1), (double)this.basePos.func_177956_o() + 0.5625D, (double)(this.basePos.func_177952_p() + 1));
         }

         if (this.headPos.field_177962_a - this.basePos.field_177962_a < 0) {
            RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 0, 16);
            RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()), 0, 32);
            if ((Boolean)this.outline.getValue()) {
               RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), 255), 0, 16);
               RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), 255), 0, 32);
            }
         } else if (this.headPos.field_177961_c - this.basePos.field_177961_c < 0) {
            RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 0, 4);
            RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()), 0, 8);
            if ((Boolean)this.outline.getValue()) {
               RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), 255), 0, 4);
               RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), 255), 0, 8);
            }
         } else if (this.headPos.field_177961_c - this.basePos.field_177961_c == 0) {
            RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 0, 32);
            RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()), 0, 16);
            if ((Boolean)this.outline.getValue()) {
               RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), 255), 0, 32);
               RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), 255), 0, 16);
            }
         } else {
            RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 0, 8);
            RenderUtil.drawBoxDire(box, 0.5625D, new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()), 0, 4);
            if ((Boolean)this.outline.getValue()) {
               RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), 255), 0, 8);
               RenderUtil.drawBoundingBoxDire((AxisAlignedBB)box, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), 255), 0, 4);
            }
         }
      } else if (this.headPos.field_177962_a - this.basePos.field_177962_a < 0) {
         RenderUtil.drawBox(this.headPos, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 31);
         RenderUtil.drawBox(this.basePos, 0.5625D, new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()), 47);
         if ((Boolean)this.outline.getValue()) {
            RenderUtil.drawBoundingBoxDire((BlockPos)this.headPos, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), (Integer)this.outAlpha.getValue()), (Integer)this.outAlpha.getValue(), 31);
            RenderUtil.drawBoundingBoxDire((BlockPos)this.basePos, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), (Integer)this.outAlpha.getValue()), (Integer)this.outAlpha.getValue(), 47);
         }
      } else if (this.headPos.field_177961_c - this.basePos.field_177961_c < 0) {
         RenderUtil.drawBox(this.headPos, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 55);
         RenderUtil.drawBox(this.basePos, 0.5625D, new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()), 59);
         if ((Boolean)this.outline.getValue()) {
            RenderUtil.drawBoundingBoxDire((BlockPos)this.headPos, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), (Integer)this.outAlpha.getValue()), (Integer)this.outAlpha.getValue(), 55);
            RenderUtil.drawBoundingBoxDire((BlockPos)this.basePos, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), (Integer)this.outAlpha.getValue()), (Integer)this.outAlpha.getValue(), 59);
         }
      } else if (this.headPos.field_177961_c - this.basePos.field_177961_c == 0) {
         RenderUtil.drawBox(this.headPos, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 47);
         RenderUtil.drawBox(this.basePos, 0.5625D, new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()), 31);
         if ((Boolean)this.outline.getValue()) {
            RenderUtil.drawBoundingBoxDire((BlockPos)this.headPos, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), (Integer)this.outAlpha.getValue()), (Integer)this.outAlpha.getValue(), 47);
            RenderUtil.drawBoundingBoxDire((BlockPos)this.basePos, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), (Integer)this.outAlpha.getValue()), (Integer)this.outAlpha.getValue(), 31);
         }
      } else {
         RenderUtil.drawBox(this.headPos, 0.5625D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 59);
         RenderUtil.drawBox(this.basePos, 0.5625D, new GSColor(this.color2.getValue(), (Integer)this.alpha.getValue()), 55);
         if ((Boolean)this.outline.getValue()) {
            RenderUtil.drawBoundingBoxDire((BlockPos)this.headPos, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), (Integer)this.outAlpha.getValue()), (Integer)this.outAlpha.getValue(), 59);
            RenderUtil.drawBoundingBoxDire((BlockPos)this.basePos, 0.5625D, (double)(Integer)this.width.getValue(), new GSColor(this.color2.getValue(), (Integer)this.outAlpha.getValue()), (Integer)this.outAlpha.getValue(), 55);
         }
      }

      if ((Boolean)this.showDamage.getValue()) {
         String[] damageText = new String[]{String.format("%.1f", this.damage)};
         if ((Boolean)this.showSelfDamage.getValue()) {
            damageText = new String[]{String.format("%.1f", this.damage) + "/" + String.format("%.1f", this.selfDamage)};
         }

         RenderUtil.drawNametag((double)this.headPos.func_177958_n() + 0.5D, (double)this.headPos.func_177956_o() + 0.28125D, (double)this.headPos.func_177952_p() + 0.5D, damageText, new GSColor(255, 255, 255), 1, 0.02666666666666667D, 0.0D);
      }

   }

   AxisAlignedBB getBox(double x, double y, double z) {
      double maxX = x + 1.0D;
      double maxZ = z + 1.0D;
      return new AxisAlignedBB(x, y, z, maxX, y, maxZ);
   }

   void drawBoxMain(BlockPos pos, BlockPos pos2, int alpha, int outline) {
      this.drawFade(pos, pos2, alpha, outline);
   }

   void drawBoxMain(double x, double y, double z, double x2, double y2, double z2) {
      AxisAlignedBB box = this.getBox(x, y, z);
      AxisAlignedBB box2 = this.getBox(x2, y2, z2);
      box = new AxisAlignedBB(box.field_72340_a, box.field_72337_e, box.field_72339_c, box.field_72336_d, box.field_72337_e + 0.5625D, box.field_72334_f);
      box2 = new AxisAlignedBB(box2.field_72340_a, box2.field_72337_e, box2.field_72339_c, box2.field_72336_d, box2.field_72337_e + 0.5625D, box2.field_72334_f);
      this.drawAnimationRender(box, box2);
   }

   public String getHudInfo() {
      Entity currentTarget = null;
      if (this.target != null) {
         currentTarget = this.target.defaultPlayer == null ? this.target.entity : this.target.defaultPlayer;
      }

      boolean isNull = currentTarget == null;
      String var3 = (String)this.hudDisplay.getValue();
      byte var4 = -1;
      switch(var3.hashCode()) {
      case -1797038671:
         if (var3.equals("Target")) {
            var4 = 0;
         }
         break;
      case 2076577:
         if (var3.equals("Both")) {
            var4 = 2;
         }
         break;
      case 2039707535:
         if (var3.equals("Damage")) {
            var4 = 1;
         }
      }

      switch(var4) {
      case 0:
         return isNull ? "[" + ChatFormatting.WHITE + "None" + ChatFormatting.GRAY + "]" : "[" + ChatFormatting.WHITE + ((Entity)currentTarget).func_70005_c_() + ChatFormatting.GRAY + "]";
      case 1:
         return "[" + ChatFormatting.WHITE + String.format("%.1f", this.damage) + ((Boolean)this.hudSelfDamage.getValue() ? " Self: " + String.format("%.1f", this.selfDamage) : "") + ChatFormatting.GRAY + "]";
      case 2:
         return "[" + ChatFormatting.WHITE + (isNull ? "None" : ((Entity)currentTarget).func_70005_c_()) + " " + String.format("%.1f", this.damage) + ((Boolean)this.hudSelfDamage.getValue() ? " Self: " + String.format("%.1f", this.selfDamage) : "") + ChatFormatting.GRAY + "]";
      default:
         return "";
      }
   }

   class MoveRotation {
      double yaw;
      double lastYaw;
      int tick;

      public MoveRotation(EntityPlayer player, double lastYaw, int tick) {
         this.yaw = (double)RotationUtil.getRotationTo(player.func_174791_d(), new Vec3d(player.field_70169_q, player.field_70167_r, player.field_70166_s)).field_189982_i;
         this.lastYaw = lastYaw;
         double difference = this.yaw - lastYaw;
         if ((lastYaw == 512.0D || !(difference > (Double)BedAura.this.resetRotate.getValue()) && !(difference < -(Double)BedAura.this.resetRotate.getValue())) && LemonClient.speedUtil.getPlayerSpeed(player) != 0.0D) {
            this.tick = tick;
         } else {
            this.tick = 0;
         }
      }
   }

   class EntityInfo {
      EntityPlayer player = null;
      EntityPlayer defaultPlayer = null;
      EntityLivingBase entity = null;
      double hp;

      public EntityInfo(EntityPlayer player, boolean predict) {
         if (player != null) {
            this.defaultPlayer = player;
            this.player = predict ? PredictUtil.predictPlayer(player, new PredictUtil.PredictSettings(((BedAura.MoveRotation)BedAura.this.playerSpeed.get(player)).tick, (Boolean)BedAura.this.calculateYPredict.getValue(), (Integer)BedAura.this.startDecrease.getValue(), (Integer)BedAura.this.exponentStartDecrease.getValue(), (Integer)BedAura.this.decreaseY.getValue(), (Integer)BedAura.this.exponentDecreaseY.getValue(), (Boolean)BedAura.this.splitXZ.getValue(), (Boolean)BedAura.this.manualOutHole.getValue(), (Boolean)BedAura.this.aboveHoleManual.getValue(), (Boolean)BedAura.this.stairPredict.getValue(), (Integer)BedAura.this.nStair.getValue(), (Double)BedAura.this.speedActivationStair.getValue())) : player;
            this.hp = (double)(player.func_110143_aJ() + player.func_110139_bj());
         }
      }

      public EntityInfo(EntityLivingBase entity) {
         if (entity != null) {
            this.entity = entity;
            this.hp = (double)(entity.func_110143_aJ() + entity.func_110139_bj());
         }
      }
   }

   class PlaceInfo {
      BedAura.EntityInfo target;
      BlockPos placePos;
      BlockPos basePos;
      float damage;
      float selfDamage;

      public PlaceInfo(BedAura.EntityInfo target, BlockPos placePos, float damage, float selfDamage, BlockPos basePos) {
         this.target = target;
         this.placePos = placePos;
         this.damage = damage;
         this.selfDamage = selfDamage;
         this.basePos = basePos;
      }
   }

   class managerClassRenderBlocks {
      ArrayList<BedAura.renderBlock> blocks = new ArrayList();

      void update(int time) {
         this.blocks.removeIf((e) -> {
            return System.currentTimeMillis() - e.start > (long)time;
         });
      }

      void render() {
         this.blocks.forEach((e) -> {
            if (BedAura.this.headPos == null || (!BedAura.isPos2(e.pos, BedAura.this.headPos) || !BedAura.isPos2(e.pos2, BedAura.this.basePos)) && (!BedAura.isPos2(e.pos2, BedAura.this.headPos) || !BedAura.isPos2(e.pos, BedAura.this.basePos))) {
               e.render();
            } else {
               e.resetTime();
            }

         });
      }

      void addRender(BlockPos pos, BlockPos pos2) {
         boolean render = true;
         Iterator var4 = this.blocks.iterator();

         while(var4.hasNext()) {
            BedAura.renderBlock block = (BedAura.renderBlock)var4.next();
            if (BedAura.isPos2(block.pos, pos) && BedAura.isPos2(block.pos2, pos2) || BedAura.isPos2(block.pos2, pos2) && BedAura.isPos2(block.pos, pos)) {
               render = false;
               block.resetTime();
               break;
            }
         }

         if (render) {
            this.blocks.add(BedAura.this.new renderBlock(pos, pos2));
         }

      }
   }

   class renderBlock {
      private final BlockPos pos;
      private final BlockPos pos2;
      private long start = System.currentTimeMillis();

      public renderBlock(BlockPos pos, BlockPos pos2) {
         this.pos = pos;
         this.pos2 = pos2;
      }

      void resetTime() {
         this.start = System.currentTimeMillis();
      }

      void render() {
         BedAura.this.drawBoxMain(this.pos, this.pos2, this.returnGradient(), this.returnOutlineGradient());
      }

      public int returnGradient() {
         long end = this.start + (long)(Integer)BedAura.this.lifeTime.getValue();
         int result = (int)((float)(end - System.currentTimeMillis()) / (float)(end - this.start) * 100.0F);
         if (result < 0) {
            result = 0;
         }

         int startFade = (Integer)BedAura.this.fadeAlpha.getValue();
         int endFade = 0;
         return (int)(((double)startFade - (double)endFade) * ((double)result / 100.0D));
      }

      public int returnOutlineGradient() {
         long end = this.start + (long)(Integer)BedAura.this.lifeTime.getValue();
         int result = (int)((float)(end - System.currentTimeMillis()) / (float)(end - this.start) * 100.0F);
         if (result < 0) {
            result = 0;
         }

         int startFade = 255;
         int endFade = 0;
         return (int)(((double)startFade - (double)endFade) * ((double)result / 100.0D));
      }
   }
}
