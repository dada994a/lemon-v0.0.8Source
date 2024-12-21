package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "HopperNuker",
   category = Category.Combat
)
public class HopperNuker extends Module {
   BooleanSetting packet = this.registerBoolean("Packet Break", false);
   DoubleSetting range = this.registerDouble("Range", 6.0D, 0.0D, 10.0D);
   BooleanSetting swing = this.registerBoolean("Swing", true);
   private final List<BlockPos> selfPlaced = new ArrayList();
   @EventHandler
   private final Listener<PacketEvent.Send> listener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
            ItemStack stack = mc.field_71439_g.field_71071_by.func_70448_g();
            if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock) {
               Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
               if (block == Blocks.field_150438_bZ) {
                  this.selfPlaced.add(packet.func_187023_a().func_177972_a(packet.func_187024_b()));
               }
            }
         }

      }
   }, new Predicate[0]);

   public void fast() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         List<BlockPos> sphere = EntityUtil.getSphere(PlayerUtil.getEyesPos(), (Double)this.range.getValue(), (Double)this.range.getValue(), false, false, 0);
         Iterator var2 = sphere.iterator();

         while(var2.hasNext()) {
            BlockPos pos = (BlockPos)var2.next();
            if (mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150438_bZ && mc.field_71441_e.func_180495_p(pos.func_177984_a()).func_177230_c() instanceof BlockShulkerBox && !this.selfPlaced.contains(pos)) {
               if ((Boolean)this.swing.getValue()) {
                  mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
               }

               if ((Boolean)this.packet.getValue()) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
               } else {
                  mc.field_71442_b.func_180512_c(pos, EnumFacing.UP);
               }
            }
         }

         this.selfPlaced.removeIf((posx) -> {
            return mc.field_71439_g.func_70011_f((double)posx.field_177962_a + 0.5D, (double)posx.field_177960_b + 0.5D, (double)posx.field_177961_c + 0.5D) > 8.0D;
         });
      }
   }
}
