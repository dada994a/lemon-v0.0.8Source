package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.HoleUtil;
import com.lemonclient.api.util.world.MathUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "AutoTrap",
   category = Category.Combat
)
public class AutoTrap extends Module {
   IntegerSetting delay = this.registerInteger("Delay", 0, 0, 20);
   IntegerSetting range = this.registerInteger("Range", 5, 0, 10);
   IntegerSetting bpt = this.registerInteger("BlocksPerTick", 4, 0, 20);
   BooleanSetting top = this.registerBoolean("Top+", false);
   BooleanSetting rotate = this.registerBoolean("Rotate", false);
   BooleanSetting packet = this.registerBoolean("Packet Place", false);
   BooleanSetting swing = this.registerBoolean("Swing", false);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", false);
   BooleanSetting check = this.registerBoolean("Switch Check", true);
   BooleanSetting detect = this.registerBoolean("Detect Break", false);
   BooleanSetting self = this.registerBoolean("Self Break", false, () -> {
      return (Boolean)this.detect.getValue();
   });
   BooleanSetting bed = this.registerBoolean("Bedrock", false, () -> {
      return (Boolean)this.detect.getValue();
   });
   BooleanSetting pause = this.registerBoolean("BedrockHole", true);
   int ob;
   int waited;
   int placed;
   BlockPos trapPos;
   BlockPos player;
   List<BlockPos> posList = new ArrayList();
   BlockPos[] sides = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1)};
   BlockPos[] blocks = new BlockPos[]{new BlockPos(0, 1, 0), new BlockPos(0, 2, 0)};
   BlockPos breakPos;
   private int obsidian = -1;
   private int place;
   @EventHandler
   private final Listener<PacketEvent.PostSend> listener = new Listener((event) -> {
      if (this.player != null && (Boolean)this.self.getValue()) {
         if (event.getPacket() instanceof CPacketPlayerDigging) {
            CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
            if (packet.func_180762_c() == Action.START_DESTROY_BLOCK) {
               BlockPos ab = packet.func_179715_a();
               this.breakPos = packet.func_179715_a();
               if (ab.equals(this.player.func_177982_a(0, 1, 0))) {
                  this.place = 17;
               }

               if (ab.equals(this.player.func_177982_a(1, 1, 0))) {
                  this.place = 18;
               }

               if (ab.equals(this.player.func_177982_a(-1, 1, 0))) {
                  this.place = 19;
               }

               if (ab.equals(this.player.func_177982_a(0, 1, 1))) {
                  this.place = 20;
               }

               if (ab.equals(this.player.func_177982_a(0, 1, -1))) {
                  this.place = 21;
               }

               if (ab.equals(this.player.func_177982_a(0, 2, 0))) {
                  this.place = 22;
               }

               if (ab.equals(this.player.func_177982_a(1, 0, 0))) {
                  this.place = 1;
               }

               if (ab.equals(this.player.func_177982_a(-1, 0, 0))) {
                  this.place = 2;
               }

               if (ab.equals(this.player.func_177982_a(0, 0, 1))) {
                  this.place = 3;
               }

               if (ab.equals(this.player.func_177982_a(0, 0, -1))) {
                  this.place = 4;
               }

               if (ab.equals(this.player.func_177982_a(2, 0, 0))) {
                  this.place = 5;
               }

               if (ab.equals(this.player.func_177982_a(-2, 0, 0))) {
                  this.place = 6;
               }

               if (ab.equals(this.player.func_177982_a(0, 0, 2))) {
                  this.place = 7;
               }

               if (ab.equals(this.player.func_177982_a(0, 0, -2))) {
                  this.place = 8;
               }

               if (ab.equals(this.player.func_177982_a(1, 1, 0))) {
                  this.place = 9;
               }

               if (ab.equals(this.player.func_177982_a(-1, 1, 0))) {
                  this.place = 10;
               }

               if (ab.equals(this.player.func_177982_a(0, 1, 1))) {
                  this.place = 11;
               }

               if (ab.equals(this.player.func_177982_a(0, 1, -1))) {
                  this.place = 12;
               }

               if (ab.equals(this.player.func_177982_a(1, 0, 1))) {
                  this.place = 13;
               }

               if (ab.equals(this.player.func_177982_a(1, 0, -1))) {
                  this.place = 14;
               }

               if (ab.equals(this.player.func_177982_a(-1, 0, 1))) {
                  this.place = 15;
               }

               if (ab.equals(this.player.func_177982_a(-1, 0, -1))) {
                  this.place = 16;
               }
            }
         }

      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && this.player != null) {
         if (event.getPacket() instanceof SPacketBlockBreakAnim) {
            SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim)event.getPacket();
            BlockPos ab = packet.func_179821_b();
            this.breakPos = packet.func_179821_b();
            if (ab.equals(this.player.func_177982_a(0, 1, 0))) {
               this.place = 17;
            }

            if (ab.equals(this.player.func_177982_a(1, 0, 0))) {
               this.place = 1;
            }

            if (ab.equals(this.player.func_177982_a(-1, 0, 0))) {
               this.place = 2;
            }

            if (ab.equals(this.player.func_177982_a(0, 0, 1))) {
               this.place = 3;
            }

            if (ab.equals(this.player.func_177982_a(0, 0, -1))) {
               this.place = 4;
            }

            if (ab.equals(this.player.func_177982_a(2, 0, 0))) {
               this.place = 5;
            }

            if (ab.equals(this.player.func_177982_a(-2, 0, 0))) {
               this.place = 6;
            }

            if (ab.equals(this.player.func_177982_a(0, 0, 2))) {
               this.place = 7;
            }

            if (ab.equals(this.player.func_177982_a(0, 0, -2))) {
               this.place = 8;
            }

            if (ab.equals(this.player.func_177982_a(1, 1, 0))) {
               this.place = 9;
            }

            if (ab.equals(this.player.func_177982_a(-1, 1, 0))) {
               this.place = 10;
            }

            if (ab.equals(this.player.func_177982_a(0, 1, 1))) {
               this.place = 11;
            }

            if (ab.equals(this.player.func_177982_a(0, 1, -1))) {
               this.place = 12;
            }

            if (ab.equals(this.player.func_177982_a(1, 0, 1))) {
               this.place = 13;
            }

            if (ab.equals(this.player.func_177982_a(1, 0, -1))) {
               this.place = 14;
            }

            if (ab.equals(this.player.func_177982_a(-1, 0, 1))) {
               this.place = 15;
            }

            if (ab.equals(this.player.func_177982_a(-1, 0, -1))) {
               this.place = 16;
            }
         }

      }
   }, new Predicate[0]);

   public static boolean isPlayerInHole(EntityPlayer target) {
      BlockPos blockPos = getLocalPlayerPosFloored(target);
      HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(blockPos, true, true, false);
      HoleUtil.HoleType holeType = holeInfo.getType();
      return holeType == HoleUtil.HoleType.SINGLE;
   }

   private void switchTo(int slot) {
      if (slot > -1 && slot < 9 && (!(Boolean)this.check.getValue() || mc.field_71439_g.field_71071_by.field_70461_c != slot)) {
         if ((Boolean)this.packetSwitch.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = slot;
            mc.field_71442_b.func_78765_e();
         }
      }

   }

   public static BlockPos getLocalPlayerPosFloored(EntityPlayer target) {
      return new BlockPos(target.func_174791_d());
   }

   public void onUpdate() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         this.placed = 0;
         if ((Integer)this.delay.getValue() > 0) {
            if (this.waited++ < (Integer)this.delay.getValue()) {
               return;
            }

            this.waited = 0;
         }

         if (BurrowUtil.findHotbarBlock(BlockObsidian.class) != -1) {
            EntityPlayer target = PlayerUtil.getNearestPlayer((double)(Integer)this.range.getValue());
            if (target != null) {
               if (!(mc.field_71439_g.func_70032_d(target) > (float)(Integer)this.range.getValue()) && isPlayerInHole(target)) {
                  BlockPos pos = EntityUtil.getEntityPos(target);
                  this.addBlock(pos);
               } else {
                  this.posList.clear();
               }

               Iterator var5 = this.posList.iterator();

               while(var5.hasNext()) {
                  BlockPos block = (BlockPos)var5.next();
                  if (this.placed > (Integer)this.bpt.getValue()) {
                     return;
                  }

                  this.ob = BurrowUtil.findHotbarBlock(BlockObsidian.class);
                  if (this.ob == -1) {
                     return;
                  }

                  if (mc.field_71441_e.func_175623_d(block) && !this.intersectsWithEntity(block)) {
                     int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
                     this.switchTo(this.ob);
                     BurrowUtil.placeBlock(block, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                     this.switchTo(oldslot);
                     ++this.placed;
                  }
               }

               this.player = EntityUtil.getEntityPos(target).func_177984_a();
               this.antiCity(this.player);
            }
         }
      } else {
         this.trapPos = null;
         this.posList.clear();
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

   private void addBlock(BlockPos pos) {
      if (BurrowUtil.findHotbarBlock(BlockObsidian.class) != -1) {
         List<BlockPos> blocklist = new ArrayList();
         blocklist.add(pos.func_177982_a(0, 2, 0));
         if ((Boolean)this.top.getValue()) {
            blocklist.add(pos.func_177982_a(0, 3, 0));
         }

         int obby = 0;
         BlockPos[] var4 = this.sides;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            BlockPos side = var4[var6];
            if (mc.field_71441_e.func_180495_p(pos.func_177971_a(side)).func_177230_c() != Blocks.field_150357_h || (Boolean)this.bed.getValue()) {
               BlockPos[] var8 = this.blocks;
               int var9 = var8.length;

               for(int var10 = 0; var10 < var9; ++var10) {
                  BlockPos blockPos = var8[var10];
                  blocklist.add(pos.func_177971_a(side).func_177971_a(blockPos));
               }

               ++obby;
            }
         }

         if (obby != 0 || (Boolean)this.pause.getValue()) {
            this.posList.addAll(blocklist);
         }
      }
   }

   private boolean noHard(Block block) {
      return block != Blocks.field_150357_h || (Boolean)this.bed.getValue();
   }

   public void antiCity(BlockPos pos) {
      this.obsidian = BurrowUtil.findHotbarBlock(BlockObsidian.class);
      if (this.obsidian != -1) {
         if (pos != null) {
            pos = new BlockPos((double)pos.field_177962_a, (double)pos.field_177960_b + 0.2D, (double)pos.field_177961_c);
            if (this.breakPos != null) {
               if ((this.breakPos.equals(pos.func_177982_a(1, 0, 0)) || this.breakPos.equals(pos.func_177982_a(1, 1, 0))) && this.isAir(pos.func_177982_a(1, 0, 0)) && this.isAir(pos.func_177982_a(1, 1, 0))) {
                  if (this.breakPos.equals(pos.func_177982_a(1, 0, 0))) {
                     this.perform(pos.func_177982_a(1, 1, 0));
                  } else {
                     this.perform(pos.func_177982_a(1, 0, 0));
                  }
               }

               if ((this.breakPos.equals(pos.func_177982_a(-1, 0, 0)) || this.breakPos.equals(pos.func_177982_a(-1, 1, 0))) && this.isAir(pos.func_177982_a(-1, 0, 0)) && this.isAir(pos.func_177982_a(-1, 1, 0))) {
                  if (this.breakPos.equals(pos.func_177982_a(-1, 0, 0))) {
                     this.perform(pos.func_177982_a(-1, 1, 0));
                  } else {
                     this.perform(pos.func_177982_a(-1, 0, 0));
                  }
               }

               if ((this.breakPos.equals(pos.func_177982_a(0, 0, 1)) || this.breakPos.equals(pos.func_177982_a(0, 1, 1))) && this.isAir(pos.func_177982_a(0, 0, 1)) && this.isAir(pos.func_177982_a(0, 1, 1))) {
                  if (this.breakPos.equals(pos.func_177982_a(0, 0, 1))) {
                     this.perform(pos.func_177982_a(0, 1, 1));
                  } else {
                     this.perform(pos.func_177982_a(0, 0, 1));
                  }
               }

               if ((this.breakPos.equals(pos.func_177982_a(0, 0, -1)) || this.breakPos.equals(pos.func_177982_a(0, 1, -1))) && this.isAir(pos.func_177982_a(0, 0, -1)) && this.isAir(pos.func_177982_a(0, 1, -1))) {
                  if (this.breakPos.equals(pos.func_177982_a(0, 0, -1))) {
                     this.perform(pos.func_177982_a(0, 1, -1));
                  } else {
                     this.perform(pos.func_177982_a(0, 0, -1));
                  }
               }
            }

            if (this.noHard(this.getBlock(pos.func_177982_a(1, 0, 0)).func_177230_c())) {
               if (this.place == 1) {
                  this.perform(pos.func_177982_a(2, 0, 0));
                  this.perform(pos.func_177982_a(1, 0, 1));
                  this.perform(pos.func_177982_a(1, 0, -1));
                  this.perform(pos.func_177982_a(1, 1, 0));
               }

               if (this.place == 5 || this.place == 9 || this.place == 13 || this.place == 14) {
                  this.perform(pos.func_177982_a(1, 0, 0));
               }
            }

            if (this.noHard(this.getBlock(pos.func_177982_a(-1, 0, 0)).func_177230_c())) {
               if (this.place == 2) {
                  this.perform(pos.func_177982_a(-2, 0, 0));
                  this.perform(pos.func_177982_a(-1, 0, 1));
                  this.perform(pos.func_177982_a(-1, 0, -1));
                  this.perform(pos.func_177982_a(-1, 1, 0));
               }

               if (this.place == 6 || this.place == 10 || this.place == 15 || this.place == 16) {
                  this.perform(pos.func_177982_a(-1, 0, 0));
               }
            }

            if (this.noHard(this.getBlock(pos.func_177982_a(0, 0, 1)).func_177230_c())) {
               if (this.place == 3) {
                  this.perform(pos.func_177982_a(0, 0, 2));
                  this.perform(pos.func_177982_a(1, 0, 1));
                  this.perform(pos.func_177982_a(-1, 0, 1));
                  this.perform(pos.func_177982_a(0, 1, 1));
               }

               if (this.place == 7 || this.place == 11 || this.place == 13 || this.place == 15) {
                  this.perform(pos.func_177982_a(0, 0, 1));
               }
            }

            if (this.noHard(this.getBlock(pos.func_177982_a(0, 0, -1)).func_177230_c())) {
               if (this.place == 4) {
                  this.perform(pos.func_177982_a(0, 0, -2));
                  this.perform(pos.func_177982_a(1, 0, -1));
                  this.perform(pos.func_177982_a(-1, 0, -1));
                  this.perform(pos.func_177982_a(0, 1, -1));
               }

               if (this.place == 8 || this.place == 12 || this.place == 14 || this.place == 16) {
                  this.perform(pos.func_177982_a(0, 0, -1));
               }
            }

            if (this.noHard(this.getBlock(pos.func_177982_a(0, 1, 0)).func_177230_c())) {
               if (this.place == 17) {
                  this.perform(pos.func_177982_a(0, 2, 0));
                  this.perform(pos.func_177982_a(0, 1, -1));
                  this.perform(pos.func_177982_a(0, 1, 1));
                  this.perform(pos.func_177982_a(1, 1, 0));
                  this.perform(pos.func_177982_a(-1, 1, 0));
               }

               if (this.place == 9 || this.place == 10 || this.place == 11 || this.place == 12 || this.place > 17) {
                  this.perform(pos.func_177982_a(0, 1, 0));
               }
            }

            this.place = 0;
         }
      }
   }

   private IBlockState getBlock(BlockPos block) {
      return block == null ? null : mc.field_71441_e.func_180495_p(block);
   }

   private void perform(BlockPos pos) {
      if (this.placed < (Integer)this.bpt.getValue()) {
         if (!PlayerCheck(pos) && this.CanPlace(pos)) {
            int old = mc.field_71439_g.field_71071_by.field_70461_c;
            this.switchTo(this.obsidian);
            BurrowUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
            this.switchTo(old);
            ++this.placed;
         }
      }
   }

   public boolean CanPlace(BlockPos block) {
      EnumFacing[] var2 = EnumFacing.field_82609_l;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumFacing face = var2[var4];
         if (isReplaceable(block) && !BlockUtil.airBlocks.contains(this.getBlock(block.func_177972_a(face))) && mc.field_71439_g.func_174818_b(block) <= MathUtil.square(5.0D)) {
            return true;
         }
      }

      return false;
   }

   public static boolean isReplaceable(BlockPos pos) {
      return BlockUtil.getState(pos).func_185904_a().func_76222_j();
   }

   private boolean isAir(BlockPos block) {
      return mc.field_71441_e.func_180495_p(block).func_177230_c() == Blocks.field_150350_a;
   }

   public static boolean PlayerCheck(BlockPos pos) {
      Iterator var1 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(pos)).iterator();

      Entity entity;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         entity = (Entity)var1.next();
      } while(!(entity instanceof EntityPlayer));

      return true;
   }
}
