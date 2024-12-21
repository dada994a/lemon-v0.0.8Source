package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.RotationUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.dev.BedCevBreaker;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "AutoCity",
   category = Category.Combat
)
public class AutoCityDev extends Module {
   public static AutoCityDev INSTANCE;
   ModeSetting breakBlock = this.registerMode("Break Block", Arrays.asList("Normal", "Packet"), "Packet");
   IntegerSetting range = this.registerInteger("Range", 6, 0, 10);
   BooleanSetting swing = this.registerBoolean("Swing", false);
   BooleanSetting rotate = this.registerBoolean("Rotate", true);
   BooleanSetting ignore = this.registerBoolean("Ignore Bed", false);
   public boolean working;
   float pitch;
   float yaw;
   BlockPos blockMine;
   @EventHandler
   private final Listener<PacketEvent.Send> listener = new Listener((event) -> {
      if ((Boolean)this.rotate.getValue()) {
         if (event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer)event.getPacket();
            packet.field_149476_e = this.yaw;
            packet.field_149473_f = this.pitch;
         }

      }
   }, new Predicate[0]);

   public AutoCityDev() {
      INSTANCE = this;
   }

   public void onUpdate() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         this.working = false;
         if (!AntiBurrow.INSTANCE.mining && !AntiRegear.INSTANCE.working && !CevBreaker.INSTANCE.working && !BedCevBreaker.INSTANCE.working) {
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

               if (this.blockMine != null && !isPos2(this.blockMine, instantPos)) {
                  this.blockMine = null;
               }
            } else {
               this.blockMine = null;
            }

            EntityPlayer aimTarget = PlayerUtil.getNearestPlayer((double)((Integer)this.range.getValue() + 2));
            if (aimTarget != null) {
               BlockPos[] offsets = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
               BlockPos playerPos = EntityUtil.getEntityPos(aimTarget);
               boolean same;
               BlockPos[] var6;
               int var7;
               int var8;
               BlockPos offset;
               if (this.blockMine != null) {
                  if (mc.field_71439_g.func_70011_f((double)this.blockMine.field_177962_a + 0.5D, (double)this.blockMine.field_177960_b + 0.5D, (double)this.blockMine.field_177961_c + 0.5D) > (double)(Integer)this.range.getValue()) {
                     this.blockMine = null;
                  } else {
                     same = false;
                     var6 = offsets;
                     var7 = offsets.length;

                     for(var8 = 0; var8 < var7; ++var8) {
                        offset = var6[var8];
                        if (isPos2(playerPos.func_177971_a(offset), this.blockMine)) {
                           same = true;
                        }
                     }

                     if (!same) {
                        this.blockMine = null;
                     }
                  }
               }

               same = true;
               var6 = offsets;
               var7 = offsets.length;

               for(var8 = 0; var8 < var7; ++var8) {
                  offset = var6[var8];
                  BlockPos pos = playerPos.func_177971_a(offset);
                  IBlockState blockState = BlockUtil.getState(pos);
                  if (BlockUtil.isAir(pos) || (Boolean)this.ignore.getValue() && blockState == Blocks.field_150324_C) {
                     same = false;
                  }
               }

               if (same) {
                  if (this.blockMine != null) {
                     this.working = true;
                  } else {
                     EnumFacing facing = RotationUtil.getFacing((double)PlayerPacketManager.INSTANCE.getServerSideRotation().field_189982_i);
                     this.blockMine = playerPos.func_177967_a(facing, -1);
                     if (mc.field_71439_g.func_70011_f((double)this.blockMine.field_177962_a + 0.5D, (double)this.blockMine.field_177960_b + 0.5D, (double)this.blockMine.field_177961_c + 0.5D) > (double)(Integer)this.range.getValue() || (Boolean)this.ignore.getValue() && BlockUtil.getBlock(this.blockMine) == Blocks.field_150324_C || BlockUtil.getBlock(this.blockMine).field_149782_v < 0.0F) {
                        List<BlockPos> posList = new ArrayList();
                        BlockPos[] var15 = offsets;
                        int var16 = offsets.length;

                        for(int var17 = 0; var17 < var16; ++var17) {
                           BlockPos offset = var15[var17];
                           BlockPos pos = playerPos.func_177971_a(offset);
                           if (!(mc.field_71439_g.func_174818_b(pos) > (double)((Integer)this.range.getValue() * (Integer)this.range.getValue())) && BlockUtil.getBlock(pos) != Blocks.field_150357_h) {
                              if ((Boolean)this.ignore.getValue() && BlockUtil.getBlock(pos) == Blocks.field_150324_C) {
                                 return;
                              }

                              if (!(mc.field_71439_g.func_70011_f((double)pos.field_177962_a + 0.5D, (double)pos.field_177960_b + 0.5D, (double)pos.field_177961_c + 0.5D) > (double)(Integer)this.range.getValue())) {
                                 posList.add(pos);
                              }
                           }
                        }

                        this.blockMine = (BlockPos)posList.stream().min(Comparator.comparing((p) -> {
                           return mc.field_71439_g.func_70011_f((double)p.field_177962_a + 0.5D, (double)p.field_177960_b + 0.5D, (double)p.field_177961_c + 0.5D);
                        })).orElse((Object)null);
                     }

                     if (this.blockMine != null) {
                        this.working = true;
                        if ((Boolean)this.swing.getValue()) {
                           mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                        }

                        if (((String)this.breakBlock.getValue()).equalsIgnoreCase("Packet")) {
                           mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, this.blockMine, EnumFacing.UP));
                           mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.blockMine, EnumFacing.UP));
                        } else {
                           mc.field_71442_b.func_180512_c(this.blockMine, EnumFacing.UP);
                        }

                     }
                  }
               }
            }
         }
      }
   }

   private static boolean isPos2(BlockPos pos1, BlockPos pos2) {
      if (pos1 != null && pos2 != null) {
         return pos1.field_177962_a == pos2.field_177962_a && pos1.field_177960_b == pos2.field_177960_b && pos1.field_177961_c == pos2.field_177961_c;
      } else {
         return false;
      }
   }
}
