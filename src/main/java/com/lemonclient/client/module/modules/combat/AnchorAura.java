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
import net.minecraft.block.BlockGlowstone;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
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
   name = "AnchorAura",
   category = Category.Combat,
   priority = 999
)
public class AnchorAura extends Module {
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
   BooleanSetting packetPlace = this.registerBoolean("Packet Place", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting swing = this.registerBoolean("Swing", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting packetSwing = this.registerBoolean("Packet Swing", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting rotate = this.registerBoolean("Rotate", true, () -> {
      return ((String)this.page.getValue()).equals("General");
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
   BooleanSetting placeInAir = this.registerBoolean("Place In Air", true, () -> {
      return ((String)this.page.getValue()).equals("Base");
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
   BooleanSetting update = this.registerBoolean("Update", true, () -> {
      return ((String)this.page.getValue()).equals("Switch");
   });
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true, () -> {
      return ((String)this.page.getValue()).equals("Switch");
   });
   BooleanSetting slowFP = this.registerBoolean("Slow Face Place", true, () -> {
      return ((String)this.page.getValue()).equals("SlowFacePlace");
   });
   IntegerSetting slowPlaceDelay = this.registerInteger("SlowFP Place Delay", 500, 0, 1000, () -> {
      return (Boolean)this.slowFP.getValue() && ((String)this.page.getValue()).equals("SlowFacePlace");
   });
   DoubleSetting slowMinDmg = this.registerDouble("SlowFP Min Dmg", 0.05D, 0.0D, 36.0D, () -> {
      return (Boolean)this.slowFP.getValue() && ((String)this.page.getValue()).equals("SlowFacePlace");
   });
   BooleanSetting showDamage = this.registerBoolean("Render Dmg", true, () -> {
      return ((String)this.page.getValue()).equals("Render");
   });
   ColorSetting color = this.registerColor("Color", new GSColor(255, 0, 0, 50), () -> {
      return ((String)this.page.getValue()).equals("Render");
   });
   IntegerSetting alpha = this.registerInteger("Alpha", 60, 0, 255, () -> {
      return ((String)this.page.getValue()).equals("Render");
   });
   IntegerSetting outAlpha = this.registerInteger("Outline Alpha", 120, 0, 255, () -> {
      return ((String)this.page.getValue()).equals("Render");
   });
   IntegerSetting width = this.registerInteger("Width", 1, 1, 10, () -> {
      return ((String)this.page.getValue()).equals("Render");
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
   ModeSetting hudDisplay = this.registerMode("HUD", Arrays.asList("Target", "Damage", "Both", "None"), "None", () -> {
      return ((String)this.page.getValue()).equals("Render");
   });
   HashMap<EntityPlayer, AnchorAura.MoveRotation> playerSpeed = new HashMap();
   AnchorAura.EntityInfo target = null;
   BlockPos placePos;
   float damage;
   float selfDamage;
   Vec3d movingPosNow = new Vec3d(-1.0D, -1.0D, -1.0D);
   BlockPos lastBestPlace = null;
   BlockPos lastBestPos = null;
   Timing calctiming = new Timing();
   Timing timing = new Timing();
   Timing updatetiming = new Timing();
   int anchorSlot;
   int glowSlot;
   int maxPredict;
   Vec2f rotation;
   int nowSlot;
   @EventHandler
   private final Listener<PacketEvent.Send> postSendListener = new Listener((event) -> {
      if (event.getPacket() instanceof CPacketHeldItemChange) {
         this.nowSlot = ((CPacketHeldItemChange)event.getPacket()).func_149614_c();
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.Send> sendListener = new Listener((event) -> {
      if ((Boolean)this.rotate.getValue() && this.rotation != null) {
         if (event.getPacket() instanceof Rotation) {
            ((Rotation)event.getPacket()).field_149476_e = this.rotation.field_189982_i;
            ((Rotation)event.getPacket()).field_149473_f = this.rotation.field_189983_j;
         }

         if (event.getPacket() instanceof PositionRotation) {
            ((PositionRotation)event.getPacket()).field_149476_e = this.rotation.field_189982_i;
            ((PositionRotation)event.getPacket()).field_149473_f = this.rotation.field_189983_j;
         }
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener((event) -> {
      if ((Boolean)this.rotate.getValue() && this.rotation != null && event.getPhase() == Phase.PRE) {
         PlayerPacket packet = new PlayerPacket(this, this.rotation);
         PlayerPacketManager.INSTANCE.addPacket(packet);
      }
   }, new Predicate[0]);

   public static boolean isAnchor(ItemStack stack) {
      return stack.func_77973_b() instanceof ItemBlock && ((ItemBlock)stack.func_77973_b()).func_179223_d() instanceof BlockObsidian && stack.func_82833_r().equals("§f1.16 Respawn Anchor");
   }

   public void onUpdate() {
      if (mc.field_71439_g != null && mc.field_71441_e != null && !EntityUtil.isDead(mc.field_71439_g) && !this.inNether() && this.anchorSlot != -1 && this.glowSlot != -1) {
         Iterator var1 = mc.field_71441_e.field_73010_i.iterator();

         while(var1.hasNext()) {
            EntityPlayer player = (EntityPlayer)var1.next();
            if (!(mc.field_71439_g.func_70068_e(player) > (double)((Integer)this.enemyRange.getValue() * (Integer)this.enemyRange.getValue()))) {
               double lastYaw = 512.0D;
               int tick = (Integer)this.startTick.getValue();
               if (this.playerSpeed.get(player) != null) {
                  AnchorAura.MoveRotation info = (AnchorAura.MoveRotation)this.playerSpeed.get(player);
                  lastYaw = info.yaw;
                  tick = info.tick + (Integer)this.addTick.getValue();
               }

               if (tick > this.maxPredict) {
                  tick = this.maxPredict;
               }

               this.playerSpeed.put(player, new AnchorAura.MoveRotation(player, lastYaw, tick));
            }
         }

         this.calc();
      } else {
         this.target = null;
         this.placePos = null;
         this.damage = this.selfDamage = 0.0F;
         this.rotation = null;
      }
   }

   public void fast() {
      if (mc.field_71439_g != null && mc.field_71441_e != null && !EntityUtil.isDead(mc.field_71439_g) && !this.inNether()) {
         if (this.updatetiming.passedMs((long)(Integer)this.updateDelay.getValue())) {
            this.updatetiming.reset();
            this.maxPredict = (Boolean)this.detect.getValue() ? (int)(mc.func_147104_D() == null ? 0L : mc.func_147104_D().field_78844_e * 2L / 50L) : (Integer)this.tickPredict.getValue();
         }

         this.anchorSlot = this.glowSlot = -1;

         for(int i = 0; i < 9; ++i) {
            ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (this.anchorSlot == -1 && isAnchor(stack)) {
               this.anchorSlot = i;
            }

            if (this.glowSlot == -1 && stack.func_77973_b() instanceof ItemBlock && ((ItemBlock)stack.func_77973_b()).func_179223_d() instanceof BlockGlowstone) {
               this.glowSlot = i;
            }
         }

         if (this.anchorSlot != -1 && this.glowSlot != -1) {
            this.bedaura();
         }
      }
   }

   private void bedaura() {
      if (this.placePos != null) {
         this.rotation = RotationUtil.getRotationTo(new Vec3d((double)this.placePos.field_177962_a + 0.5D, (double)this.placePos.field_177960_b + 0.5D, (double)this.placePos.field_177961_c + 0.5D));
         this.place();
      }
   }

   private void calc() {
      if (this.calctiming.passedMs((long)(Integer)this.calcDelay.getValue())) {
         this.calctiming.reset();
         this.target = null;
         this.placePos = null;
         this.damage = this.selfDamage = 0.0F;
         this.rotation = null;
         AnchorAura.EntityInfo self = new AnchorAura.EntityInfo(mc.field_71439_g, (Boolean)this.selfPredict.getValue());
         AnchorAura.PlaceInfo placeInfo = this.getPlaceInfo(self, this.findBlocksExcluding());
         if (placeInfo == null) {
            return;
         }

         this.target = placeInfo.target;
         if (ModuleManager.isModuleEnabled("AutoEz")) {
            AutoEz.INSTANCE.addTargetedPlayer(this.target.defaultPlayer.func_70005_c_());
         }

         BlockPos bedPos = placeInfo.placePos;
         if (bedPos == null) {
            return;
         }

         this.damage = placeInfo.damage;
         this.selfDamage = placeInfo.selfDamage;
         this.placePos = bedPos;
      }

   }

   private void place() {
      if (this.timing.passedMs((long)this.getPlaceDelay())) {
         EnumFacing side = BurrowUtil.getFirstFacing(this.placePos);
         if (side == null) {
            if (!(Boolean)this.placeInAir.getValue()) {
               return;
            }

            side = EnumFacing.DOWN;
         }

         BlockPos neighbour = this.placePos.func_177972_a(side);
         EnumFacing opposite = side.func_176734_d();
         Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
         if (BlockUtil.blackList.contains(mc.field_71441_e.func_180495_p(neighbour).func_177230_c()) && !ColorMain.INSTANCE.sneaking) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
         }

         int oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
         if (oldSlot != this.anchorSlot) {
            this.switchTo(this.anchorSlot);
         }

         if ((Boolean)this.packetPlace.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(neighbour, EnumFacing.UP, EnumHand.MAIN_HAND, 0.5F, 1.0F, 0.5F));
         } else {
            mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, neighbour, EnumFacing.UP, hitVec, EnumHand.MAIN_HAND);
         }

         if (((ColorMain)ModuleManager.getModule(ColorMain.class)).sneaking) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
         }

         side = EnumFacing.UP;
         Vec3d facing = this.getHitVecOffset(side);
         this.switchTo(this.glowSlot);
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(this.placePos, side, EnumHand.MAIN_HAND, (float)facing.field_72450_a, (float)facing.field_72448_b, (float)facing.field_72449_c));
         if (oldSlot == this.glowSlot) {
            this.switchTo(this.anchorSlot);
         } else {
            this.switchTo(oldSlot);
         }

         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(this.placePos, side, EnumHand.MAIN_HAND, (float)facing.field_72450_a, (float)facing.field_72448_b, (float)facing.field_72449_c));
         if ((Boolean)this.swing.getValue()) {
            this.swing();
         }

         this.timing.reset();
      }

   }

   private AnchorAura.PlaceInfo getPlaceInfo(AnchorAura.EntityInfo self, List<BlockPos> posList) {
      AnchorAura.PlaceInfo placeInfo = null;
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
      AnchorAura.EntityInfo player;
      EntityPlayer entityPlayer;
      Iterator var21;
      switch(var6) {
      case 0:
         EntityPlayer entityPlayer = (EntityPlayer)playerList.stream().min(Comparator.comparing((p) -> {
            return mc.field_71439_g.func_70032_d(p);
         })).orElse((Object)null);
         if (entityPlayer != null) {
            AnchorAura.EntityInfo player = new AnchorAura.EntityInfo(entityPlayer, (Boolean)this.predict.getValue());
            placeInfo = this.calculateBestPlacement(player, self, posList);
         }
         break;
      case 1:
         AnchorAura.PlaceInfo best = null;
         var8 = playerList.iterator();

         while(true) {
            AnchorAura.PlaceInfo info;
            do {
               do {
                  if (!var8.hasNext()) {
                     placeInfo = best;
                     return placeInfo;
                  }

                  player = (EntityPlayer)var8.next();
               } while(player == null);

               player = new AnchorAura.EntityInfo(player, (Boolean)this.predict.getValue());
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
                     placeInfo = this.calculateBestPlacement(new AnchorAura.EntityInfo(player, (Boolean)this.predict.getValue()), self, posList);
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
         AnchorAura.PlaceInfo best = null;
         if (target != null) {
            player = new AnchorAura.EntityInfo(target, (Boolean)this.predict.getValue());
            best = this.calculateBestPlacement(player, self, posList);
         }

         if (best == null) {
            var21 = playerList.iterator();

            label87:
            while(true) {
               AnchorAura.PlaceInfo info;
               do {
                  do {
                     if (!var21.hasNext()) {
                        break label87;
                     }

                     entityPlayer = (EntityPlayer)var21.next();
                  } while(entityPlayer == null);

                  AnchorAura.EntityInfo player = new AnchorAura.EntityInfo(entityPlayer, (Boolean)this.predict.getValue());
                  info = this.calculateBestPlacement(player, self, posList);
               } while(best != null && !(info.damage > best.damage));

               best = info;
            }
         }

         placeInfo = best;
      }

      return placeInfo;
   }

   private List<BlockPos> findBlocksExcluding() {
      return (List)EntityUtil.getSphere(PlayerUtil.getEyesPos(), (Double)this.range.getValue() + 1.0D, (Double)this.yRange.getValue(), false, false, 0).stream().filter(this::block).collect(Collectors.toList());
   }

   private AnchorAura.PlaceInfo calculateBestPlacement(AnchorAura.EntityInfo target, AnchorAura.EntityInfo self, List<BlockPos> blocks) {
      AnchorAura.PlaceInfo best = new AnchorAura.PlaceInfo(target, (BlockPos)null, (float)Math.min(Math.min((Double)this.minDmg.getValue(), (Double)this.slowMinDmg.getValue()), this.fpMinDmg.getMin()), -1.0F);
      if (target != null && self != null) {
         Iterator var5 = blocks.iterator();

         while(true) {
            BlockPos pos;
            float targetDamage;
            float selfDamage;
            while(true) {
               double x;
               double y;
               double z;
               while(true) {
                  do {
                     if (!var5.hasNext()) {
                        return best;
                     }

                     pos = (BlockPos)var5.next();
                     x = (double)pos.func_177958_n() + 0.5D;
                     y = (double)pos.func_177956_o() + 0.5D;
                     z = (double)pos.func_177952_p() + 0.5D;
                     targetDamage = DamageUtil.calculateDamage(target.player, x, y, z, 5.0F, "Bed");
                  } while(targetDamage <= best.damage);

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

            best = new AnchorAura.PlaceInfo(target, pos, targetDamage, selfDamage);
         }
      } else {
         return best;
      }
   }

   private int getPlaceDelay() {
      return (double)this.damage >= (Double)this.minDmg.getValue() ? (Integer)this.placeDelay.getValue() : (Integer)this.slowPlaceDelay.getValue();
   }

   private boolean block(BlockPos pos) {
      return BlockUtil.canReplace(pos) && this.inRange(pos);
   }

   private boolean inRange(BlockPos pos) {
      double x = (double)pos.field_177962_a - mc.field_71439_g.field_70165_t;
      double z = (double)pos.field_177961_c - mc.field_71439_g.field_70161_v;
      double y = (double)(pos.field_177960_b - PlayerUtil.getEyesPos().field_177960_b);
      double add = Math.sqrt(y * y) / 2.0D;
      return x * x + z * z <= ((Double)this.range.getValue() - add) * ((Double)this.range.getValue() - add) && y * y <= (Double)this.yRange.getValue() * (Double)this.yRange.getValue();
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

   private void swing() {
      if ((Boolean)this.packetSwing.getValue()) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketAnimation(EnumHand.MAIN_HAND));
      } else {
         mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
      }

   }

   private boolean inNether() {
      return mc.field_71439_g.field_71093_bK == -1;
   }

   public void onEnable() {
      this.calctiming.reset();
      this.timing.reset();
      this.updatetiming.reset();
      if ((Boolean)this.reset.getValue()) {
         this.lastBestPlace = null;
         this.lastBestPos = null;
         this.movingPosNow = new Vec3d(-1.0D, -1.0D, -1.0D);
      }

   }

   public void onDisable() {
      this.placePos = null;
      if ((Boolean)this.reset.getValue()) {
         this.lastBestPlace = null;
         this.lastBestPos = null;
         this.movingPosNow = new Vec3d(-1.0D, -1.0D, -1.0D);
      }

   }

   public void onWorldRender(RenderEvent event) {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         if (this.placePos != null) {
            if ((Boolean)this.anime.getValue()) {
               this.lastBestPlace = this.placePos;
            } else {
               this.drawBoxMain((double)this.placePos.field_177962_a, (double)this.placePos.field_177960_b, (double)this.placePos.field_177961_c);
            }
         }

         if ((Boolean)this.anime.getValue()) {
            if (this.lastBestPlace != null) {
               if (this.movingPosNow.field_72450_a == -1.0D && this.movingPosNow.field_72448_b == -1.0D && this.movingPosNow.field_72449_c == -1.0D) {
                  this.movingPosNow = new Vec3d((double)((float)this.lastBestPlace.func_177958_n()), (double)((float)this.lastBestPlace.func_177956_o()), (double)((float)this.lastBestPlace.func_177952_p()));
               }

               this.movingPosNow = new Vec3d(this.movingPosNow.field_72450_a + ((double)this.lastBestPlace.func_177958_n() - this.movingPosNow.field_72450_a) * (double)((Double)this.movingPlaceSpeed.getValue()).floatValue(), this.movingPosNow.field_72448_b + ((double)this.lastBestPlace.func_177956_o() - this.movingPosNow.field_72448_b) * (double)((Double)this.movingPlaceSpeed.getValue()).floatValue(), this.movingPosNow.field_72449_c + ((double)this.lastBestPlace.func_177952_p() - this.movingPosNow.field_72449_c) * (double)((Double)this.movingPlaceSpeed.getValue()).floatValue());
               if (Math.abs(this.movingPosNow.field_72450_a - (double)this.lastBestPlace.func_177958_n()) <= 0.125D && Math.abs(this.movingPosNow.field_72448_b - (double)this.lastBestPlace.func_177956_o()) <= 0.125D && Math.abs(this.movingPosNow.field_72449_c - (double)this.lastBestPlace.func_177952_p()) <= 0.125D) {
                  this.lastBestPlace = null;
               }
            }

            if (this.movingPosNow.field_72450_a != -1.0D && this.movingPosNow.field_72448_b != -1.0D && this.movingPosNow.field_72449_c != -1.0D) {
               this.drawBoxMain(this.movingPosNow.field_72450_a, this.movingPosNow.field_72448_b, this.movingPosNow.field_72449_c);
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
      RenderUtil.drawBox(box, true, 1.0D, new GSColor(this.color.getValue(), (Integer)this.alpha.getValue()), 63);
      RenderUtil.drawBoundingBox(box, (double)(Integer)this.width.getValue(), new GSColor(this.color.getValue(), (Integer)this.outAlpha.getValue()));
      if ((Boolean)this.showDamage.getValue()) {
         box = this.getBox(x, y, z);
         String[] damageText = new String[]{String.format("%.1f", this.damage)};
         RenderUtil.drawNametag(box.field_72340_a + 0.5D, box.field_72338_b + 0.5D, box.field_72339_c + 0.5D, damageText, new GSColor(255, 255, 255), 1, 0.02666666666666667D, 0.0D);
      }

   }

   public String getHudInfo() {
      Entity currentTarget = null;
      if (this.target != null) {
         currentTarget = this.target.defaultPlayer;
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
         return isNull ? "[" + ChatFormatting.WHITE + "None" + ChatFormatting.GRAY + "]" : "[" + ChatFormatting.WHITE + currentTarget.func_70005_c_() + ChatFormatting.GRAY + "]";
      case 1:
         return "[" + ChatFormatting.WHITE + String.format("%.1f", this.damage) + ChatFormatting.GRAY + "]";
      case 2:
         return "[" + ChatFormatting.WHITE + (isNull ? "None" : currentTarget.func_70005_c_()) + " " + String.format("%.1f", this.damage) + ChatFormatting.GRAY + "]";
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
         if ((lastYaw == 512.0D || !(difference > (Double)AnchorAura.this.resetRotate.getValue()) && !(difference < -(Double)AnchorAura.this.resetRotate.getValue())) && LemonClient.speedUtil.getPlayerSpeed(player) != 0.0D) {
            this.tick = tick;
         } else {
            this.tick = 0;
         }
      }
   }

   class EntityInfo {
      EntityPlayer player = null;
      EntityPlayer defaultPlayer = null;
      double hp;

      public EntityInfo(EntityPlayer player, boolean predict) {
         if (player != null) {
            this.defaultPlayer = player;
            this.player = predict ? PredictUtil.predictPlayer(player, new PredictUtil.PredictSettings(((AnchorAura.MoveRotation)AnchorAura.this.playerSpeed.get(player)).tick, (Boolean)AnchorAura.this.calculateYPredict.getValue(), (Integer)AnchorAura.this.startDecrease.getValue(), (Integer)AnchorAura.this.exponentStartDecrease.getValue(), (Integer)AnchorAura.this.decreaseY.getValue(), (Integer)AnchorAura.this.exponentDecreaseY.getValue(), (Boolean)AnchorAura.this.splitXZ.getValue(), (Boolean)AnchorAura.this.manualOutHole.getValue(), (Boolean)AnchorAura.this.aboveHoleManual.getValue(), (Boolean)AnchorAura.this.stairPredict.getValue(), (Integer)AnchorAura.this.nStair.getValue(), (Double)AnchorAura.this.speedActivationStair.getValue())) : player;
            this.hp = (double)(player.func_110143_aJ() + player.func_110139_bj());
         }
      }
   }

   class PlaceInfo {
      AnchorAura.EntityInfo target;
      BlockPos placePos;
      float damage;
      float selfDamage;

      public PlaceInfo(AnchorAura.EntityInfo target, BlockPos placePos, float damage, float selfDamage) {
         this.target = target;
         this.placePos = placePos;
         this.damage = damage;
         this.selfDamage = selfDamage;
      }
   }
}
