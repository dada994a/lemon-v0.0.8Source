package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "AutoAnvil",
   category = Category.Combat
)
public class AutoAnvil extends Module {
   ModeSetting anvilMode = this.registerMode("Mode", Arrays.asList("Pick", "Feet", "None"), "Pick");
   ModeSetting target = this.registerMode("Target", Arrays.asList("Nearest", "Looking"), "Nearest");
   BooleanSetting rotate = this.registerBoolean("Rotate", true);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", false);
   BooleanSetting packetPlace = this.registerBoolean("Packet Place", false);
   BooleanSetting swing = this.registerBoolean("Swing", false);
   DoubleSetting enemyRange = this.registerDouble("Range", 5.9D, 0.0D, 6.0D);
   DoubleSetting decrease = this.registerDouble("Decrease", 2.0D, 0.0D, 6.0D);
   IntegerSetting tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
   IntegerSetting blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 8);
   IntegerSetting hDistance = this.registerInteger("H Distance", 7, 1, 10);
   IntegerSetting minH = this.registerInteger("Min H", 3, 1, 10);
   IntegerSetting maxH = this.registerInteger("Max H", 3, 1, 10);
   private boolean noMaterials = false;
   private boolean enoughSpace = true;
   private boolean blockUp = false;
   private int[] slot_mat = new int[]{-1, -1, -1};
   private double[] enemyCoords;
   int[][] model = new int[][]{{1, 1, 1}, {-1, 1, -1}, {-1, 1, 1}, {1, 1, -1}};
   private int blocksPlaced = 0;
   private int delayTimeTicks = 0;
   private int offsetSteps = 0;
   private BlockPos base;
   private EntityPlayer aimTarget;
   private static ArrayList<Vec3d> to_place = new ArrayList();

   public void onEnable() {
      this.blocksPlaced = 0;
      this.blockUp = false;
      this.slot_mat = new int[]{-1, -1, -1};
      to_place = new ArrayList();
      if (mc.field_71439_g == null) {
         this.disable();
      }

   }

   public void onDisable() {
      if (mc.field_71439_g != null) {
         if (this.noMaterials) {
            this.setDisabledMessage("No Materials Detected... AutoAnvil turned OFF!");
         } else if (!this.enoughSpace) {
            this.setDisabledMessage("Not enough space... AutoAnvil turned OFF!");
         } else if (this.blockUp) {
            this.setDisabledMessage("Enemy head blocked.. AutoAnvil turned OFF!");
         }

         this.noMaterials = false;
      }
   }

   public void onUpdate() {
      if (mc.field_71439_g == null) {
         this.disable();
      } else {
         if (((String)this.target.getValue()).equals("Nearest")) {
            this.aimTarget = PlayerUtil.getNearestPlayer((Double)this.enemyRange.getValue());
         } else if (((String)this.target.getValue()).equals("Looking")) {
            this.aimTarget = PlayerUtil.findLookingPlayer((Double)this.enemyRange.getValue());
         }

         if (this.aimTarget != null && !mc.field_71439_g.field_70128_L) {
            if (this.getMaterialsSlot()) {
               this.enemyCoords = new double[]{this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v};
               this.enoughSpace = this.createStructure();
            } else {
               this.noMaterials = true;
            }

            if (!this.noMaterials && this.enoughSpace && !this.blockUp) {
               if (this.delayTimeTicks < (Integer)this.tickDelay.getValue()) {
                  ++this.delayTimeTicks;
               } else {
                  this.delayTimeTicks = 0;
                  if (!BlockUtil.isAir(new BlockPos(this.enemyCoords[0], this.enemyCoords[1] + 2.0D, this.enemyCoords[2])) && !(BlockUtil.getBlock(this.enemyCoords[0], this.enemyCoords[1] + 2.0D, this.enemyCoords[2]) instanceof BlockAnvil)) {
                     this.blockUp = true;
                  }

                  for(this.blocksPlaced = 0; this.blocksPlaced <= (Integer)this.blocksPerTick.getValue(); ++this.offsetSteps) {
                     int maxSteps = to_place.size();
                     if (this.offsetSteps >= maxSteps) {
                        this.offsetSteps = 0;
                        break;
                     }

                     BlockPos offsetPos = new BlockPos((Vec3d)to_place.get(this.offsetSteps));
                     BlockPos targetPos = (new BlockPos(this.enemyCoords[0], this.enemyCoords[1], this.enemyCoords[2])).func_177982_a(offsetPos.func_177958_n(), offsetPos.func_177956_o(), offsetPos.func_177952_p());
                     boolean tryPlacing = true;
                     if (this.offsetSteps > 0 && this.offsetSteps < to_place.size() - 1) {
                        Iterator var5 = mc.field_71441_e.func_72839_b((Entity)null, new AxisAlignedBB(targetPos)).iterator();

                        while(var5.hasNext()) {
                           Entity entity = (Entity)var5.next();
                           if (entity instanceof EntityPlayer) {
                              tryPlacing = false;
                              break;
                           }
                        }
                     }

                     if (tryPlacing && this.placeBlock(targetPos, this.offsetSteps)) {
                        ++this.blocksPlaced;
                     }
                  }

                  BlockPos instantPos = null;
                  if (ModuleManager.isModuleEnabled(PacketMine.class)) {
                     instantPos = PacketMine.INSTANCE.packetPos;
                  }

                  if (((String)this.anvilMode.getValue()).equalsIgnoreCase("Pick") && (instantPos == null || !instantPos.equals(new BlockPos(this.enemyCoords[0], this.enemyCoords[1], this.enemyCoords[2])))) {
                     mc.field_71442_b.func_180512_c(new BlockPos(this.enemyCoords[0], this.enemyCoords[1], this.enemyCoords[2]), EnumFacing.UP);
                  }

               }
            } else {
               this.disable();
            }
         } else {
            this.disable();
         }
      }
   }

   private boolean placeBlock(BlockPos pos, int step) {
      if (this.intersectsWithEntity(pos)) {
         return false;
      } else if (!BlockUtil.canReplace(pos)) {
         return false;
      } else {
         int utilSlot = step == 0 && ((String)this.anvilMode.getValue()).equalsIgnoreCase("feet") ? 2 : (step >= to_place.size() - 1 ? 1 : 0);
         if (utilSlot == 0 && BlockUtil.canBeClicked(this.base)) {
            return false;
         } else {
            int slot = this.slot_mat[utilSlot];
            int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
            if (mc.field_71439_g.field_71071_by.func_70301_a(slot) != ItemStack.field_190927_a) {
               if (oldslot != slot) {
                  if ((Boolean)this.packetSwitch.getValue()) {
                     mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
                  } else {
                     mc.field_71439_g.field_71071_by.field_70461_c = slot;
                  }
               } else {
                  oldslot = -1;
               }

               BurrowUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packetPlace.getValue(), false, (Boolean)this.swing.getValue());
               if (oldslot != -1) {
                  if ((Boolean)this.packetSwitch.getValue()) {
                     mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(oldslot));
                  } else {
                     mc.field_71439_g.field_71071_by.field_70461_c = oldslot;
                  }
               }

               return true;
            } else {
               return false;
            }
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

   private boolean getMaterialsSlot() {
      boolean feet = ((String)this.anvilMode.getValue()).equalsIgnoreCase("Feet");

      int i;
      for(i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock) {
            Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
            if (block instanceof BlockObsidian) {
               this.slot_mat[0] = i;
            } else if (block instanceof BlockAnvil) {
               this.slot_mat[1] = i;
            } else if (feet && (block instanceof BlockPressurePlate || block instanceof BlockButton)) {
               this.slot_mat[2] = i;
            }
         }
      }

      i = 0;
      int[] var7 = this.slot_mat;
      int var8 = var7.length;

      for(int var5 = 0; var5 < var8; ++var5) {
         int val = var7[var5];
         if (val != -1) {
            ++i;
         }
      }

      return i - (feet ? 1 : 0) == 2;
   }

   private boolean createStructure() {
      to_place = new ArrayList();
      if (((String)this.anvilMode.getValue()).equalsIgnoreCase("feet")) {
         to_place.add(new Vec3d(0.0D, 0.0D, 0.0D));
      }

      int hDistanceMod = (Integer)this.hDistance.getValue();

      for(double distEnemy = (double)mc.field_71439_g.func_70032_d(this.aimTarget); distEnemy > (Double)this.decrease.getValue(); distEnemy -= (Double)this.decrease.getValue()) {
         --hDistanceMod;
      }

      hDistanceMod += (int)(mc.field_71439_g.field_70163_u - this.aimTarget.field_70163_u);
      double min_found = Double.MAX_VALUE;
      int cor = -1;
      int i = 0;
      BlockPos[] posList = new BlockPos[]{new BlockPos(this.enemyCoords[0] + 1.0D, this.enemyCoords[1], this.enemyCoords[2] + 1.0D), new BlockPos(this.enemyCoords[0] - 1.0D, this.enemyCoords[1], this.enemyCoords[2] - 1.0D), new BlockPos(this.enemyCoords[0] - 1.0D, this.enemyCoords[1], this.enemyCoords[2] + 1.0D), new BlockPos(this.enemyCoords[0] + 1.0D, this.enemyCoords[1], this.enemyCoords[2] - 1.0D)};
      BlockPos[] var9 = posList;
      int incr = posList.length;

      BlockPos targetPos;
      for(int var11 = 0; var11 < incr; ++var11) {
         targetPos = var9[var11];
         boolean breakOut = false;

         for(int h = 0; h <= (Integer)this.minH.getValue(); ++h) {
            if (BlockUtil.checkEntity(targetPos.func_177981_b(h))) {
               breakOut = true;
               ++i;
               break;
            }
         }

         if (!breakOut) {
            double distance_now = mc.field_71439_g.func_174818_b(targetPos);
            if (distance_now < min_found) {
               min_found = distance_now;
               cor = i;
            }

            ++i;
         }
      }

      if (cor == -1) {
         return false;
      } else {
         List<Vec3d> baseList = new ArrayList();
         baseList.add(new Vec3d((double)this.model[cor][0], (double)(this.model[cor][1] - 1), (double)this.model[cor][2]));
         baseList.add(new Vec3d((double)this.model[cor][0], (double)this.model[cor][1], (double)this.model[cor][2]));

         for(incr = 1; incr != (Integer)this.maxH.getValue() && BlockUtil.getBlock(this.enemyCoords[0], this.enemyCoords[1] + (double)incr, this.enemyCoords[2]) instanceof BlockAir && incr < hDistanceMod; ++incr) {
            baseList.add(new Vec3d((double)this.model[cor][0], (double)(this.model[cor][1] + incr), (double)this.model[cor][2]));
         }

         boolean possible = incr >= (Integer)this.minH.getValue() && incr <= (Integer)this.maxH.getValue();
         targetPos = new BlockPos(this.enemyCoords[0], this.enemyCoords[1], this.enemyCoords[2]);
         double x = mc.field_71439_g.func_174818_b((new BlockPos(targetPos)).func_177982_a(this.model[cor][0], 0, 0));
         double z = mc.field_71439_g.func_174818_b((new BlockPos(targetPos)).func_177982_a(0, 0, this.model[cor][2]));
         Vec3d base = new Vec3d((double)this.model[cor][0], (double)(this.model[cor][1] + incr - 1), 0.0D);
         if (x > z) {
            base = new Vec3d(0.0D, (double)(this.model[cor][1] + incr - 1), (double)this.model[cor][2]);
         }

         this.base = targetPos.func_177963_a(base.field_72450_a, base.field_72448_b, base.field_72449_c);
         to_place.add(base);
         double yRef = base.field_72448_b;
         if (BurrowUtil.getFirstFacing(targetPos.func_177963_a(0.0D, yRef, 0.0D)) == null) {
            to_place.addAll(baseList);
         }

         to_place.add(new Vec3d(0.0D, yRef, 0.0D));
         return possible;
      }
   }
}
