package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.dev.BedCevBreaker;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.block.BlockConcretePowder;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "AutoHoleMine",
   category = Category.Combat
)
public final class AutoHoleMine extends Module {
   public static AutoHoleMine INSTANCE;
   BooleanSetting breakTrap = this.registerBoolean("Break Trap", false);
   BooleanSetting doubleMine = this.registerBoolean("Double Mine", true);
   BooleanSetting ignore = this.registerBoolean("Ignore Bed", false);
   BooleanSetting ignorePiston = this.registerBoolean("Ignore Piston", false);
   BooleanSetting ignoreWeb = this.registerBoolean("Ignore Web", false);
   BooleanSetting fire = this.registerBoolean("Fire", false);
   BooleanSetting sand = this.registerBoolean("Falling Blocks", false);
   public boolean working;
   BlockPos[] side = new BlockPos[]{new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0)};

   public AutoHoleMine() {
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
            }

            EntityPlayer target = PlayerUtil.getNearestPlayer(8.0D);
            if (target != null) {
               BlockPos feet = new BlockPos(target.field_70165_t, target.field_70163_u + 0.2D, target.field_70161_v);
               double breakRange = 0.0D;
               BlockPos doublePos = null;
               if (ModuleManager.isModuleEnabled(PacketMine.class)) {
                  doublePos = PacketMine.INSTANCE.doublePos;
                  breakRange = (Double)PacketMine.INSTANCE.breakRange.getValue();
               }

               BlockPos pos = null;
               BlockPos[] var8 = this.side;
               int var9 = var8.length;

               int var10;
               BlockPos side;
               BlockPos side;
               for(var10 = 0; var10 < var9; ++var10) {
                  BlockPos side = var8[var10];
                  side = feet.func_177971_a(side);
                  side = side.func_177971_a(side);
                  if (BlockUtil.isAir(side)) {
                     if (BlockUtil.isAir(side.func_177984_a())) {
                        return;
                     }

                     if (BlockUtil.isAirBlock(side) && BlockUtil.isAirBlock(side.func_177984_a())) {
                        if (!(Boolean)this.breakTrap.getValue()) {
                           return;
                        }

                        pos = side.func_177984_a();
                     }
                  }
               }

               if (pos != null) {
                  this.surroundMine(pos);
               } else {
                  List<BlockPos> posList = new ArrayList();
                  BlockPos[] var17 = this.side;
                  var10 = var17.length;

                  BlockPos surroundPos;
                  int var21;
                  for(var21 = 0; var21 < var10; ++var21) {
                     side = var17[var21];
                     side = feet.func_177971_a(side);
                     surroundPos = side.func_177971_a(side);
                     if (BlockUtil.isAirBlock(surroundPos) && BlockUtil.isAirBlock(surroundPos.func_177984_a())) {
                        if (this.checkMine(side, breakRange)) {
                           posList.add(side);
                        }
                     } else if (BlockUtil.isAir(side) && BlockUtil.isAirBlock(surroundPos.func_177984_a())) {
                        if (this.checkMine(surroundPos, breakRange)) {
                           posList.add(surroundPos);
                        }
                     } else if (BlockUtil.isAir(side) && BlockUtil.isAirBlock(surroundPos) && this.checkMine(surroundPos.func_177984_a(), breakRange)) {
                        posList.add(surroundPos.func_177984_a());
                     }
                  }

                  Stream var10001;
                  EntityPlayerSP var10002;
                  if (!posList.isEmpty()) {
                     var10001 = posList.stream();
                     var10002 = mc.field_71439_g;
                     var10002.getClass();
                     this.surroundMine((BlockPos)var10001.min(Comparator.comparing(var10002::func_174818_b)).orElse((Object)null));
                  } else {
                     BlockPos[] var20;
                     int var23;
                     if ((Boolean)this.doubleMine.getValue()) {
                        List<AutoHoleMine.DoubleBreak> breakList = new ArrayList();
                        var20 = this.side;
                        var21 = var20.length;

                        BlockPos crystalPos;
                        for(var23 = 0; var23 < var21; ++var23) {
                           side = var20[var23];
                           surroundPos = feet.func_177971_a(side);
                           crystalPos = surroundPos.func_177971_a(side);
                           if (!BlockUtil.isAir(surroundPos) && !BlockUtil.isAirBlock(crystalPos) && BlockUtil.isAirBlock(crystalPos.func_177984_a())) {
                              if (this.checkMine(surroundPos, breakRange) && this.checkMine(crystalPos, breakRange)) {
                                 breakList.add(new AutoHoleMine.DoubleBreak(surroundPos, crystalPos));
                              }
                           } else if (!BlockUtil.isAir(surroundPos) && !BlockUtil.isAirBlock(crystalPos.func_177984_a()) && BlockUtil.isAirBlock(crystalPos)) {
                              if (this.checkMine(surroundPos, breakRange) && this.checkMine(crystalPos.func_177984_a(), breakRange)) {
                                 breakList.add(new AutoHoleMine.DoubleBreak(surroundPos, crystalPos.func_177984_a()));
                              }
                           } else if (BlockUtil.isAir(surroundPos) && !BlockUtil.isAirBlock(crystalPos) && !BlockUtil.isAirBlock(crystalPos.func_177984_a()) && this.checkMine(crystalPos, breakRange) && this.checkMine(crystalPos.func_177984_a(), breakRange)) {
                              breakList.add(new AutoHoleMine.DoubleBreak(crystalPos, crystalPos.func_177984_a()));
                           }
                        }

                        if (breakList.isEmpty()) {
                           var20 = this.side;
                           var21 = var20.length;

                           for(var23 = 0; var23 < var21; ++var23) {
                              side = var20[var23];
                              surroundPos = feet.func_177971_a(side);
                              crystalPos = surroundPos.func_177971_a(side);
                              if (this.checkMine(surroundPos, breakRange) && this.checkMine(crystalPos, breakRange) && this.checkMine(crystalPos.func_177984_a(), breakRange)) {
                                 breakList.add(new AutoHoleMine.DoubleBreak(crystalPos, crystalPos.func_177984_a()));
                              }
                           }
                        }

                        if (breakList.isEmpty()) {
                           var20 = this.side;
                           var21 = var20.length;

                           for(var23 = 0; var23 < var21; ++var23) {
                              side = var20[var23];
                              surroundPos = feet.func_177971_a(side);
                              crystalPos = surroundPos.func_177971_a(side);
                              if (!BlockUtil.isAirBlock(crystalPos) && !BlockUtil.isAirBlock(crystalPos.func_177984_a()) && !this.checkMine(crystalPos) && !this.checkMine(crystalPos.func_177984_a()) && this.checkMine(surroundPos, breakRange) && this.checkMine(surroundPos.func_177984_a(), breakRange)) {
                                 breakList.add(new AutoHoleMine.DoubleBreak(surroundPos, surroundPos.func_177984_a()));
                              }
                           }
                        }

                        if (!breakList.isEmpty()) {
                           AutoHoleMine.DoubleBreak doubleBreak = (AutoHoleMine.DoubleBreak)breakList.stream().min(Comparator.comparing(AutoHoleMine.DoubleBreak::maxRange)).orElse((Object)null);
                           this.surroundMine(doubleBreak.doublePos);
                           if (doublePos == null) {
                              this.surroundMine(doubleBreak.packetPos);
                           }

                           return;
                        }
                     } else {
                        var17 = this.side;
                        var10 = var17.length;

                        for(var21 = 0; var21 < var10; ++var21) {
                           side = var17[var21];
                           side = feet.func_177971_a(side);
                           surroundPos = side.func_177971_a(side);
                           if (!BlockUtil.isAir(side) && this.checkMine(side, breakRange)) {
                              if (BlockUtil.isAirBlock(surroundPos) && this.checkMine(surroundPos, breakRange) || BlockUtil.isAirBlock(surroundPos.func_177984_a()) && this.checkMine(surroundPos.func_177984_a(), breakRange)) {
                                 posList.add(side);
                              }
                           } else if (!BlockUtil.isAirBlock(surroundPos) && this.checkMine(surroundPos, breakRange)) {
                              if (BlockUtil.isAir(side) && this.checkMine(side, breakRange) || BlockUtil.isAirBlock(surroundPos.func_177984_a()) && this.checkMine(surroundPos.func_177984_a(), breakRange)) {
                                 posList.add(surroundPos);
                              }
                           } else if (!BlockUtil.isAirBlock(surroundPos.func_177984_a()) && this.checkMine(surroundPos.func_177984_a(), breakRange) && (BlockUtil.isAir(side) && this.checkMine(side, breakRange) || BlockUtil.isAirBlock(surroundPos) && this.checkMine(surroundPos, breakRange))) {
                              posList.add(surroundPos.func_177984_a());
                           }
                        }

                        if (posList.isEmpty()) {
                           var17 = this.side;
                           var10 = var17.length;

                           for(var21 = 0; var21 < var10; ++var21) {
                              side = var17[var21];
                              side = feet.func_177971_a(side);
                              surroundPos = side.func_177971_a(side);
                              if (this.checkMine(side, breakRange) && this.checkMine(surroundPos, breakRange) && this.checkMine(surroundPos.func_177984_a(), breakRange)) {
                                 posList.add(surroundPos.func_177984_a());
                              }
                           }
                        }

                        if (!posList.isEmpty()) {
                           var10001 = posList.stream();
                           var10002 = mc.field_71439_g;
                           var10002.getClass();
                           this.surroundMine((BlockPos)var10001.min(Comparator.comparing(var10002::func_174818_b)).orElse((Object)null));
                           return;
                        }
                     }

                     boolean hole = true;
                     var20 = this.side;
                     var21 = var20.length;

                     for(var23 = 0; var23 < var21; ++var23) {
                        side = var20[var23];
                        if (BlockUtil.isAir(feet.func_177971_a(side)) && BlockUtil.isAir(feet.func_177971_a(side).func_177984_a())) {
                           hole = false;
                        }
                     }

                     if (hole) {
                        var20 = this.side;
                        var21 = var20.length;

                        for(var23 = 0; var23 < var21; ++var23) {
                           side = var20[var23];
                           surroundPos = feet.func_177971_a(side);
                           if (this.checkMine(surroundPos, breakRange)) {
                              posList.add(surroundPos);
                           }
                        }

                        if (!posList.isEmpty()) {
                           var10001 = posList.stream();
                           var10002 = mc.field_71439_g;
                           var10002.getClass();
                           this.surroundMine((BlockPos)var10001.min(Comparator.comparing(var10002::func_174818_b)).orElse((Object)null));
                        }

                     }
                  }
               }
            }
         }
      }
   }

   private boolean checkMine(BlockPos pos) {
      return !BlockUtil.isAir(pos) && BlockUtil.getBlock(pos).field_149782_v >= 0.0F && this.can(pos);
   }

   private boolean checkMine(BlockPos pos, double range) {
      return !BlockUtil.isAir(pos) && BlockUtil.getBlock(pos).field_149782_v >= 0.0F && this.can(pos) && this.getDistance(pos) <= range;
   }

   private boolean can(BlockPos pos) {
      return (!(Boolean)this.ignore.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150324_C) && (!(Boolean)this.ignorePiston.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150332_K) && (!(Boolean)this.ignoreWeb.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150321_G) && ((Boolean)this.fire.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150480_ab) && ((Boolean)this.sand.getValue() || mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150354_m && mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150351_n && mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150467_bQ && !(mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockConcretePowder));
   }

   private void surroundMine(BlockPos pos) {
      if (pos != null && this.checkMine(pos)) {
         this.working = true;
         BlockPos doublePos = null;
         BlockPos instantPos = null;
         if (ModuleManager.isModuleEnabled(PacketMine.class)) {
            instantPos = PacketMine.INSTANCE.packetPos;
            doublePos = PacketMine.INSTANCE.doublePos;
         }

         if (instantPos == null || !instantPos.equals(pos)) {
            if (doublePos == null || !doublePos.equals(pos)) {
               mc.field_71442_b.func_180512_c(pos, BlockUtil.getRayTraceFacing(pos));
            }
         }
      }
   }

   private double getDistance(BlockPos pos) {
      return mc.field_71439_g.func_70011_f((double)pos.field_177962_a + 0.5D, (double)pos.field_177960_b + 0.5D, (double)pos.field_177961_c + 0.5D);
   }

   class DoubleBreak {
      BlockPos packetPos;
      BlockPos doublePos;

      public DoubleBreak(BlockPos packetPos, BlockPos doublePos) {
         this.packetPos = packetPos;
         this.doublePos = doublePos;
      }

      public double maxRange() {
         double packetRange = AutoHoleMine.this.getDistance(this.packetPos);
         double doubleRange = AutoHoleMine.this.getDistance(this.doublePos);
         return Math.max(packetRange, doubleRange);
      }
   }
}
