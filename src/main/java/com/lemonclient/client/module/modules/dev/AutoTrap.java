package com.lemonclient.client.module.modules.dev;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "BetterTrap",
   category = Category.Dev
)
public class AutoTrap extends Module {
   DoubleSetting range = this.registerDouble("Range", 5.0D, 0.0D, 10.0D);
   IntegerSetting delay = this.registerInteger("Delay", 50, 0, 500);
   IntegerSetting retryDelay = this.registerInteger("RetryDelay", 50, 0, 500);
   IntegerSetting blocksPerPlace = this.registerInteger("BlocksPerTick", 8, 1, 30);
   BooleanSetting chest = this.registerBoolean("EnderChest", true);
   BooleanSetting helpBlocks = this.registerBoolean("HelpBlocks", false);
   BooleanSetting only = this.registerBoolean("OnlyUntrapped", true);
   BooleanSetting strict = this.registerBoolean("Strict", true);
   BooleanSetting rotate = this.registerBoolean("Rotate", true);
   BooleanSetting raytrace = this.registerBoolean("Raytrace", false);
   BooleanSetting antiScaffold = this.registerBoolean("AntiScaffold", false);
   BooleanSetting antiStep = this.registerBoolean("AntiStep", false);
   BooleanSetting noGhost = this.registerBoolean("Packet", false);
   BooleanSetting swing = this.registerBoolean("Swing", false);
   BooleanSetting check = this.registerBoolean("SwitchCheck", false);
   BooleanSetting packet = this.registerBoolean("PacketSwitch", false);
   private final Timing timer = new Timing();
   private final Map<BlockPos, Integer> retries = new HashMap();
   private final Timing retryTimer = new Timing();
   public EntityPlayer target;
   private boolean didPlace = false;
   private int lastHotbarSlot;
   private int placements = 0;
   List<BlockPos> posList;

