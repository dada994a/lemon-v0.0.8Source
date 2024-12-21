package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.combat.CrystalUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.InputUpdateEvent;

@Module.Declaration(
   name = "Surround",
   category = Category.Combat
)
public class Surround extends Module {
   ModeSetting time = this.registerMode("Time Mode", Arrays.asList("Tick", "onUpdate", "Fast"), "Tick");
   BooleanSetting once = this.registerBoolean("Once", true);
   BooleanSetting echest = this.registerBoolean("Ender Chest", true);
   BooleanSetting floor = this.registerBoolean("Floor", true);
   IntegerSetting delay = this.registerInteger("Delay", 0, 0, 20);
   IntegerSetting range = this.registerInteger("Range", 5, 0, 10);
   IntegerSetting bpt = this.registerInteger("BlocksPerTick", 4, 0, 20);
   BooleanSetting rotate = this.registerBoolean("Rotate", false);
   BooleanSetting packet = this.registerBoolean("Packet Place", false);
   BooleanSetting swing = this.registerBoolean("Swing", false);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true);
   BooleanSetting check = this.registerBoolean("Switch Check", true);
   BooleanSetting forceBase = this.registerBoolean("Force Base", false);
   BooleanSetting hit = this.registerBoolean("Hit", true);
   BooleanSetting packetBreak = this.registerBoolean("Packet Break", false);
   BooleanSetting antiWeakness = this.registerBoolean("Anti Weakness", true);
   BooleanSetting packetswitch = this.registerBoolean("Silent Switch", true);
   List<EntityEnderCrystal> crystals = new ArrayList();
   List<BlockPos> surround = new ArrayList();
   List<BlockPos> hasEntity = new ArrayList();
   List<BlockPos> posList = new ArrayList();
   List<BlockPos> floorPos = new ArrayList();
   int placed;
   int waited;
   int slot;
   double y;
   BlockPos[] sides = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1)};
   BlockPos[] neighbour = new BlockPos[]{new BlockPos(0, -1, 0), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1), new BlockPos(0, 1, 0)};
   @EventHandler
   private final Listener<InputUpdateEvent> inputUpdateEventListener = new Listener((event) -> {
      if (event.getMovementInput() instanceof MovementInputFromOptions) {
         if (event.getMovementInput().field_78901_c) {
            this.disable();
         }

         if (event.getMovementInput().field_187255_c || event.getMovementInput().field_187256_d || event.getMovementInput().field_187257_e || event.getMovementInput().field_187258_f) {
            double posY = mc.field_71439_g.field_70163_u - this.y;
            if (posY * posY > 0.25D) {
               this.disable();
            }
         }
      }

   }, new Predicate[0]);

   public void onEnable() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         this.y = mc.field_71439_g.field_70163_u;
      } else {
         this.disable();
      }
   }

   public void onUpdate() {
      if (((String)this.time.getValue()).equals("onUpdate")) {
         this.doSurround();
      }

   }

   public void onTick() {
      if (((String)this.time.getValue()).equals("Tick")) {
         this.doSurround();
      }

   }

   public void fast() {
      if (((String)this.time.getValue()).equals("Fast")) {
         this.doSurround();
      }

   }

   private void doSurround() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         this.slot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
         if (this.slot == -1 && (Boolean)this.echest.getValue()) {
            this.slot = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
         }

         if (this.slot != -1) {
            if (this.waited++ >= (Integer)this.delay.getValue()) {
               this.waited = this.placed = 0;
               this.calc();
               if ((Boolean)this.hit.getValue() && !this.crystals.isEmpty()) {
                  Entity crystal = null;
                  Iterator var2 = this.crystals.iterator();
                  if (var2.hasNext()) {
                     EntityEnderCrystal enderCrystal = (EntityEnderCrystal)var2.next();
                     crystal = enderCrystal;
                  }

                  this.breakCrystal(crystal);
               }

               Iterator var10;
               BlockPos pos;
               if ((Boolean)this.floor.getValue()) {
                  var10 = this.floorPos.iterator();

                  while(var10.hasNext()) {
                     pos = (BlockPos)var10.next();
                     this.surround.add(pos.func_177977_b());
                  }
               }

               if (!this.surround.isEmpty()) {
                  var10 = this.surround.iterator();

                  while(var10.hasNext()) {
                     pos = (BlockPos)var10.next();
                     if (this.placed >= (Integer)this.bpt.getValue()) {
                        break;
                     }

                     if (mc.field_71441_e.func_175623_d(pos) || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150480_ab || mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockLiquid) {
                        EnumFacing face = BurrowUtil.getFirstFacing(pos);
                        if (face == null || (Boolean)this.forceBase.getValue()) {
                           boolean canPlace = false;
                           BlockPos[] var5 = this.neighbour;
                           int var6 = var5.length;

                           for(int var7 = 0; var7 < var6; ++var7) {
                              BlockPos side = var5[var7];
                              BlockPos blockPos = pos.func_177971_a(side);
                              if (!this.intersectsWithEntity(blockPos) && BlockUtil.hasNeighbour(blockPos)) {
                                 this.placeBlock(blockPos, BurrowUtil.getFirstFacing(blockPos));
                                 canPlace = true;
                                 break;
                              }
                           }

                           if (!canPlace) {
                              continue;
                           }

                           face = BurrowUtil.getFirstFacing(pos);
                        }

                        this.placeBlock(pos, face);
                     }
                  }

                  if ((Boolean)this.once.getValue()) {
                     this.disable();
                  }

               }
            }
         }
      } else {
         this.disable();
      }
   }

   private void breakCrystal(Entity crystal) {
      if (crystal != null) {
         int oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
         if ((Boolean)this.antiWeakness.getValue() && mc.field_71439_g.func_70644_a(MobEffects.field_76437_t)) {
            int newSlot = -1;

            for(int i = 0; i < 9; ++i) {
               ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
               if (stack != ItemStack.field_190927_a) {
                  if (stack.func_77973_b() instanceof ItemSword) {
                     newSlot = i;
                     break;
                  }

                  if (stack.func_77973_b() instanceof ItemTool) {
                     newSlot = i;
                  }
               }
            }

            if (newSlot != -1) {
               this.switchTo(newSlot);
            }
         }

         if (!(Boolean)this.packetBreak.getValue()) {
            CrystalUtil.breakCrystal(crystal, (Boolean)this.swing.getValue());
         } else {
            CrystalUtil.breakCrystalPacket(crystal, (Boolean)this.swing.getValue());
         }

         if ((Boolean)this.packetswitch.getValue()) {
            this.switchTo(oldSlot);
         }

      }
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

   private void placeBlock(BlockPos pos, EnumFacing side) {
      if (this.placed < (Integer)this.bpt.getValue()) {
         if (!this.intersectsWithEntity(pos)) {
            if (side != null) {
               BlockPos neighbour = pos.func_177972_a(side);
               EnumFacing opposite = side.func_176734_d();
               Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
               if ((BlockUtil.blackList.contains(mc.field_71441_e.func_180495_p(neighbour).func_177230_c()) || BlockUtil.shulkerList.contains(mc.field_71441_e.func_180495_p(neighbour).func_177230_c())) && !mc.field_71439_g.func_70093_af()) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
                  mc.field_71439_g.func_70095_a(true);
               }

               if ((Boolean)this.rotate.getValue()) {
                  BurrowUtil.faceVector(hitVec, true);
               }

               int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
               this.switchTo(this.slot);
               BurrowUtil.rightClickBlock(neighbour, hitVec, EnumHand.MAIN_HAND, opposite, (Boolean)this.packet.getValue(), (Boolean)this.swing.getValue());
               this.switchTo(oldslot);
               ++this.placed;
            }
         }
      }
   }

   private void calc() {
      this.crystals = new ArrayList();
      this.surround = new ArrayList();
      this.hasEntity = new ArrayList();
      this.posList = new ArrayList();
      this.floorPos = new ArrayList();
      BlockPos playerPos = PlayerUtil.getPlayerPos();
      playerPos = new BlockPos((double)playerPos.field_177962_a, (double)playerPos.field_177960_b + 0.55D, (double)playerPos.field_177961_c);
      this.addPos(playerPos);
      if (!this.hasEntity.isEmpty()) {
         this.entityCalc();
      }

   }

   private void entityCalc() {
      this.posList = new ArrayList();
      this.posList.addAll(this.hasEntity);
      this.hasEntity = new ArrayList();
      Iterator var1 = this.posList.iterator();

      while(var1.hasNext()) {
         BlockPos pos = (BlockPos)var1.next();
         this.addPos(pos);
      }

      this.hasEntity.removeIf((blockPos) -> {
         return blockPos == null || this.floorPos.contains(blockPos) || mc.field_71439_g.func_174818_b(blockPos) > (double)((Integer)this.range.getValue() * (Integer)this.range.getValue());
      });
      this.surround.removeIf((blockPos) -> {
         return blockPos == null || mc.field_71439_g.func_174818_b(blockPos) > (double)((Integer)this.range.getValue() * (Integer)this.range.getValue());
      });
      if (!this.hasEntity.isEmpty()) {
         this.entityCalc();
      }

   }

   private void addPos(BlockPos pos) {
      if (!this.floorPos.contains(pos)) {
         BlockPos[] var2 = this.sides;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            BlockPos side = var2[var4];
            BlockPos blockPos = pos.func_177971_a(side);
            if (this.intersectsWithEntity(blockPos)) {
               this.hasEntity.add(blockPos);
            } else {
               this.surround.add(blockPos);
            }
         }

         this.floorPos.add(pos);
      }
   }

   private boolean intersectsWithEntity(BlockPos pos) {
      Iterator var2 = mc.field_71441_e.field_72996_f.iterator();

      while(var2.hasNext()) {
         Entity entity = (Entity)var2.next();
         if (!(entity instanceof EntityItem) && (new AxisAlignedBB(pos)).func_72326_a(entity.func_174813_aQ())) {
            if (entity instanceof EntityEnderCrystal) {
               this.crystals.add((EntityEnderCrystal)entity);
            } else if (entity instanceof EntityPlayer) {
               return true;
            }
         }
      }

      return false;
   }
}
