package com.lemonclient.client.module.modules.dev;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.InputUpdateEvent;

@Module.Declaration(
   name = "BlockMove",
   category = Category.Dev,
   priority = 120
)
public class BlockMove extends Module {
   BooleanSetting middle = this.registerBoolean("Middle", true);
   IntegerSetting delay = this.registerInteger("Delay", 250, 0, 2000);
   BooleanSetting only = this.registerBoolean("Only In Block", true);
   BooleanSetting avoid = this.registerBoolean("Avoid Out", true, () -> {
      return !(Boolean)this.only.getValue();
   });
   Timing timer = new Timing();
   Vec3d[] sides = new Vec3d[]{new Vec3d(0.24D, 0.0D, 0.24D), new Vec3d(-0.24D, 0.0D, 0.24D), new Vec3d(0.24D, 0.0D, -0.24D), new Vec3d(-0.24D, 0.0D, -0.24D)};
   @EventHandler
   private final Listener<InputUpdateEvent> inputUpdateEventListener = new Listener((event) -> {
      Vec3d vec = mc.field_71439_g.func_174791_d();
      boolean air = true;
      AxisAlignedBB playerBox = mc.field_71439_g.field_70121_D;
      Vec3d[] var5 = this.sides;
      int var6 = var5.length;

      int x;
      for(x = 0; x < var6; ++x) {
         Vec3d vec3d = var5[x];
         if (!air) {
            break;
         }

         for(int i = 0; i < 2; ++i) {
            BlockPos posx = new BlockPos(vec.func_178787_e(vec3d).func_72441_c(0.0D, (double)i, 0.0D));
            if (!BlockUtil.isAir(posx)) {
               AxisAlignedBB box = mc.field_71441_e.func_180495_p(posx).func_185918_c(mc.field_71441_e, posx);
               if (playerBox.func_72326_a(box)) {
                  air = false;
                  break;
               }
            }
         }
      }

      if (!air) {
         if (event.getMovementInput() instanceof MovementInputFromOptions) {
            if (this.timer.passedMs((long)(Integer)this.delay.getValue())) {
               BlockPos pos = (Boolean)this.middle.getValue() ? PlayerUtil.getPlayerPos() : new BlockPos((double)Math.round(vec.field_72450_a), vec.field_72448_b, (double)Math.round(vec.field_72449_c));
               EnumFacing facing = mc.field_71439_g.func_174811_aO();
               x = pos.func_177972_a(facing).field_177962_a - pos.field_177962_a;
               int z = pos.func_177972_a(facing).field_177961_c - pos.field_177961_c;
               boolean addX = x != 0;
               if (event.getMovementInput().field_187255_c) {
                  vec = this.add(pos, addX, addX ? x < 0 : z < 0);
               } else if (event.getMovementInput().field_187256_d) {
                  vec = this.add(pos, addX, addX ? x > 0 : z > 0);
               } else if (event.getMovementInput().field_187257_e) {
                  vec = this.add(pos, !addX, addX ? x > 0 : z < 0);
               } else if (event.getMovementInput().field_187258_f) {
                  vec = this.add(pos, !addX, addX ? x < 0 : z > 0);
               }

               if (vec != null) {
                  mc.field_71439_g.func_70107_b(vec.field_72450_a, vec.field_72448_b, vec.field_72449_c);
                  this.timer.reset();
               }
            }

            event.getMovementInput().field_187255_c = false;
            event.getMovementInput().field_187256_d = false;
            event.getMovementInput().field_187257_e = false;
            event.getMovementInput().field_187258_f = false;
            event.getMovementInput().field_192832_b = 0.0F;
            event.getMovementInput().field_78902_a = 0.0F;
         }

      }
   }, 200, new Predicate[0]);

   private Vec3d add(BlockPos pos, boolean x, boolean negative) {
      Vec3d vec;
      if (negative) {
         if (x) {
            vec = this.pos(pos.func_177982_a(-1, 0, 0));
         } else {
            vec = this.pos(pos.func_177982_a(0, 0, -1));
         }
      } else if (x) {
         vec = this.pos(pos.func_177982_a(1, 0, 0));
      } else {
         vec = this.pos(pos.func_177982_a(0, 0, 1));
      }

      return vec;
   }

   private Vec3d pos(BlockPos pos) {
      if ((Boolean)this.middle.getValue()) {
         return new Vec3d((double)pos.field_177962_a + 0.5D, (double)pos.field_177960_b, (double)pos.field_177961_c + 0.5D);
      } else {
         Vec3d vec = new Vec3d((double)pos.field_177962_a, (double)pos.field_177960_b, (double)pos.field_177961_c);
         Vec3d lastVec = vec;
         boolean any = !mc.field_71441_e.func_175623_d(pos) || !mc.field_71441_e.func_175623_d(pos.func_177984_a());
         vec = new Vec3d((double)pos.field_177962_a - 1.0E-8D, (double)pos.field_177960_b, (double)pos.field_177961_c);
         if (mc.field_71441_e.func_175623_d(new BlockPos(vec)) && mc.field_71441_e.func_175623_d((new BlockPos(vec)).func_177984_a())) {
            lastVec = vec;
         } else {
            any = true;
         }

         vec = new Vec3d((double)pos.field_177962_a, (double)pos.field_177960_b, (double)pos.field_177961_c - 1.0E-8D);
         if (mc.field_71441_e.func_175623_d(new BlockPos(vec)) && mc.field_71441_e.func_175623_d((new BlockPos(vec)).func_177984_a())) {
            lastVec = vec;
         } else {
            any = true;
         }

         vec = new Vec3d((double)pos.field_177962_a - 1.0E-8D, (double)pos.field_177960_b, (double)pos.field_177961_c - 1.0E-8D);
         if (mc.field_71441_e.func_175623_d(new BlockPos(vec)) && mc.field_71441_e.func_175623_d((new BlockPos(vec)).func_177984_a())) {
            lastVec = vec;
         } else {
            any = true;
         }

         return !(Boolean)this.only.getValue() && !any && (Boolean)this.avoid.getValue() ? null : lastVec;
      }
   }
}
