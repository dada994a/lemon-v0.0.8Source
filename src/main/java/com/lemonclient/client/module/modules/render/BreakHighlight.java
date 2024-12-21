package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.event.events.DrawBlockDamageEvent;
import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.util.render.RenderUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "BreakHighlight",
   category = Category.Render
)
public class BreakHighlight extends Module {
   public static BreakHighlight INSTANCE;
   BooleanSetting cancelAnimation = this.registerBoolean("No Animation", true);
   IntegerSetting range = this.registerInteger("Range", 64, 0, 256);
   IntegerSetting playerRange = this.registerInteger("Player Range", 16, 0, 64);
   BooleanSetting showProgress = this.registerBoolean("Show Progress", false);
   IntegerSetting decimal = this.registerInteger("Decimal", 2, 0, 2, () -> {
      return (Boolean)this.showProgress.getValue();
   });
   BooleanSetting doubleMine = this.registerBoolean("Double Mine", true);
   ColorSetting nameColor = this.registerColor("Name Color", new GSColor(255, 255, 255));
   ModeSetting renderType = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
   ColorSetting color = this.registerColor("Color", new GSColor(0, 255, 0, 255));
   ColorSetting dColor = this.registerColor("Double Color", new GSColor(0, 255, 0, 255), () -> {
      return (Boolean)this.doubleMine.getValue();
   });
   IntegerSetting alpha = this.registerInteger("Alpha", 100, 0, 255);
   IntegerSetting outAlpha = this.registerInteger("Outline Alpha", 255, 0, 255);
   IntegerSetting width = this.registerInteger("Width", 1, 0, 5);
   DoubleSetting scale = this.registerDouble("Text Scale", 0.025D, 0.01D, 0.05D);
   HashMap<EntityPlayer, BreakHighlight.renderBlock> list = new HashMap();
   BlockPos lastBreak;
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         if (event.getPacket() instanceof SPacketBlockBreakAnim) {
            SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim)event.getPacket();
            BlockPos blockPos = packet.func_179821_b();
            if (mc.field_71439_g.func_174818_b(blockPos) > (double)((Integer)this.range.getValue() * (Integer)this.range.getValue())) {
               return;
            }

            EntityPlayer entityPlayer = (EntityPlayer)mc.field_71441_e.func_73045_a(packet.func_148845_c());
            if (entityPlayer == null) {
               return;
            }

