package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.misc.Wrapper;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.combat.CrystalUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;

@Module.Declaration(
   name = "CevBreaker",
   category = Category.Combat
)
public class CevBreaker extends Module {
   public static CevBreaker INSTANCE;
   ModeSetting page = this.registerMode("Page", Arrays.asList("General", "Place"), "General");
   IntegerSetting delay = this.registerInteger("Delay", 50, 0, 1000, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting helpBlock = this.registerBoolean("Help Block", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   DoubleSetting maxRange = this.registerDouble("Max Range", 5.0D, 0.0D, 10.0D, () -> {
      return (Boolean)this.helpBlock.getValue() && ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting down = this.registerBoolean("Down Block", true, () -> {
      return (Boolean)this.helpBlock.getValue() && ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting packet = this.registerBoolean("Packet Place", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting rotate = this.registerBoolean("Rotate", false, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting strictFacing = this.registerBoolean("Strict Facing", false, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting swing = this.registerBoolean("Swing", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting bypassSwitch = this.registerBoolean("Bypass Switch", false, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting instantMine = this.registerBoolean("Instant Mine", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting pickBypass = this.registerBoolean("Pick Bypass", false, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting strict = this.registerBoolean("Strict", false, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting packetCrystal = this.registerBoolean("Packet Crystal", false, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting crystalBypass = this.registerBoolean("Crystal Bypass", false, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   IntegerSetting breakDelay = this.registerInteger("Break Delay", 50, 0, 1000, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   ModeSetting breakCrystal = this.registerMode("Break Crystal", Arrays.asList("Vanilla", "Packet"), "Packet", () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting airCheck = this.registerBoolean("Air Check", true, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   BooleanSetting antiWeakness = this.registerBoolean("AntiWeakness", true, () -> {
      return ((String)this.page.getValue()).equals("Place");
   });
   public boolean working;
   boolean offhand;
   boolean start;
   boolean anyCrystal;
   int blockSlot;
   int crystalSlot;
   int pickSlot;
   long time;
   EnumFacing facing;
   Timing timer = new Timing();
   Timing breakTimer = new Timing();
   BlockPos[] side = new BlockPos[]{new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0)};
   BlockPos placePos;
   int lastSlot;
   @EventHandler
   private final Listener<PacketEvent.PostSend> postSendListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         if (event.getPacket() instanceof CPacketHeldItemChange) {
            int slot = ((CPacketHeldItemChange)event.getPacket()).func_149614_c();
            if (slot != this.lastSlot) {
               this.lastSlot = slot;
               if ((Boolean)this.strict.getValue()) {
                  EnumFacing facing = BlockUtil.getRayTraceFacing(this.placePos, this.facing);
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, this.placePos, facing));
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.placePos, facing));
                  if ((Boolean)this.swing.getValue()) {
                     mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                  }

                  this.time = System.currentTimeMillis() + (long)this.calcBreakTime();
               }
            }
         }

      }
   }, new Predicate[0]);

   public CevBreaker() {
      INSTANCE = this;
   }

   public void onEnable() {
      if (mc.field_71476_x != null && mc.field_71476_x.field_72313_a == Type.BLOCK && mc.field_71441_e.func_180495_p(mc.field_71476_x.func_178782_a()).func_177230_c() != Blocks.field_150357_h) {
         this.placePos = mc.field_71476_x.func_178782_a();
         this.start = this.offhand = false;
         this.getItem();
         this.doBreak();
         this.timer.reset();
      } else {
         this.disable();
      }
   }

   public void fast() {
      this.working = false;
      if (mc.field_71441_e != null && mc.field_71439_g != null && this.placePos != null && !mc.field_71439_g.field_70128_L) {
         if (mc.field_71441_e.func_175623_d(this.placePos.func_177984_a()) && mc.field_71441_e.func_175623_d(this.placePos.func_177984_a().func_177984_a())) {
            this.getItem();
            if (this.anyCrystal && this.blockSlot != -1 && this.pickSlot != -1) {
               if (this.crystalSlot != -1) {
                  if (mc.field_71441_e.func_175623_d(this.placePos.func_177977_b()) && mc.field_71441_e.func_175623_d(this.placePos.func_177978_c()) && mc.field_71441_e.func_175623_d(this.placePos.func_177976_e()) && mc.field_71441_e.func_175623_d(this.placePos.func_177974_f()) && mc.field_71441_e.func_175623_d(this.placePos.func_177968_d())) {
                     this.helpBlock(this.placePos);
                  } else if (!AntiRegear.INSTANCE.working && !AntiBurrow.INSTANCE.mining) {
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

                     this.working = true;
                     if (!this.isPos2(instantPos, this.placePos)) {
                        this.doBreak();
                     }

                     if (!this.start && mc.field_71441_e.func_175623_d(this.placePos)) {
                        this.time = System.currentTimeMillis() + (long)((Boolean)this.instantMine.getValue() ? 0 : this.calcBreakTime());
                        this.start = true;
                     }

                     Entity crystal = this.getCrystal();
                     if (mc.field_71441_e.func_180495_p(this.placePos).func_177230_c() instanceof BlockAir) {
                        this.breakCrystalPiston(crystal);
                        this.breakTimer.reset();
                     }

                     if (this.time <= System.currentTimeMillis()) {
                        if (this.start && this.timer.passedMs((long)(Integer)this.delay.getValue())) {
                           this.run(this.blockSlot, (Boolean)this.bypassSwitch.getValue(), false, () -> {
                              BurrowUtil.placeBlock(this.placePos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                           });
                           this.run(this.crystalSlot, (Boolean)this.crystalBypass.getValue(), true, () -> {
                              this.placeCrystal(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                           });
                           this.run(this.pickSlot, (Boolean)this.pickBypass.getValue(), false, () -> {
                              this.facing = EnumFacing.UP;
                              if ((Boolean)this.strictFacing.getValue()) {
                                 this.facing = BlockUtil.getRayTraceFacing(this.placePos, this.facing);
                              }

                              mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.placePos, this.facing));
                              if (!(Boolean)this.instantMine.getValue()) {
                                 mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, this.placePos, this.facing));
                                 mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.placePos, this.facing));
                                 this.time = System.currentTimeMillis() + (long)this.calcBreakTime();
                              }

                              if ((Boolean)this.swing.getValue()) {
                                 mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                              }

                           });
                           if (!(Boolean)this.airCheck.getValue() || BlockUtil.isAir(this.placePos)) {
                              this.breakCrystalPiston(this.getCrystal());
                           }

                           this.timer.reset();
                        }

                     }
                  }
               }
            } else {
               this.disable();
            }
         } else {
            this.disable();
         }
      } else {
         this.disable();
      }
   }

   private void helpBlock(BlockPos pos) {
      List<BlockPos> blocks = NonNullList.func_191196_a();
      BlockPos[] var3 = this.side;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         BlockPos side = var3[var5];
         blocks.add(pos.func_177971_a(side));
      }

      if ((Boolean)this.down.getValue()) {
         blocks.add(pos.func_177977_b());
      }

      BlockPos finalPos = (BlockPos)blocks.stream().filter((p) -> {
         return mc.field_71439_g.func_174818_b(p) <= (Double)this.maxRange.getValue() * (Double)this.maxRange.getValue();
      }).max(Comparator.comparing((p) -> {
         return mc.field_71439_g.func_174818_b(p);
      })).orElse((Object)null);
      this.run(this.blockSlot, (Boolean)this.bypassSwitch.getValue(), false, () -> {
         BurrowUtil.placeBlock(finalPos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
      });
   }

   private void getItem() {
      this.blockSlot = this.crystalSlot = this.pickSlot = -1;
      this.anyCrystal = false;
      if (mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemEndCrystal) {
         this.crystalSlot = 11;
         this.offhand = true;
      }

      for(int i = 0; i < 36; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemEndCrystal) {
            this.anyCrystal = true;
            if ((Boolean)this.crystalBypass.getValue() || i < 9) {
               this.crystalSlot = i;
            }
            break;
         }
      }

      this.blockSlot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
      this.pickSlot = this.findItem();
   }

   private void breakCrystalPiston(Entity crystal) {
      if (crystal != null) {
         if (this.breakTimer.passedMs((long)(Integer)this.breakDelay.getValue())) {
            this.breakTimer.reset();
            int newSlot = -1;
            if ((Boolean)this.antiWeakness.getValue() && mc.field_71439_g.func_70644_a(MobEffects.field_76437_t)) {
               for(int i = 0; i < 9; ++i) {
                  ItemStack stack = Wrapper.getPlayer().field_71071_by.func_70301_a(i);
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
            }

            this.run(newSlot, (Boolean)this.pickBypass.getValue(), false, () -> {
               if (((String)this.breakCrystal.getValue()).equalsIgnoreCase("Vanilla")) {
                  CrystalUtil.breakCrystal(crystal, (Boolean)this.swing.getValue());
               } else if (((String)this.breakCrystal.getValue()).equalsIgnoreCase("Packet")) {
                  CrystalUtil.breakCrystalPacket(crystal, (Boolean)this.swing.getValue());
               }

            });
         }
      }
   }

   private Entity getCrystal() {
      Iterator var1 = mc.field_71441_e.field_72996_f.iterator();

      Entity t;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         t = (Entity)var1.next();
      } while(!(t instanceof EntityEnderCrystal) || !(t.func_70011_f((double)this.placePos.field_177962_a + 0.5D, (double)this.placePos.field_177960_b + 1.5D, (double)this.placePos.field_177961_c + 0.5D) < 3.0D));

      return t;
   }

   private void doBreak() {
      if (this.placePos != null && !mc.field_71441_e.func_175623_d(this.placePos) && mc.field_71441_e.func_180495_p(this.placePos).func_177230_c() != Blocks.field_150357_h) {
         if ((Boolean)this.swing.getValue()) {
            mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
         }

         mc.field_71442_b.func_180512_c(this.placePos, BlockUtil.getRayTraceFacing(this.placePos, EnumFacing.UP));
      }
   }

   private boolean isPos2(BlockPos pos1, BlockPos pos2) {
      if (pos1 != null && pos2 != null) {
         return pos1.field_177962_a == pos2.field_177962_a && pos1.field_177960_b == pos2.field_177960_b && pos1.field_177961_c == pos2.field_177961_c;
      } else {
         return false;
      }
   }

   private void placeCrystal(EnumHand hand) {
      if ((Boolean)this.packetCrystal.getValue()) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(this.placePos, EnumFacing.UP, hand, 0.0F, 0.0F, 0.0F));
      } else {
         mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, this.placePos, EnumFacing.UP, (new Vec3d(this.placePos)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e(new Vec3d(EnumFacing.UP.func_176730_m())), hand);
      }

      if ((Boolean)this.swing.getValue()) {
         mc.field_71439_g.func_184609_a(hand);
      }

   }

   private void run(int slot, boolean bypass, boolean update, Runnable runnable) {
      int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
      if (slot >= 0 && slot != oldslot) {
         if (!bypass && slot <= 8) {
            if ((Boolean)this.packetSwitch.getValue()) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
            } else {
               mc.field_71439_g.field_71071_by.field_70461_c = slot;
            }

            runnable.run();
            if ((Boolean)this.packetSwitch.getValue()) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(oldslot));
            } else {
               mc.field_71439_g.field_71071_by.field_70461_c = oldslot;
            }
         } else {
            ItemStack itemStack = mc.field_71439_g.field_71071_by.func_70301_a(slot);
            if (slot < 9) {
               slot += 36;
            }

            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketClickWindow(0, slot, mc.field_71439_g.field_71071_by.field_70461_c, ClickType.SWAP, ItemStack.field_190927_a, mc.field_71439_g.field_71069_bz.func_75136_a(mc.field_71439_g.field_71071_by)));
            runnable.run();
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketClickWindow(0, slot, mc.field_71439_g.field_71071_by.field_70461_c, ClickType.SWAP, update ? itemStack : ItemStack.field_190927_a, mc.field_71439_g.field_71069_bz.func_75136_a(mc.field_71439_g.field_71071_by)));
         }

      } else {
         runnable.run();
      }
   }

   private int calcBreakTime() {
      return this.getBreakTime() * 70;
   }

   private int getBreakTime() {
      float hardness = 50.0F;
      float breakSpeed = this.getSpeed(Blocks.field_150343_Z.func_176194_O().func_177621_b());
      if (breakSpeed < 0.0F) {
         return -1;
      } else {
         float relativeDamage = this.getSpeed(Blocks.field_150343_Z.func_176194_O().func_177621_b()) / hardness / 30.0F;
         return (int)Math.ceil((double)(0.7F / relativeDamage));
      }
   }

   private int findItem() {
      int result = mc.field_71439_g.field_71071_by.field_70461_c;
      double speed = this.getSpeed(Blocks.field_150343_Z.func_176194_O().func_177621_b(), mc.field_71439_g.func_184614_ca());

      for(int i = 0; i < ((Boolean)this.pickBypass.getValue() ? 36 : 9); ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         double stackSpeed = this.getSpeed(Blocks.field_150343_Z.func_176194_O().func_177621_b(), stack);
         if (stackSpeed > speed) {
            speed = stackSpeed;
            result = i;
         }
      }

      return result;
   }

   private double getSpeed(IBlockState state, ItemStack stack) {
      double str = (double)stack.func_150997_a(state);
      int effect = EnchantmentHelper.func_77506_a(Enchantments.field_185305_q, stack);
      return Math.max(str + (str > 1.0D ? (double)(effect * effect) + 1.0D : 0.0D), 0.0D);
   }

   private float getSpeed(IBlockState blockState) {
      ItemStack itemStack = mc.field_71439_g.field_71071_by.func_70301_a(this.pickSlot);
      float digSpeed = mc.field_71439_g.field_71071_by.func_70301_a(this.pickSlot).func_150997_a(blockState);
      int efficiencyModifier;
      if (!itemStack.func_190926_b() && (double)digSpeed > 1.0D && (efficiencyModifier = EnchantmentHelper.func_77506_a(Enchantments.field_185305_q, itemStack)) > 0) {
         digSpeed += (float)(StrictMath.pow((double)efficiencyModifier, 2.0D) + 1.0D);
      }

      if (mc.field_71439_g.func_70644_a(MobEffects.field_76422_e)) {
         digSpeed *= 1.0F + (float)(mc.field_71439_g.func_70660_b(MobEffects.field_76422_e).func_76458_c() + 1) * 0.2F;
      }

      if (mc.field_71439_g.func_70644_a(MobEffects.field_76419_f)) {
         float fatigueScale;
         switch(mc.field_71439_g.func_70660_b(MobEffects.field_76419_f).func_76458_c()) {
         case 0:
            fatigueScale = 0.3F;
            break;
         case 1:
            fatigueScale = 0.09F;
            break;
         case 2:
            fatigueScale = 0.0027F;
            break;
         default:
            fatigueScale = 8.1E-4F;
         }

         digSpeed *= fatigueScale;
      }

      if (mc.field_71439_g.func_70055_a(Material.field_151586_h) && !EnchantmentHelper.func_185287_i(mc.field_71439_g)) {
         digSpeed /= 5.0F;
      }

      return digSpeed;
   }
}
