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
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "AntiRegear",
   category = Category.Combat
)
public class AntiRegear extends Module {
   public static AntiRegear INSTANCE;
   DoubleSetting reach = this.registerDouble("Range", 5.5D, 0.0D, 10.0D);
   BooleanSetting packet = this.registerBoolean("Packet Break", false);
   BooleanSetting swing = this.registerBoolean("Swing", true);
   List<BlockPos> selfPlaced = new ArrayList();
   public boolean working;
   @EventHandler
   private final Listener<PacketEvent.Send> listener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
            if (mc.field_71439_g.func_184586_b(packet.func_187022_c()).func_77973_b() instanceof ItemShulkerBox) {
               this.selfPlaced.add(packet.func_187023_a().func_177972_a(packet.func_187024_b()));
            }
         }

      }
   }, new Predicate[0]);

   public AntiRegear() {
      INSTANCE = this;
   }

   public void fast() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         List<BlockPos> sphere = new ArrayList();
         Iterator var2 = PlayerUtil.getNearPlayers(16.0D, 10).iterator();

         while(var2.hasNext()) {
            EntityPlayer target = (EntityPlayer)var2.next();
            Iterator var4 = EntityUtil.getSphere(EntityUtil.getEntityPos(target), 6.5D, 6.5D, false, false, 0).iterator();

            while(var4.hasNext()) {
               BlockPos pos = (BlockPos)var4.next();
               if (!this.selfPlaced.contains(pos) && mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockShulkerBox && mc.field_71439_g.func_70011_f((double)pos.field_177962_a + 0.5D, (double)pos.field_177960_b + 0.5D, (double)pos.field_177961_c + 0.5D) <= (Double)this.reach.getValue()) {
                  sphere.add(pos);
               }
            }
         }

         this.working = !sphere.isEmpty();
         var2 = sphere.iterator();
         if (var2.hasNext()) {
            BlockPos pos = (BlockPos)var2.next();
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

         this.selfPlaced.removeIf((posx) -> {
            return !(mc.field_71441_e.func_180495_p(posx).func_177230_c() instanceof BlockShulkerBox);
         });
      } else {
         this.working = false;
      }
   }
}