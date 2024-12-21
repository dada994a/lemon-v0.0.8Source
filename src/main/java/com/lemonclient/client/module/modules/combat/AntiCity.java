package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.MathUtil;
import com.lemonclient.api.util.world.combat.CrystalUtil;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "AntiCity",
   category = Category.Combat
)
public class AntiCity extends Module {
   ModeSetting time = this.registerMode("Time Mode", Arrays.asList("Tick", "onUpdate", "Fast"), "Tick");
   IntegerSetting bpt = this.registerInteger("Blocks Per Tick", 4, 0, 20);
   BooleanSetting self = this.registerBoolean("Self", false);
   BooleanSetting smart = this.registerBoolean("Smart", false);
   BooleanSetting breakCrystal = this.registerBoolean("Break Crystal", true);
   BooleanSetting rotate = this.registerBoolean("Rotate", true);
   BooleanSetting packet = this.registerBoolean("Packet", true);
   BooleanSetting swing = this.registerBoolean("Swing", true);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true);
   BooleanSetting check = this.registerBoolean("Switch Check", true);
   BooleanSetting packetBreak = this.registerBoolean("Packet Break", false);
   BooleanSetting antiWeakness = this.registerBoolean("Anti Weakness", true);
   BooleanSetting silentSwitch = this.registerBoolean("Silent Switch", true);
   BlockPos breakPos;
   private int obsidian = -1;
   private int placeID;
   @EventHandler
   private final Listener<PacketEvent.PostSend> sendListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         if ((Boolean)this.self.getValue()) {
            if (event.getPacket() instanceof CPacketPlayerDigging && ((CPacketPlayerDigging)event.getPacket()).func_180762_c() == Action.START_DESTROY_BLOCK) {
               CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
               BlockPos ab = packet.func_179715_a();
               this.breakPos = packet.func_179715_a();
               BlockPos player = EntityUtil.getPlayerPos(mc.field_71439_g);
               if (ab.equals(player.func_177982_a(1, 0, 0))) {
                  this.placeID = 1;
               }

               if (ab.equals(player.func_177982_a(-1, 0, 0))) {
                  this.placeID = 2;
               }

               if (ab.equals(player.func_177982_a(0, 0, 1))) {
                  this.placeID = 3;
               }

               if (ab.equals(player.func_177982_a(0, 0, -1))) {
                  this.placeID = 4;
               }

               if (ab.equals(player.func_177982_a(2, 0, 0))) {
                  this.placeID = 5;
               }

               if (ab.equals(player.func_177982_a(-2, 0, 0))) {
                  this.placeID = 6;
               }

               if (ab.equals(player.func_177982_a(0, 0, 2))) {
                  this.placeID = 7;
               }

               if (ab.equals(player.func_177982_a(0, 0, -2))) {
                  this.placeID = 8;
               }

               if (ab.equals(player.func_177982_a(1, 1, 0))) {
                  this.placeID = 9;
               }

               if (ab.equals(player.func_177982_a(-1, 1, 0))) {
                  this.placeID = 10;
               }

               if (ab.equals(player.func_177982_a(0, 1, 1))) {
                  this.placeID = 11;
               }

               if (ab.equals(player.func_177982_a(0, 1, -1))) {
                  this.placeID = 12;
               }

               if (ab.equals(player.func_177982_a(1, 0, 1))) {
                  this.placeID = 13;
               }

               if (ab.equals(player.func_177982_a(1, 0, -1))) {
                  this.placeID = 14;
               }

               if (ab.equals(player.func_177982_a(-1, 0, 1))) {
                  this.placeID = 15;
               }

               if (ab.equals(player.func_177982_a(-1, 0, -1))) {
                  this.placeID = 16;
               }
            }

         }
      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         if (event.getPacket() instanceof SPacketBlockBreakAnim) {
            SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim)event.getPacket();
            BlockPos ab = packet.func_179821_b();
            this.breakPos = packet.func_179821_b();
            BlockPos player = EntityUtil.getPlayerPos(mc.field_71439_g);
            if (ab.equals(player.func_177982_a(1, 0, 0))) {
               this.placeID = 1;
            }

            if (ab.equals(player.func_177982_a(-1, 0, 0))) {
               this.placeID = 2;
            }

            if (ab.equals(player.func_177982_a(0, 0, 1))) {
               this.placeID = 3;
            }

            if (ab.equals(player.func_177982_a(0, 0, -1))) {
               this.placeID = 4;
            }

            if (ab.equals(player.func_177982_a(2, 0, 0))) {
               this.placeID = 5;
            }

            if (ab.equals(player.func_177982_a(-2, 0, 0))) {
               this.placeID = 6;
            }

            if (ab.equals(player.func_177982_a(0, 0, 2))) {
               this.placeID = 7;
            }

            if (ab.equals(player.func_177982_a(0, 0, -2))) {
               this.placeID = 8;
            }

            if (ab.equals(player.func_177982_a(1, 1, 0))) {
               this.placeID = 9;
            }

            if (ab.equals(player.func_177982_a(-1, 1, 0))) {
               this.placeID = 10;
            }

            if (ab.equals(player.func_177982_a(0, 1, 1))) {
               this.placeID = 11;
            }

            if (ab.equals(player.func_177982_a(0, 1, -1))) {
               this.placeID = 12;
            }

            if (ab.equals(player.func_177982_a(1, 0, 1))) {
               this.placeID = 13;
            }

            if (ab.equals(player.func_177982_a(1, 0, -1))) {
               this.placeID = 14;
            }

            if (ab.equals(player.func_177982_a(-1, 0, 1))) {
               this.placeID = 15;
            }

            if (ab.equals(player.func_177982_a(-1, 0, -1))) {
               this.placeID = 16;
            }
         }

      }
   }, new Predicate[0]);
   int placed;

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

   public static boolean noHard(Block block) {
      return block != Blocks.field_150357_h;
   }

   public void onUpdate() {
      if (((String)this.time.getValue()).equals("onUpdate")) {
         this.antiCity();
      }

      this.placed = 0;
   }

   public void onTick() {
      if (((String)this.time.getValue()).equals("Tick")) {
         this.antiCity();
      }

   }

   public void fast() {
      if (((String)this.time.getValue()).equals("Fast")) {
         this.antiCity();
      }

   }

   public void antiCity() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (!(LemonClient.speedUtil.getPlayerSpeed(mc.field_71439_g) >= 15.0D)) {
            this.obsidian = BurrowUtil.findHotbarBlock(BlockObsidian.class);
            if (this.obsidian != -1) {
               BlockPos pos = EntityUtil.getPlayerPos(mc.field_71439_g);
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

                  if (noHard(this.getBlock(pos.func_177982_a(1, 0, 0)).func_177230_c())) {
                     if (this.placeID == 1) {
                        this.perform(pos.func_177982_a(2, 0, 0));
                        this.perform(pos.func_177982_a(1, 0, 1));
                        this.perform(pos.func_177982_a(1, 0, -1));
                        this.perform(pos.func_177982_a(1, 1, 0));
                        if (EntityCheck(pos.func_177982_a(2, 0, 0))) {
                           this.perform(pos.func_177982_a(3, 0, 0));
                           this.perform(pos.func_177982_a(3, 1, 0));
                        }
                     }

                     if (this.placeID == 5) {
                        this.perform(pos.func_177982_a(1, 0, 0));
                        this.perform(pos.func_177982_a(2, 1, 0));
                        this.perform(pos.func_177982_a(3, 0, 0));
                     }

                     if (this.placeID == 9) {
                        this.perform(pos.func_177982_a(1, 0, 0));
                        this.perform(pos.func_177982_a(2, 1, 0));
                     }

                     if (this.placeID == 13 || this.placeID == 14) {
                        this.perform(pos.func_177982_a(1, 0, 0));
                     }
                  }

                  if (noHard(this.getBlock(pos.func_177982_a(-1, 0, 0)).func_177230_c())) {
                     if (this.placeID == 2) {
                        this.perform(pos.func_177982_a(-2, 0, 0));
                        this.perform(pos.func_177982_a(-1, 0, 1));
                        this.perform(pos.func_177982_a(-1, 0, -1));
                        this.perform(pos.func_177982_a(-1, 1, 0));
                        if (EntityCheck(pos.func_177982_a(-2, 0, 0))) {
                           this.perform(pos.func_177982_a(-3, 0, 0));
                           this.perform(pos.func_177982_a(-3, 1, 0));
                        }
                     }

                     if (this.placeID == 6) {
                        this.perform(pos.func_177982_a(-1, 0, 0));
                        this.perform(pos.func_177982_a(-2, 1, 0));
                        this.perform(pos.func_177982_a(-3, 0, 0));
                     }

                     if (this.placeID == 10) {
                        this.perform(pos.func_177982_a(-1, 0, 0));
                        this.perform(pos.func_177982_a(-2, 1, 0));
                     }

                     if (this.placeID == 15 || this.placeID == 16) {
                        this.perform(pos.func_177982_a(-1, 0, 0));
                     }
                  }

                  if (noHard(this.getBlock(pos.func_177982_a(0, 0, 1)).func_177230_c())) {
                     if (this.placeID == 3) {
                        this.perform(pos.func_177982_a(0, 0, 2));
                        this.perform(pos.func_177982_a(1, 0, 1));
                        this.perform(pos.func_177982_a(-1, 0, 1));
                        this.perform(pos.func_177982_a(0, 1, 1));
                        if (EntityCheck(pos.func_177982_a(0, 0, 2))) {
                           this.perform(pos.func_177982_a(0, 0, 3));
                           this.perform(pos.func_177982_a(0, 1, 3));
                        }
                     }

                     if (this.placeID == 7) {
                        this.perform(pos.func_177982_a(0, 0, 1));
                        this.perform(pos.func_177982_a(0, 1, 2));
                        this.perform(pos.func_177982_a(0, 0, 3));
                     }

                     if (this.placeID == 11) {
                        this.perform(pos.func_177982_a(0, 0, 1));
                        this.perform(pos.func_177982_a(0, 1, 2));
                     }

                     if (this.placeID == 13 || this.placeID == 15) {
                        this.perform(pos.func_177982_a(0, 0, 1));
                     }
                  }

                  if (noHard(this.getBlock(pos.func_177982_a(0, 0, -1)).func_177230_c())) {
                     if (this.placeID == 4) {
                        this.perform(pos.func_177982_a(0, 0, -2));
                        this.perform(pos.func_177982_a(1, 0, -1));
                        this.perform(pos.func_177982_a(-1, 0, -1));
                        this.perform(pos.func_177982_a(0, 1, -1));
                        if (EntityCheck(pos.func_177982_a(0, 0, -2))) {
                           this.perform(pos.func_177982_a(0, 0, -3));
                           this.perform(pos.func_177982_a(0, 1, -3));
                        }
                     }

                     if (this.placeID == 8) {
                        this.perform(pos.func_177982_a(0, 0, -1));
                        this.perform(pos.func_177982_a(0, 1, -2));
                        this.perform(pos.func_177982_a(0, 0, -3));
                     }

                     if (this.placeID == 12) {
                        this.perform(pos.func_177982_a(0, 0, -1));
                        this.perform(pos.func_177982_a(0, 1, -2));
                     }

                     if (this.placeID == 14 || this.placeID == 16) {
                        this.perform(pos.func_177982_a(0, 0, -1));
                     }
                  }

                  this.placeID = 0;
               }
            }
         }
      }
   }

   public static boolean EntityCheck(BlockPos pos) {
      Iterator var1 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(pos)).iterator();

      Entity entity;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         entity = (Entity)var1.next();
      } while(entity instanceof EntityItem || entity instanceof EntityXPOrb || entity == null);

      return true;
   }

   private IBlockState getBlock(BlockPos block) {
      return block == null ? null : mc.field_71441_e.func_180495_p(block);
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

         if ((Boolean)this.silentSwitch.getValue()) {
            this.switchTo(oldSlot);
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

   private void perform(BlockPos pos) {
      if (this.placed < (Integer)this.bpt.getValue()) {
         BlockPos instantPos = null;
         if (ModuleManager.isModuleEnabled(PacketMine.class)) {
            instantPos = PacketMine.INSTANCE.packetPos;
         }

         if (!PlayerCheck(pos) && this.CanPlace(pos) && (!(Boolean)this.smart.getValue() || !this.isPos2(pos, instantPos))) {
            if ((Boolean)this.breakCrystal.getValue()) {
               Iterator var3 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(pos)).iterator();

               while(var3.hasNext()) {
                  Entity entity = (Entity)var3.next();
                  if (entity instanceof EntityEnderCrystal) {
                     this.breakCrystal(entity);
                  }
               }
            }

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
