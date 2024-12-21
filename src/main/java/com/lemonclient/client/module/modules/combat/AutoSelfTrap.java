package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.PredictUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.HoleUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "AutoSelfTrap",
   category = Category.Combat,
   priority = 999
)
public class AutoSelfTrap extends Module {
   BooleanSetting test = this.registerBoolean("Test", false);
   ModeSetting page = this.registerMode("Page", Arrays.asList("Target", "Place"), "Target");
   ModeSetting target = this.registerMode("Target", Arrays.asList("Normal", "Predict", "Both"), "Predict", () -> {
      return ((String)this.page.getValue()).equals("Target");
   });
   IntegerSetting tickPredict = this.registerInteger("Tick Predict", 8, 0, 30, () -> {
      return ((String)this.page.getValue()).equals("Target");
   });
   BooleanSetting doublePredict = this.registerBoolean("Double Predict", true, () -> {
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
   BooleanSetting yCheck = this.registerBoolean("Y Check", true, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting smart = this.registerBoolean("Stuck Check", true, () -> {
      return ((String)this.page.getValue()).equals("Place") && (Boolean)this.yCheck.getValue();
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
   BooleanSetting rotate = this.registerBoolean("Rotate", false, () -> {
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
   BooleanSetting check = this.registerBoolean("Switch Check", true, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting doubleHole = this.registerBoolean("Double Hole", false, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting customHole = this.registerBoolean("Custom Hole", false, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting fourHole = this.registerBoolean("FourBlocks Hole", false, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   List<BlockPos> posList = new ArrayList();
   Timing timer = new Timing();
   Timing placeTimer = new Timing();
   boolean self;
   int placed;
   int waited;
   BlockPos[] sides = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};

   public void fast() {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
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
         this.waited = 0;
         this.placed = 0;
         List<BlockPos> holePos = new ArrayList();
         List<EntityPlayer> targets = new ArrayList(mc.field_71441_e.field_73010_i);
         targets.removeIf((playerx) -> {
            return EntityUtil.basicChecksEntity(playerx) || (double)mc.field_71439_g.func_70032_d(playerx) > (Double)this.range.getValue() + (Double)this.playerRange.getValue() || (Boolean)this.holeCheck.getValue() && this.inHole(playerx);
         });
         if ((Boolean)this.test.getValue()) {
            targets.add(mc.field_71439_g);
         }

         List<EntityPlayer> listPlayer = new ArrayList();
         if (!((String)this.target.getValue()).equals("Predict")) {
            listPlayer.addAll(targets);
         }

         if (!((String)this.target.getValue()).equals("Normal")) {
            Iterator var4 = targets.iterator();

            while(var4.hasNext()) {
               EntityPlayer player = (EntityPlayer)var4.next();
               listPlayer.add(PredictUtil.predictPlayer(player, new PredictUtil.PredictSettings((Integer)this.tickPredict.getValue(), (Boolean)this.calculateYPredict.getValue(), (Integer)this.startDecrease.getValue(), (Integer)this.exponentStartDecrease.getValue(), (Integer)this.decreaseY.getValue(), (Integer)this.exponentDecreaseY.getValue(), (Boolean)this.splitXZ.getValue(), (Boolean)this.manualOutHole.getValue(), (Boolean)this.aboveHoleManual.getValue(), (Boolean)this.stairPredict.getValue(), (Integer)this.nStair.getValue(), (Double)this.speedActivationStair.getValue())));
               if ((Boolean)this.doublePredict.getValue()) {
                  listPlayer.add(PredictUtil.predictPlayer(player, new PredictUtil.PredictSettings((Integer)this.tickPredict.getValue() * 2, (Boolean)this.calculateYPredict.getValue(), (Integer)this.startDecrease.getValue(), (Integer)this.exponentStartDecrease.getValue(), (Integer)this.decreaseY.getValue(), (Integer)this.exponentDecreaseY.getValue(), (Boolean)this.splitXZ.getValue(), (Boolean)this.manualOutHole.getValue(), (Boolean)this.aboveHoleManual.getValue(), (Boolean)this.stairPredict.getValue(), (Integer)this.nStair.getValue(), (Double)this.speedActivationStair.getValue())));
               }
            }
         }

         boolean fill = false;
         Iterator var15 = listPlayer.iterator();

         label140:
         while(var15.hasNext()) {
            EntityPlayer target = (EntityPlayer)var15.next();
            Iterator var7 = EntityUtil.getSphere(new BlockPos(target.field_70165_t, target.field_70163_u, target.field_70161_v), (Double)this.playerRange.getValue() + 1.0D, (Double)this.playerYRange.getValue() + 1.0D, false, false, 0).iterator();

            while(true) {
               BlockPos pos;
               HoleUtil.HoleType holeType;
               do {
                  do {
                     do {
                        do {
                           do {
                              do {
                                 do {
                                    if (!var7.hasNext()) {
                                       continue label140;
                                    }

                                    pos = (BlockPos)var7.next();
                                    if (this.isPos2(PlayerUtil.getPlayerPos(), pos)) {
                                       fill = true;
                                    }
                                 } while(!this.checkInRange(target, pos));

                                 HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, true, false);
                                 holeType = holeInfo.getType();
                              } while(holeType == HoleUtil.HoleType.NONE);
                           } while(mc.field_71439_g.func_174818_b(pos) > (Double)this.range.getValue() * (Double)this.range.getValue());
                        } while(!mc.field_71441_e.func_175623_d(pos));
                     } while(!mc.field_71441_e.func_175623_d(pos.func_177984_a()));
                  } while(!mc.field_71441_e.func_175623_d(pos.func_177981_b(2)));
               } while((Boolean)this.yCheck.getValue() && target.field_70163_u <= (double)pos.field_177960_b);

               boolean cant = false;
               if ((Boolean)this.smart.getValue()) {
                  for(int high = 0; (double)high < target.field_70163_u - (double)pos.field_177960_b; ++high) {
                     if (high != 0) {
                        if (mc.field_71439_g.field_70163_u > (double)pos.field_177960_b && !mc.field_71441_e.func_175623_d(new BlockPos(pos.field_177962_a, pos.field_177960_b + high, pos.field_177961_c))) {
                           cant = true;
                        }

                        if (mc.field_71439_g.field_70163_u < (double)pos.field_177960_b) {
                           BlockPos newPos = new BlockPos(pos.field_177962_a, pos.field_177960_b + high, pos.field_177961_c);
                           if (mc.field_71441_e.func_175623_d(newPos) && (mc.field_71441_e.func_175623_d(newPos.func_177977_b()) || mc.field_71441_e.func_175623_d(newPos.func_177984_a()))) {
                              cant = true;
                           }
                        }
                     }
                  }
               }

               if (!cant) {
                  if (holeType == HoleUtil.HoleType.SINGLE) {
                     holePos.add(pos);
                  }

                  if ((Boolean)this.doubleHole.getValue() && holeType == HoleUtil.HoleType.DOUBLE) {
                     holePos.add(pos);
                  }

                  if ((Boolean)this.customHole.getValue() && holeType == HoleUtil.HoleType.CUSTOM) {
                     holePos.add(pos);
                  }

                  if ((Boolean)this.fourHole.getValue() && holeType == HoleUtil.HoleType.FOUR) {
                     holePos.add(pos);
                  }
               }
            }
         }

         this.self = fill;
         holePos.removeIf((posx) -> {
            return !this.checkPlaceRange(posx);
         });
         return holePos;
      } else {
         return new ArrayList();
      }
   }

   private boolean checkInRange(EntityPlayer player, BlockPos pos) {
      BlockPos targetPos = new BlockPos(Math.floor(player.field_70165_t), Math.floor(player.field_70163_u), Math.floor(player.field_70161_v));
      double x = (double)targetPos.field_177962_a - ((double)pos.field_177962_a + 0.5D);
      double y = (double)(targetPos.field_177960_b - (pos.field_177960_b + 1));
      double z = (double)targetPos.field_177961_c - ((double)pos.field_177961_c + 0.5D);
      if (!(Boolean)this.yCheck.getValue()) {
         double y2 = (double)targetPos.field_177960_b - ((double)pos.field_177960_b + 0.5D);
         if (y2 * y2 > (Double)this.playerYRange.getValue() * (Double)this.playerYRange.getValue()) {
            return false;
         }
      }

      return x * x <= (Double)this.playerRange.getValue() * (Double)this.playerRange.getValue() && y * y <= (Double)this.playerYRange.getValue() * (Double)this.playerYRange.getValue() && z * z <= (Double)this.playerRange.getValue() * (Double)this.playerRange.getValue();
   }

   private boolean checkPlaceRange(BlockPos pos) {
      BlockPos playerPos = new BlockPos(Math.floor(mc.field_71439_g.field_70165_t), Math.floor(mc.field_71439_g.field_70163_u), Math.floor(mc.field_71439_g.field_70161_v));
      double x = (double)playerPos.field_177962_a - ((double)pos.field_177962_a + 0.5D);
      double y = (double)playerPos.field_177960_b - ((double)pos.field_177960_b + 0.5D);
      double z = (double)playerPos.field_177961_c - ((double)pos.field_177961_c + 0.5D);
      return x * x <= (Double)this.range.getValue() * (Double)this.range.getValue() && y * y <= (Double)this.yRange.getValue() * (Double)this.yRange.getValue() && z * z <= (Double)this.range.getValue() * (Double)this.range.getValue();
   }

   public boolean inHole(EntityPlayer aimTarget) {
      HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(EntityUtil.getEntityPos(aimTarget), false, false, false);
      HoleUtil.HoleType holeType = holeInfo.getType();
      return holeType != HoleUtil.HoleType.NONE;
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

   private void switchTo(int slot) {
      if (slot > -1 && slot < 9 && (!(Boolean)this.check.getValue() || mc.field_71439_g.field_71071_by.field_70461_c != slot)) {
         if ((Boolean)this.packetSwitch.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = slot;
         }

         mc.field_71442_b.func_78765_e();
      }

   }

   private void placeBlock(BlockPos pos) {
      boolean isPlayer = this.isPlayer(pos);
      if (isPlayer && BlockUtil.airBlocks.contains(mc.field_71441_e.func_180495_p(pos).func_177230_c())) {
         int obby = BurrowUtil.findHotbarBlock(BlockObsidian.class);
         int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
         if (obby != -1) {
            BlockPos ori = pos.func_177984_a();
            if (BurrowUtil.getFirstFacing(pos.func_177981_b(2)) != null) {
               this.switchTo(obby);
            } else {
               BlockPos e = null;
               boolean isNull = true;
               BlockPos[] var8 = this.sides;
               int var9 = var8.length;

               int var10;
               BlockPos side;
               BlockPos added;
               for(var10 = 0; var10 < var9; ++var10) {
                  side = var8[var10];
                  added = ori.func_177984_a().func_177971_a(side);
                  if (!this.intersectsWithEntity(added) && BurrowUtil.getFirstFacing(added) != null) {
                     e = added;
                     isNull = false;
                     break;
                  }
               }

               if (isNull) {
                  var8 = this.sides;
                  var9 = var8.length;

                  for(var10 = 0; var10 < var9; ++var10) {
                     side = var8[var10];
                     added = ori.func_177971_a(side);
                     if (!this.intersectsWithEntity(added) && !this.intersectsWithEntity(added.func_177984_a())) {
                        this.switchTo(obby);
                        this.placeblock(added);
                        e = added.func_177984_a();
                        break;
                     }
                  }
               } else {
                  this.switchTo(obby);
               }

               this.placeblock(e);
               this.switchTo(oldslot);
            }

            this.placeblock(pos.func_177981_b(2));
            this.switchTo(oldslot);
         }
      }

      ++this.placed;
   }

   private void placeblock(BlockPos pos) {
      if (!ColorMain.INSTANCE.breakList.contains(pos)) {
         BurrowUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
      }
   }

   public static void rightClickBlock(BlockPos pos, EnumFacing facing, Vec3d hVec, boolean packet, boolean swing) {
      Vec3d hitVec = (new Vec3d(pos)).func_178787_e(hVec).func_178787_e((new Vec3d(facing.func_176730_m())).func_186678_a(0.5D));
      if (packet) {
         rightClickBlock(pos, hitVec, EnumHand.MAIN_HAND, facing);
      } else {
         mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, pos, facing, hitVec, EnumHand.MAIN_HAND);
      }

      if (swing) {
         mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
      }

   }

   public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction) {
      float f = (float)(vec.field_72450_a - (double)pos.func_177958_n());
      float f1 = (float)(vec.field_72448_b - (double)pos.func_177956_o());
      float f2 = (float)(vec.field_72449_c - (double)pos.func_177952_p());
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
   }
}