            if (this.list.containsKey(entityPlayer)) {
               if (this.isPos2(((BreakHighlight.renderBlock)this.list.get(entityPlayer)).pos.pos, blockPos)) {
                  return;
               }

               ((BreakHighlight.renderBlock)this.list.get(entityPlayer)).pos.updatePos(blockPos);
            } else {
               this.list.put(entityPlayer, new BreakHighlight.renderBlock(new BreakHighlight.breakPos(blockPos), entityPlayer));
            }
         }

      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<DrawBlockDamageEvent> drawBlockDamageEventListener = new Listener((event) -> {
      if ((Boolean)this.cancelAnimation.getValue()) {
         event.cancel();
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.PostSend> listener = new Listener((event) -> {
      if (event.getPacket() instanceof CPacketPlayerDigging) {
         CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
         if (packet.func_180762_c() == Action.START_DESTROY_BLOCK) {
            this.lastBreak = packet.func_179715_a();
         }
      }

   }, new Predicate[0]);

   public BreakHighlight() {
      INSTANCE = this;
   }

   private boolean isPos2(BlockPos pos1, BlockPos pos2) {
      if (pos1 != null && pos2 != null) {
         return pos1.field_177962_a == pos2.field_177962_a && pos1.field_177960_b == pos2.field_177960_b && pos1.field_177961_c == pos2.field_177961_c;
      } else {
         return false;
      }
   }

   public void onWorldRender(RenderEvent event) {
      if (mc.field_71439_g != null && mc.field_71441_e != null) {
         List<EntityPlayer> playerList = mc.field_71441_e.field_73010_i;
         Iterator var3 = playerList.iterator();

         while(true) {
            while(true) {
               EntityPlayer player;
               BlockPos pos;
               BlockPos dPos;
               int rangeSq;
               int playerSq;
               do {
                  do {
                     do {
                        if (!var3.hasNext()) {
                           return;
                        }

                        player = (EntityPlayer)var3.next();
                     } while(!this.list.containsKey(player));

                     pos = ((BreakHighlight.renderBlock)this.list.get(player)).pos.pos;
                     dPos = ((BreakHighlight.renderBlock)this.list.get(player)).pos.dPos;
                     if (pos != null && mc.field_71441_e.func_180495_p(pos).func_185887_b(mc.field_71441_e, pos) < 0.0F) {
                        ((BreakHighlight.renderBlock)this.list.get(player)).pos.remove();
                     }

                     if (dPos != null && mc.field_71441_e.func_180495_p(dPos).func_185887_b(mc.field_71441_e, dPos) < 0.0F) {
                        ((BreakHighlight.renderBlock)this.list.get(player)).pos.removeDouble();
                     }

                     if (this.isPos2(pos, dPos)) {
                        dPos = null;
                     }
                  } while(pos == null && dPos == null);

                  rangeSq = (Integer)this.range.getValue() * (Integer)this.range.getValue();
                  playerSq = (Integer)this.playerRange.getValue() * (Integer)this.playerRange.getValue();
               } while(pos != null && mc.field_71439_g.func_174818_b(pos) > (double)rangeSq && dPos != null && mc.field_71439_g.func_174818_b(dPos) > (double)rangeSq);

               if (pos != null && player.func_174818_b(pos) > (double)playerSq && dPos != null && player.func_174818_b(dPos) > (double)playerSq) {
                  this.list.remove(player);
               } else {
                  ((BreakHighlight.renderBlock)this.list.get(player)).update();
               }
            }
         }
      } else {
         this.list.clear();
      }
   }

   public static GSColor getRainbowColor(int damage) {
      return GSColor.fromHSB((float)((1 + damage * 32) % 11520) / 11520.0F, 1.0F, 1.0F);
   }

   private void renderBox(BreakHighlight.breakPos pos, EntityPlayer player) {
      String[] name = new String[]{player.func_70005_c_()};
      BlockPos blockPos = pos.pos;
      if (blockPos != null) {
         float mineDamage = (float)(System.currentTimeMillis() - pos.start) / (float)pos.time;
         if (mineDamage > 1.0F) {
            mineDamage = 1.0F;
         }

         AxisAlignedBB getSelectedBoundingBox = new AxisAlignedBB(blockPos);
         Vec3d getCenter = getSelectedBoundingBox.func_189972_c();
         float prognum = mineDamage * 100.0F;
         if ((Boolean)this.showProgress.getValue()) {
            String[] progress = new String[]{String.format("%.0f", prognum)};
            if ((Integer)this.decimal.getValue() == 1) {
               progress = new String[]{String.format("%.1f", prognum)};
            } else if ((Integer)this.decimal.getValue() == 2) {
               progress = new String[]{String.format("%.2f", prognum)};
            }

            RenderUtil.drawNametag((double)blockPos.func_177958_n() + 0.5D, (double)blockPos.func_177956_o() + 0.39D, (double)blockPos.func_177952_p() + 0.5D, progress, getRainbowColor((int)prognum), 1, (Double)this.scale.getValue(), 0.0D);
            RenderUtil.drawNametag((double)blockPos.func_177958_n() + 0.5D, (double)blockPos.func_177956_o() + 0.61D, (double)blockPos.func_177952_p() + 0.5D, name, new GSColor(this.nameColor.getColor(), 255), 1, (Double)this.scale.getValue(), 0.0D);
         } else {
            RenderUtil.drawNametag((double)blockPos.func_177958_n() + 0.5D, (double)blockPos.func_177956_o() + 0.5D, (double)blockPos.func_177952_p() + 0.5D, name, new GSColor(this.nameColor.getColor(), 255), 1, (Double)this.scale.getValue(), 0.0D);
         }

         this.renderESP((new AxisAlignedBB(getCenter.field_72450_a, getCenter.field_72448_b, getCenter.field_72449_c, getCenter.field_72450_a, getCenter.field_72448_b, getCenter.field_72449_c)).func_72314_b((getSelectedBoundingBox.field_72340_a - getSelectedBoundingBox.field_72336_d) * 0.5D * (double)MathHelper.func_76131_a(mineDamage, 0.0F, 1.0F), (getSelectedBoundingBox.field_72338_b - getSelectedBoundingBox.field_72337_e) * 0.5D * (double)MathHelper.func_76131_a(mineDamage, 0.0F, 1.0F), (getSelectedBoundingBox.field_72339_c - getSelectedBoundingBox.field_72334_f) * 0.5D * (double)MathHelper.func_76131_a(mineDamage, 0.0F, 1.0F)), false);
      }

      if ((Boolean)this.doubleMine.getValue()) {
         BlockPos doubleBlockPos = pos.dPos;
         if (doubleBlockPos != null) {
            float doubleMineDamage = (float)(System.currentTimeMillis() - pos.dStart) / (float)pos.dTime;
            if (doubleMineDamage > 1.0F) {
               doubleMineDamage = 1.0F;
            }

            AxisAlignedBB getDoubleSelectedBoundingBox = new AxisAlignedBB(doubleBlockPos);
            Vec3d getDoubleCenter = getDoubleSelectedBoundingBox.func_189972_c();
            float doublePrognum = doubleMineDamage * 100.0F;
            if ((Boolean)this.showProgress.getValue()) {
               String[] progress = new String[]{String.format("%.0f", doublePrognum)};
               if ((Integer)this.decimal.getValue() == 1) {
                  progress = new String[]{String.format("%.1f", doublePrognum)};
               } else if ((Integer)this.decimal.getValue() == 2) {
                  progress = new String[]{String.format("%.2f", doublePrognum)};
               }

               RenderUtil.drawNametag((double)doubleBlockPos.func_177958_n() + 0.5D, (double)doubleBlockPos.func_177956_o() + 0.39D, (double)doubleBlockPos.func_177952_p() + 0.5D, progress, getRainbowColor((int)doublePrognum), 1, (Double)this.scale.getValue(), 0.0D);
               RenderUtil.drawNametag((double)doubleBlockPos.func_177958_n() + 0.5D, (double)doubleBlockPos.func_177956_o() + 0.61D, (double)doubleBlockPos.func_177952_p() + 0.5D, name, new GSColor(this.nameColor.getColor(), 255), 1, (Double)this.scale.getValue(), 0.0D);
            } else {
               RenderUtil.drawNametag((double)doubleBlockPos.func_177958_n() + 0.5D, (double)doubleBlockPos.func_177956_o() + 0.5D, (double)doubleBlockPos.func_177952_p() + 0.5D, name, new GSColor(this.nameColor.getColor(), 255), 1, (Double)this.scale.getValue(), 0.0D);
            }

            this.renderESP((new AxisAlignedBB(getDoubleCenter.field_72450_a, getDoubleCenter.field_72448_b, getDoubleCenter.field_72449_c, getDoubleCenter.field_72450_a, getDoubleCenter.field_72448_b, getDoubleCenter.field_72449_c)).func_72314_b((getDoubleSelectedBoundingBox.field_72340_a - getDoubleSelectedBoundingBox.field_72336_d) * 0.5D * (double)MathHelper.func_76131_a(doubleMineDamage, 0.0F, 1.0F), (getDoubleSelectedBoundingBox.field_72338_b - getDoubleSelectedBoundingBox.field_72337_e) * 0.5D * (double)MathHelper.func_76131_a(doubleMineDamage, 0.0F, 1.0F), (getDoubleSelectedBoundingBox.field_72339_c - getDoubleSelectedBoundingBox.field_72334_f) * 0.5D * (double)MathHelper.func_76131_a(doubleMineDamage, 0.0F, 1.0F)), true);
         }

      }
   }

   private void renderESP(AxisAlignedBB axisAlignedBB, boolean dm) {
      GSColor fillColor = new GSColor(dm ? this.dColor.getValue() : this.color.getValue(), (Integer)this.alpha.getValue());
      GSColor outlineColor = new GSColor(dm ? this.dColor.getValue() : this.color.getValue(), (Integer)this.outAlpha.getValue());
      String var5 = (String)this.renderType.getValue();
      byte var6 = -1;
      switch(var5.hashCode()) {
      case 2189731:
         if (var5.equals("Fill")) {
            var6 = 0;
         }
         break;
      case 558407714:
         if (var5.equals("Outline")) {
            var6 = 1;
         }
      }

      switch(var6) {
      case 0:
         RenderUtil.drawBox(axisAlignedBB, true, 0.0D, fillColor, 63);
         break;
      case 1:
         RenderUtil.drawBoundingBox(axisAlignedBB, (double)(Integer)this.width.getValue(), outlineColor);
         break;
      default:
         RenderUtil.drawBox(axisAlignedBB, true, 0.0D, fillColor, 63);
         RenderUtil.drawBoundingBox(axisAlignedBB, (double)(Integer)this.width.getValue(), outlineColor);
      }

   }

   private int calcBreakTime(BlockPos pos) {
      if (pos == null) {
         return -1;
      } else {
         IBlockState blockState = mc.field_71441_e.func_180495_p(pos);
         float hardness = blockState.func_185887_b(mc.field_71441_e, pos);
         float breakSpeed = this.getBreakSpeed(pos, blockState);
         if (breakSpeed == -1.0F) {
            return -1;
         } else {
            float relativeDamage = breakSpeed / hardness / 30.0F;
            int ticks = (int)Math.ceil((double)(0.7F / relativeDamage));
            return ticks * 50;
         }
      }
   }

   private float getBreakSpeed(BlockPos pos, IBlockState blockState) {
      float maxSpeed = 1.0F;
      int slot = this.findItem(pos);
      float speed = mc.field_71439_g.field_71071_by.func_70301_a(slot).func_150997_a(blockState);
      if (speed <= 1.0F) {
         return maxSpeed;
      } else {
         int efficiency = EnchantmentHelper.func_77506_a(Enchantments.field_185305_q, mc.field_71439_g.field_71071_by.func_70301_a(slot));
         if (efficiency > 0) {
            speed += (float)(efficiency * efficiency) + 1.0F;
         }

         if (speed > maxSpeed) {
            maxSpeed = speed;
         }

         return maxSpeed;
      }
   }

   public int findItem(BlockPos pos) {
      return pos == null ? mc.field_71439_g.field_71071_by.field_70461_c : findBestTool(pos, mc.field_71441_e.func_180495_p(pos));
   }

   public static int findBestTool(BlockPos pos, IBlockState state) {
      int result = mc.field_71439_g.field_71071_by.field_70461_c;
      if (state.func_185887_b(mc.field_71441_e, pos) > 0.0F) {
         double speed = getSpeed(state, mc.field_71439_g.func_184614_ca());

         for(int i = 0; i < 36; ++i) {
            ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
            double stackSpeed = getSpeed(state, stack);
            if (stackSpeed > speed) {
               speed = stackSpeed;
               result = i;
            }
         }
      }

      return result;
   }

   public static double getSpeed(IBlockState state, ItemStack stack) {
      double str = (double)stack.func_150997_a(state);
      int effect = EnchantmentHelper.func_77506_a(Enchantments.field_185305_q, stack);
      return Math.max(str + (str > 1.0D ? (double)(effect * effect) + 1.0D : 0.0D), 0.0D);
   }

   public static class breakPos {
      private BlockPos pos;
      private BlockPos dPos = null;
      private long start;
      private long dStart;
      private long time;
      private long dTime;

      public breakPos(BlockPos pos) {
         this.pos = pos;
         this.start = System.currentTimeMillis();
         this.time = (long)BreakHighlight.INSTANCE.calcBreakTime(pos);
      }

      public void updatePos(BlockPos pos) {
         if (this.dPos == null) {
            this.dPos = this.pos;
            this.dStart = this.start;
            this.dTime = (long)((double)this.time * 1.4D);
         }

         this.pos = pos;
         this.start = System.currentTimeMillis();
         this.time = (long)BreakHighlight.INSTANCE.calcBreakTime(pos);
      }

      public long getEnd() {
         return this.start + this.time;
      }

      public void update() {
         this.time = (long)BreakHighlight.INSTANCE.calcBreakTime(this.pos);
         if (this.dPos != null && BlockUtil.airBlocks.contains(BreakHighlight.mc.field_71441_e.func_180495_p(this.dPos).func_177230_c())) {
            this.removeDouble();
         }

      }

      public void remove() {
         this.pos = null;
      }

      public void removeDouble() {
         this.dPos = null;
      }
   }

   class renderBlock {
      private final BreakHighlight.breakPos pos;
      private final EntityPlayer player;

      public renderBlock(BreakHighlight.breakPos pos, EntityPlayer player) {
         this.pos = pos;
         this.player = player;
      }

      void update() {
         this.pos.update();
         BreakHighlight.this.renderBox(this.pos, this.player);
      }
   }
}
