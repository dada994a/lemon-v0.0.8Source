package com.lemonclient.client.module.modules.dev;

import com.lemonclient.api.event.Phase;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerPacket;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.combat.AntiBurrow;
import com.lemonclient.client.module.modules.combat.AntiRegear;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.lemonclient.mixin.mixins.accessor.AccessorCPacketVehicleMove;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.RayTraceResult.Type;

@Module.Declaration(
   name = "BedCev",
   category = Category.Dev
)
public class BedCevBreaker extends Module {
   public static BedCevBreaker INSTANCE;
   IntegerSetting delay = this.registerInteger("Delay", 50, 0, 1000);
   BooleanSetting helpBlock = this.registerBoolean("Help Block", true);
   DoubleSetting maxRange = this.registerDouble("Max Range", 5.0D, 0.0D, 10.0D, () -> {
      return (Boolean)this.helpBlock.getValue();
   });
   BooleanSetting down = this.registerBoolean("Down Block", true, () -> {
      return (Boolean)this.helpBlock.getValue();
   });
   BooleanSetting packet = this.registerBoolean("Packet Place", true);
   BooleanSetting rotate = this.registerBoolean("Rotate", false);
   BooleanSetting swing = this.registerBoolean("Swing", true);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true);
   BooleanSetting instantMine = this.registerBoolean("Instant Mine", true);
   BooleanSetting pickBypass = this.registerBoolean("Pick Bypass", false);
   BooleanSetting strict = this.registerBoolean("Strict", false);
   public boolean working;
   boolean offhand;
   boolean start;
   boolean anyBed;
   int blockSlot;
   int bedSlot;
   int pickSlot;
   long time;
   EnumFacing facing;
   Vec2f rotation;
   Timing timer = new Timing();
   BlockPos[] side = new BlockPos[]{new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0)};
   @EventHandler
   private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener((event) -> {
      if (this.rotation != null && event.getPhase() == Phase.PRE) {
         PlayerPacket packet = new PlayerPacket(this, new Vec2f(this.rotation.field_189982_i, PlayerPacketManager.INSTANCE.getServerSideRotation().field_189983_j));
         PlayerPacketManager.INSTANCE.addPacket(packet);
      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.Send> sendListener = new Listener((event) -> {
      if (this.rotation != null) {
         if (event.getPacket() instanceof Rotation) {
            ((Rotation)event.getPacket()).field_149476_e = this.rotation.field_189982_i;
         }

         if (event.getPacket() instanceof PositionRotation) {
            ((PositionRotation)event.getPacket()).field_149476_e = this.rotation.field_189982_i;
         }

         if (event.getPacket() instanceof CPacketVehicleMove) {
            ((AccessorCPacketVehicleMove)event.getPacket()).setYaw(this.rotation.field_189982_i);
         }
      }

   }, new Predicate[0]);
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

   public BedCevBreaker() {
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
         if (this.canPlaceBedWithoutBase() && this.space(this.placePos)) {
            this.getItem();
            if (this.anyBed && this.blockSlot != -1 && this.pickSlot != -1) {
               if (this.bedSlot != 0) {
                  if (mc.field_71441_e.func_175623_d(this.placePos.func_177978_c()) && mc.field_71441_e.func_175623_d(this.placePos.func_177976_e()) && mc.field_71441_e.func_175623_d(this.placePos.func_177974_f()) && mc.field_71441_e.func_175623_d(this.placePos.func_177968_d())) {
                     this.helpBlock(this.placePos);
                     this.rotation = null;
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

                     if (this.time <= System.currentTimeMillis()) {
                        if (this.start && this.timer.passedMs((long)(Integer)this.delay.getValue())) {
                           if (BlockUtil.isAir(this.placePos)) {
                              this.run(this.blockSlot, false, () -> {
                                 BurrowUtil.placeBlock(this.placePos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                              });
                           }

                           BlockPos basePos;
                           if (this.block(this.placePos.func_177974_f())) {
                              this.rotation = new Vec2f(90.0F, 90.0F);
                              basePos = this.placePos.func_177982_a(1, 0, 0);
                           } else if (this.block(this.placePos.func_177978_c())) {
                              this.rotation = new Vec2f(0.0F, 90.0F);
                              basePos = this.placePos.func_177982_a(0, 0, -1);
                           } else if (this.block(this.placePos.func_177976_e())) {
                              this.rotation = new Vec2f(-90.0F, 90.0F);
                              basePos = this.placePos.func_177982_a(-1, 0, 0);
                           } else {
                              if (!this.block(this.placePos.func_177968_d())) {
                                 this.rotation = null;
                                 return;
                              }

                              this.rotation = new Vec2f(180.0F, 90.0F);
                              basePos = this.placePos.func_177982_a(0, 0, 1);
                           }

                           if (PlayerPacketManager.INSTANCE.getServerSideRotation().field_189982_i != this.rotation.field_189982_i) {
                              return;
                           }

                           EnumHand hand = this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
                           EnumFacing opposite = EnumFacing.DOWN.func_176734_d();
                           Vec3d hitVec = (new Vec3d(basePos)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
                           if (BlockUtil.blackList.contains(mc.field_71441_e.func_180495_p(basePos).func_177230_c()) && !ColorMain.INSTANCE.sneaking) {
                              mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, net.minecraft.network.play.client.CPacketEntityAction.Action.START_SNEAKING));
                           }

                           this.run(this.bedSlot, false, () -> {
                              if ((Boolean)this.packet.getValue()) {
                                 mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(basePos, EnumFacing.UP, hand, 0.5F, 1.0F, 0.5F));
                              } else {
                                 mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, basePos, EnumFacing.UP, hitVec, hand);
                              }

                              if ((Boolean)this.swing.getValue()) {
                                 mc.field_71439_g.func_184609_a(hand);
                              }

                           });
                           this.run(this.pickSlot, (Boolean)this.pickBypass.getValue(), () -> {
                              this.facing = BlockUtil.getRayTraceFacing(this.placePos, EnumFacing.UP);
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
                           EnumFacing side = EnumFacing.UP;
                           Vec3d vec = this.getHitVecOffset(side);
                           mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(this.placePos.func_177984_a(), side, hand, (float)vec.field_72450_a, (float)vec.field_72448_b, (float)vec.field_72449_c));
                           if ((Boolean)this.swing.getValue()) {
                              mc.field_71439_g.func_184609_a(hand);
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

   private Vec3d getHitVecOffset(EnumFacing face) {
      Vec3i vec = face.func_176730_m();
      return new Vec3d((double)((float)vec.field_177962_a * 0.5F + 0.5F), (double)((float)vec.field_177960_b * 0.5F + 0.5F), (double)((float)vec.field_177961_c * 0.5F + 0.5F));
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
      }).filter(this::canPlaceBase).max(Comparator.comparing((p) -> {
         return mc.field_71439_g.func_174818_b(p);
      })).orElse((Object)null);
      this.run(this.blockSlot, false, () -> {
         BurrowUtil.placeBlock(finalPos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
      });
   }

   private boolean canPlaceBase(BlockPos pos) {
      if (ColorMain.INSTANCE.breakList.contains(pos)) {
         return false;
      } else if (BurrowUtil.getBedFacing(pos) == null) {
         return false;
      } else {
         return this.space(pos) && !this.intersectsWithEntity(pos);
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

   private boolean canPlaceBedWithoutBase() {
      return this.space(this.placePos) && (this.space(this.placePos.func_177974_f()) || this.space(this.placePos.func_177978_c()) || this.space(this.placePos.func_177976_e()) || this.space(this.placePos.func_177968_d()));
   }

   private boolean block(BlockPos pos) {
      if (BlockUtil.canReplace(pos)) {
         return false;
      } else {
         return this.space(pos) && this.solid(pos);
      }
   }

   private boolean solid(BlockPos pos) {
      return !BlockUtil.isBlockUnSolid(pos) && !(mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockBed) && mc.field_71441_e.func_180495_p(pos).isSideSolid(mc.field_71441_e, pos, EnumFacing.UP) && BlockUtil.getBlock(pos).field_149787_q;
   }

   private boolean space(BlockPos pos) {
      return mc.field_71441_e.func_180495_p(pos.func_177984_a()).func_177230_c() == Blocks.field_150324_C || mc.field_71441_e.func_175623_d(pos.func_177984_a());
   }

   private void getItem() {
      this.blockSlot = this.bedSlot = this.pickSlot = -1;
      this.anyBed = false;
      if (mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemEndCrystal) {
         this.bedSlot = 11;
         this.offhand = true;
      }

      for(int i = 0; i < 36; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBed) {
            this.anyBed = true;
            if (i < 9) {
               this.bedSlot = i;
            }
            break;
         }
      }

      this.blockSlot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
      this.pickSlot = this.findItem();
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

   private void run(int slot, boolean bypass, Runnable runnable) {
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
            if (slot < 9) {
               slot += 36;
            }

            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketClickWindow(0, slot, mc.field_71439_g.field_71071_by.field_70461_c, ClickType.SWAP, ItemStack.field_190927_a, mc.field_71439_g.field_71069_bz.func_75136_a(mc.field_71439_g.field_71071_by)));
            runnable.run();
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketClickWindow(0, slot, mc.field_71439_g.field_71071_by.field_70461_c, ClickType.SWAP, ItemStack.field_190927_a, mc.field_71439_g.field_71069_bz.func_75136_a(mc.field_71439_g.field_71071_by)));
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
