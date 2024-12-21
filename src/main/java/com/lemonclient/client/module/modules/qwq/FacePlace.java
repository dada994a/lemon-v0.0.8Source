package com.lemonclient.client.module.modules.qwq;

import com.lemonclient.api.event.Phase;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.misc.CrystalUtil;
import com.lemonclient.api.util.misc.MathUtil;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.PlayerPacket;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.PredictUtil;
import com.lemonclient.api.util.player.RotationUtil;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.util.render.RenderUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.HoleUtil;
import com.lemonclient.api.util.world.combat.DamageUtil;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.dev.OffHand;
import com.lemonclient.client.module.modules.dev.OffHandCat;
import com.lemonclient.client.module.modules.dev.PistonAura;
import com.lemonclient.client.module.modules.dev.PullCrystal;
import com.lemonclient.mixin.mixins.accessor.AccessorCPacketUseEntity;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "FacePlace",
   category = Category.qwq,
   priority = 999
)
public class FacePlace extends Module {
   public static Entity renderEnt;
   ModeSetting p = this.registerMode("Page", Arrays.asList("General", "Combat", "Predict", "Dev", "Render"), "General");
   ModeSetting logic = this.registerMode("Logic", Arrays.asList("PlaceBreak", "BreakPlace"), "BreakPlace", () -> {
      return ((String)this.p.getValue()).equals("General");
   });
   IntegerSetting updateDelay = this.registerInteger("UpdateDelay", 25, 0, 1000, () -> {
      return ((String)this.p.getValue()).equals("General");
   });
   BooleanSetting place = this.registerBoolean("Place", true, () -> {
      return ((String)this.p.getValue()).equals("General");
   });
   BooleanSetting calc = this.registerBoolean("CalcHitVec", true, () -> {
      return ((String)this.p.getValue()).equals("General");
   });
   BooleanSetting y256 = this.registerBoolean("Y256", true, () -> {
      return ((String)this.p.getValue()).equals("General");
   });
   IntegerSetting placeDelay = this.registerInteger("PlaceDelay", 25, 0, 1000, () -> {
      return (Boolean)this.place.getValue() && ((String)this.p.getValue()).equals("General");
   });
   BooleanSetting explode = this.registerBoolean("Explode", true, () -> {
      return ((String)this.p.getValue()).equals("General");
   });
   IntegerSetting hitDelay = this.registerInteger("HitDelay", 25, 0, 1000, () -> {
      return (Boolean)this.explode.getValue() && ((String)this.p.getValue()).equals("General");
   });
   BooleanSetting antiWeakness = this.registerBoolean("AntiWeakness", false, () -> {
      return (Boolean)this.explode.getValue() && ((String)this.p.getValue()).equals("General");
   });
   BooleanSetting silentAntiWeak = this.registerBoolean("SilentAntiWeakness", false, () -> {
      return (Boolean)this.antiWeakness.getValue() && ((String)this.p.getValue()).equals("General");
   });
   BooleanSetting weakBypass = this.registerBoolean("BypassSilent", false, () -> {
      return (Boolean)this.antiWeakness.getValue() && (Boolean)this.silentAntiWeak.getValue() && ((String)this.p.getValue()).equals("General");
   });
   BooleanSetting packetWeak = this.registerBoolean("PacketSwitch", false, () -> {
      return (Boolean)this.antiWeakness.getValue() && (Boolean)this.silentAntiWeak.getValue() && !(Boolean)this.weakBypass.getValue() && ((String)this.p.getValue()).equals("General");
   });
   BooleanSetting wall = this.registerBoolean("Wall", true, () -> {
      return ((String)this.p.getValue()).equals("General");
   });
   BooleanSetting wallAI = this.registerBoolean("WallAI", true, () -> {
      return (Boolean)this.wall.getValue() && ((String)this.p.getValue()).equals("General");
   });
   IntegerSetting enemyRange = this.registerInteger("EnemyRange", 7, 1, 16, () -> {
      return ((String)this.p.getValue()).equals("General");
   });
   DoubleSetting placeRange = this.registerDouble("PlaceRange", 5.5D, 0.0D, 6.0D, () -> {
      return (Boolean)this.place.getValue() && ((String)this.p.getValue()).equals("General");
   });
   DoubleSetting placeWallRange = this.registerDouble("PlaceWallRange", 3.0D, 0.1D, 6.0D, () -> {
      return !(Boolean)this.wallAI.getValue() && ((String)this.p.getValue()).equals("General");
   });
   DoubleSetting breakRange = this.registerDouble("BreakRange", 5.5D, 0.0D, 6.0D, () -> {
      return (Boolean)this.explode.getValue() && ((String)this.p.getValue()).equals("General");
   });
   IntegerSetting breakMinDmg = this.registerInteger("BreakMinDmg", 2, 0, 36, () -> {
      return (Boolean)this.explode.getValue() && ((String)this.p.getValue()).equals("General");
   });
   DoubleSetting breakWallRange = this.registerDouble("BreakWallRange", 3.0D, 0.1D, 6.0D, () -> {
      return ((String)this.p.getValue()).equals("General");
   });
   DoubleSetting minDamage = this.registerDouble("MinDmg", 4.0D, 0.0D, 36.0D, () -> {
      return ((String)this.p.getValue()).equals("General");
   });
   DoubleSetting maxDamage = this.registerDouble("MaxDmg", 12.0D, 0.0D, 36.0D, () -> {
      return ((String)this.p.getValue()).equals("General");
   });
   ModeSetting godMode = this.registerMode("SelfDamage", Arrays.asList("Auto", "GodMode", "NoGodMode"), "Auto", () -> {
      return ((String)this.p.getValue()).equals("General");
   });
   BooleanSetting forcePlace = this.registerBoolean("ForcePlace", false, () -> {
      return ((String)this.p.getValue()).equals("General");
   });
   DoubleSetting maxSelfDMG = this.registerDouble("MaxSelfDmg", 12.0D, 0.0D, 36.0D, () -> {
      return ((String)this.p.getValue()).equals("General") && !((String)this.godMode.getValue()).equals("GodMode");
   });
   DoubleSetting balance = this.registerDouble("HealthBalance", 1.5D, 0.0D, 10.0D, () -> {
      return ((String)this.p.getValue()).equals("General") && !((String)this.godMode.getValue()).equals("GodMode");
   });
   ModeSetting switchMode = this.registerMode("SwitchMode", Arrays.asList("AutoSwitch", "Off"), "Off", () -> {
      return ((String)this.p.getValue()).equals("Combat");
   });
   BooleanSetting offhand = this.registerBoolean("Offhand", false, () -> {
      return ((String)this.p.getValue()).equals("Combat") && ((String)this.switchMode.getValue()).equals("AutoSwitch");
   });
   BooleanSetting switchBack = this.registerBoolean("SwitchBack", true, () -> {
      return ((String)this.p.getValue()).equals("Combat") && ((String)this.switchMode.getValue()).equals("AutoSwitch") && !(Boolean)this.offhand.getValue();
   });
   BooleanSetting bypass = this.registerBoolean("Bypass", false, () -> {
      return ((String)this.p.getValue()).equals("Combat") && ((String)this.switchMode.getValue()).equals("AutoSwitch") && !(Boolean)this.offhand.getValue() && (Boolean)this.switchBack.getValue();
   });
   DoubleSetting switchSpeed = this.registerDouble("SwitchSpeed", 10.0D, 0.1D, 20.0D, () -> {
      return ((String)this.p.getValue()).equals("Combat") && ((String)this.switchMode.getValue()).equals("AutoSwitch") && !(Boolean)this.offhand.getValue() && (Boolean)this.switchBack.getValue() && (Boolean)this.bypass.getValue();
   });
   BooleanSetting forceUpdate = this.registerBoolean("ForceUpdate", false, () -> {
      return ((String)this.p.getValue()).equals("Combat") && !(Boolean)this.offhand.getValue() && (Boolean)this.switchBack.getValue() && (Boolean)this.bypass.getValue();
   });
   BooleanSetting packetSwitch = this.registerBoolean("PacketSwitch", false, () -> {
      return ((String)this.p.getValue()).equals("Combat") && !(Boolean)this.offhand.getValue() && (Boolean)this.switchBack.getValue();
   });
   BooleanSetting packet = this.registerBoolean("PacketCrystal", true, () -> {
      return ((String)this.p.getValue()).equals("Combat");
   });
   BooleanSetting rotate = this.registerBoolean("Rotate", true, () -> {
      return ((String)this.p.getValue()).equals("Combat");
   });
   BooleanSetting placeRotate = this.registerBoolean("PlaceRotate", true, () -> {
      return ((String)this.p.getValue()).equals("Combat") && (Boolean)this.rotate.getValue();
   });
   BooleanSetting swing = this.registerBoolean("Swing", true, () -> {
      return ((String)this.p.getValue()).equals("Combat");
   });
   BooleanSetting packetSwing = this.registerBoolean("PacketSwing", false, () -> {
      return ((String)this.p.getValue()).equals("Combat") && (Boolean)this.swing.getValue();
   });
   BooleanSetting crystalCheck = this.registerBoolean("CrystalCheck", false, () -> {
      return ((String)this.p.getValue()).equals("Combat");
   });
   BooleanSetting highVersion = this.registerBoolean("1.13Place", false, () -> {
      return ((String)this.p.getValue()).equals("Combat");
   });
   BooleanSetting PacketExplode = this.registerBoolean("PacketExplode", true, () -> {
      return ((String)this.p.getValue()).equals("Combat");
   });
   IntegerSetting PacketExplodeDelay = this.registerInteger("PacketExplodeDelay", 45, 0, 500, () -> {
      return (Boolean)this.PacketExplode.getValue() && ((String)this.p.getValue()).equals("Combat");
   });
   BooleanSetting ClientSide = this.registerBoolean("ClientSide", false, () -> {
      return ((String)this.p.getValue()).equals("Combat");
   });
   BooleanSetting PredictHit = this.registerBoolean("PredictHit", false, () -> {
      return ((String)this.p.getValue()).equals("Combat");
   });
   IntegerSetting PredictHitFactor = this.registerInteger("PredictHitFactor", 2, 1, 20, () -> {
      return (Boolean)this.PredictHit.getValue() && ((String)this.p.getValue()).equals("Combat");
   });
   BooleanSetting target = this.registerBoolean("Target", true, () -> {
      return ((String)this.p.getValue()).equals("Predict");
   });
   BooleanSetting self = this.registerBoolean("Self", true, () -> {
      return ((String)this.p.getValue()).equals("Predict");
   });
   IntegerSetting tickPredict = this.registerInteger("TickPredict", 8, 0, 30, () -> {
      return ((String)this.p.getValue()).equals("Predict");
   });
   BooleanSetting calculateYPredict = this.registerBoolean("CalculateYPredict", true, () -> {
      return ((String)this.p.getValue()).equals("Predict");
   });
   IntegerSetting startDecrease = this.registerInteger("StartDecrease", 39, 0, 200, () -> {
      return ((String)this.p.getValue()).equals("Predict") && (Boolean)this.calculateYPredict.getValue();
   });
   IntegerSetting exponentStartDecrease = this.registerInteger("ExponentStart", 2, 1, 5, () -> {
      return ((String)this.p.getValue()).equals("Predict") && (Boolean)this.calculateYPredict.getValue();
   });
   IntegerSetting decreaseY = this.registerInteger("DecreaseY", 2, 1, 5, () -> {
      return ((String)this.p.getValue()).equals("Predict") && (Boolean)this.calculateYPredict.getValue();
   });
   IntegerSetting exponentDecreaseY = this.registerInteger("ExponentDecreaseY", 1, 1, 3, () -> {
      return ((String)this.p.getValue()).equals("Predict") && (Boolean)this.calculateYPredict.getValue();
   });
   BooleanSetting splitXZ = this.registerBoolean("SplitXZ", true, () -> {
      return ((String)this.p.getValue()).equals("Predict");
   });
   BooleanSetting manualOutHole = this.registerBoolean("ManualOutHole", false, () -> {
      return ((String)this.p.getValue()).equals("Predict");
   });
   BooleanSetting aboveHoleManual = this.registerBoolean("AboveHoleManual", false, () -> {
      return ((String)this.p.getValue()).equals("Predict") && (Boolean)this.manualOutHole.getValue();
   });
   BooleanSetting stairPredict = this.registerBoolean("StairPredict", false, () -> {
      return ((String)this.p.getValue()).equals("Predict");
   });
   IntegerSetting nStair = this.registerInteger("NStair", 2, 1, 4, () -> {
      return ((String)this.p.getValue()).equals("Predict") && (Boolean)this.stairPredict.getValue();
   });
   DoubleSetting speedActivationStair = this.registerDouble("SpeedActivationStair", 0.11D, 0.0D, 1.0D, () -> {
      return ((String)this.p.getValue()).equals("Predict") && (Boolean)this.stairPredict.getValue();
   });
   BooleanSetting onlyInHole = this.registerBoolean("InHole Only", false, () -> {
      return ((String)this.p.getValue()).equals("Dev");
   });
   BooleanSetting sword = this.registerBoolean("Pause While Swording", false, () -> {
      return ((String)this.p.getValue()).equals("Dev");
   });
   BooleanSetting pause = this.registerBoolean("PausePistonAura", true, () -> {
      return ((String)this.p.getValue()).equals("Dev");
   });
   BooleanSetting eat = this.registerBoolean("WhileEating", true, () -> {
      return ((String)this.p.getValue()).equals("Dev");
   });
   ModeSetting mode = this.registerMode("Mode", Arrays.asList("Solid", "Both", "Outline"), "Full", () -> {
      return ((String)this.p.getValue()).equals("Render");
   });
   BooleanSetting showDamage = this.registerBoolean("ShowDamage", false, () -> {
      return ((String)this.p.getValue()).equals("Render");
   });
   BooleanSetting flat = this.registerBoolean("Flat", true, () -> {
      return ((String)this.p.getValue()).equals("Render");
   });
   ColorSetting color = this.registerColor("Color", new GSColor(255, 255, 255), () -> {
      return ((String)this.p.getValue()).equals("Render");
   });
   IntegerSetting alpha = this.registerInteger("Alpha", 50, 0, 255, () -> {
      return ((String)this.p.getValue()).equals("Render");
   });
   IntegerSetting outAlpha = this.registerInteger("OutlineAlpha", 125, 0, 255, () -> {
      return ((String)this.p.getValue()).equals("Render");
   });
   BooleanSetting move = this.registerBoolean("Move", true, () -> {
      return ((String)this.p.getValue()).equals("Render");
   });
   DoubleSetting movingSpeed = this.registerDouble("MovingSpeed", 0.1D, 0.01D, 0.5D, () -> {
      return ((String)this.p.getValue()).equals("Render") && (Boolean)this.move.getValue();
   });
   BooleanSetting reset = this.registerBoolean("Reset", true, () -> {
      return ((String)this.p.getValue()).equals("Render") && (Boolean)this.move.getValue();
   });
   BooleanSetting fade = this.registerBoolean("Fade", true, () -> {
      return ((String)this.p.getValue()).equals("Render");
   });
   IntegerSetting fadeAlpha = this.registerInteger("FadeAlpha", 50, 0, 255, () -> {
      return ((String)this.p.getValue()).equals("Render") && (Boolean)this.fade.getValue();
   });
   IntegerSetting fadeOutAlpha = this.registerInteger("FadeOutlineAlpha", 125, 0, 255, () -> {
      return ((String)this.p.getValue()).equals("Render") && (Boolean)this.fade.getValue();
   });
   IntegerSetting lifeTime = this.registerInteger("LifeTime", 3000, 0, 5000, () -> {
      return ((String)this.p.getValue()).equals("Render") && (Boolean)this.fade.getValue();
   });
   BooleanSetting scale = this.registerBoolean("BoxScale", true, () -> {
      return ((String)this.p.getValue()).equals("Render") && ((Boolean)this.move.getValue() || (Boolean)this.fade.getValue());
   });
   DoubleSetting growSpeed = this.registerDouble("BoxGrowSpeed", 0.1D, 0.01D, 0.5D, () -> {
      return ((String)this.p.getValue()).equals("Render") && this.scale.isVisible() && (Boolean)this.scale.getValue();
   });
   DoubleSetting reduceSpeed = this.registerDouble("BoxReduceSpeed", 0.1D, 0.01D, 0.5D, () -> {
      return ((String)this.p.getValue()).equals("Render") && this.scale.isVisible() && (Boolean)this.scale.getValue();
   });
   FacePlace.managerClassRenderBlocks managerRenderBlocks = new FacePlace.managerClassRenderBlocks();
   PredictUtil.PredictSettings settings;
   List<Short> shorts = new ArrayList();
   Timing PacketExplodeTimer = new Timing();
   Timing ExplodeTimer = new Timing();
   Timing UpdateTimer = new Timing();
   Timing PlaceTimer = new Timing();
   Timing CalcTimer = new Timing();
   EntityEnderCrystal lastCrystal;
   Vec3d movingPlaceNow = new Vec3d(-1.0D, -1.0D, -1.0D);
   BlockPos lastBestPlace = null;
   BlockPos render;
   BlockPos webPos;
   boolean ShouldInfoLastBreak = false;
   boolean afterAttacking = false;
   boolean canPredictHit = false;
   boolean calculated;
   int lastEntityID = -1;
   int placements = 0;
   int StuckTimes = 0;
   int crystalSlot;
   double size = -1.0D;
   float damage;
   Vec3d lastHitVec;
   Timing timer = new Timing();
   @EventHandler
   private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener((event) -> {
      if (event.getPhase() == Phase.PRE && this.lastHitVec != null && (Boolean)this.rotate.getValue()) {
         PlayerPacket packet = new PlayerPacket(this, RotationUtil.getRotationTo(this.lastHitVec));
         PlayerPacketManager.INSTANCE.addPacket(packet);
      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.Send> sendListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if ((Boolean)this.rotate.getValue() && this.lastHitVec != null) {
            Vec2f vec = RotationUtil.getRotationTo(this.lastHitVec);
            if (event.getPacket() instanceof Rotation) {
               ((Rotation)event.getPacket()).field_149476_e = vec.field_189982_i;
               ((Rotation)event.getPacket()).field_149473_f = vec.field_189983_j;
            }

            if (event.getPacket() instanceof PositionRotation) {
               ((PositionRotation)event.getPacket()).field_149476_e = vec.field_189982_i;
               ((PositionRotation)event.getPacket()).field_149473_f = vec.field_189983_j;
            }
         }

      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         Iterator var3;
         Entity e;
         if (event.getPacket() instanceof SPacketSpawnObject) {
            SPacketSpawnObject packet = (SPacketSpawnObject)event.getPacket();
            if ((Boolean)this.PredictHit.getValue()) {
               var3 = mc.field_71441_e.field_72996_f.iterator();

               label106:
               while(true) {
                  do {
                     if (!var3.hasNext()) {
                        break label106;
                     }

                     e = (Entity)var3.next();
                  } while(!(e instanceof EntityItem) && !(e instanceof EntityArrow) && !(e instanceof EntityEnderPearl) && !(e instanceof EntitySnowball) && !(e instanceof EntityEgg));

                  if (e.func_70011_f(packet.func_186880_c(), packet.func_186882_d(), packet.func_186881_e()) <= 6.0D) {
                     this.lastEntityID = -1;
                     this.canPredictHit = false;
                     event.cancel();
                  }
               }
            }

            if (packet.func_148993_l() == 51) {
               this.lastEntityID = packet.func_149001_c();
               EntityEnderCrystal crystal = (EntityEnderCrystal)mc.field_71441_e.func_73045_a(this.lastEntityID);
               if (crystal != null && (Boolean)this.PacketExplode.getValue() && this.PacketExplodeTimer.passedMs((long)(Integer)this.PacketExplodeDelay.getValue()) && (Boolean)this.explode.getValue() && this.lastCrystal != null && renderEnt != null) {
                  if ((Boolean)this.wall.getValue() && (double)mc.field_71439_g.func_70032_d(this.lastCrystal) > (Double)this.breakWallRange.getValue() && !CrystalUtil.calculateRaytrace((Entity)this.lastCrystal)) {
                     return;
                  }

                  if (this.canHitCrystal(crystal)) {
                     this.PacketExplode(this.lastEntityID);
                     this.PacketExplodeTimer.reset();
                  }
               }
            }
         }

         if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packetx = (SPacketSoundEffect)event.getPacket();
            if (packetx.func_186978_a().equals(SoundEvents.field_187601_be) || packetx.func_186978_a().equals(SoundEvents.field_187635_cQ)) {
               this.canPredictHit = false;
            }

            EntityPlayer entity;
            if (packetx.func_186978_a().equals(SoundEvents.field_191263_gW)) {
               for(var3 = mc.field_71441_e.field_73010_i.iterator(); var3.hasNext(); renderEnt = entity) {
                  entity = (EntityPlayer)var3.next();
               }
            }

            if (packetx.func_186977_b() == SoundCategory.BLOCKS && packetx.func_186978_a() == SoundEvents.field_187539_bB && this.render != null) {
               this.ShouldInfoLastBreak = true;
               var3 = (new ArrayList(mc.field_71441_e.field_72996_f)).iterator();

               while(var3.hasNext()) {
                  e = (Entity)var3.next();
                  if (e instanceof EntityEnderCrystal && e.func_70011_f(packetx.func_149207_d(), packetx.func_149211_e(), packetx.func_149210_f()) <= 6.0D) {
                     e.func_70106_y();
                  }
               }
            }
         }

      }
   }, new Predicate[0]);
   boolean tryCalc;
   BlockPos blockPos;

   public void windowClick(int windowId, int slotId, int mouseButton, ClickType type, ItemStack itemstack, EntityPlayer player, short id) {
      player.field_71070_bA.func_184996_a(slotId, mouseButton, type, player);
   }

   private void switchTo(int slot, boolean bypass, boolean shouldSwitch, boolean back, Runnable runnable) {
      int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
      if (shouldSwitch && slot >= 0 && slot != oldslot) {
         if (bypass) {
            if (this.timer.passedMs((long)(1000.0D / (Double)this.switchSpeed.getValue()))) {
               this.timer.reset();
               if (slot < 9) {
                  slot += 36;
               }

               short id = mc.field_71439_g.field_71069_bz.func_75136_a(mc.field_71439_g.field_71071_by);
               this.shorts.add(id);
               if (!(Boolean)this.packetSwitch.getValue()) {
                  this.windowClick(0, slot, oldslot, ClickType.SWAP, ItemStack.field_190927_a, mc.field_71439_g, id);
               }

               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketClickWindow(0, slot, oldslot, ClickType.SWAP, ItemStack.field_190927_a, id));
               runnable.run();
               mc.field_71439_g.field_71070_bA.func_75142_b();
               id = mc.field_71439_g.field_71069_bz.func_75136_a(mc.field_71439_g.field_71071_by);
               this.shorts.add(id);
               if (!(Boolean)this.packetSwitch.getValue()) {
                  this.windowClick(0, slot, oldslot, ClickType.SWAP, Items.field_185158_cP.func_190903_i(), mc.field_71439_g, id);
               }

               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketClickWindow(0, slot, oldslot, ClickType.SWAP, (Boolean)this.forceUpdate.getValue() ? Items.field_185158_cP.func_190903_i() : ItemStack.field_190927_a, id));
               mc.field_71439_g.field_71070_bA.func_75142_b();
            }
         } else if (slot < 9) {
            boolean packetSwitch = back && (Boolean)this.packetSwitch.getValue();
            if (packetSwitch) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
            } else {
               mc.field_71439_g.field_71071_by.field_70461_c = slot;
            }

            runnable.run();
            if (back) {
               if (packetSwitch) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(oldslot));
               } else {
                  mc.field_71439_g.field_71071_by.field_70461_c = oldslot;
               }
            }
         }

      } else {
         runnable.run();
      }
   }

   public static double getRange(Vec3d a, double x, double y, double z) {
      double xl = a.field_72450_a - x;
      double yl = a.field_72448_b - y;
      double zl = a.field_72449_c - z;
      return Math.sqrt(xl * xl + yl * yl + zl * zl);
   }

   public void onTick() {
      if (this.tryCalc) {
         if (this.UpdateTimer.passedMs((long)(Integer)this.updateDelay.getValue())) {
            FacePlace.CrystalTarget crystalTarget = this.Calc();
            renderEnt = crystalTarget.target;
            this.render = crystalTarget.blockPos;
            this.damage = (float)crystalTarget.dmg;
            if (renderEnt == null || this.render == null) {
               if ((Boolean)this.reset.getValue()) {
                  this.lastBestPlace = null;
               }

               this.damage = 0.0F;
               this.render = null;
               this.switchOffhand(false);
               this.pausePA(false);
               this.lastHitVec = null;
               return;
            }

            if (renderEnt instanceof EntityPlayer) {
               AutoEz.INSTANCE.addTargetedPlayer(renderEnt.func_70005_c_());
            }

            this.UpdateTimer.reset();
         }

      }
   }

   public void fast() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         this.managerRenderBlocks.update((Integer)this.lifeTime.getValue());
         if (this.CalcTimer.passedMs(1000L)) {
            this.CalcTimer.reset();
            this.calculated = true;
         }

         this.crystalSlot = this.getItemHotbar();
         if (this.crystalSlot == -1 && (!(Boolean)this.offhand.getValue() || !((String)this.switchMode.getValue()).equals("AutoSwitch")) && mc.field_71439_g.func_184592_cb().func_77973_b() != Items.field_185158_cP) {
            if ((Boolean)this.reset.getValue()) {
               this.lastBestPlace = null;
            }

            this.damage = 0.0F;
            renderEnt = null;
            this.render = null;
            this.switchOffhand(false);
            this.pausePA(false);
            this.lastHitVec = null;
            this.tryCalc = false;
         } else {
            this.tryCalc = true;
            if (renderEnt != null && this.render != null) {
               if (!(Boolean)this.eat.getValue() && EntityUtil.isEating()) {
                  this.lastHitVec = null;
               } else {
                  if (((String)this.logic.getValue()).equals("BreakPlace")) {
                     this.explode();
                     this.place((Boolean)this.crystalCheck.getValue());
                  } else {
                     this.place((Boolean)this.crystalCheck.getValue());
                     this.explode();
                  }

               }
            }
         }
      }
   }

   public void explode() {
      if (this.damage != 0.0F) {
         EntityEnderCrystal crystal = (EntityEnderCrystal)mc.field_71441_e.field_72996_f.stream().filter((e) -> {
            return e instanceof EntityEnderCrystal && !e.field_70128_L && this.canHitCrystal((EntityEnderCrystal)e);
         }).map((e) -> {
            return (EntityEnderCrystal)e;
         }).min(Comparator.comparing((e) -> {
            return mc.field_71439_g.func_70032_d(e);
         })).orElse((Object)null);
         if (mc.field_71439_g != null && crystal != null && renderEnt != null) {
            if ((Boolean)this.explode.getValue() && (double)mc.field_71439_g.func_70032_d(crystal) <= (Double)this.breakRange.getValue()) {
               this.lastCrystal = crystal;
               if ((Boolean)this.wall.getValue() && !((double)mc.field_71439_g.func_70032_d(crystal) <= (Double)this.breakWallRange.getValue()) && !CrystalUtil.calculateRaytrace((Entity)crystal)) {
                  this.afterAttacking = false;
                  ++this.StuckTimes;
               } else {
                  if (this.StuckTimes > 0) {
                     this.StuckTimes = 0;
                  }

                  this.lastHitVec = new Vec3d(crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v);
                  this.ExplodeCrystal(this.lastCrystal);
                  this.afterAttacking = true;
               }
            }

            if ((Boolean)this.ClientSide.getValue()) {
               Iterator var2 = mc.field_71441_e.func_72910_y().iterator();

               while(var2.hasNext()) {
                  Entity o = (Entity)var2.next();
                  if (o instanceof EntityEnderCrystal && o.func_70011_f(o.field_70165_t, o.field_70163_u, o.field_70161_v) <= 6.0D) {
                     o.func_70106_y();
                  }
               }

               mc.field_71441_e.func_73022_a();
            }
         }

      }
   }

   private boolean in(int number, int floor, int ceil) {
      return number >= floor && number <= ceil;
   }

   private boolean crystalPlaceBoxIntersectsCrystalBox(BlockPos placePos, Double x, Double y, Double z) {
      return this.in(x.intValue() - placePos.field_177960_b, 0, 2) && this.in(y.intValue() - placePos.field_177962_a, -1, 1) && this.in(z.intValue() - placePos.field_177961_c, -1, 1);
   }

   private void place(boolean check) {
      if ((Boolean)this.place.getValue() && this.render != null) {
         boolean useOffhand = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP;
         if (mc.field_71439_g.field_71071_by.field_70461_c != this.crystalSlot && !useOffhand) {
            if (!((String)this.switchMode.getValue()).equals("AutoSwitch")) {
               return;
            }

            if ((Boolean)this.offhand.getValue()) {
               this.switchOffhand(true);
               return;
            }
         }

         this.pausePA((Boolean)this.pause.getValue());
         if (this.PlaceTimer.passedMs((long)(Integer)this.placeDelay.getValue())) {
            boolean detected = true;
            Iterator var4 = (new ArrayList(mc.field_71441_e.field_72996_f)).iterator();

            while(var4.hasNext()) {
               Entity entity = (Entity)var4.next();
               if (entity instanceof EntityEnderCrystal && !entity.field_70128_L && this.crystalPlaceBoxIntersectsCrystalBox(this.render, entity.field_70165_t, entity.field_70163_u, entity.field_70161_v)) {
                  detected = false;
               }
            }

            if (detected || !check) {
               EnumHand hand = useOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
               EnumFacing facing = (Boolean)this.y256.getValue() && this.render.func_177956_o() == 255 ? EnumFacing.DOWN : EnumFacing.UP;
               if ((Boolean)this.calc.getValue()) {
                  RayTraceResult result = mc.field_71441_e.func_72933_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d((double)this.render.func_177958_n() + 0.5D, (double)this.render.func_177956_o() + 0.5D, (double)this.render.func_177952_p() + 0.5D));
                  if (result != null && result.field_178784_b != null) {
                     facing = result.field_178784_b;
                  }
               }

               EnumFacing opposite = facing.func_176734_d();
               Vec3d vec = (new Vec3d(this.render)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e(new Vec3d(opposite.func_176730_m()));
               this.lastHitVec = (Boolean)this.placeRotate.getValue() ? vec : new Vec3d((double)this.render.field_177962_a + 0.5D, (double)(this.render.field_177960_b + 1), (double)this.render.field_177961_c + 0.5D);
               this.switchTo(this.crystalSlot, this.findInventory(), !useOffhand && ((String)this.switchMode.getValue()).equals("AutoSwitch"), (Boolean)this.switchBack.getValue(), () -> {
                  if ((Boolean)this.packet.getValue()) {
                     mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(this.render, facing, hand, 0.0F, 0.0F, 0.0F));
                  } else {
                     mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, this.render, facing, vec, hand);
                  }

               });
               if ((Boolean)this.swing.getValue()) {
                  if ((Boolean)this.packetSwing.getValue()) {
                     mc.field_71439_g.field_71174_a.func_147297_a(new CPacketAnimation(hand));
                  } else {
                     mc.field_71439_g.func_184609_a(hand);
                  }
               }

               ++this.placements;
               this.PlaceTimer.reset();
            }
         }

         if ((Boolean)this.PredictHit.getValue() && renderEnt != null && DamageUtil.calculateCrystalDamage((EntityLivingBase)renderEnt, (double)this.render.field_177962_a + 0.5D, (double)(this.render.field_177960_b + 1), (double)this.render.field_177961_c + 0.5D) > (float)(Integer)this.breakMinDmg.getValue()) {
            try {
               if (renderEnt.field_70128_L || !this.canPredictHit || (Boolean)this.wall.getValue() && (double)mc.field_71439_g.func_70032_d(this.lastCrystal) > (Double)this.breakWallRange.getValue() && !CrystalUtil.calculateRaytrace((Entity)this.lastCrystal)) {
                  this.PlaceTimer.reset();
                  return;
               }

               if ((Boolean)this.wall.getValue() && (double)mc.field_71439_g.func_70032_d(this.lastCrystal) > (Double)this.breakWallRange.getValue() && !CrystalUtil.calculateRaytrace((Entity)this.lastCrystal)) {
                  return;
               }

               if ((double)(mc.field_71439_g.func_110143_aJ() + mc.field_71439_g.func_110139_bj()) > (Double)this.maxSelfDMG.getValue() && this.lastEntityID != -1 && this.lastCrystal != null && this.canPredictHit) {
                  for(int i = 0; i < (Integer)this.PredictHitFactor.getValue(); ++i) {
                     this.PacketExplode(this.lastEntityID + i + 2);
                  }
               }
            } catch (Exception var9) {
            }
         }
      }

   }

   public FacePlace.CrystalTarget Calc() {
      if ((Boolean)this.sword.getValue() && mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemSword) {
         return new FacePlace.CrystalTarget((BlockPos)null, PlayerUtil.getNearestPlayer((double)(Integer)this.enemyRange.getValue()), 0.0D);
      } else {
         List<EntityLivingBase> entities = this.getEntities();
         double damage = 0.0D;
         EntityLivingBase renderEnt = null;
         BlockPos render = null;
         BlockPos setToAir = null;
         IBlockState state = null;
         List default_blocks;
         if ((Boolean)this.wall.getValue() && (Boolean)this.wallAI.getValue()) {
            double TempRange = (Double)this.placeRange.getValue();
            double temp2 = TempRange - (double)this.StuckTimes * 0.5D;
            if (this.StuckTimes > 0) {
               TempRange = (Double)this.placeRange.getValue();
               if (temp2 > (Double)this.placeWallRange.getValue()) {
                  TempRange = temp2;
               } else if ((Double)this.placeWallRange.getValue() < (Double)this.placeRange.getValue()) {
                  TempRange = 3.0D;
               }
            }

            default_blocks = this.renditions(TempRange);
         } else {
            default_blocks = this.renditions((Double)this.placeRange.getValue());
         }

         this.settings = new PredictUtil.PredictSettings((Integer)this.tickPredict.getValue(), (Boolean)this.calculateYPredict.getValue(), (Integer)this.startDecrease.getValue(), (Integer)this.exponentStartDecrease.getValue(), (Integer)this.decreaseY.getValue(), (Integer)this.exponentDecreaseY.getValue(), (Boolean)this.splitXZ.getValue(), (Boolean)this.manualOutHole.getValue(), (Boolean)this.aboveHoleManual.getValue(), (Boolean)this.stairPredict.getValue(), (Integer)this.nStair.getValue(), (Double)this.speedActivationStair.getValue());
         EntityPlayer player = mc.field_71439_g;
         if ((Boolean)this.self.getValue()) {
            player = PredictUtil.predictPlayer((EntityLivingBase)player, this.settings);
         }

         Iterator var14 = entities.iterator();

         label191:
         while(var14.hasNext()) {
            EntityLivingBase entity2 = (EntityLivingBase)var14.next();
            if (!(((EntityLivingBase)entity2).func_110143_aJ() <= 0.0F) && !((EntityLivingBase)entity2).field_70128_L) {
               if ((Boolean)this.target.getValue()) {
                  entity2 = PredictUtil.predictPlayer((EntityLivingBase)entity2, this.settings);
               }

               BlockPos playerPos = new BlockPos(((EntityLivingBase)entity2).func_174791_d());
               Block web = mc.field_71441_e.func_180495_p(playerPos).func_177230_c();
               if (web == Blocks.field_150321_G) {
                  setToAir = playerPos;
                  state = mc.field_71441_e.func_180495_p(playerPos);
                  mc.field_71441_e.func_175698_g(playerPos);
               }

               BlockPos vec = new BlockPos(((EntityLivingBase)entity2).field_70165_t, ((EntityLivingBase)entity2).field_70163_u, ((EntityLivingBase)entity2).field_70161_v);
               Vec3d doubleTargetPos = new Vec3d(vec);
               List<BlockPos> legBlocks = this.findLegBlocks(doubleTargetPos);
               this.canPredictHit = (!(Boolean)this.PredictHit.getValue() || !((EntityLivingBase)entity2).func_184614_ca().func_77973_b().equals(Items.field_151062_by)) && !((EntityLivingBase)entity2).func_184592_cb().func_77973_b().equals(Items.field_151062_by) || !ModuleManager.getModule("AutoMend").isEnabled();
               legBlocks.addAll(default_blocks);
               Iterator var21 = legBlocks.iterator();

               while(true) {
                  BlockPos blockPos;
                  double d;
                  while(true) {
                     do {
                        do {
                           do {
                              do {
                                 do {
                                    do {
                                       do {
                                          if (!var21.hasNext()) {
                                             if (setToAir != null) {
                                                mc.field_71441_e.func_175656_a(setToAir, state);
                                                this.webPos = render;
                                             }

                                             if (renderEnt != null) {
                                                break label191;
                                             }
                                             continue label191;
                                          }

                                          blockPos = (BlockPos)var21.next();
                                       } while(this.intersectsWithEntity(blockPos.func_177984_a()));
                                    } while(this.intersectsWithEntity(blockPos.func_177981_b(2)));
                                 } while(blockPos.equals(vec));
                              } while(((EntityLivingBase)entity2).func_174818_b(blockPos) >= (double)((Integer)this.enemyRange.getValue() * (Integer)this.enemyRange.getValue()));
                           } while(mc.field_71439_g.func_70011_f((double)blockPos.func_177958_n(), (double)blockPos.func_177956_o(), (double)blockPos.func_177952_p()) > (Double)this.placeRange.getValue());
                        } while((Boolean)this.wall.getValue() && PlayerUtil.getDistanceI(blockPos) > (Double)this.placeWallRange.getValue() && !CrystalUtil.calculateRaytrace(blockPos));

                        d = (double)DamageUtil.calculateCrystalDamage((EntityLivingBase)entity2, (double)blockPos.func_177958_n() + 0.5D, (double)(blockPos.func_177956_o() + 1), (double)blockPos.func_177952_p() + 0.5D);
                        if (d > (Double)this.maxDamage.getValue()) {
                           return new FacePlace.CrystalTarget((BlockPos)null, PlayerUtil.getNearestPlayer((double)(Integer)this.enemyRange.getValue()), 0.0D);
                        }
                     } while(d < damage);

                     float healthTarget = ((EntityLivingBase)entity2).func_110143_aJ() + ((EntityLivingBase)entity2).func_110139_bj();
                     float healthSelf = mc.field_71439_g.func_110143_aJ() + mc.field_71439_g.func_110139_bj();
                     double self = (double)DamageUtil.calculateCrystalDamage((EntityLivingBase)player, (double)blockPos.func_177958_n() + 0.5D, (double)(blockPos.func_177956_o() + 1), (double)blockPos.func_177952_p() + 0.5D);
                     String var29 = (String)this.godMode.getValue();
                     byte var30 = -1;
                     switch(var29.hashCode()) {
                     case 2052559:
                        if (var29.equals("Auto")) {
                           var30 = 1;
                        }
                        break;
                     case 1860843551:
                        if (var29.equals("GodMode")) {
                           var30 = 0;
                        }
                     }

                     switch(var30) {
                     case 0:
                        self = 0.0D;
                        break;
                     case 1:
                        if (mc.field_71439_g.func_184812_l_()) {
                           self = 0.0D;
                        }
                     }

                     if (d >= (double)healthTarget) {
                        if (self == 0.0D || !(self + (Double)this.balance.getValue() >= (double)healthSelf) || (Boolean)this.forcePlace.getValue()) {
                           break;
                        }
                     } else if (!(d < (Double)this.minDamage.getValue()) && (!(self > d) || !(d < (double)healthTarget)) && (self == 0.0D || !(self + (Double)this.balance.getValue() >= (double)healthSelf)) && (self == 0.0D || !(self + (Double)this.balance.getValue() >= (Double)this.maxSelfDMG.getValue()))) {
                        break;
                     }
                  }

                  damage = d;
                  render = blockPos;
                  renderEnt = entity2;
               }
            }
         }

         if (renderEnt == null) {
            renderEnt = PlayerUtil.getNearestPlayer((double)(Integer)this.enemyRange.getValue());
         }

         return new FacePlace.CrystalTarget(render, (Entity)renderEnt, damage);
      }
   }

   private void switchOffhand(boolean value) {
      if (ModuleManager.isModuleEnabled(OffHand.class)) {
         OffHand.INSTANCE.autoCrystal = value;
      }

      if (ModuleManager.isModuleEnabled(OffHandCat.class)) {
         OffHandCat.INSTANCE.autoCrystal = value;
      }

   }

   private void pausePA(boolean value) {
      if (ModuleManager.isModuleEnabled(PistonAura.class)) {
         PistonAura.INSTANCE.autoCrystal = value;
      }

      if (ModuleManager.isModuleEnabled(PullCrystal.class)) {
         PullCrystal.INSTANCE.autoCrystal = value;
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
      } while(entity instanceof EntityItem || entity instanceof EntityEnderCrystal || !(new AxisAlignedBB(pos)).func_72326_a(entity.func_174813_aQ()));

      return true;
   }

   public List<EntityLivingBase> getEntities() {
      return (List)mc.field_71441_e.field_73010_i.stream().filter((entity) -> {
         return mc.field_71439_g.func_70032_d(entity) < (float)(Integer)this.enemyRange.getValue();
      }).filter((entity) -> {
         return !EntityUtil.basicChecksEntity(entity);
      }).sorted(Comparator.comparingDouble((entity) -> {
         return (double)entity.func_70032_d(mc.field_71439_g);
      })).filter((entity) -> {
         return !(Boolean)this.onlyInHole.getValue() || HoleUtil.isInHole(entity, false, false, false);
      }).collect(Collectors.toList());
   }

   public void ExplodeCrystal(Entity crystal) {
      if (crystal != null && this.ExplodeTimer.passedMs((long)(Integer)this.hitDelay.getValue()) && mc.func_147114_u() != null) {
         this.PacketExplode(crystal.func_145782_y());
         EnumHand hand = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
         if ((Boolean)this.swing.getValue()) {
            if ((Boolean)this.packetSwing.getValue()) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketAnimation(hand));
            } else {
               mc.field_71439_g.func_184609_a(hand);
            }
         }

         mc.field_71439_g.func_184821_cY();
         this.ExplodeTimer.reset();
      }

   }

   public void PacketExplode(int i) {
      if (this.lastCrystal != null && renderEnt != null) {
         try {
            if ((double)mc.field_71439_g.func_70032_d(this.lastCrystal) > (Double)this.breakRange.getValue() || !this.canHitCrystal(this.lastCrystal)) {
               return;
            }

            int slot = -1;
            if ((Boolean)this.antiWeakness.getValue() && mc.field_71439_g.func_70644_a(MobEffects.field_76437_t) && (!mc.field_71439_g.func_70644_a(MobEffects.field_76420_g) || ((PotionEffect)Objects.requireNonNull(mc.field_71439_g.func_70660_b(MobEffects.field_76420_g))).func_76458_c() < 1)) {
               for(int b = 0; b < (this.findInventory() ? 36 : 9); ++b) {
                  ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(b);
                  if (stack != ItemStack.field_190927_a) {
                     if (stack.func_77973_b() instanceof ItemSword) {
                        slot = b;
                        break;
                     }

                     if (stack.func_77973_b() instanceof ItemTool) {
                        slot = b;
                        break;
                     }
                  }
               }
            }

            this.switchTo(slot, (Boolean)this.weakBypass.getValue(), true, (Boolean)this.packetWeak.getValue(), () -> {
               CPacketUseEntity crystal = new CPacketUseEntity();
               setEntityId(crystal, i);
               setAction(crystal, Action.ATTACK);
               mc.field_71439_g.field_71174_a.func_147297_a(crystal);
            });
         } catch (Exception var5) {
         }
      }

   }

   public static void setEntityId(CPacketUseEntity packet, int entityId) {
      ((AccessorCPacketUseEntity)packet).setId(entityId);
   }

   public static void setAction(CPacketUseEntity packet, Action action) {
      ((AccessorCPacketUseEntity)packet).setAction(action);
   }

   public boolean canHitCrystal(EntityEnderCrystal crystal) {
      if ((double)mc.field_71439_g.func_70032_d(crystal) > (Double)this.breakRange.getValue()) {
         return false;
      } else {
         float healthSelf = mc.field_71439_g.func_110143_aJ() + mc.field_71439_g.func_110139_bj();
         if (!mc.field_71439_g.field_70128_L && !(healthSelf <= 0.0F)) {
            float selfDamage = DamageUtil.calculateCrystalDamage((EntityLivingBase)((Boolean)this.self.getValue() ? PredictUtil.predictPlayer(mc.field_71439_g, this.settings) : mc.field_71439_g), crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v);
            String var4 = (String)this.godMode.getValue();
            byte var5 = -1;
            switch(var4.hashCode()) {
            case 2052559:
               if (var4.equals("Auto")) {
                  var5 = 1;
               }
               break;
            case 1860843551:
               if (var4.equals("GodMode")) {
                  var5 = 0;
               }
            }

            switch(var5) {
            case 0:
               selfDamage = 0.0F;
               break;
            case 1:
               if (mc.field_71439_g.func_184812_l_()) {
                  selfDamage = 0.0F;
               }
            }

            if (selfDamage != 0.0F && (double)selfDamage + (Double)this.balance.getValue() >= (double)healthSelf) {
               return false;
            } else if (this.render == null || !(new AxisAlignedBB(this.render)).func_72326_a(crystal.func_174813_aQ()) && !(new AxisAlignedBB(this.render.func_177984_a())).func_72326_a(crystal.func_174813_aQ())) {
               Stream var10000 = mc.field_71441_e.field_73010_i.stream().filter((p) -> {
                  return !EntityUtil.basicChecksEntity(p);
               });
               crystal.getClass();
               List<EntityPlayer> entities = (List)var10000.sorted(Comparator.comparing(crystal::func_70032_d)).collect(Collectors.toList());
               Iterator var12 = entities.iterator();

               double target;
               double minDamage;
               do {
                  if (!var12.hasNext()) {
                     return false;
                  }

                  EntityPlayer player = (EntityPlayer)var12.next();
                  target = (double)DamageUtil.calculateCrystalDamage(player, crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v);
                  if (target > (double)(player.func_110143_aJ() + player.func_110139_bj())) {
                     return true;
                  }

                  minDamage = (double)(Integer)this.breakMinDmg.getValue();
               } while(target < minDamage || (double)selfDamage > target);

               return true;
            } else {
               return true;
            }
         } else {
            return false;
         }
      }
   }

   public static List<BlockPos> getSphere(Vec3d loc, double r, double h, boolean hollow, boolean sphere, int plus_y) {
      List<BlockPos> circleBlocks = new ArrayList();
      int cx = (int)loc.field_72450_a;
      int cy = (int)loc.field_72448_b;
      int cz = (int)loc.field_72449_c;

      for(int x = cx - (int)r; (double)x <= (double)cx + r; ++x) {
         for(int z = cz - (int)r; (double)z <= (double)cz + r; ++z) {
            for(int y = sphere ? cy - (int)r : cy; (double)y < (sphere ? (double)cy + r : (double)cy + h); ++y) {
               double dist = (double)((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0));
               if (dist < r * r && (!hollow || !(dist < (r - 1.0D) * (r - 1.0D)))) {
                  BlockPos l = new BlockPos(x, y + plus_y, z);
                  circleBlocks.add(l);
               }
            }
         }
      }

      return circleBlocks;
   }

   public List<BlockPos> renditions(double range) {
      NonNullList<BlockPos> positions = NonNullList.func_191196_a();
      positions.addAll((Collection)getSphere(this.getPlayerPos(), range, range, false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
      return positions;
   }

   public static List<BlockPos> getLegVec(Vec3d add) {
      List<BlockPos> circleBlocks = new ArrayList();
      BlockPos uwu = new BlockPos(add.field_72450_a, add.field_72448_b, add.field_72449_c);
      circleBlocks.add(uwu);
      return circleBlocks;
   }

   private List<BlockPos> findLegBlocks(Vec3d targetPos) {
      NonNullList<BlockPos> positions = NonNullList.func_191196_a();
      positions.addAll((Collection)getLegVec(targetPos.func_72441_c(0.0D, 0.0D, 0.0D)).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
      return positions;
   }

   public Vec3d getPlayerPos() {
      return new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
   }

   public boolean canPlaceCrystal(BlockPos blockPos) {
      BlockPos boost = blockPos.func_177982_a(0, 1, 0);
      BlockPos boost2 = blockPos.func_177982_a(0, 2, 0);
      if (mc.field_71441_e.func_180495_p(boost).func_177230_c() != Blocks.field_150355_j && mc.field_71441_e.func_180495_p(boost).func_177230_c() != Blocks.field_150392_bi && mc.field_71441_e.func_180495_p(boost).func_177230_c() != Blocks.field_150358_i && mc.field_71441_e.func_180495_p(boost).func_177230_c() != Blocks.field_189877_df && mc.field_71441_e.func_180495_p(boost).func_177230_c() != Blocks.field_150353_l && mc.field_71441_e.func_180495_p(boost).func_177230_c() != Blocks.field_150356_k) {
         if (mc.field_71441_e.func_180495_p(boost2).func_177230_c() != Blocks.field_150355_j && mc.field_71441_e.func_180495_p(boost2).func_177230_c() != Blocks.field_150392_bi && mc.field_71441_e.func_180495_p(boost2).func_177230_c() != Blocks.field_150358_i && mc.field_71441_e.func_180495_p(boost2).func_177230_c() != Blocks.field_189877_df && mc.field_71441_e.func_180495_p(boost2).func_177230_c() != Blocks.field_150353_l && mc.field_71441_e.func_180495_p(boost2).func_177230_c() != Blocks.field_150356_k) {
            if (mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150357_h && mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150343_Z) {
               return false;
            } else {
               if ((Boolean)this.highVersion.getValue()) {
                  if (mc.field_71441_e.func_180495_p(boost).func_177230_c() != Blocks.field_150350_a) {
                     return false;
                  }
               } else if (mc.field_71441_e.func_180495_p(boost).func_177230_c() != Blocks.field_150350_a || mc.field_71441_e.func_180495_p(boost2).func_177230_c() != Blocks.field_150350_a) {
                  return false;
               }

               Iterator var4 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost)).iterator();

               Entity entity;
               while(var4.hasNext()) {
                  entity = (Entity)var4.next();
                  if (!(entity instanceof EntityEnderCrystal)) {
                     return false;
                  }
               }

               var4 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost2)).iterator();

               while(var4.hasNext()) {
                  entity = (Entity)var4.next();
                  if (!(entity instanceof EntityEnderCrystal)) {
                     return false;
                  }
               }

               this.webCalc();
               if (this.afterAttacking) {
                  var4 = mc.field_71441_e.field_72996_f.iterator();

                  while(var4.hasNext()) {
                     entity = (Entity)var4.next();
                     if (entity instanceof EntityEnderCrystal) {
                        EntityEnderCrystal entityEnderCrystal = (EntityEnderCrystal)entity;
                        if (!(Math.abs(entityEnderCrystal.field_70163_u - (double)(blockPos.func_177956_o() + 1)) >= 2.0D)) {
                           double d2 = this.lastCrystal != null ? this.lastCrystal.func_70011_f((double)blockPos.func_177958_n() + 0.5D, (double)(blockPos.func_177956_o() + 1), (double)blockPos.func_177952_p() + 0.5D) : 10000.0D;
                           if (!(d2 <= 6.0D) && !(getRange(entityEnderCrystal.func_174791_d(), (double)blockPos.func_177958_n() + 0.5D, (double)(blockPos.func_177956_o() + 1), (double)blockPos.func_177952_p() + 0.5D) >= 2.0D)) {
                              return false;
                           }
                        }
                     }
                  }
               }

               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void webCalc() {
      if (this.webPos != null) {
         if (mc.field_71439_g.func_174818_b(this.webPos) > MathUtil.square((Double)this.breakRange.getValue())) {
            this.webPos = null;
         } else {
            Iterator var1 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(this.webPos)).iterator();

            while(var1.hasNext()) {
               Entity entity = (Entity)var1.next();
               if (entity instanceof EntityEnderCrystal) {
                  this.webPos = null;
                  break;
               }
            }
         }
      }

   }

   private int getItemHotbar() {
      for(int i = 0; i < (this.findInventory() ? 36 : 9); ++i) {
         Item item = mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
         if (Item.func_150891_b(item) == Item.func_150891_b(Items.field_185158_cP)) {
            return i;
         }
      }

      return -1;
   }

   public void onEnable() {
      this.lastEntityID = -1;
      this.size = 0.0D;
      this.ShouldInfoLastBreak = false;
      this.afterAttacking = false;
      this.canPredictHit = true;
      this.PlaceTimer.reset();
      this.ExplodeTimer.reset();
      this.PacketExplodeTimer.reset();
      this.UpdateTimer.reset();
      this.CalcTimer.reset();
      this.timer = new Timing();
      if ((Boolean)this.reset.getValue()) {
         this.lastBestPlace = null;
         this.managerRenderBlocks.blocks.clear();
      }

   }

   public void onDisable() {
      this.switchOffhand(false);
      this.pausePA(false);
      this.lastHitVec = null;
      renderEnt = null;
      this.render = null;
      this.StuckTimes = 0;
      if ((Boolean)this.reset.getValue()) {
         this.lastBestPlace = null;
         this.managerRenderBlocks.blocks.clear();
      }

   }

   private boolean findInventory() {
      return (Boolean)this.bypass.getValue() && (Boolean)this.switchBack.getValue();
   }

   public String getHudInfo() {
      return renderEnt == null ? "" : "[" + ChatFormatting.WHITE + renderEnt.func_70005_c_() + ChatFormatting.GRAY + "]";
   }

   public void onWorldRender(RenderEvent event) {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         if (this.render != null) {
            this.blockPos = this.render;
            if ((Boolean)this.move.getValue()) {
               this.lastBestPlace = this.render;
            } else {
               this.drawBoxMain((double)this.blockPos.field_177962_a, (double)this.blockPos.field_177960_b, (double)this.blockPos.field_177961_c);
            }

            if ((Boolean)this.fade.getValue()) {
               this.managerRenderBlocks.addRender(this.render);
            }
         }

         this.managerRenderBlocks.render();
         if ((Boolean)this.move.getValue()) {
            if (this.lastBestPlace != null) {
               if (this.movingPlaceNow.field_72450_a == -1.0D && this.movingPlaceNow.field_72448_b == -1.0D && this.movingPlaceNow.field_72449_c == -1.0D) {
                  this.movingPlaceNow = new Vec3d((double)((float)this.lastBestPlace.func_177958_n()), (double)((float)this.lastBestPlace.func_177956_o()), (double)((float)this.lastBestPlace.func_177952_p()));
               }

               this.movingPlaceNow = new Vec3d(this.movingPlaceNow.field_72450_a + ((double)this.lastBestPlace.func_177958_n() - this.movingPlaceNow.field_72450_a) * (double)((Double)this.movingSpeed.getValue()).floatValue(), this.movingPlaceNow.field_72448_b + ((double)this.lastBestPlace.func_177956_o() - this.movingPlaceNow.field_72448_b) * (double)((Double)this.movingSpeed.getValue()).floatValue(), this.movingPlaceNow.field_72449_c + ((double)this.lastBestPlace.func_177952_p() - this.movingPlaceNow.field_72449_c) * (double)((Double)this.movingSpeed.getValue()).floatValue());
               if (Math.abs(this.movingPlaceNow.field_72450_a - (double)this.lastBestPlace.func_177958_n()) <= 0.125D && Math.abs(this.movingPlaceNow.field_72448_b - (double)this.lastBestPlace.func_177956_o()) <= 0.125D && Math.abs(this.movingPlaceNow.field_72449_c - (double)this.lastBestPlace.func_177952_p()) <= 0.125D) {
                  this.lastBestPlace = null;
               }
            }

            if (this.movingPlaceNow.field_72450_a != -1.0D && this.movingPlaceNow.field_72448_b != -1.0D && this.movingPlaceNow.field_72449_c != -1.0D) {
               this.drawBoxMain(this.movingPlaceNow.field_72450_a, this.movingPlaceNow.field_72448_b, this.movingPlaceNow.field_72449_c);
            }

         }
      }
   }

   AxisAlignedBB getBox(double x, double y, double z) {
      double maxX = x + 1.0D;
      double maxZ = z + 1.0D;
      return new AxisAlignedBB(x, y, z, maxX, y + 1.0D, maxZ);
   }

   void drawBoxMain(double x, double y, double z) {
      AxisAlignedBB box = this.getBox(x, y, z);
      if ((Boolean)this.scale.getValue() && this.scale.isVisible()) {
         if (renderEnt != null && this.render != null && !(Math.abs(x - (double)this.render.field_177962_a) > 0.5D) && !(Math.abs(z - (double)this.render.field_177961_c) > 0.5D)) {
            if (this.size != 1.0D) {
               this.size += (Double)this.growSpeed.getValue();
            }
         } else {
            this.size -= (Double)this.reduceSpeed.getValue();
         }

         if (this.size > 1.0D) {
            this.size = 1.0D;
         }

         if (this.size < 0.0D) {
            this.size = 0.0D;
         }

         if ((renderEnt == null || this.render == null) && this.size == 0.0D) {
            this.movingPlaceNow = new Vec3d(-1.0D, -1.0D, -1.0D);
         }

         box = box.func_186662_g((1.0D - this.size) * (1.0D - this.size) / 2.0D - 1.0D);
      } else if (renderEnt == null || this.render == null) {
         this.movingPlaceNow = new Vec3d(-1.0D, -1.0D, -1.0D);
      }

      if ((Boolean)this.flat.getValue()) {
         box = new AxisAlignedBB(box.field_72340_a, box.field_72337_e, box.field_72339_c, box.field_72336_d, box.field_72337_e, box.field_72334_f);
      }

      String var8 = (String)this.mode.getValue();
      byte var9 = -1;
      switch(var8.hashCode()) {
      case 2076577:
         if (var8.equals("Both")) {
            var9 = 2;
         }
         break;
      case 80066187:
         if (var8.equals("Solid")) {
            var9 = 1;
         }
         break;
      case 558407714:
         if (var8.equals("Outline")) {
            var9 = 0;
         }
      }

      switch(var9) {
      case 0:
         RenderUtil.drawBoundingBox(box, 1.0D, new GSColor(this.color.getValue(), (Integer)this.outAlpha.getValue()));
         break;
      case 1:
         RenderUtil.drawBox(box, true, (Boolean)this.flat.getValue() ? 0.0D : 1.0D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 63);
         break;
      case 2:
         RenderUtil.drawBox(box, true, (Boolean)this.flat.getValue() ? 0.0D : 1.0D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 63);
         RenderUtil.drawBoundingBox(box, 1.0D, new GSColor(this.color.getValue(), (Integer)this.outAlpha.getValue()));
      }

      if ((Boolean)this.showDamage.getValue()) {
         box = this.getBox(x, y, z);
         String[] damageText = new String[]{String.format("%.1f", this.damage)};
         RenderUtil.drawNametag(box.field_72340_a + 0.5D, box.field_72338_b + 0.5D, box.field_72339_c + 0.5D, damageText, new GSColor(255, 255, 255), 1, 0.02666666666666667D, 0.0D);
      }

   }

   void drawBoxMain(double x, double y, double z, double percent) {
      int alpha = (int)((double)(Integer)this.fadeAlpha.getValue() * percent);
      int outAlpha = (int)((double)(Integer)this.fadeOutAlpha.getValue() * percent);
      AxisAlignedBB box = this.getBox(x, y, z);
      if ((Boolean)this.scale.getValue() && this.scale.isVisible()) {
         box = box.func_186662_g((1.0D - percent) * (1.0D - percent) / 2.0D - 1.0D);
      }

      if ((Boolean)this.flat.getValue()) {
         box = new AxisAlignedBB(box.field_72340_a, box.field_72337_e, box.field_72339_c, box.field_72336_d, box.field_72337_e, box.field_72334_f);
      }

      String var12 = (String)this.mode.getValue();
      byte var13 = -1;
      switch(var12.hashCode()) {
      case 2076577:
         if (var12.equals("Both")) {
            var13 = 2;
         }
         break;
      case 80066187:
         if (var12.equals("Solid")) {
            var13 = 1;
         }
         break;
      case 558407714:
         if (var12.equals("Outline")) {
            var13 = 0;
         }
      }

      switch(var13) {
      case 0:
         RenderUtil.drawBoundingBox(box, 1.0D, new GSColor(this.color.getValue(), outAlpha));
         break;
      case 1:
         RenderUtil.drawBox(box, true, (Boolean)this.flat.getValue() ? 0.0D : 1.0D, new GSColor(this.color.getValue(), alpha), 63);
         break;
      case 2:
         RenderUtil.drawBox(box, true, (Boolean)this.flat.getValue() ? 0.0D : 1.0D, new GSColor(this.color.getValue(), alpha), 63);
         RenderUtil.drawBoundingBox(box, 1.0D, new GSColor(this.color.getValue(), outAlpha));
      }

   }

   boolean sameBlockPos(BlockPos first, BlockPos second) {
      if (first != null && second != null) {
         return first.func_177958_n() == second.func_177958_n() && first.func_177956_o() == second.func_177956_o() && first.func_177952_p() == second.func_177952_p();
      } else {
         return false;
      }
   }

   class renderBlock {
      private final BlockPos pos;
      private long start = System.currentTimeMillis();

      public renderBlock(BlockPos pos) {
         this.pos = pos;
      }

      void resetTime() {
         this.start = System.currentTimeMillis();
      }

      void render() {
         FacePlace.this.drawBoxMain((double)this.pos.field_177962_a, (double)this.pos.field_177960_b, (double)this.pos.field_177961_c, this.percent());
      }

      public double percent() {
         long end = this.start + (long)(Integer)FacePlace.this.lifeTime.getValue();
         double result = (double)(end - System.currentTimeMillis()) / (double)(end - this.start);
         if (result < 0.0D) {
            result = 0.0D;
         }

         if (result > 1.0D) {
            result = 1.0D;
         }

         return result;
      }
   }

   class managerClassRenderBlocks {
      ArrayList<FacePlace.renderBlock> blocks = new ArrayList();

      void update(int time) {
         this.blocks.removeIf((e) -> {
            return System.currentTimeMillis() - e.start > (long)time;
         });
      }

      void render() {
         this.blocks.forEach((e) -> {
            if (FacePlace.this.render != null && FacePlace.this.sameBlockPos(e.pos, FacePlace.this.render)) {
               e.resetTime();
            } else {
               e.render();
            }

         });
      }

      void addRender(BlockPos pos) {
         boolean render = true;
         Iterator var3 = this.blocks.iterator();

         while(var3.hasNext()) {
            FacePlace.renderBlock block = (FacePlace.renderBlock)var3.next();
            if (FacePlace.this.sameBlockPos(block.pos, pos)) {
               render = false;
               block.resetTime();
               break;
            }
         }

         if (render) {
            this.blocks.add(FacePlace.this.new renderBlock(pos));
         }

      }
   }

   public static class CrystalTarget {
      public BlockPos blockPos;
      public Entity target;
      public double dmg;

      public CrystalTarget(BlockPos block, Entity target, double dmg) {
         this.blockPos = block;
         this.target = target;
         this.dmg = dmg;
      }
   }
}
