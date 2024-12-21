package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockConcretePowder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "AutoMineBurrow",
   category = Category.Combat
)
public class AntiBurrow extends Module {
   public static AntiBurrow INSTANCE;
   ModeSetting breakBlock = this.registerMode("Break Block", Arrays.asList("Normal", "Packet"), "Packet");
   DoubleSetting balance = this.registerDouble("Reduce", 0.24D, 0.0D, 0.5D);
   BooleanSetting up = this.registerBoolean("Head", true);
   BooleanSetting down = this.registerBoolean("Feet", true);
   BooleanSetting first = this.registerBoolean("Head First", false, () -> {
      return (Boolean)this.up.getValue();
   });
   BooleanSetting swing = this.registerBoolean("Swing", true);
   BooleanSetting rotate = this.registerBoolean("Rotate", false);
   BooleanSetting ignore = this.registerBoolean("Ignore Bed", false);
   BooleanSetting ignorePiston = this.registerBoolean("Ignore Piston", false);
   BooleanSetting ignoreWeb = this.registerBoolean("Ignore Web", false);
   BooleanSetting fire = this.registerBoolean("Fire", false);
   BooleanSetting sand = this.registerBoolean("Falling Blocks", false);
   IntegerSetting range = this.registerInteger("Range", 5, 0, 10);
   BooleanSetting doubleMine = this.registerBoolean("Double Mine", false);
   public double yaw;
   public double pitch;
   public boolean mining;
   @EventHandler
   private final Listener<PacketEvent.Send> listener = new Listener((event) -> {
      if ((Boolean)this.rotate.getValue() && this.mining) {
         if (event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer)event.getPacket();
            packet.field_149476_e = (float)this.yaw;
            packet.field_149473_f = (float)this.pitch;
         }

      }
   }, new Predicate[0]);
   public static final List<Block> airBlocks;

   public AntiBurrow() {
      INSTANCE = this;
   }

   public void onUpdate() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (!AntiRegear.INSTANCE.working) {
            BlockPos instantPos = null;
            if (ModuleManager.isModuleEnabled(PacketMine.class)) {
               instantPos = PacketMine.INSTANCE.packetPos;
            }

            if (instantPos != null) {
               if (instantPos.equals(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 2.0D, mc.field_71439_g.field_70161_v))) {
                  return;
               }

               if (instantPos.equals(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 1.0D, mc.field_71439_g.field_70161_v))) {
                  return;
               }

               if (mc.field_71441_e.func_180495_p(instantPos).func_177230_c() == Blocks.field_150321_G) {
                  return;
               }
            }

            BlockPos pos = this.getCityPos((BlockPos)null);
            this.mining = pos != null;
            if (this.mining) {
               if ((Boolean)this.doubleMine.getValue()) {
                  BlockPos doublePos = null;
                  if (ModuleManager.isModuleEnabled(PacketMine.class)) {
                     doublePos = PacketMine.INSTANCE.doublePos;
                  }

                  if (doublePos == null) {
                     this.doBreak(this.getCityPos(pos));
                  }
               }

               double[] rotate = EntityUtil.calculateLookAt((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D, mc.field_71439_g);
               this.yaw = rotate[0];
               this.pitch = rotate[1];
               this.doBreak(pos);
            }
         }
      }
   }

   public BlockPos getCityPos(BlockPos pos) {
      EntityPlayer player = PlayerUtil.getNearestPlayer((double)(Integer)this.range.getValue());
      if (player == null) {
         return null;
      } else {
         Vec3d[] sides = new Vec3d[]{new Vec3d((Double)this.balance.getValue(), 0.0D, (Double)this.balance.getValue()), new Vec3d(-(Double)this.balance.getValue(), 0.0D, (Double)this.balance.getValue()), new Vec3d((Double)this.balance.getValue(), 0.0D, -(Double)this.balance.getValue()), new Vec3d(-(Double)this.balance.getValue(), 0.0D, -(Double)this.balance.getValue())};
         int x;
         Vec3d[] var5;
         int var6;
         int var7;
         Vec3d side;
         BlockPos burrowPos;
         if ((Boolean)this.first.getValue() && (Boolean)this.up.getValue()) {
            for(x = 1; x > -1 && ((Boolean)this.down.getValue() || x != 0); --x) {
               var5 = sides;
               var6 = sides.length;

               for(var7 = 0; var7 < var6; ++var7) {
                  side = var5[var7];
                  burrowPos = new BlockPos(player.field_70165_t + side.field_72450_a, player.field_70163_u + (double)x, player.field_70161_v + side.field_72449_c);
                  if (this.intersect(player, burrowPos) && !this.isPos2(burrowPos, pos) && this.burrow(burrowPos)) {
                     return burrowPos;
                  }
               }
            }
         } else {
            for(x = (Boolean)this.down.getValue() ? 0 : 1; x < 2 && ((Boolean)this.up.getValue() || x != 1); ++x) {
               var5 = sides;
               var6 = sides.length;

               for(var7 = 0; var7 < var6; ++var7) {
                  side = var5[var7];
                  burrowPos = new BlockPos(player.field_70165_t + side.field_72450_a, player.field_70163_u + (double)x, player.field_70161_v + side.field_72449_c);
                  if (this.intersect(player, burrowPos) && !this.isPos2(burrowPos, pos) && this.burrow(burrowPos)) {
                     return burrowPos;
                  }
               }
            }
         }

         return null;
      }
   }

   private boolean burrow(BlockPos pos) {
      return !airBlocks.contains(mc.field_71441_e.func_180495_p(pos).func_177230_c()) && BlockUtil.getBlock(pos).field_149782_v >= 0.0F && (!(Boolean)this.ignore.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150324_C) && (!(Boolean)this.ignorePiston.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150332_K) && (!(Boolean)this.ignoreWeb.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150321_G) && ((Boolean)this.fire.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150480_ab) && ((Boolean)this.sand.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150354_m && mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150351_n && mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150467_bQ && !(mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockConcretePowder));
   }

   private void doBreak(BlockPos pos) {
      if (pos != null) {
         if ((Boolean)this.swing.getValue()) {
            mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
         }

         if (((String)this.breakBlock.getValue()).equals("Packet")) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
         } else {
            mc.field_71442_b.func_180512_c(pos, EnumFacing.UP);
         }

      }
   }

   private boolean intersect(EntityPlayer player, BlockPos pos) {
      return player.field_70121_D.func_72326_a(mc.field_71441_e.func_180495_p(pos).func_185918_c(mc.field_71441_e, pos));
   }

   private boolean isPos2(BlockPos pos1, BlockPos pos2) {
      if (pos1 != null && pos2 != null) {
         return pos1.field_177962_a == pos2.field_177962_a && pos1.field_177960_b == pos2.field_177960_b && pos1.field_177961_c == pos2.field_177961_c;
      } else {
         return false;
      }
   }

   static {
      airBlocks = Arrays.asList(Blocks.field_150350_a, Blocks.field_150353_l, Blocks.field_150356_k, Blocks.field_150355_j, Blocks.field_150358_i, Blocks.field_150349_c);
   }
}
