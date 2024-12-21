package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.event.events.DeathEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.InventoryUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Iterator;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "AutoSelfFill",
   category = Category.Combat
)
public class AutoSelfFill extends Module {
   IntegerSetting delay = this.registerInteger("Delay", 10, 0, 50);
   BooleanSetting rotate = this.registerBoolean("Rotate", true);
   BooleanSetting packet = this.registerBoolean("Packet Place", true);
   BooleanSetting ps = this.registerBoolean("Packet Switch", true);
   BooleanSetting swing = this.registerBoolean("Swing", true);
   BooleanSetting obsidian = this.registerBoolean("Obsidian", true);
   BooleanSetting echest = this.registerBoolean("Ender Chest", true);
   BooleanSetting web = this.registerBoolean("Web", true);
   BooleanSetting skull = this.registerBoolean("Skull", true);
   BooleanSetting plate = this.registerBoolean("Slab", true);
   BooleanSetting upPlate = this.registerBoolean("Up Slab", true);
   BooleanSetting trapdoor = this.registerBoolean("Trapdoor", true);
   int new_slot = -1;
   int waited;
   boolean door;
   boolean block;
   @EventHandler
   private final Listener<DeathEvent> deathEventListener = new Listener((event) -> {
      if (event.player == mc.field_71439_g) {
         this.disable();
      }

   }, new Predicate[0]);

   public void onUpdate() {
      if (this.waited++ >= (Integer)this.delay.getValue()) {
         this.waited = 0;
         if (BlockUtil.isAir(PlayerUtil.getPlayerPos()) && mc.field_71439_g.field_70122_E && this.intersectsWithEntity(PlayerUtil.getPlayerPos())) {
            this.placeBlock();
         }

      }
   }

   public void placeBlock() {
      this.new_slot = this.find_in_hotbar();
      if (this.new_slot != -1) {
         int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
         this.switchTo(this.new_slot);
         if (this.door) {
            this.placeTrapdoor();
         } else if ((Boolean)this.upPlate.getValue() && this.new_slot == BurrowUtil.findHotbarBlock(BlockSlab.class)) {
            this.burrowUp();
         } else if (this.block) {
            this.burrow();
         } else {
            BurrowUtil.placeBlock(PlayerUtil.getPlayerPos(), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
         }

         if ((Boolean)this.ps.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(mc.field_71439_g.field_71071_by.field_70461_c));
         } else {
            this.switchTo(oldslot);
         }

      }
   }

   private void switchTo(int slot) {
      if (mc.field_71439_g.field_71071_by.field_70461_c != slot && slot > -1 && slot < 9) {
         if ((Boolean)this.ps.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(this.new_slot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = slot;
            mc.field_71442_b.func_78765_e();
         }
      }

   }

   private int find_in_hotbar() {
      this.door = this.block = false;
      int newHand = -1;
      if ((Boolean)this.trapdoor.getValue()) {
         newHand = BurrowUtil.findHotbarBlock(BlockTrapDoor.class);
         if (newHand != -1) {
            this.door = true;
         }
      }

      if (newHand == -1 && (Boolean)this.skull.getValue()) {
         newHand = InventoryUtil.findSkullSlot();
      }

      if (newHand == -1 && (Boolean)this.web.getValue()) {
         newHand = BurrowUtil.findHotbarBlock(BlockWeb.class);
      }

      if (newHand == -1 && (Boolean)this.plate.getValue()) {
         newHand = BurrowUtil.findHotbarBlock(BlockSlab.class);
      }

      if (newHand == -1 && (Boolean)this.obsidian.getValue()) {
         newHand = BurrowUtil.findHotbarBlock(BlockObsidian.class);
         if (newHand != -1) {
            this.block = true;
         }
      }

      if (newHand == -1 && (Boolean)this.echest.getValue()) {
         newHand = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
         if (newHand != -1) {
            this.block = true;
         }
      }

      return newHand;
   }

   private void placeTrapdoor() {
      BlockPos originalPos = PlayerUtil.getPlayerPos();
      EnumFacing facing = BurrowUtil.getTrapdoorFacing(originalPos);
      if (facing != null) {
         BlockPos neighbour = originalPos.func_177972_a(facing);
         EnumFacing opposite = facing.func_176734_d();
         double x = mc.field_71439_g.field_70165_t;
         double y = (double)((int)mc.field_71439_g.field_70163_u);
         double z = mc.field_71439_g.field_70161_v;
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(x, y + 0.20000000298023224D, z, mc.field_71439_g.field_70122_E));
         BurrowUtil.rightClickBlock(neighbour, opposite, new Vec3d(0.5D, 0.8D, 0.5D), (Boolean)this.packet.getValue(), (Boolean)this.swing.getValue());
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(x, y, z, mc.field_71439_g.field_70122_E));
      }
   }

   private void burrow() {
      BlockPos originalPos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
      mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.42D, mc.field_71439_g.field_70161_v, true));
      mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.75D, mc.field_71439_g.field_70161_v, true));
      mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.01D, mc.field_71439_g.field_70161_v, true));
      mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.16D, mc.field_71439_g.field_70161_v, true));
      BurrowUtil.placeBlock(originalPos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
      mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.01D, mc.field_71439_g.field_70161_v, false));
   }

   private void burrowUp() {
      BlockPos originalPos = PlayerUtil.getPlayerPos();
      BlockPos neighbour;
      EnumFacing opposite;
      if (!mc.field_71441_e.func_175623_d(originalPos.func_177968_d())) {
         neighbour = originalPos.func_177972_a(EnumFacing.SOUTH);
         opposite = EnumFacing.SOUTH.func_176734_d();
      } else if (!mc.field_71441_e.func_175623_d(originalPos.func_177978_c())) {
         neighbour = originalPos.func_177972_a(EnumFacing.NORTH);
         opposite = EnumFacing.NORTH.func_176734_d();
      } else if (!mc.field_71441_e.func_175623_d(originalPos.func_177974_f())) {
         neighbour = originalPos.func_177972_a(EnumFacing.EAST);
         opposite = EnumFacing.EAST.func_176734_d();
      } else {
         if (mc.field_71441_e.func_175623_d(originalPos.func_177976_e())) {
            return;
         }

         neighbour = originalPos.func_177972_a(EnumFacing.WEST);
         opposite = EnumFacing.WEST.func_176734_d();
      }

      mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.42D, mc.field_71439_g.field_70161_v, true));
      mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.75D, mc.field_71439_g.field_70161_v, true));
      mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.01D, mc.field_71439_g.field_70161_v, true));
      mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.16D, mc.field_71439_g.field_70161_v, true));
      BurrowUtil.rightClickBlock(neighbour, opposite, new Vec3d(0.5D, 0.8D, 0.5D), (Boolean)this.packet.getValue(), (Boolean)this.swing.getValue());
      mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.01D, mc.field_71439_g.field_70161_v, false));
   }

   private boolean intersectsWithEntity(BlockPos pos) {
      Iterator var2 = mc.field_71441_e.field_72996_f.iterator();

      Entity entity;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entity = (Entity)var2.next();
      } while(entity instanceof EntityItem || entity == mc.field_71439_g || !(new AxisAlignedBB(pos)).func_72326_a(entity.func_174813_aQ()));

      return true;
   }
}