   public void onEnable() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
         this.retries.clear();
      }
   }

   public void onTick() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         this.doTrap();
      }
   }

   private void doTrap() {
      if (!this.check()) {
         this.doStaticTrap();
         if (this.didPlace) {
            this.timer.reset();
         }

      }
   }

   private void doStaticTrap() {
      int obbySlot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
      int eChestSlot = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
      int slot = (Boolean)this.chest.getValue() ? eChestSlot : (obbySlot == -1 ? eChestSlot : obbySlot);
      if (slot != -1) {
         int originalSlot = mc.field_71439_g.field_71071_by.field_70461_c;
         Vec3d[] sides = new Vec3d[]{new Vec3d(0.3D, 0.5D, 0.3D), new Vec3d(-0.3D, 0.5D, 0.3D), new Vec3d(0.3D, 0.5D, -0.3D), new Vec3d(-0.3D, 0.5D, -0.3D)};
         List<Vec3d> placeTargets = new ArrayList();
         Vec3d[] var7 = sides;
         int var8 = sides.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Vec3d vec3d = var7[var9];
            placeTargets.addAll(EntityUtil.targets(this.target.func_174791_d().func_178787_e(vec3d), (Boolean)this.antiScaffold.getValue(), (Boolean)this.antiStep.getValue(), false, false, false, (Boolean)this.raytrace.getValue()));
         }

         this.posList = this.placeList(placeTargets, this.target);
         if (!this.posList.isEmpty()) {
            this.switchTo(slot);
            Iterator var11 = this.posList.iterator();

            while(var11.hasNext()) {
               BlockPos pos = (BlockPos)var11.next();
               this.placeBlock(pos);
            }

            this.switchTo(originalSlot);
         }

      }
   }

   private List<BlockPos> placeList(List<Vec3d> list, EntityPlayer target) {
      list.sort((vec3d, vec3d2) -> {
         return Double.compare(mc.field_71439_g.func_70092_e(vec3d2.field_72450_a, vec3d2.field_72448_b, vec3d2.field_72449_c), mc.field_71439_g.func_70092_e(vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72449_c));
      });
      List<BlockPos> posList = new ArrayList();
      Iterator var4 = list.iterator();

      while(true) {
         while(true) {
            BlockPos position;
            do {
               do {
                  if (!var4.hasNext()) {
                     posList.sort(Comparator.comparingDouble((pos) -> {
                        return (double)pos.field_177960_b;
                     }));
                     return posList;
                  }

                  Vec3d vec3d3 = (Vec3d)var4.next();
                  position = new BlockPos(vec3d3);
               } while(this.intersectsWithEntity(position));
            } while(!BlockUtil.isAir(position));

            int placeability = BlockUtil.isPositionPlaceable(position, (Boolean)this.raytrace.getValue());
            if (placeability == 1 && (this.retries.get(position) == null || (Integer)this.retries.get(position) < 4)) {
               posList.add(position);
               this.retries.put(position, this.retries.get(position) == null ? 1 : (Integer)this.retries.get(position) + 1);
               this.retryTimer.reset();
            } else {
               if (placeability != 3 && (Boolean)this.helpBlocks.getValue() && (long)position.func_177956_o() == Math.round(target.field_70163_u) + 1L) {
                  posList.add(position.func_177977_b());
               }

               posList.add(position);
            }
         }
      }
   }

   private void switchTo(int slot) {
      if (slot > -1 && slot < 9 && (!(Boolean)this.check.getValue() || mc.field_71439_g.field_71071_by.field_70461_c != slot)) {
         if ((Boolean)this.packet.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = slot;
            mc.field_71442_b.func_78765_e();
         }
      }

   }

   private boolean check() {
      this.didPlace = false;
      this.placements = 0;
      int obbySlot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
      int eChestSlot = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
      int slot = (Boolean)this.chest.getValue() ? eChestSlot : (obbySlot == -1 ? eChestSlot : obbySlot);
      if (this.retryTimer.passedMs((long)(Integer)this.retryDelay.getValue())) {
         this.retries.clear();
         this.retryTimer.reset();
      }

      if (slot == -1) {
         return true;
      } else {
         if (mc.field_71439_g.field_71071_by.field_70461_c != this.lastHotbarSlot && mc.field_71439_g.field_71071_by.field_70461_c != obbySlot) {
            this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
         }

         this.target = this.getTarget((Double)this.range.getValue(), (Boolean)this.only.getValue());
         return this.target == null || !this.timer.passedMs((long)(Integer)this.delay.getValue());
      }
   }

   private EntityPlayer getTarget(double range, boolean trapped) {
      EntityPlayer target = null;
      double distance = Math.pow(range, 2.0D) + 1.0D;
      Iterator var7 = mc.field_71441_e.field_73010_i.iterator();

      while(true) {
         EntityPlayer player;
         do {
            do {
               if (!var7.hasNext()) {
                  return target;
               }

               player = (EntityPlayer)var7.next();
            } while(!EntityUtil.isPlayerValid(player, (float)range));
         } while(trapped && EntityUtil.isTrapped(player, (Boolean)this.antiScaffold.getValue(), (Boolean)this.antiStep.getValue(), false, false, false));

         if (!(LemonClient.speedUtil.getPlayerSpeed(player) > 15.0D)) {
            if (target == null) {
               target = player;
               distance = mc.field_71439_g.func_70068_e(player);
            } else if (mc.field_71439_g.func_70068_e(player) < distance) {
               target = player;
               distance = mc.field_71439_g.func_70068_e(player);
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
      } while(entity.field_70128_L || entity instanceof EntityItem || entity instanceof EntityXPOrb || entity instanceof EntityExpBottle || entity instanceof EntityArrow || !(new AxisAlignedBB(pos)).func_72326_a(entity.func_174813_aQ()));

      return true;
   }

   private void placeBlock(BlockPos pos) {
      if (this.placements < (Integer)this.blocksPerPlace.getValue()) {
         BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.noGhost.getValue(), (Boolean)this.strict.getValue(), (Boolean)this.raytrace.getValue(), (Boolean)this.swing.getValue());
         this.didPlace = true;
         ++this.placements;
      }

   }
}
