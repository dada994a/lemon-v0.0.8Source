package com.lemonclient.client.module.modules.combat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.InventoryUtil;
import com.lemonclient.api.util.player.PlacementUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.SpoofRotationUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.combat.CrystalUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.properties.IProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "Blocker",
   category = Category.Combat
)
public class Blocker extends Module {
   ModeSetting time = this.registerMode("Time Mode", Arrays.asList("Tick", "onUpdate", "Both", "Fast"), "Tick");
   ModeSetting breakType = this.registerMode("Type", Arrays.asList("Vanilla", "Packet"), "Vanilla");
   BooleanSetting packet = this.registerBoolean("Packet Place", false);
   BooleanSetting swing = this.registerBoolean("Swing", false);
   BooleanSetting rotate = this.registerBoolean("Rotate", true);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true);
   BooleanSetting check = this.registerBoolean("Switch Check", true);
   BooleanSetting anvilBlocker = this.registerBoolean("Anvil", true);
   BooleanSetting fallingBlocks = this.registerBoolean("Block FallingBlocks", true);
   BooleanSetting trap = this.registerBoolean("Trap", true, () -> {
      return (Boolean)this.fallingBlocks.getValue();
   });
   ModeSetting fallingMode = this.registerMode("Block Mode", Arrays.asList("Break", "Torch", "Skull"), "Break", () -> {
      return (Boolean)this.fallingBlocks.getValue();
   });
   BooleanSetting pistonBlocker = this.registerBoolean("Break Piston", true);
   BooleanSetting pistonBlockerNew = this.registerBoolean("Block Piston", true);
   BooleanSetting antiFacePlace = this.registerBoolean("Shift AntiFacePlace", true);
   ModeSetting blockPlaced = this.registerMode("Block Place", Arrays.asList("Pressure", "String"), "String", () -> {
      return (Boolean)this.antiFacePlace.getValue();
   });
   IntegerSetting BlocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 10);
   IntegerSetting tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
   DoubleSetting range = this.registerDouble("Range", 5.0D, 0.0D, 10.0D);
   DoubleSetting yrange = this.registerDouble("YRange", 5.0D, 0.0D, 10.0D);
   List<BlockPos> pistonList = new ArrayList();
   private int delayTimeTicks = 0;
   BlockPos[] sides = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};

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

   public void onEnable() {
      this.pistonList = new ArrayList();
      SpoofRotationUtil.ROTATION_UTIL.onEnable();
      PlacementUtil.onEnable();
   }

   public void onDisable() {
      SpoofRotationUtil.ROTATION_UTIL.onDisable();
      PlacementUtil.onDisable();
   }

   public void onUpdate() {
      if (((String)this.time.getValue()).equals("onUpdate") || ((String)this.time.getValue()).equals("Both")) {
         this.block();
      }

   }

   public void onTick() {
      if (((String)this.time.getValue()).equals("Tick") || ((String)this.time.getValue()).equals("Both")) {
         this.block();
      }

   }

   public void fast() {
      if (((String)this.time.getValue()).equals("Fast")) {
         this.block();
      }

   }

   private void block() {
      if (mc.field_71439_g != null && mc.field_71441_e != null && !mc.field_71439_g.field_70128_L) {
         if (this.delayTimeTicks < (Integer)this.tickDelay.getValue()) {
            ++this.delayTimeTicks;
         } else {
            SpoofRotationUtil.ROTATION_UTIL.shouldSpoofAngles(true);
            this.delayTimeTicks = 0;
            if ((Boolean)this.anvilBlocker.getValue()) {
               this.blockAnvil();
            }

            if ((Boolean)this.fallingBlocks.getValue()) {
               this.blockFallingBlocks();
            }

            if ((Boolean)this.pistonBlocker.getValue()) {
               this.blockPiston();
            }

            if ((Boolean)this.pistonBlockerNew.getValue()) {
               this.blockPA();
            }

            if ((Boolean)this.antiFacePlace.getValue() && mc.field_71474_y.field_74311_E.func_151468_f()) {
               this.antiFacePlace();
            }
         }

      } else {
         this.pistonList.clear();
      }
   }

   private List<BlockPos> posList() {
      return EntityUtil.getSphere(PlayerUtil.getPlayerPos(), (Double)this.range.getValue(), (Double)this.yrange.getValue(), false, false, 0);
   }

   private void antiFacePlace() {
      int blocksPlaced = 0;
      Vec3d[] var2 = new Vec3d[]{new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(0.0D, 1.0D, -1.0D)};
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Vec3d surround = var2[var4];
         BlockPos pos = new BlockPos(mc.field_71439_g.field_70165_t + surround.field_72450_a, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + surround.field_72449_c);
         Block temp;
         if ((temp = BlockUtil.getBlock(pos)) instanceof BlockObsidian || temp == Blocks.field_150357_h) {
            if (blocksPlaced++ == 0) {
               InventoryUtil.getHotBarPressure((String)this.blockPlaced.getValue());
            }

            PlacementUtil.placeItem(new BlockPos((double)pos.func_177958_n(), (double)pos.func_177956_o() + surround.field_72448_b, (double)pos.func_177952_p()), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), Items.field_151007_F.getClass());
            if (blocksPlaced == (Integer)this.BlocksPerTick.getValue()) {
               return;
            }
         }
      }

   }

   private void blockPA() {
      int slot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
      if (slot != -1) {
         Iterator var2 = this.posList().iterator();

         while(true) {
            BlockPos pos;
            do {
               do {
                  if (!var2.hasNext()) {
                     this.pistonList.removeIf((blockPos) -> {
                        return mc.field_71439_g.func_174818_b(blockPos) > (Double)this.range.getValue() * (Double)this.range.getValue();
                     });
                     if (!this.pistonList.isEmpty()) {
                        int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
                        this.switchTo(slot);
                        Iterator var7 = this.pistonList.iterator();

                        label34:
                        while(true) {
                           BlockPos pos;
                           BlockPos head;
                           do {
                              if (!var7.hasNext()) {
                                 this.switchTo(oldslot);
                                 break label34;
                              }

                              pos = (BlockPos)var7.next();
                              head = this.getHeadPos(pos);
                           } while(!BlockUtil.canReplace(pos) && !BlockUtil.canReplace(head));

                           BurrowUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                           BurrowUtil.placeBlock(head, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                        }
                     }

                     this.pistonList.removeIf((blockPos) -> {
                        return mc.field_71441_e.func_180495_p(blockPos).func_177230_c() == Blocks.field_150343_Z;
                     });
                     return;
                  }

                  pos = (BlockPos)var2.next();
               } while(this.pistonList.contains(pos));
            } while(!(mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockPistonBase) && mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150331_J && mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150320_F);

            this.pistonList.add(pos);
         }
      }
   }

   public BlockPos getHeadPos(BlockPos pos) {
      ImmutableMap<IProperty<?>, Comparable<?>> properties = mc.field_71441_e.func_180495_p(pos).func_177228_b();
      UnmodifiableIterator var3 = properties.keySet().iterator();

      while(true) {
         IProperty prop;
         do {
            do {
               if (!var3.hasNext()) {
                  return null;
               }

               prop = (IProperty)var3.next();
            } while(prop.func_177699_b() != EnumFacing.class);
         } while(!prop.func_177701_a().equals("facing") && !prop.func_177701_a().equals("rotation"));

         BlockPos pushPos = pos.func_177972_a((EnumFacing)properties.get(prop));
         BlockPos[] var6 = this.sides;
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            BlockPos side = var6[var8];
            if (this.isPos2(pos.func_177971_a(side), pushPos)) {
               return pos.func_177971_a(side);
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

   private void blockAnvil() {
      Iterator var1 = mc.field_71441_e.field_72996_f.iterator();

      while(var1.hasNext()) {
         Entity t = (Entity)var1.next();
         if (t instanceof EntityFallingBlock) {
            Block ex = ((EntityFallingBlock)t).field_175132_d.func_177230_c();
            if (ex instanceof BlockAnvil && (int)t.field_70165_t == (int)mc.field_71439_g.field_70165_t && (int)t.field_70161_v == (int)mc.field_71439_g.field_70161_v && BlockUtil.getBlock(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 2.0D, mc.field_71439_g.field_70161_v) instanceof BlockAir) {
               this.placeBlock(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 2.0D, mc.field_71439_g.field_70161_v));
            }
         }
      }

   }

   private void blockFallingBlocks() {
      Iterator var1 = mc.field_71441_e.field_72996_f.iterator();

      while(var1.hasNext()) {
         Entity t = (Entity)var1.next();
         if (t instanceof EntityFallingBlock) {
            Block ex = ((EntityFallingBlock)t).field_175132_d.func_177230_c();
            if (!(ex instanceof BlockAnvil) && (int)t.field_70165_t == (int)mc.field_71439_g.field_70165_t && (int)t.field_70161_v == (int)mc.field_71439_g.field_70161_v && (int)t.field_70163_u > (int)mc.field_71439_g.field_70163_u) {
               if ((Boolean)this.trap.getValue()) {
                  this.placeBlock(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 2.0D, mc.field_71439_g.field_70161_v));
               }

               int slot = -1;
               String var5 = (String)this.fallingMode.getValue();
               byte var6 = -1;
               switch(var5.hashCode()) {
               case 64448735:
                  if (var5.equals("Break")) {
                     var6 = 0;
                  }
                  break;
               case 79955773:
                  if (var5.equals("Skull")) {
                     var6 = 2;
                  }
                  break;
               case 80995292:
                  if (var5.equals("Torch")) {
                     var6 = 1;
                  }
               }

               switch(var6) {
               case 0:
                  mc.field_71442_b.func_180512_c(PlayerUtil.getPlayerPos(), EnumFacing.UP);
                  break;
               case 1:
                  slot = BurrowUtil.findHotbarBlock(BlockRedstoneTorch.class);
                  break;
               case 2:
                  slot = BurrowUtil.findHotbarBlock(BlockSkull.class);
               }

               if (slot != -1) {
                  int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
                  this.switchTo(slot);
                  BurrowUtil.placeBlock(PlayerUtil.getPlayerPos(), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                  this.switchTo(oldslot);
               }
            }
         }
      }

   }

   private void blockPiston() {
      Iterator var1 = mc.field_71441_e.field_72996_f.iterator();

      while(true) {
         Entity t;
         do {
            do {
               do {
                  do {
                     do {
                        if (!var1.hasNext()) {
                           return;
                        }

                        t = (Entity)var1.next();
                     } while(!(t instanceof EntityEnderCrystal));
                  } while(!(t.field_70165_t >= mc.field_71439_g.field_70165_t - 1.5D));
               } while(!(t.field_70165_t <= mc.field_71439_g.field_70165_t + 1.5D));
            } while(!(t.field_70161_v >= mc.field_71439_g.field_70161_v - 1.5D));
         } while(!(t.field_70161_v <= mc.field_71439_g.field_70161_v + 1.5D));

         for(int i = -2; i < 3; ++i) {
            for(int j = -2; j < 3; ++j) {
               if ((i == 0 || j == 0) && BlockUtil.getBlock(t.field_70165_t + (double)i, t.field_70163_u, t.field_70161_v + (double)j) instanceof BlockPistonBase) {
                  this.breakCrystalPiston(t);
               }
            }
         }
      }
   }

   private void placeBlock(BlockPos pos) {
      if (mc.field_71441_e.func_175623_d(pos)) {
         int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
         int obsidianSlot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
         if (obsidianSlot != -1) {
            this.switchTo(obsidianSlot);
            boolean isNull = true;
            if (BurrowUtil.getFirstFacing(pos) == null) {
               BlockPos[] var5 = this.sides;
               int var6 = var5.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  BlockPos side = var5[var7];
                  BlockPos added = pos.func_177971_a(side);
                  if (!this.intersectsWithEntity(added) && BurrowUtil.getFirstFacing(added) != null) {
                     BurrowUtil.placeBlock(added, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                     isNull = false;
                     break;
                  }
               }
            } else {
               isNull = false;
            }

            if (!isNull) {
               BurrowUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
            }

            this.switchTo(oldslot);
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

   private void breakCrystalPiston(Entity crystal) {
      if ((Boolean)this.rotate.getValue()) {
         SpoofRotationUtil.ROTATION_UTIL.lookAtPacket(crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v, mc.field_71439_g);
      }

      if (((String)this.breakType.getValue()).equals("Vanilla")) {
         CrystalUtil.breakCrystal(crystal, (Boolean)this.swing.getValue());
      } else {
         CrystalUtil.breakCrystalPacket(crystal, (Boolean)this.swing.getValue());
      }

      if ((Boolean)this.rotate.getValue()) {
         SpoofRotationUtil.ROTATION_UTIL.resetRotation();
      }

   }
}
