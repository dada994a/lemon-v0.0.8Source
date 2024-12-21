package com.lemonclient.client.module.modules.dev;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.lemonclient.api.event.Phase;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MathUtil;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerPacket;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPiston;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "AntiHoleCamper",
   category = Category.Dev,
   priority = 1000
)
public class AntiHoleCamper extends Module {
   IntegerSetting delay = this.registerInteger("Delay", 0, 0, 20);
   BooleanSetting pause = this.registerBoolean("Pause When Move", true);
   ModeSetting mode = this.registerMode("Mode", Arrays.asList("Block", "Torch", "Both"), "Block");
   IntegerSetting range = this.registerInteger("Range", 6, 0, 10);
   BooleanSetting look = this.registerBoolean("Looking Target", false);
   BooleanSetting ground = this.registerBoolean("OnGround Check", true);
   BooleanSetting box = this.registerBoolean("Entity Box", true);
   BooleanSetting hole = this.registerBoolean("Double Hole Check", false);
   BooleanSetting pushCheck = this.registerBoolean("Push Check", false);
   BooleanSetting headCheck = this.registerBoolean("Head Check", false);
   BooleanSetting breakRedstone = this.registerBoolean("Break Redstone", false);
   BooleanSetting pushedCheck = this.registerBoolean("Pushed Check", true, () -> {
      return (Boolean)this.breakRedstone.getValue();
   });
   ModeSetting breakBlock = this.registerMode("Break Block", Arrays.asList("Normal", "Packet"), "Packet", () -> {
      return (Boolean)this.breakRedstone.getValue();
   });
   BooleanSetting packetPiston = this.registerBoolean("Packet Place Piston", true);
   BooleanSetting packetRedstone = this.registerBoolean("Packet Place Redstone", true);
   BooleanSetting swing = this.registerBoolean("Swing", true);
   BooleanSetting rotate = this.registerBoolean("Rotate", false);
   BooleanSetting block = this.registerBoolean("Place Block", true);
   BooleanSetting packet = this.registerBoolean("Packet Place", true, () -> {
      return (Boolean)this.block.getValue();
   });
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true);
   BooleanSetting update = this.registerBoolean("Update Controller", true);
   BooleanSetting force = this.registerBoolean("Force Rotate", true);
   BooleanSetting strict = this.registerBoolean("Strict", true);
   BooleanSetting raytrace = this.registerBoolean("RayTrace", true);
   DoubleSetting maxSpeed = this.registerDouble("Max Target Speed", 5.0D, 0.0D, 50.0D);
   BooleanSetting debug = this.registerBoolean("Debug Msg", true);
   ModeSetting disable = this.registerMode("Disable Mode", Arrays.asList("NoDisable", "Check", "AutoDisable"), "AutoDisable");
   private final Timing timer = new Timing();
   BlockPos beforePlayerPos;
   BlockPos pistonPos;
   BlockPos redstonePos;
   AntiHoleCamper.PistonPos pos = null;
   boolean useBlock;
   int redstoneSlot;
   int pistonSlot;
   int obsiSlot;
   int waited;
   int[] enemyCoordsInt;
   EntityPlayer aimTarget = null;
   Vec2f rotation;
   Vec3d[] sides = new Vec3d[]{new Vec3d(0.25D, 0.0D, 0.25D), new Vec3d(-0.25D, 0.0D, 0.25D), new Vec3d(0.25D, 0.0D, -0.25D), new Vec3d(-0.25D, 0.0D, -0.25D)};
   @EventHandler
   private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener((event) -> {
      if (event.getPhase() == Phase.PRE && this.rotation != null) {
         PlayerPacket packet = new PlayerPacket(this, new Vec2f(this.rotation.field_189982_i, PlayerPacketManager.INSTANCE.getServerSideRotation().field_189983_j));
         PlayerPacketManager.INSTANCE.addPacket(packet);
      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.Send> sendListener = new Listener((event) -> {
      if (this.rotation != null && (Boolean)this.force.getValue()) {
         if (event.getPacket() instanceof Rotation) {
            ((Rotation)event.getPacket()).field_149476_e = this.rotation.field_189982_i;
         }

         if (event.getPacket() instanceof PositionRotation) {
            ((PositionRotation)event.getPacket()).field_149476_e = this.rotation.field_189982_i;
         }
      }

   }, new Predicate[0]);

   private void switchTo(int slot, Runnable runnable) {
      int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
      if (slot >= 0 && slot != oldslot) {
         if (slot < 9) {
            boolean packetSwitch = (Boolean)this.packetSwitch.getValue();
            if (packetSwitch) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
            } else {
               mc.field_71439_g.field_71071_by.field_70461_c = slot;
            }

            if ((Boolean)this.update.getValue()) {
               mc.field_71442_b.func_78765_e();
            }

            runnable.run();
            if (packetSwitch) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(oldslot));
            } else {
               mc.field_71439_g.field_71071_by.field_70461_c = oldslot;
            }
         }

      } else {
         runnable.run();
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

   private boolean airBlock(BlockPos pos) {
      return BlockUtil.canReplace(pos);
   }

   private boolean canPlacePiston(BlockPos pos, EnumFacing facing) {
      BlockPos p = pos.func_177972_a(facing);
      BlockPos push = pos.func_177967_a(facing, -1);
      double feetY = mc.field_71439_g.field_70163_u;
      return (!this.intersectsWithEntity(p) && this.airBlock(p) && (!(PlayerUtil.getDistanceI(p) < 1.4D + (double)p.func_177956_o() - feetY) || !((double)p.func_177956_o() > feetY + 1.0D)) && (!(PlayerUtil.getDistanceI(p) < 2.4D + feetY - (double)p.func_177956_o()) || !((double)p.func_177956_o() < feetY)) && BlockUtil.canPlaceWithoutBase(p, (Boolean)this.strict.getValue(), (Boolean)this.raytrace.getValue(), true) || this.isFacing(p, pos) && (mc.field_71441_e.func_180495_p(p).func_177230_c() instanceof BlockPistonBase || mc.field_71441_e.func_180495_p(p).func_177230_c() == Blocks.field_150331_J || mc.field_71441_e.func_180495_p(p).func_177230_c() == Blocks.field_150320_F)) && (!(Boolean)this.hole.getValue() || this.airBlock(push)) && (!(Boolean)this.pushCheck.getValue() || this.airBlock(push.func_177984_a()) && (this.airBlock(push.func_177981_b(2)) || this.airBlock(push)));
   }

   public BlockPos getRedstonePos(BlockPos pistonPos) {
      BlockPos pos = this.hasRedstoneBlock(pistonPos);
      if (pos != null) {
         return pos;
      } else {
         List<BlockPos> redstone = new ArrayList();
         int var6;
         if (this.useBlock) {
            EnumFacing[] var4 = EnumFacing.field_82609_l;
            int var5 = var4.length;

            for(var6 = 0; var6 < var5; ++var6) {
               EnumFacing facing = var4[var6];
               redstone.add(pistonPos.func_177972_a(facing));
            }
         } else {
            BlockPos[] offsets = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
            BlockPos[] var14 = offsets;
            var6 = offsets.length;

            for(int var15 = 0; var15 < var6; ++var15) {
               BlockPos offs = var14[var15];

               for(int i = 0; i < 2; ++i) {
                  BlockPos torch = pistonPos.func_177979_c(i).func_177971_a(offs);
                  if (i != 1 || !BlockUtil.isBlockUnSolid(torch.func_177984_a())) {
                     redstone.add(torch);
                  }
               }
            }
         }

         List<BlockPos> redstone = (List)redstone.stream().filter((p) -> {
            return !ColorMain.INSTANCE.breakList.contains(p) && !this.intersectsWithEntity(p) && mc.field_71439_g.func_70011_f((double)p.func_177958_n() + 0.5D, (double)p.func_177956_o() + 0.5D, (double)p.func_177952_p() + 0.5D) <= (double)(Integer)this.range.getValue();
         }).collect(Collectors.toList());
         if (redstone.isEmpty()) {
            return null;
         } else {
            List<BlockPos> hasBase = (List)redstone.stream().filter((p) -> {
               return BlockUtil.canPlaceWithoutBase(p, (Boolean)this.strict.getValue(), (Boolean)this.raytrace.getValue(), false);
            }).collect(Collectors.toList());
            if (hasBase.isEmpty()) {
               hasBase.addAll(redstone);
            }

            Stream var10000 = hasBase.stream();
            EntityPlayerSP var10001 = mc.field_71439_g;
            var10001.getClass();
            return (BlockPos)var10000.min(Comparator.comparing(var10001::func_174818_b)).orElse((Object)null);
         }
      }
   }

   public void onTick() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         this.rotation = null;
         this.aimTarget = null;
         this.redstoneSlot = this.pistonSlot = this.obsiSlot - 1;
         if (!this.ready()) {
            if (!((String)this.disable.getValue()).equals("NoDisable")) {
               this.disable();
            }

         } else {
            if (!(Boolean)this.look.getValue()) {
               this.aimTarget = PlayerUtil.getNearestPlayer((double)(Integer)this.range.getValue() + 1.5D);
            } else {
               this.aimTarget = PlayerUtil.findLookingPlayer((double)(Integer)this.range.getValue() + 1.5D);
            }

            this.pos = null;
            if (this.aimTarget != null) {
               if (LemonClient.speedUtil.getPlayerSpeed(this.aimTarget) > (Double)this.maxSpeed.getValue()) {
                  return;
               }

               if (!this.aimTarget.field_70122_E && (Boolean)this.ground.getValue()) {
                  return;
               }

               this.beforePlayerPos = new BlockPos(this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v);
               this.enemyCoordsInt = new int[]{(int)this.aimTarget.field_70165_t, (int)this.aimTarget.field_70163_u, (int)this.aimTarget.field_70161_v};
               ArrayList list;
               Vec3d[] var2;
               int var3;
               int var4;
               Vec3d side;
               Vec3d vec3d;
               BlockPos blockPos;
               AntiHoleCamper.PistonPos piston;
               if (!(Boolean)this.box.getValue()) {
                  this.pos = this.getPos(this.beforePlayerPos, this.beforePlayerPos);
               } else {
                  list = new ArrayList();
                  var2 = this.sides;
                  var3 = var2.length;

                  for(var4 = 0; var4 < var3; ++var4) {
                     side = var2[var4];
                     vec3d = new Vec3d(this.aimTarget.field_70165_t + side.field_72450_a, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v + side.field_72449_c);
                     blockPos = vec3toBlockPos(vec3d);
                     piston = this.getPos(blockPos, blockPos);
                     if (piston != null) {
                        list.add(piston);
                     }
                  }

                  this.pos = (AntiHoleCamper.PistonPos)list.stream().filter((p) -> {
                     return p.getMaxRange() <= (double)(Integer)this.range.getValue();
                  }).min(Comparator.comparing(AntiHoleCamper.PistonPos::getMaxRange)).orElse((Object)null);
               }

               if (this.pos == null) {
                  if ((Boolean)this.box.getValue()) {
                     list = new ArrayList();
                     var2 = this.sides;
                     var3 = var2.length;

                     for(var4 = 0; var4 < var3; ++var4) {
                        side = var2[var4];
                        vec3d = new Vec3d(this.aimTarget.field_70165_t + side.field_72450_a, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v + side.field_72449_c);
                        blockPos = vec3toBlockPos(vec3d);
                        piston = this.getPos(blockPos.func_177984_a(), blockPos);
                        if (piston != null) {
                           list.add(piston);
                        }
                     }

                     this.pos = (AntiHoleCamper.PistonPos)list.stream().filter((p) -> {
                        return p.getMaxRange() <= (double)(Integer)this.range.getValue();
                     }).min(Comparator.comparing(AntiHoleCamper.PistonPos::getMaxRange)).orElse((Object)null);
                  } else {
                     this.pos = this.getPos(this.beforePlayerPos.func_177984_a(), this.beforePlayerPos);
                  }
               }
            } else {
               if ((Boolean)this.debug.getValue()) {
                  MessageBus.sendClientDeleteMessage("Cant find target", Notification.Type.ERROR, "AntiCamp", 7);
               }

               if (!((String)this.disable.getValue()).equals("NoDisable")) {
                  this.disable();
               }
            }

            if (this.pistonPos != null && this.beforePlayerPos != null) {
               float[] angle = MathUtil.calcAngle(new Vec3d((double)this.pistonPos.field_177962_a, 0.0D, (double)this.pistonPos.field_177961_c), new Vec3d((double)this.beforePlayerPos.field_177962_a, 0.0D, (double)this.beforePlayerPos.field_177961_c));
               this.rotation = new Vec2f(angle[0] + 180.0F, angle[1]);
            }

            if (this.waited++ >= (Integer)this.delay.getValue() && (!MotionUtil.isMoving(mc.field_71439_g) || !(Boolean)this.pause.getValue())) {
               this.waited = 0;
               boolean placed = false;
               if (this.pos != null) {
                  this.pistonPos = this.pos.piston;
                  this.redstonePos = this.pos.redstone;
                  this.beforePlayerPos = this.pos.calcPos;
                  if (BurrowUtil.getFirstFacing(this.redstonePos) == null) {
                     this.placePiston(this.pistonPos);
                     this.placeRedstone(this.redstonePos);
                  } else {
                     this.placeRedstone(this.redstonePos);
                     this.placePiston(this.pistonPos);
                  }
               }

               if ((Boolean)this.block.getValue() && this.beforePlayerPos != null) {
                  if (this.timer.passedMs(500L)) {
                     this.switchTo(this.obsiSlot, () -> {
                        BlockUtil.placeBlock(this.beforePlayerPos, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), (Boolean)this.strict.getValue(), (Boolean)this.raytrace.getValue(), (Boolean)this.swing.getValue());
                     });
                     this.beforePlayerPos = null;
                     if (((String)this.disable.getValue()).equals("AutoDisable")) {
                        this.disable();
                        return;
                     }
                  }
               } else if (((String)this.disable.getValue()).equals("AutoDisable")) {
                  this.disable();
               }

               if (this.beforePlayerPos != null) {
                  placed = mc.field_71441_e.func_180495_p(this.beforePlayerPos).func_177230_c() == Blocks.field_150343_Z;
                  if ((Boolean)this.breakRedstone.getValue() && this.redstonePos != null && !this.airBlock(this.redstonePos) && (!(Boolean)this.pushedCheck.getValue() || mc.field_71441_e.func_180495_p(this.beforePlayerPos).func_177230_c() == Blocks.field_150332_K || mc.field_71441_e.func_180495_p(this.beforePlayerPos.func_177984_a()).func_177230_c() == Blocks.field_150332_K)) {
                     this.doBreak(this.redstonePos);
                  }
               }

               if (placed) {
                  this.beforePlayerPos = null;
               }

               if (((String)this.disable.getValue()).equals("Check") && (!(Boolean)this.block.getValue() || placed)) {
                  this.disable();
               }

            }
         }
      } else {
         this.disable();
      }
   }

   private boolean isPos2(BlockPos pos1, BlockPos pos2) {
      if (pos1 != null && pos2 != null) {
         return pos1.field_177962_a == pos2.field_177962_a && pos1.field_177960_b == pos2.field_177960_b && pos1.field_177961_c == pos2.field_177961_c;
      } else {
         return false;
      }
   }

   private void placePiston(BlockPos pistonPos) {
      if (BlockUtil.isAir(pistonPos)) {
         mc.field_71439_g.field_71174_a.func_147297_a(new Rotation(this.rotation.field_189982_i, this.rotation.field_189983_j, true));
         this.switchTo(this.pistonSlot, () -> {
            BlockUtil.placeBlock(pistonPos, false, (Boolean)this.packetPiston.getValue(), (Boolean)this.strict.getValue(), (Boolean)this.raytrace.getValue(), (Boolean)this.swing.getValue());
            if ((Boolean)this.rotate.getValue()) {
               EntityUtil.facePlacePos(pistonPos, (Boolean)this.strict.getValue(), (Boolean)this.raytrace.getValue());
            }

         });
      }
   }

   private void placeRedstone(BlockPos redstonePos) {
      if (!this.useBlock && mc.field_71441_e.func_180495_p(redstonePos.func_177977_b()).func_177230_c() == Blocks.field_150350_a) {
         BlockPos obsiPos = new BlockPos(redstonePos.field_177962_a, redstonePos.field_177960_b - 1, redstonePos.field_177961_c);
         this.switchTo(this.obsiSlot, () -> {
            BlockUtil.placeBlock(obsiPos, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), (Boolean)this.strict.getValue(), (Boolean)this.raytrace.getValue(), (Boolean)this.swing.getValue());
         });
      }

      this.switchTo(this.redstoneSlot, () -> {
         BlockUtil.placeBlock(redstonePos, (Boolean)this.rotate.getValue(), (Boolean)this.packetRedstone.getValue(), (Boolean)this.strict.getValue(), (Boolean)this.raytrace.getValue(), (Boolean)this.swing.getValue());
      });
   }

   private AntiHoleCamper.PistonPos getPos(BlockPos calcPos, BlockPos playerPos) {
      if (mc.field_71441_e.func_180495_p(calcPos).func_177230_c() != Blocks.field_150357_h && mc.field_71441_e.func_180495_p(calcPos).func_177230_c() != Blocks.field_150343_Z) {
         List<AntiHoleCamper.PistonPos> posList = new ArrayList();
         if ((Boolean)this.headCheck.getValue() && !this.airBlock(playerPos.func_177981_b(2))) {
            return null;
         } else {
            EnumFacing[] var4 = EnumFacing.field_82609_l;
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               EnumFacing facing = var4[var6];
               if (facing != EnumFacing.UP && facing != EnumFacing.DOWN && this.canPlacePiston(calcPos, facing)) {
                  BlockPos pistonPos = calcPos.func_177972_a(facing);
                  BlockPos redstonePos = this.getRedstonePos(pistonPos);
                  if (redstonePos != null && (BlockUtil.hasNeighbour(redstonePos) || BlockUtil.hasNeighbour(pistonPos))) {
                     posList.add(new AntiHoleCamper.PistonPos(pistonPos, redstonePos, calcPos));
                  }
               }
            }

            return (AntiHoleCamper.PistonPos)posList.stream().filter((p) -> {
               return p.getMaxRange() <= (double)(Integer)this.range.getValue();
            }).min(Comparator.comparing(AntiHoleCamper.PistonPos::getMaxRange)).orElse((Object)null);
         }
      } else {
         return null;
      }
   }

   public static BlockPos vec3toBlockPos(Vec3d vec3d) {
      return new BlockPos(Math.floor(vec3d.field_72450_a), (double)Math.round(vec3d.field_72448_b), Math.floor(vec3d.field_72449_c));
   }

   private boolean ready() {
      this.pistonSlot = findHotbarBlock(Blocks.field_150331_J);
      if (this.pistonSlot == -1) {
         this.pistonSlot = findHotbarBlock(Blocks.field_150320_F);
      }

      this.redstoneSlot = !((String)this.mode.getValue()).equals("Torch") ? findHotbarBlock(Blocks.field_150451_bX) : findHotbarBlock(Blocks.field_150429_aA);
      if (((String)this.mode.getValue()).equals("Both") && this.redstoneSlot == -1) {
         this.redstoneSlot = findHotbarBlock(Blocks.field_150429_aA);
      }

      this.obsiSlot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
      if (this.redstoneSlot == -1) {
         if ((Boolean)this.debug.getValue()) {
            MessageBus.sendClientDeleteMessage("Cant find Redstone", Notification.Type.ERROR, "AntiCamp", 7);
         }

         return false;
      } else {
         this.useBlock = this.redstoneSlot == findHotbarBlock(Blocks.field_150451_bX);
         if ((!this.useBlock || (Boolean)this.block.getValue()) && this.obsiSlot == -1) {
            if ((Boolean)this.debug.getValue()) {
               MessageBus.sendClientDeleteMessage("Cant find Obsidian", Notification.Type.ERROR, "AntiCamp", 7);
            }

            return false;
         } else if (BurrowUtil.findHotbarBlock(ItemPiston.class) == -1) {
            if ((Boolean)this.debug.getValue()) {
               MessageBus.sendClientDeleteMessage("Cant find Piston", Notification.Type.ERROR, "AntiCamp", 7);
            }

            return false;
         } else {
            return true;
         }
      }
   }

   public static int findHotbarBlock(Block blockIn) {
      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock && ((ItemBlock)stack.func_77973_b()).func_179223_d() == blockIn) {
            return i;
         }
      }

      return -1;
   }

   private void doBreak(BlockPos pos) {
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

   public boolean isFacing(BlockPos pos, BlockPos facingPos) {
      ImmutableMap<IProperty<?>, Comparable<?>> properties = mc.field_71441_e.func_180495_p(pos).func_177228_b();
      UnmodifiableIterator var4 = properties.keySet().iterator();

      BlockPos pushPos;
      do {
         IProperty prop;
         do {
            do {
               if (!var4.hasNext()) {
                  return false;
               }

               prop = (IProperty)var4.next();
            } while(prop.func_177699_b() != EnumFacing.class);
         } while(!prop.func_177701_a().equals("facing") && !prop.func_177701_a().equals("rotation"));

         pushPos = pos.func_177972_a((EnumFacing)properties.get(prop));
      } while(!this.isPos2(facingPos, pushPos));

      return true;
   }

   public BlockPos hasRedstoneBlock(BlockPos pos) {
      List<BlockPos> redstone = new ArrayList();
      BlockPos[] offsets = new BlockPos[]{new BlockPos(0, -1, 0), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
      BlockPos[] var4 = offsets;
      int var5 = offsets.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         BlockPos redstonePos = var4[var6];
         redstone.add(pos.func_177971_a(redstonePos));
      }

      if (this.useBlock) {
         redstone.add(pos.func_177982_a(0, 1, 0));
      }

      return (BlockPos)redstone.stream().filter((p) -> {
         return BlockUtil.getBlock(p) == Blocks.field_150429_aA || BlockUtil.getBlock(p) == Blocks.field_150451_bX;
      }).min(Comparator.comparing(PlayerUtil::getDistanceI)).orElse((Object)null);
   }

   static class PistonPos {
      public BlockPos piston;
      public BlockPos redstone;
      public BlockPos calcPos;

      public PistonPos(BlockPos pistonPos, BlockPos redstonePos, BlockPos pos) {
         this.piston = pistonPos;
         this.redstone = redstonePos;
         this.calcPos = pos;
      }

      public double getMaxRange() {
         return this.piston != null && this.redstone != null ? Math.max(PlayerUtil.getDistance(this.piston), PlayerUtil.getDistance(this.redstone)) : 999999.0D;
      }
   }
}
