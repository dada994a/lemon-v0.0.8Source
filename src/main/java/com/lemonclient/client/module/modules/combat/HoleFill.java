package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.misc.CrystalUtil;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.InventoryUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.PredictUtil;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.util.render.RenderUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.HoleUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "HoleFill",
   category = Category.Combat,
   priority = 999
)
public class HoleFill extends Module {
   BooleanSetting test = this.registerBoolean("Test", false);
   ModeSetting page = this.registerMode("Page", Arrays.asList("Target", "Place", "HoleFill", "SelfFill"), "Target");
   IntegerSetting maxTarget = this.registerInteger("Max Target", 10, 1, 50, () -> {
      return ((String)this.page.getValue()).equals("Target");
   });
   IntegerSetting tickAdd = this.registerInteger("Tick Add", 8, 0, 30, () -> {
      return ((String)this.page.getValue()).equals("Target");
   });
   IntegerSetting maxTick = this.registerInteger("Max Tick", 8, 0, 30, () -> {
      return ((String)this.page.getValue()).equals("Target");
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
   IntegerSetting delay = this.registerInteger("Calc Delay", 0, 0, 1000, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting upPlate = this.registerBoolean("Up Slab", true, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting selfFill = this.registerBoolean("Self Fill", true, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting mine = this.registerBoolean("Mine SelfFill", true, () -> {
      return ((String)this.page.getValue()).equals("Place") && (Boolean)this.selfFill.getValue();
   });
   BooleanSetting selfTrap = this.registerBoolean("Self Trap", true, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting yCheck = this.registerBoolean("Y Check", true, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting web = this.registerBoolean("Web", true, () -> {
      return ((String)this.page.getValue()).equals("Place") && (Boolean)this.yCheck.getValue();
   });
   BooleanSetting above = this.registerBoolean("Above", true, () -> {
      return ((String)this.page.getValue()).equals("Place") && (Boolean)this.yCheck.getValue();
   });
   BooleanSetting raytraceCheck = this.registerBoolean("Raytrace Check", true, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting holeCheck = this.registerBoolean("InHole Check", true, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   IntegerSetting placeDelay = this.registerInteger("Place Delay", 50, 0, 1000, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   IntegerSetting bpc = this.registerInteger("Block pre Tick", 6, 1, 20, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   DoubleSetting range = this.registerDouble("Range", 6.0D, 0.0D, 10.0D, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   DoubleSetting yRange = this.registerDouble("Y Range", 2.5D, 0.0D, 6.0D, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   DoubleSetting playerRange = this.registerDouble("Enemy Range", 3.0D, 0.0D, 6.0D, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   DoubleSetting playerYRange = this.registerDouble("Enemy YRange", 3.0D, 0.0D, 10.0D, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   DoubleSetting safety = this.registerDouble("Safety Range", 3.0D, 0.0D, 6.0D, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting rotate = this.registerBoolean("Rotate", false, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting strict = this.registerBoolean("Strict", false, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting raytrace = this.registerBoolean("RayTrace", false, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting onGround = this.registerBoolean("OnGround", false, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting packet = this.registerBoolean("Packet Place", false, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting swing = this.registerBoolean("Swing", false, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting render = this.registerBoolean("Render", false, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting box = this.registerBoolean("Box", true, () -> {
      return ((String)this.page.getValue()).equals("Place") && (Boolean)this.render.getValue();
   });
   BooleanSetting outline = this.registerBoolean("Outline", true, () -> {
      return ((String)this.page.getValue()).equals("Place") && (Boolean)this.render.getValue();
   });
   IntegerSetting width = this.registerInteger("Width", 1, 1, 5, () -> {
      return ((String)this.page.getValue()).equals("Place") && (Boolean)this.render.getValue() && (Boolean)this.outline.getValue();
   });
   ColorSetting color = this.registerColor("Color", new GSColor(255, 0, 0), () -> {
      return ((String)this.page.getValue()).equals("Place") && (Boolean)this.render.getValue();
   });
   IntegerSetting alpha = this.registerInteger("Alpha", 75, 0, 255, () -> {
      return ((String)this.page.getValue()).equals("Place") && (Boolean)this.render.getValue() && (Boolean)this.box.getValue();
   });
   IntegerSetting outAlpha = this.registerInteger("Outline Alpha", 125, 0, 255, () -> {
      return ((String)this.page.getValue()).equals("Place") && (Boolean)this.render.getValue() && (Boolean)this.outline.getValue();
   });
   BooleanSetting animate = this.registerBoolean("Animate", true, () -> {
      return ((String)this.page.getValue()).equals("Place") && (Boolean)this.render.getValue();
   });
   IntegerSetting time = this.registerInteger("Life Time", 1000, 0, 2500, () -> {
      return ((String)this.page.getValue()).equals("Place") && (Boolean)this.render.getValue();
   });
   BooleanSetting hObby = this.registerBoolean("H-Obby", true, () -> {
      return ((String)this.page.getValue()).equals("HoleFill");
   });
   BooleanSetting hEChest = this.registerBoolean("H-EChest", true, () -> {
      return ((String)this.page.getValue()).equals("HoleFill");
   });
   BooleanSetting hWeb = this.registerBoolean("H-Web", true, () -> {
      return ((String)this.page.getValue()).equals("HoleFill");
   });
   BooleanSetting hSlab = this.registerBoolean("H-Slab", true, () -> {
      return ((String)this.page.getValue()).equals("HoleFill");
   });
   BooleanSetting hSkull = this.registerBoolean("H-Skull", true, () -> {
      return ((String)this.page.getValue()).equals("HoleFill");
   });
   BooleanSetting hTrap = this.registerBoolean("H-Trapdoor", true, () -> {
      return ((String)this.page.getValue()).equals("HoleFill");
   });
   BooleanSetting sObby = this.registerBoolean("S-Obby", true, () -> {
      return ((String)this.page.getValue()).equals("SelfFill");
   });
   BooleanSetting sEChest = this.registerBoolean("S-EChest", true, () -> {
      return ((String)this.page.getValue()).equals("SelfFill");
   });
   BooleanSetting sWeb = this.registerBoolean("S-Web", true, () -> {
      return ((String)this.page.getValue()).equals("SelfFill");
   });
   BooleanSetting sSlab = this.registerBoolean("S-Slab", true, () -> {
      return ((String)this.page.getValue()).equals("SelfFill");
   });
   BooleanSetting sSkull = this.registerBoolean("S-Skull", true, () -> {
      return ((String)this.page.getValue()).equals("SelfFill");
   });
   BooleanSetting sTrap = this.registerBoolean("S-Trapdoor", true, () -> {
      return ((String)this.page.getValue()).equals("SelfFill");
   });
   ModeSetting jumpMode = this.registerMode("JumpMode", Arrays.asList("Normal", "Future", "Strict"), "Normal", () -> {
      return ((String)this.page.getValue()).equals("SelfFill");
   });
   ModeSetting rubberBand = this.registerMode("RubberBand", Arrays.asList("Cn", "Strict", "Future", "FutureStrict", "Troll", "Void", "Auto", "Test", "Custom"), "Cn", () -> {
      return ((String)this.page.getValue()).equals("SelfFill");
   });
   HoleFill.managerClassRenderBlocks managerRenderBlocks = new HoleFill.managerClassRenderBlocks();
   List<BlockPos> posList = new ArrayList();
   Timing timer = new Timing();
   Timing placeTimer = new Timing();
   boolean trapdoor;
   boolean mined;
   boolean self;
   boolean placedSelf;
   int placed;
   BlockPos savePos;
   BlockPos[] sides = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};

   public void fast() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && (mc.field_71439_g.field_70122_E || !(Boolean)this.onGround.getValue())) {
         this.managerRenderBlocks.update((Integer)this.time.getValue());
         if (this.timer.passedMs((long)(Integer)this.delay.getValue())) {
            this.posList = this.calc();
            this.timer.reset();
         }

         if (this.placeTimer.passedMs((long)(Integer)this.placeDelay.getValue())) {
            Iterator var1 = this.posList.iterator();

            while(var1.hasNext()) {
               BlockPos pos = (BlockPos)var1.next();
               if (this.placed >= (Integer)this.bpc.getValue()) {
                  break;
               }

               this.placeBlock(pos);
            }

            this.placeTimer.reset();
         }

         if ((Boolean)this.mine.getValue() && !this.self && this.placedSelf) {
            if (this.mined) {
               if (mc.field_71441_e.func_175623_d(PlayerUtil.getPlayerPos())) {
                  if (this.savePos != null) {
                     mc.field_71442_b.func_180512_c(this.savePos, EnumFacing.UP);
                  } else if (ModuleManager.isModuleEnabled(PacketMine.class)) {
                     PacketMine.INSTANCE.lastBlock = null;
                  }

                  this.mined = false;
                  this.placedSelf = false;
               }
            } else if (!mc.field_71441_e.func_175623_d(PlayerUtil.getPlayerPos())) {
               BlockPos instantPos = null;
               if (ModuleManager.isModuleEnabled(PacketMine.class)) {
                  instantPos = PacketMine.INSTANCE.packetPos;
               }

               this.savePos = instantPos;
               mc.field_71442_b.func_180512_c(PlayerUtil.getPlayerPos(), EnumFacing.UP);
               this.mined = true;
            }
         }

      }
   }

   private boolean isPos2(BlockPos pos1, BlockPos pos2) {
      if (pos1 != null && pos2 != null) {
         return pos1.field_177962_a == pos2.field_177962_a && pos1.field_177960_b == pos2.field_177960_b && pos1.field_177961_c == pos2.field_177961_c;
      } else {
         return false;
      }
   }

   private List<BlockPos> calc() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         this.placed = 0;
         List<HoleFill.HoleInfo> holeList = new ArrayList();
         Iterator var2 = EntityUtil.getSphere(PlayerUtil.getEyesPos(), (Double)this.range.getValue() + 1.0D, (Double)this.yRange.getValue() + 1.0D, false, false, 0).iterator();

         while(var2.hasNext()) {
            BlockPos pos = (BlockPos)var2.next();
            if (BlockUtil.canReplace(pos) && BlockUtil.canReplace(pos.func_177984_a())) {
               HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, true, false);
               HoleUtil.HoleType holeType = holeInfo.getType();
               if (holeType != HoleUtil.HoleType.NONE) {
                  AxisAlignedBB box = holeInfo.getCentre();
                  holeList.add(new HoleFill.HoleInfo(pos, new AxisAlignedBB(box.field_72340_a - (Double)this.playerRange.getValue(), box.field_72338_b, box.field_72339_c - (Double)this.playerRange.getValue(), box.field_72336_d + (Double)this.playerRange.getValue(), box.field_72337_e + (Double)this.playerYRange.getValue(), box.field_72334_f + (Double)this.playerRange.getValue())));
               }
            }
         }

         List<BlockPos> holePos = new ArrayList();
         List<EntityPlayer> targets = (List)PlayerUtil.getNearPlayers((Double)this.range.getValue() + (Double)this.playerRange.getValue(), this.maxTarget.getMax()).stream().filter((playerx) -> {
            return !(Boolean)this.holeCheck.getValue() || !HoleUtil.isInHole(playerx, false, false, false);
         }).collect(Collectors.toList());
         if ((Boolean)this.test.getValue()) {
            targets.add(mc.field_71439_g);
         }

         List<EntityPlayer> listPlayer = new ArrayList();
         Iterator var17 = targets.iterator();

         while(var17.hasNext()) {
            EntityPlayer player = (EntityPlayer)var17.next();

            for(int tick = 0; tick <= (Integer)this.maxTick.getValue() + (Integer)this.tickAdd.getValue(); tick += (Integer)this.tickAdd.getValue()) {
               if (tick >= (Integer)this.maxTick.getValue()) {
                  tick = (Integer)this.maxTick.getValue();
               }

               listPlayer.add(PredictUtil.predictPlayer(player, new PredictUtil.PredictSettings((Integer)this.maxTick.getValue(), (Boolean)this.calculateYPredict.getValue(), (Integer)this.startDecrease.getValue(), (Integer)this.exponentStartDecrease.getValue(), (Integer)this.decreaseY.getValue(), (Integer)this.exponentDecreaseY.getValue(), (Boolean)this.splitXZ.getValue(), (Boolean)this.manualOutHole.getValue(), (Boolean)this.aboveHoleManual.getValue(), (Boolean)this.stairPredict.getValue(), (Integer)this.nStair.getValue(), (Double)this.speedActivationStair.getValue())));
               if (tick == (Integer)this.maxTick.getValue()) {
                  break;
               }
            }
         }

         boolean fill = false;
         BlockPos selfPos = PlayerUtil.getPlayerPos();
         Iterator var21 = holeList.iterator();

         while(true) {
            label103:
            while(var21.hasNext()) {
               HoleFill.HoleInfo hole = (HoleFill.HoleInfo)var21.next();
               Iterator var9 = listPlayer.iterator();

               EntityPlayer target;
               do {
                  while(true) {
                     do {
                        if (!var9.hasNext()) {
                           continue label103;
                        }

                        target = (EntityPlayer)var9.next();
                     } while(!target.field_70121_D.func_72326_a(hole.box));

                     if (!(Boolean)this.yCheck.getValue() || (int)(target.field_70163_u + 0.5D) == hole.pos.field_177960_b + 1) {
                        break;
                     }

                     if (target.field_70163_u < (double)(hole.pos.field_177960_b + 1)) {
                        if (!(Boolean)this.web.getValue() || !target.field_70134_J) {
                           break;
                        }
                     } else {
                        if (!(Boolean)this.above.getValue()) {
                           break;
                        }

                        boolean cancel = false;

                        for(int high = (int)target.field_70163_u - (hole.pos.field_177960_b + 1); high > 0; --high) {
                           BlockPos pos = hole.pos.func_177981_b(high);
                           if (!BlockUtil.canReplace(pos) && !BlockUtil.isBlockUnSolid(pos)) {
                              cancel = true;
                              break;
                           }
                        }

                        if (!cancel) {
                           break;
                        }
                     }
                  }
               } while((Boolean)this.raytraceCheck.getValue() && !CrystalUtil.calculateRaytrace(target, new BlockPos((double)hole.pos.field_177962_a, target.field_70163_u + (double)target.eyeHeight, (double)hole.pos.field_177961_c)));

               if (this.isPos2(selfPos, hole.pos)) {
                  fill = true;
               }

               holePos.add(hole.pos);
            }

            this.self = fill;
            List<BlockPos> self = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), (Double)this.safety.getValue(), (Double)this.safety.getValue(), false, false, 0);
            holePos.removeIf((posx) -> {
               return self.contains(posx) && !this.isPlayer(posx) || !this.checkPlaceRange(posx);
            });
            return holePos;
         }
      } else {
         return new ArrayList();
      }
   }

   private boolean checkPlaceRange(BlockPos pos) {
      BlockPos playerPos = new BlockPos(Math.floor(mc.field_71439_g.field_70165_t), Math.floor(mc.field_71439_g.field_70163_u), Math.floor(mc.field_71439_g.field_70161_v));
      double x = (double)playerPos.field_177962_a - ((double)pos.field_177962_a + 0.5D);
      double y = (double)playerPos.field_177960_b - ((double)pos.field_177960_b + 0.5D);
      double z = (double)playerPos.field_177961_c - ((double)pos.field_177961_c + 0.5D);
      return x * x <= (Double)this.range.getValue() * (Double)this.range.getValue() && y * y <= (Double)this.yRange.getValue() * (Double)this.yRange.getValue() && z * z <= (Double)this.range.getValue() * (Double)this.range.getValue();
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

   private boolean isPlayer(BlockPos pos) {
      Iterator var2 = mc.field_71441_e.field_73010_i.iterator();

      EntityPlayer entity;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entity = (EntityPlayer)var2.next();
      } while(entity != mc.field_71439_g || !(new AxisAlignedBB(pos)).func_72326_a(entity.func_174813_aQ()));

      return true;
   }

   private void placeBlock(BlockPos pos) {
      if (pos != null) {
         boolean isPlayer = this.isPlayer(pos);
         int slot;
         if (isPlayer && (Boolean)this.selfTrap.getValue() && BlockUtil.canReplace(pos)) {
            slot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
            if (slot != -1) {
               this.switchTo(slot, () -> {
                  BlockPos ori = pos.func_177984_a();
                  if (BurrowUtil.getFirstFacing(pos.func_177981_b(2)) == null) {
                     BlockPos e = null;
                     boolean isNull = true;
                     BlockPos[] var5 = this.sides;
                     int var6 = var5.length;

                     int var7;
                     BlockPos side;
                     BlockPos added;
                     for(var7 = 0; var7 < var6; ++var7) {
                        side = var5[var7];
                        added = ori.func_177984_a().func_177971_a(side);
                        if (!this.intersectsWithEntity(added) && BurrowUtil.getFirstFacing(added) != null) {
                           e = added;
                           isNull = false;
                           break;
                        }
                     }

                     if (isNull) {
                        var5 = this.sides;
                        var6 = var5.length;

                        for(var7 = 0; var7 < var6; ++var7) {
                           side = var5[var7];
                           added = ori.func_177971_a(side);
                           if (!this.intersectsWithEntity(added) && !this.intersectsWithEntity(added.func_177984_a())) {
                              this.placeTrapBlock(added);
                              e = added.func_177984_a();
                              break;
                           }
                        }
                     }

                     this.placeTrapBlock(e);
                  }

                  this.placeTrapBlock(pos.func_177981_b(2));
               });
            }
         }

         if (!ColorMain.INSTANCE.breakList.contains(pos)) {
            if ((!isPlayer || (Boolean)this.selfFill.getValue()) && (!this.intersectsWithEntity(pos) || isPlayer) && BlockUtil.canReplace(pos)) {
               slot = this.findRightBlock(isPlayer);
               if (slot != -1) {
                  this.trapdoor = slot == InventoryUtil.findFirstBlockSlot(BlockTrapDoor.class, 0, 8) || (Boolean)this.upPlate.getValue() && slot == BurrowUtil.findHotbarBlock(BlockSlab.class);
                  boolean jump = slot == BurrowUtil.findHotbarBlock(BlockEnderChest.class) || slot == BurrowUtil.findHotbarBlock(BlockObsidian.class);
                  EnumFacing side = this.trapdoor ? BurrowUtil.getTrapdoorFacing(pos) : BlockUtil.getFirstFacing(pos, (Boolean)this.strict.getValue(), (Boolean)this.raytrace.getValue());
                  if (side != null) {
                     BlockPos neighbour = pos.func_177972_a(side);
                     EnumFacing opposite = side.func_176734_d();
                     Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, this.trapdoor ? 0.8D : 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
                     if ((BlockUtil.blackList.contains(mc.field_71441_e.func_180495_p(neighbour).func_177230_c()) || BlockUtil.shulkerList.contains(mc.field_71441_e.func_180495_p(neighbour).func_177230_c())) && !mc.field_71439_g.func_70093_af()) {
                        mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
                        mc.field_71439_g.func_70095_a(true);
                     }

                     if (isPlayer) {
                        this.placedSelf = true;
                        if (this.trapdoor) {
                           double x = mc.field_71439_g.field_70165_t;
                           double y = (double)((int)mc.field_71439_g.field_70163_u);
                           double z = mc.field_71439_g.field_70161_v;
                           if (slot == InventoryUtil.findFirstBlockSlot(BlockTrapDoor.class, 0, 8)) {
                              mc.field_71439_g.field_71174_a.func_147297_a(new Position(x, y + 0.20000000298023224D, z, mc.field_71439_g.field_70122_E));
                           } else {
                              this.jump();
                           }

                           mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
                           BurrowUtil.rightClickBlock(neighbour, opposite, new Vec3d(0.5D, 0.8D, 0.5D), true, (Boolean)this.swing.getValue());
                           if (slot == InventoryUtil.findFirstBlockSlot(BlockTrapDoor.class, 0, 8)) {
                              mc.field_71439_g.field_71174_a.func_147297_a(new Position(x, y, z, mc.field_71439_g.field_70122_E));
                           } else {
                              this.rubberBand();
                           }

                           mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(mc.field_71439_g.field_71071_by.field_70461_c));
                           return;
                        }

                        if (jump) {
                           this.jump();
                        }
                     }

                     if ((Boolean)this.rotate.getValue()) {
                        BurrowUtil.faceVector(hitVec, true);
                     }

                     this.switchTo(slot, () -> {
                        BurrowUtil.rightClickBlock(neighbour, hitVec, EnumHand.MAIN_HAND, opposite, (Boolean)this.packet.getValue(), (Boolean)this.swing.getValue());
                     });
                     if (isPlayer) {
                        this.rubberBand();
                     }

                     this.managerRenderBlocks.addRender(pos);
                     ++this.placed;
                  }
               }
            }
         }
      }
   }

   private void switchTo(int slot, Runnable runnable) {
      int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
      if (slot >= 0 && slot != oldslot) {
         if (slot < 9) {
            boolean packetSwitch = (Boolean)this.packetSwitch.getValue();
            if (packetSwitch) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
            } else {
               mc.field_71439_g.field_71071_by.field_70461_c = slot;
            }

            runnable.run();
            if (packetSwitch) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(oldslot));
            } else {
               mc.field_71439_g.field_71071_by.field_70461_c = oldslot;
            }
         }

      } else {
         runnable.run();
      }
   }

   public static BlockPos getFlooredPosition(Entity entity) {
      return new BlockPos(Math.floor(entity.field_70165_t), (double)Math.round(entity.field_70163_u), Math.floor(entity.field_70161_v));
   }

   private void placeTrapBlock(BlockPos pos) {
      if (!ColorMain.INSTANCE.breakList.contains(pos)) {
         BurrowUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
      }
   }

   private int findRightBlock(boolean selfFill) {
      int slot = -1;
      if (selfFill) {
         if ((Boolean)this.sTrap.getValue()) {
            slot = InventoryUtil.findFirstBlockSlot(BlockTrapDoor.class, 0, 8);
         }

         if ((Boolean)this.sSkull.getValue() && slot == -1) {
            slot = InventoryUtil.findSkullSlot();
         }

         if ((Boolean)this.sWeb.getValue() && slot == -1) {
            slot = InventoryUtil.findFirstBlockSlot(BlockWeb.class, 0, 8);
         }

         if ((Boolean)this.sSlab.getValue() && slot == -1) {
            slot = BurrowUtil.findHotbarBlock(BlockSlab.class);
         }

         if ((Boolean)this.sEChest.getValue() && slot == -1) {
            slot = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
         }

         if ((Boolean)this.sObby.getValue() && slot == -1) {
            slot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
         }
      } else {
         if ((Boolean)this.hObby.getValue()) {
            slot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
         }

         if ((Boolean)this.hEChest.getValue() && slot == -1) {
            slot = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
         }

         if ((Boolean)this.hSlab.getValue() && slot == -1) {
            slot = BurrowUtil.findHotbarBlock(BlockSlab.class);
         }

         if ((Boolean)this.hWeb.getValue() && slot == -1) {
            slot = InventoryUtil.findFirstBlockSlot(BlockWeb.class, 0, 8);
         }

         if ((Boolean)this.hSkull.getValue() && slot == -1) {
            slot = InventoryUtil.findSkullSlot();
         }

         if ((Boolean)this.hTrap.getValue()) {
            slot = InventoryUtil.findFirstBlockSlot(BlockTrapDoor.class, 0, 8);
         }
      }

      return slot;
   }

   public void onWorldRender(RenderEvent event) {
      this.managerRenderBlocks.render();
   }

   boolean sameBlockPos(BlockPos first, BlockPos second) {
      if (first != null && second != null) {
         return first.func_177958_n() == second.func_177958_n() && first.func_177956_o() == second.func_177956_o() && first.func_177952_p() == second.func_177952_p();
      } else {
         return false;
      }
   }

   public static void back() {
      Iterator var0 = ((List)mc.field_71441_e.field_72996_f.stream().filter((e) -> {
         return e instanceof EntityEnderCrystal && !e.field_70128_L;
      }).sorted(Comparator.comparing((e) -> {
         return mc.field_71439_g.func_70032_d(e);
      })).collect(Collectors.toList())).iterator();

      while(var0.hasNext()) {
         Entity crystal = (Entity)var0.next();
         if (crystal instanceof EntityEnderCrystal) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketUseEntity(crystal));
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketAnimation(EnumHand.OFF_HAND));
         }
      }

   }

   private boolean canGoTo(BlockPos pos) {
      return isAir(pos) && isAir(pos.func_177984_a());
   }

   public static boolean isAir(Vec3d vec3d) {
      return isAir(new BlockPos(vec3d));
   }

   public static boolean isAir(BlockPos pos) {
      return BlockUtil.canReplace(pos);
   }

   public static Vec3d getEyesPos() {
      return new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v);
   }

   private void jump() {
      String var1 = (String)this.jumpMode.getValue();
      byte var2 = -1;
      switch(var1.hashCode()) {
      case -1955878649:
         if (var1.equals("Normal")) {
            var2 = 0;
         }
         break;
      case -1808119063:
         if (var1.equals("Strict")) {
            var2 = 2;
         }
         break;
      case 2115664355:
         if (var1.equals("Future")) {
            var2 = 1;
         }
      }

      switch(var2) {
      case 0:
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.419999986886978D, mc.field_71439_g.field_70161_v, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.7531999805212015D, mc.field_71439_g.field_70161_v, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.001335979112147D, mc.field_71439_g.field_70161_v, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.166109260938214D, mc.field_71439_g.field_70161_v, false));
         break;
      case 1:
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.419997486886978D, mc.field_71439_g.field_70161_v, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.7500025D, mc.field_71439_g.field_70161_v, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.999995D, mc.field_71439_g.field_70161_v, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.170005001788139D, mc.field_71439_g.field_70161_v, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.2426050013947485D, mc.field_71439_g.field_70161_v, false));
         break;
      case 2:
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.419998586886978D, mc.field_71439_g.field_70161_v, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.7500014D, mc.field_71439_g.field_70161_v, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.9999972D, mc.field_71439_g.field_70161_v, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.170002801788139D, mc.field_71439_g.field_70161_v, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.170009801788139D, mc.field_71439_g.field_70161_v, false));
      }

   }

   private void rubberBand() {
      String var1 = (String)this.rubberBand.getValue();
      byte var2 = -1;
      switch(var1.hashCode()) {
      case -1808119063:
         if (var1.equals("Strict")) {
            var2 = 4;
         }
         break;
      case 2187:
         if (var1.equals("Cn")) {
            var2 = 0;
         }
         break;
      case 2052559:
         if (var1.equals("Auto")) {
            var2 = 6;
         }
         break;
      case 2672052:
         if (var1.equals("Void")) {
            var2 = 5;
         }
         break;
      case 81082065:
         if (var1.equals("Troll")) {
            var2 = 3;
         }
         break;
      case 1358728844:
         if (var1.equals("FutureStrict")) {
            var2 = 2;
         }
         break;
      case 2115664355:
         if (var1.equals("Future")) {
            var2 = 1;
         }
      }

      double distance;
      BlockPos bestPos;
      BlockPos pos;
      switch(var2) {
      case 0:
         distance = 0.0D;
         bestPos = null;
         Iterator var10 = BlockUtil.getBox(6.0F).iterator();

         while(true) {
            do {
               do {
                  do {
                     if (!var10.hasNext()) {
                        if (bestPos != null) {
                           mc.field_71439_g.field_71174_a.func_147297_a(new Position((double)bestPos.func_177958_n() + 0.5D, (double)bestPos.func_177956_o(), (double)bestPos.func_177952_p() + 0.5D, false));
                        } else {
                           mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, -7.0D, mc.field_71439_g.field_70161_v, false));
                        }

                        return;
                     }

                     pos = (BlockPos)var10.next();
                  } while(!this.canGoTo(pos));
               } while(mc.field_71439_g.func_70011_f((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D) <= 3.0D);
            } while(bestPos != null && mc.field_71439_g.func_70011_f((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D) >= distance);

            bestPos = pos;
            distance = mc.field_71439_g.func_70011_f((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D);
         }
      case 1:
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.242609801394749D, mc.field_71439_g.field_70161_v, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 2.340028003576279D, mc.field_71439_g.field_70161_v, false));
         break;
      case 2:
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.315205001001358D, mc.field_71439_g.field_70161_v, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.315205001001358D, mc.field_71439_g.field_70161_v, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 2.485225002789497D, mc.field_71439_g.field_70161_v, false));
         break;
      case 3:
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 3.3400880035762786D, mc.field_71439_g.field_70161_v, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 1.0D, mc.field_71439_g.field_70161_v, false));
         break;
      case 4:
         distance = 0.0D;
         bestPos = null;

         for(int i = 0; i < 20; ++i) {
            pos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.5D + (double)i, mc.field_71439_g.field_70161_v);
            if (this.canGoTo(pos) && mc.field_71439_g.func_70011_f((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D) > 5.0D && (bestPos == null || mc.field_71439_g.func_70011_f((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D) < distance)) {
               bestPos = pos;
               distance = mc.field_71439_g.func_70011_f((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D);
            }
         }

         if (bestPos != null) {
            mc.field_71439_g.field_71174_a.func_147297_a(new Position((double)bestPos.func_177958_n() + 0.5D, (double)bestPos.func_177956_o(), (double)bestPos.func_177952_p() + 0.5D, false));
         } else {
            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, -7.0D, mc.field_71439_g.field_70161_v, false));
         }
         break;
      case 5:
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, -7.0D, mc.field_71439_g.field_70161_v, false));
         break;
      case 6:
         for(int i = -10; i < 10; ++i) {
            if (i == -1) {
               i = 4;
            }

            if (mc.field_71441_e.func_180495_p(getFlooredPosition(mc.field_71439_g).func_177982_a(0, i, 0)).func_177230_c().equals(Blocks.field_150350_a) && mc.field_71441_e.func_180495_p(getFlooredPosition(mc.field_71439_g).func_177982_a(0, i + 1, 0)).func_177230_c().equals(Blocks.field_150350_a)) {
               BlockPos pos = getFlooredPosition(mc.field_71439_g).func_177982_a(0, i, 0);
               mc.field_71439_g.field_71174_a.func_147297_a(new Position((double)pos.func_177958_n() + 0.3D, (double)pos.func_177956_o(), (double)pos.func_177952_p() + 0.3D, false));
               break;
            }
         }
      }

   }

   static class HoleInfo {
      BlockPos pos;
      AxisAlignedBB box;

      public HoleInfo(BlockPos pos, AxisAlignedBB box) {
         this.pos = pos;
         this.box = box;
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
         AxisAlignedBB alignedBB = new AxisAlignedBB(this.pos);
         if ((Boolean)HoleFill.this.animate.getValue()) {
            alignedBB = alignedBB.func_186662_g(this.delta() * this.delta() * this.delta() / 2.0D - 1.0D);
         }

         if ((Boolean)HoleFill.this.box.getValue()) {
            RenderUtil.drawBox(alignedBB, true, 1.0D, new GSColor(HoleFill.this.color.getColor(), this.returnGradient()), 63);
         }

         if ((Boolean)HoleFill.this.outline.getValue()) {
            RenderUtil.drawBoundingBox(alignedBB, (double)(Integer)HoleFill.this.width.getValue(), new GSColor(HoleFill.this.color.getColor(), this.returnOutGradient()));
         }

      }

      public double delta() {
         long end = this.start + (long)(Integer)HoleFill.this.time.getValue();
         double result = (double)(end - System.currentTimeMillis()) / (double)(end - this.start);
         if (result < 0.0D) {
            result = 0.0D;
         }

         if (result > 1.0D) {
            result = 1.0D;
         }

         return 1.0D - result;
      }

      public int returnGradient() {
         return (int)((double)(Integer)HoleFill.this.alpha.getValue() * this.delta());
      }

      public int returnOutGradient() {
         return (int)((double)(Integer)HoleFill.this.outAlpha.getValue() * this.delta());
      }
   }

   class managerClassRenderBlocks {
      ArrayList<HoleFill.renderBlock> blocks = new ArrayList();

      void update(int time) {
         this.blocks.removeIf((e) -> {
            return System.currentTimeMillis() - e.start > (long)time;
         });
      }

      void render() {
         this.blocks.forEach(HoleFill.renderBlock::render);
      }

      void addRender(BlockPos pos) {
         boolean render = true;
         Iterator var3 = this.blocks.iterator();

         while(var3.hasNext()) {
            HoleFill.renderBlock block = (HoleFill.renderBlock)var3.next();
            if (HoleFill.this.sameBlockPos(block.pos, pos)) {
               render = false;
               block.resetTime();
               break;
            }
         }

         if (render) {
            this.blocks.add(HoleFill.this.new renderBlock(pos));
         }

      }
   }
}
