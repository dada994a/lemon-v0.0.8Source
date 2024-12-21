package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PhaseUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "AutoPhase",
   category = Category.Combat
)
public class AutoPhase extends Module {
   ModeSetting mode = this.registerMode("Mode", Arrays.asList("5b", "Jp"), "5b");
   ModeSetting bound;
   BooleanSetting twoBeePvP;
   BooleanSetting update;
   BooleanSetting packet;
   BooleanSetting swing;
   BooleanSetting mine;
   BooleanSetting burrow;
   BooleanSetting doubleBurrow;
   IntegerSetting entity;
   BooleanSetting ignoreCrystal;
   IntegerSetting checkDelay;
   BlockPos originalPos;
   boolean down;
   Timing timing;
   Timing timer;
   int tpid;
   List<Block> blockList;
   BlockPos[] sides;
   BlockPos[] height;
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener;
   @EventHandler
   private final Listener<PacketEvent.Send> sendListener;

   public AutoPhase() {
      this.bound = this.registerMode("Bounds", PhaseUtil.bound, "Min", () -> {
         return ((String)this.mode.getValue()).equals("5b");
      });
      this.twoBeePvP = this.registerBoolean("2b2tpvp", false, () -> {
         return ((String)this.mode.getValue()).equals("5b");
      });
      this.update = this.registerBoolean("Update Pos", false, () -> {
         return ((String)this.mode.getValue()).equals("5b");
      });
      this.packet = this.registerBoolean("Packet Place", true, () -> {
         return ((String)this.mode.getValue()).equals("Jp");
      });
      this.swing = this.registerBoolean("Swing", true, () -> {
         return ((String)this.mode.getValue()).equals("Jp");
      });
      this.mine = this.registerBoolean("Mine", true, () -> {
         return ((String)this.mode.getValue()).equals("Jp");
      });
      this.burrow = this.registerBoolean("Try Burrow", true, () -> {
         return ((String)this.mode.getValue()).equals("Jp");
      });
      this.doubleBurrow = this.registerBoolean("Double", true, () -> {
         return ((String)this.mode.getValue()).equals("Jp") && (Boolean)this.burrow.getValue();
      });
      this.entity = this.registerInteger("Entity Time", 5, 0, 10, () -> {
         return ((String)this.mode.getValue()).equals("Jp");
      });
      this.ignoreCrystal = this.registerBoolean("Ignore Crystal", true, () -> {
         return ((String)this.mode.getValue()).equals("Jp");
      });
      this.checkDelay = this.registerInteger("Check Time", 50, 0, 500, () -> {
         return ((String)this.mode.getValue()).equals("Jp");
      });
      this.timing = new Timing();
      this.timer = new Timing();
      this.tpid = 0;
      this.blockList = Arrays.asList(Blocks.field_150357_h, Blocks.field_150343_Z, Blocks.field_150477_bB, Blocks.field_150467_bQ);
      this.sides = new BlockPos[]{new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0)};
      this.height = new BlockPos[]{new BlockPos(0, 0, 0), new BlockPos(0, 1, 0)};
      this.receiveListener = new Listener((event) -> {
         if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.tpid = ((SPacketPlayerPosLook)event.getPacket()).field_186966_g;
         }

      }, new Predicate[0]);
      this.sendListener = new Listener((event) -> {
         if (event.getPacket() instanceof PositionRotation || event.getPacket() instanceof Position) {
            ++this.tpid;
         }

      }, new Predicate[0]);
   }

   public void onEnable() {
      if (((String)this.mode.getValue()).equals("Jp")) {
         this.down = true;
         this.originalPos = PlayerUtil.getPlayerPos();
         this.originalPos = new BlockPos((double)this.originalPos.field_177962_a, (double)this.originalPos.field_177960_b + 0.2D, (double)this.originalPos.field_177961_c);
         if (BurrowUtil.findHotbarBlock(BlockTrapDoor.class) == -1 || !mc.field_71441_e.func_175623_d(this.originalPos)) {
            this.disable();
            return;
         }

         mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t, (double)((int)mc.field_71439_g.field_70163_u), mc.field_71439_g.field_70161_v);
         this.timing.reset();
         this.timer.reset();
         this.down = false;
      }

   }

   public void onDisable() {
      if (((String)this.mode.getValue()).equals("Jp") && ModuleManager.isModuleEnabled(PacketMine.class)) {
         PacketMine.INSTANCE.lastBlock = null;
      }

   }

   public void onUpdate() {
      if (((String)this.mode.getValue()).equals("Jp")) {
         this.trapdoor();
      } else {
         this.packetFly();
      }

   }

   void packetFly() {
      double[] clip = MotionUtil.forward(0.0624D);
      if (mc.field_71439_g.field_70122_E) {
         this.tp(0.0D, -0.0624D, 0.0D, false);
      } else {
         this.tp(clip[0], 0.0D, clip[1], true);
      }

      this.disable();
   }

   void tp(double x, double y, double z, boolean onGround) {
      double[] dir = MotionUtil.forward(-0.0312D);
      if ((Boolean)this.twoBeePvP.getValue()) {
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t + dir[0], mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + dir[1], onGround));
      }

      mc.field_71439_g.field_71174_a.func_147297_a(new Position(((Boolean)this.twoBeePvP.getValue() ? x / 2.0D : x) + mc.field_71439_g.field_70165_t, y + mc.field_71439_g.field_70163_u, ((Boolean)this.twoBeePvP.getValue() ? z / 2.0D : z) + mc.field_71439_g.field_70161_v, onGround));
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketConfirmTeleport(this.tpid - 1));
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketConfirmTeleport(this.tpid));
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketConfirmTeleport(this.tpid + 1));
      PhaseUtil.doBounds((String)this.bound.getValue(), true);
      if ((Boolean)this.update.getValue()) {
         mc.field_71439_g.func_70107_b(x, y, z);
      }

   }

   private void trapdoor() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L && this.originalPos != null) {
         if (!this.down) {
            if (BurrowUtil.findHotbarBlock(BlockTrapDoor.class) == -1) {
               this.disable();
               return;
            }

            if (this.intersectsWithEntity(this.originalPos) && this.timer.passedS((double)(Integer)this.entity.getValue())) {
               this.disable();
               return;
            }

            EnumFacing facing = BurrowUtil.getTrapdoorFacing(this.originalPos);
            BlockPos burrowPos = null;
            BlockPos[] var3 = this.sides;
            int var4 = var3.length;
            int var5 = 0;

            while(true) {
               if (var5 < var4) {
                  BlockPos side = var3[var5];
                  BlockPos blockPos = PlayerUtil.getPlayerPos().func_177971_a(side);
                  if (BlockUtil.getBlock(blockPos) != Blocks.field_150357_h && BlockUtil.getBlock(blockPos) != Blocks.field_150343_Z) {
                     ++var5;
                     continue;
                  }

                  burrowPos = blockPos;
               }

               int obsidian = BurrowUtil.findHotbarBlock(BlockObsidian.class);
               if (facing == null || burrowPos == null && (Boolean)this.burrow.getValue()) {
                  if ((Boolean)this.burrow.getValue()) {
                     boolean placed = false;
                     if (obsidian != -1) {
                        BlockPos[] var47 = this.sides;
                        int var48 = var47.length;

                        for(int var49 = 0; var49 < var48; ++var49) {
                           BlockPos side = var47[var49];
                           BlockPos blockPos = PlayerUtil.getPlayerPos().func_177971_a(side);
                           if (!this.intersectsWithEntity(blockPos) && BlockUtil.hasNeighbour(blockPos)) {
                              mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(obsidian));
                              BurrowUtil.placeBlock(blockPos, EnumHand.MAIN_HAND, false, false, false, false);
                              if ((Boolean)this.doubleBurrow.getValue()) {
                                 BurrowUtil.placeBlock(blockPos.func_177984_a(), EnumHand.MAIN_HAND, false, false, false, false);
                              }

                              placed = true;
                              break;
                           }
                        }
                     }

                     if (!placed) {
                        this.disable();
                        return;
                     }
                  } else {
                     this.disable();
                  }

                  return;
               }

               if ((Boolean)this.burrow.getValue() && (Boolean)this.doubleBurrow.getValue() && BlockUtil.isAir(burrowPos.func_177984_a())) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(obsidian));
                  BurrowUtil.placeBlock(burrowPos.func_177984_a(), EnumHand.MAIN_HAND, false, false, false, false);
               }

               BlockPos neighbour = this.originalPos.func_177972_a(facing);
               EnumFacing opposite = facing.func_176734_d();
               double x = mc.field_71439_g.field_70165_t;
               double y = mc.field_71439_g.field_70163_u;
               double z = mc.field_71439_g.field_70161_v;
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(x, y + 0.20000000298023224D, z, mc.field_71439_g.field_70122_E));
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(BurrowUtil.findHotbarBlock(BlockTrapDoor.class)));
               boolean sneak = false;
               if ((BlockUtil.blackList.contains(mc.field_71441_e.func_180495_p(neighbour).func_177230_c()) || BlockUtil.shulkerList.contains(mc.field_71441_e.func_180495_p(neighbour).func_177230_c())) && !mc.field_71439_g.func_70093_af()) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
                  mc.field_71439_g.func_70095_a(true);
                  sneak = true;
               }

               rightClickBlock(neighbour, opposite, new Vec3d(0.5D, 0.8D, 0.5D), (Boolean)this.packet.getValue(), (Boolean)this.swing.getValue());
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(mc.field_71439_g.field_71071_by.field_70461_c));
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(x, y, z, mc.field_71439_g.field_70122_E));
               if (sneak) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
                  mc.field_71439_g.func_70095_a(false);
               }

               if ((Boolean)this.burrow.getValue()) {
                  if (burrowPos == null) {
                     return;
                  }

                  mc.field_71439_g.func_70107_b((double)burrowPos.field_177962_a + 0.5D, (double)burrowPos.field_177960_b, (double)burrowPos.field_177961_c + 0.5D);
                  this.disable();
               } else {
                  int bedrocks = 0;
                  int blocks = 0;
                  double xAdd = 0.0D;
                  double zAdd = 0.0D;
                  BlockPos[] var23 = this.sides;
                  int var24 = var23.length;

                  for(int var25 = 0; var25 < var24; ++var25) {
                     BlockPos side = var23[var25];
                     BlockPos[] var27 = this.sides;
                     int var28 = var27.length;

                     for(int var29 = 0; var29 < var28; ++var29) {
                        BlockPos add = var27[var29];
                        if (!this.isPos2(this.originalPos, this.originalPos.func_177971_a(side).func_177971_a(add)) && !this.isPos2(side, add)) {
                           int bedrock = 0;
                           int block = 0;
                           BlockPos sidePos = this.originalPos.func_177971_a(side);
                           BlockPos addPos = this.originalPos.func_177971_a(add);
                           BlockPos addSide = this.originalPos.func_177971_a(side).func_177971_a(add);
                           BlockPos[] var36 = this.height;
                           int var37 = var36.length;

                           for(int var38 = 0; var38 < var37; ++var38) {
                              BlockPos high = var36[var38];
                              Block sideState = mc.field_71441_e.func_180495_p(sidePos.func_177971_a(high)).func_177230_c();
                              Block addState = mc.field_71441_e.func_180495_p(addPos.func_177971_a(high)).func_177230_c();
                              Block addSideState = mc.field_71441_e.func_180495_p(addSide.func_177971_a(high)).func_177230_c();
                              if (this.blockList.contains(sideState)) {
                                 block += 3;
                              }

                              if (sideState == Blocks.field_150357_h) {
                                 bedrock += 3;
                              }

                              if (this.blockList.contains(addState)) {
                                 block += 3;
                              }

                              if (addState == Blocks.field_150357_h) {
                                 bedrock += 3;
                              }

                              if (this.blockList.contains(addSideState)) {
                                 ++block;
                              }

                              if (addSideState == Blocks.field_150357_h) {
                                 ++bedrock;
                              }
                           }

                           boolean shouldSet = false;
                           if (block > blocks) {
                              shouldSet = true;
                           } else if (block == blocks && bedrock > bedrocks) {
                              shouldSet = true;
                           }

                           if (shouldSet) {
                              bedrocks = bedrock;
                              blocks = block;
                              xAdd = this.getAdd(side.field_177962_a + add.field_177962_a);
                              zAdd = this.getAdd(side.field_177961_c + add.field_177961_c);
                           }
                        }
                     }
                  }

                  mc.field_71439_g.func_70107_b((double)this.originalPos.func_177958_n() + xAdd, (double)this.originalPos.func_177956_o(), (double)this.originalPos.func_177952_p() + zAdd);
                  mc.field_71439_g.field_70159_w = 0.0D;
                  mc.field_71439_g.field_70179_y = 0.0D;
                  if (mc.field_71439_g.field_70165_t == (double)this.originalPos.func_177958_n() + xAdd && mc.field_71439_g.field_70161_v == (double)this.originalPos.func_177952_p() + zAdd && !mc.field_71441_e.func_175623_d(this.originalPos) && this.timing.passedMs((long)(Integer)this.checkDelay.getValue())) {
                     this.down = true;
                  }
               }
               break;
            }
         }

         if (this.down) {
            this.timing.reset();
            mc.field_71439_g.field_70159_w = 0.0D;
            mc.field_71439_g.field_70179_y = 0.0D;
            if ((Boolean)this.mine.getValue()) {
               mc.field_71442_b.func_180512_c(this.originalPos, EnumFacing.UP);
            } else {
               this.disable();
            }

            if (mc.field_71441_e.func_175623_d(this.originalPos)) {
               this.disable();
            }
         }

      } else {
         this.disable();
      }
   }

   private double getAdd(int pos) {
      return pos == 1 ? 0.99999999D : 0.0D;
   }

   private boolean isPos2(BlockPos pos1, BlockPos pos2) {
      if (pos1 != null && pos2 != null) {
         return pos1.field_177962_a == pos2.field_177962_a && pos1.field_177960_b == pos2.field_177960_b && pos1.field_177961_c == pos2.field_177961_c;
      } else {
         return false;
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

   private boolean intersectsWithEntity(BlockPos pos) {
      Iterator var2 = mc.field_71441_e.field_72996_f.iterator();

      Entity entity;
      do {
         do {
            do {
               if (!var2.hasNext()) {
                  return false;
               }

               entity = (Entity)var2.next();
            } while(entity instanceof EntityItem);
         } while(entity instanceof EntityEnderCrystal && (Boolean)this.ignoreCrystal.getValue());
      } while(entity == mc.field_71439_g || !(new AxisAlignedBB(pos)).func_72326_a(entity.func_174813_aQ()));

      return true;
   }
}
