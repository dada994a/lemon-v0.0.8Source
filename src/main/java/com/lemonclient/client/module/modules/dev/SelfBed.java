package com.lemonclient.client.module.modules.dev;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.combat.DamageUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBed;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Module.Declaration(
   name = "SelfBed",
   category = Category.Dev,
   priority = 999
)
public class SelfBed extends Module {
   ModeSetting page = this.registerMode("Page", Arrays.asList("General", "Calc"), "General");
   BooleanSetting packetPlace = this.registerBoolean("Packet Place", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting placeSwing = this.registerBoolean("Place Swing", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting breakSwing = this.registerBoolean("Break Swing", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting packetSwing = this.registerBoolean("Packet Swing", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting highVersion = this.registerBoolean("1.13", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting autoSwitch = this.registerBoolean("Auto Switch", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting update = this.registerBoolean("Update", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting silentSwitch = this.registerBoolean("Switch Back", true, () -> {
      return ((String)this.page.getValue()).equals("General") && (Boolean)this.autoSwitch.getValue();
   });
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   IntegerSetting calcDelay = this.registerInteger("Calc Delay", 0, 0, 1000, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   IntegerSetting placeDelay = this.registerInteger("Place Delay", 0, 0, 1000, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   IntegerSetting breakDelay = this.registerInteger("Break Delay", 0, 0, 1000, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   DoubleSetting range = this.registerDouble("Place Range", 5.0D, 0.0D, 10.0D, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   DoubleSetting yRange = this.registerDouble("Y Range", 2.5D, 0.0D, 10.0D, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   ModeSetting handMode = this.registerMode("Hand", Arrays.asList("Main", "Off", "Auto"), "Auto", () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   DoubleSetting maxDmg = this.registerDouble("Max Self Dmg", 10.0D, 0.0D, 20.0D, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   BooleanSetting antiSuicide = this.registerBoolean("Anti Suicide", true, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   BlockPos headPos;
   BlockPos basePos;
   float damage;
   float selfDamage;
   String face;
   Timing basetiming = new Timing();
   Timing calctiming = new Timing();
   Timing placetiming = new Timing();
   Timing breaktiming = new Timing();
   EnumHand hand;
   int slot;
   Vec2f rotation;
   int nowSlot;
   @EventHandler
   private final Listener<PacketEvent.Send> postSendListener = new Listener((event) -> {
      if (event.getPacket() instanceof CPacketHeldItemChange) {
         this.nowSlot = ((CPacketHeldItemChange)event.getPacket()).func_149614_c();
      }

   }, new Predicate[0]);

   public void onUpdate() {
      if (mc.field_71439_g != null && mc.field_71441_e != null && !EntityUtil.isDead(mc.field_71439_g) && !this.inNether()) {
         this.calc();
      } else {
         this.headPos = this.basePos = null;
         this.damage = this.selfDamage = 0.0F;
         this.rotation = null;
      }
   }

   public void fast() {
      if (mc.field_71439_g != null && mc.field_71441_e != null && !EntityUtil.isDead(mc.field_71439_g) && !this.inNether()) {
         if (mc.field_71439_g.field_71158_b.field_192832_b != 0.0F || mc.field_71439_g.field_71158_b.field_78902_a != 0.0F) {
            this.bedaura();
         }
      }
   }

   private void bedaura() {
      if (this.headPos != null && this.basePos != null) {
         if (this.isBed(this.headPos) || this.isBed(this.basePos)) {
            this.breakBed();
         }

         this.place();
         this.breakBed();
      }
   }

   private void calc() {
      if (this.calctiming.passedMs((long)(Integer)this.calcDelay.getValue())) {
         this.calctiming.reset();
         this.headPos = this.basePos = null;
         this.damage = this.selfDamage = 0.0F;
         this.rotation = null;
         if (mc.field_71439_g.field_71158_b.field_192832_b == 0.0F && mc.field_71439_g.field_71158_b.field_78902_a == 0.0F) {
            return;
         }

         boolean offhand = !((String)this.handMode.getValue()).equals("Main") && mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151104_aV;
         if (!offhand && !((String)this.handMode.getValue()).equals("Off")) {
            this.slot = BurrowUtil.findHotbarBlock(ItemBed.class);
            if (this.slot == -1) {
               return;
            }
         }

         this.hand = offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
         BlockPos bedPos = this.findBlocksExcluding();
         if (bedPos == null) {
            return;
         }

         this.headPos = bedPos;
         if (mc.field_71439_g.func_174811_aO().equals(EnumFacing.SOUTH)) {
            this.face = "SOUTH";
            this.rotation = new Vec2f(0.0F, 90.0F);
            bedPos = new BlockPos(this.headPos.field_177962_a, this.headPos.field_177960_b, this.headPos.field_177961_c - 1);
         } else if (mc.field_71439_g.func_174811_aO().equals(EnumFacing.WEST)) {
            this.face = "WEST";
            this.rotation = new Vec2f(90.0F, 90.0F);
            bedPos = new BlockPos(this.headPos.field_177962_a + 1, this.headPos.field_177960_b, this.headPos.field_177961_c);
         } else if (mc.field_71439_g.func_174811_aO().equals(EnumFacing.NORTH)) {
            this.face = "NORTH";
            this.rotation = new Vec2f(180.0F, 90.0F);
            bedPos = new BlockPos(this.headPos.field_177962_a, this.headPos.field_177960_b, this.headPos.field_177961_c + 1);
         } else {
            this.face = "EAST";
            this.rotation = new Vec2f(-90.0F, 90.0F);
            bedPos = new BlockPos(this.headPos.field_177962_a - 1, this.headPos.field_177960_b, this.headPos.field_177961_c);
         }

         if (!this.block(bedPos, true)) {
            this.headPos = this.basePos = null;
            this.damage = this.selfDamage = 0.0F;
            this.rotation = null;
            return;
         }

         this.headPos = this.headPos.func_177984_a();
         this.basePos = bedPos.func_177984_a();
      }

   }

   private void place() {
      if (this.placetiming.passedMs((long)(Integer)this.placeDelay.getValue())) {
         BlockPos neighbour = this.basePos.func_177977_b();
         EnumFacing opposite = EnumFacing.DOWN.func_176734_d();
         Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
         boolean sneak = false;
         if (BlockUtil.blackList.contains(mc.field_71441_e.func_180495_p(neighbour).func_177230_c()) && !mc.field_71439_g.func_70093_af()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
            sneak = true;
         }

         this.run(() -> {
            BurrowUtil.rightClickBlock(neighbour, hitVec, this.hand, opposite, (Boolean)this.packetPlace.getValue());
         }, this.slot);
         if ((Boolean)this.placeSwing.getValue()) {
            this.swing(this.hand);
         }

         if (sneak) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
         }

         this.placetiming.reset();
      }

   }

   private void run(Runnable runnable, int slot) {
      if (this.hand == EnumHand.OFF_HAND) {
         runnable.run();
      } else {
         int oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
         if (slot != oldSlot) {
            if ((Boolean)this.autoSwitch.getValue()) {
               this.switchTo(slot);
               if (this.nowSlot == slot || mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_151104_aV) {
                  runnable.run();
               }

               if ((Boolean)this.silentSwitch.getValue()) {
                  this.switchTo(oldSlot);
               }
            }
         } else {
            runnable.run();
         }

      }
   }

   private void breakBed() {
      if (this.breaktiming.passedMs((long)(Integer)this.breakDelay.getValue())) {
         EnumFacing side = EnumFacing.UP;
         if (((ColorMain)ModuleManager.getModule(ColorMain.class)).sneaking) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
         }

         Vec3d facing = this.getHitVecOffset(side);
         if (this.isBed(this.headPos) && !this.isBed(this.basePos)) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(this.headPos, side, this.hand, (float)facing.field_72450_a, (float)facing.field_72448_b, (float)facing.field_72449_c));
         } else {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(this.basePos, side, this.hand, (float)facing.field_72450_a, (float)facing.field_72448_b, (float)facing.field_72449_c));
         }

         if ((Boolean)this.breakSwing.getValue()) {
            this.swing(this.hand);
         }

         this.breaktiming.reset();
      }

   }

   private BlockPos findBlocksExcluding() {
      double x = mc.field_71439_g.field_70169_q;
      double z = mc.field_71439_g.field_70166_s;
      double dX = mc.field_71439_g.field_70165_t - x;
      double dZ = mc.field_71439_g.field_70161_v - z;
      List<BlockPos> posList = new ArrayList();
      int[] var10 = new int[]{-3, -2, -1, 0, 1, 2};
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         int y = var10[var12];
         posList.addAll((Collection)EntityUtil.getSphere(PlayerUtil.getEyesPos(), (Double)this.range.getValue() + 1.0D, 1.0D, false, false, y).stream().filter((p) -> {
            return (mc.field_71439_g.field_70165_t - x) * (mc.field_71439_g.field_70165_t - (double)p.field_177962_a) > 0.0D && (mc.field_71439_g.field_70161_v - z) * (mc.field_71439_g.field_70161_v - (double)p.field_177961_c) > 0.0D;
         }).filter(this::canPlaceBed).filter((p) -> {
            return (x - (double)p.field_177962_a) * dX >= 0.0D && (z - (double)p.field_177961_c) * dZ >= 0.0D;
         }).filter((p) -> {
            double dmg = (double)DamageUtil.calculateDamage(mc.field_71439_g, (double)p.field_177962_a + 0.5D, (double)p.field_177960_b + 1.5625D, (double)p.field_177961_c + 0.5D, 5.0F, "Bed");
            return dmg <= (Double)this.maxDmg.getValue() && (!(Boolean)this.antiSuicide.getValue() || dmg <= (double)(EntityUtil.getHealth(mc.field_71439_g) + 1.0F));
         }).collect(Collectors.toList()));
      }

      Stream var10000 = posList.stream();
      EntityPlayerSP var10001 = mc.field_71439_g;
      var10001.getClass();
      BlockPos pos = (BlockPos)var10000.min(Comparator.comparing(var10001::func_174818_b)).orElse((Object)null);
      return pos;
   }

   private boolean canPlaceBed(BlockPos blockPos) {
      if (!this.block(blockPos, false)) {
         return false;
      } else {
         BlockPos pos = blockPos.func_177967_a(mc.field_71439_g.func_174811_aO(), -1);
         return this.block(pos, true) && this.inRange(pos.func_177984_a());
      }
   }

   private boolean block(BlockPos pos, boolean rangeCheck) {
      if (!this.space(pos.func_177984_a())) {
         return false;
      } else if (BlockUtil.canReplace(pos)) {
         return false;
      } else if (!(Boolean)this.highVersion.getValue() && !this.solid(pos)) {
         return false;
      } else {
         return !rangeCheck || this.inRange(pos.func_177984_a());
      }
   }

   private boolean isBed(BlockPos pos) {
      Block block = mc.field_71441_e.func_180495_p(pos).func_177230_c();
      return block == Blocks.field_150324_C || block instanceof BlockBed;
   }

   private boolean space(BlockPos pos) {
      return mc.field_71441_e.func_175623_d(pos) || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150324_C;
   }

   private boolean solid(BlockPos pos) {
      return !BlockUtil.isBlockUnSolid(pos) && !(mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockBed) && mc.field_71441_e.func_180495_p(pos).isSideSolid(mc.field_71441_e, pos, EnumFacing.UP);
   }

   private boolean inRange(BlockPos pos) {
      double x = (double)pos.field_177962_a - mc.field_71439_g.field_70165_t;
      double z = (double)pos.field_177961_c - mc.field_71439_g.field_70161_v;
      double y = (double)(pos.field_177960_b - PlayerUtil.getEyesPos().field_177960_b);
      double add = Math.sqrt(y * y) / 2.0D;
      return x * x + z * z <= ((Double)this.range.getValue() - add) * ((Double)this.range.getValue() - add) && y * y <= (Double)this.yRange.getValue() * (Double)this.yRange.getValue();
   }

   private Vec3d getHitVecOffset(EnumFacing face) {
      Vec3i vec = face.func_176730_m();
      return new Vec3d((double)((float)vec.field_177962_a * 0.5F + 0.5F), (double)((float)vec.field_177960_b * 0.5F + 0.5F), (double)((float)vec.field_177961_c * 0.5F + 0.5F));
   }

   private void switchTo(int slot) {
      if (slot > -1 && slot < 9) {
         if ((Boolean)this.packetSwitch.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = slot;
         }

         if ((Boolean)this.update.getValue()) {
            mc.field_71442_b.func_78765_e();
         }
      }

   }

   private void swing(EnumHand hand) {
      if ((Boolean)this.packetSwing.getValue()) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketAnimation(hand));
      } else {
         mc.field_71439_g.func_184609_a(hand);
      }

   }

   private boolean inNether() {
      return mc.field_71439_g.field_71093_bK == 0;
   }

   public void onEnable() {
      this.calctiming.reset();
      this.basetiming.reset();
      this.placetiming.reset();
      this.breaktiming.reset();
   }
}
