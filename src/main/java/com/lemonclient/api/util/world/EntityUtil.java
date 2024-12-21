package com.lemonclient.api.util.world;

import com.lemonclient.api.util.misc.Wrapper;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;

public class EntityUtil {
   private static final Minecraft mc = Minecraft.func_71410_x();
   public static final Vec3d[] antiDropOffsetList = new Vec3d[]{new Vec3d(0.0D, -2.0D, 0.0D)};
   public static final Vec3d[] platformOffsetList = new Vec3d[]{new Vec3d(0.0D, -1.0D, 0.0D), new Vec3d(0.0D, -1.0D, -1.0D), new Vec3d(0.0D, -1.0D, 1.0D), new Vec3d(-1.0D, -1.0D, 0.0D), new Vec3d(1.0D, -1.0D, 0.0D)};
   public static final Vec3d[] legOffsetList = new Vec3d[]{new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 0.0D, 1.0D)};
   public static final Vec3d[] OffsetList = new Vec3d[]{new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(0.0D, 2.0D, 0.0D)};
   public static final Vec3d[] antiStepOffsetList = new Vec3d[]{new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(0.0D, 2.0D, -1.0D)};
   public static final Vec3d[] antiScaffoldOffsetList = new Vec3d[]{new Vec3d(0.0D, 3.0D, 0.0D)};
   public static final Vec3d[] doubleLegOffsetList = new Vec3d[]{new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-2.0D, 0.0D, 0.0D), new Vec3d(2.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -2.0D), new Vec3d(0.0D, 0.0D, 2.0D)};

   public static void faceXYZ(double x, double y, double z) {
      faceYawAndPitch(getXYZYaw(x, y, z), getXYZPitch(x, y, z));
   }

   public static float getXYZYaw(double x, double y, double z) {
      float[] angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(mc.func_184121_ak()), new Vec3d(x, y, z));
      return angle[0];
   }

   public static boolean stopSneaking(boolean isSneaking) {
      if (isSneaking && mc.field_71439_g != null) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
      }

      return false;
   }

   public static int getDamagePercent(ItemStack stack) {
      return (int)((double)(stack.func_77958_k() - stack.func_77952_i()) / Math.max(0.1D, (double)stack.func_77958_k()) * 100.0D);
   }

   public static void faceVector(Vec3d vec) {
      float[] rotations = getLegitRotations(vec);
      sendPlayerRot(rotations[0], rotations[1], mc.field_71439_g.field_70122_E);
   }

   public static void facePosFacing(BlockPos pos, EnumFacing side) {
      Vec3d hitVec = (new Vec3d(pos)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(side.func_176730_m())).func_186678_a(0.5D));
      faceVector(hitVec);
   }

   public static void facePlacePos(BlockPos pos, boolean strict, boolean raytrace) {
      EnumFacing side = BlockUtil.getFirstFacing(pos, strict, raytrace);
      if (side != null) {
         BlockPos neighbour = pos.func_177972_a(side);
         EnumFacing opposite = side.func_176734_d();
         Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
         BlockUtil.faceVector(hitVec);
      }
   }

   public static float getXYZPitch(double x, double y, double z) {
      float[] angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(mc.func_184121_ak()), new Vec3d(x, y, z));
      return angle[1];
   }

   public static void faceYawAndPitch(float yaw, float pitch) {
      sendPlayerRot(yaw, pitch, mc.field_71439_g.field_70122_E);
   }

   public static void sendPlayerRot(float yaw, float pitch, boolean onGround) {
      mc.field_71439_g.field_71174_a.func_147297_a(new Rotation(yaw, pitch, onGround));
   }

   public static float[] getLegitRotations(Vec3d vec) {
      Vec3d eyesPos = BlockUtil.getEyesPos();
      double diffX = vec.field_72450_a - eyesPos.field_72450_a;
      double diffY = vec.field_72448_b - eyesPos.field_72448_b;
      double diffZ = vec.field_72449_c - eyesPos.field_72449_c;
      double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
      float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
      float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
      return new float[]{mc.field_71439_g.field_70177_z + MathHelper.func_76142_g(yaw - mc.field_71439_g.field_70177_z), mc.field_71439_g.field_70125_A + MathHelper.func_76142_g(pitch - mc.field_71439_g.field_70125_A)};
   }

   public static Vec2f getRotations(Vec3d vec) {
      Vec3d eyesPos = BlockUtil.getEyesPos();
      double diffX = vec.field_72450_a - eyesPos.field_72450_a;
      double diffY = vec.field_72448_b - eyesPos.field_72448_b;
      double diffZ = vec.field_72449_c - eyesPos.field_72449_c;
      double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
      float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
      float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
      return new Vec2f(mc.field_71439_g.field_70177_z + MathHelper.func_76142_g(yaw - mc.field_71439_g.field_70177_z), mc.field_71439_g.field_70125_A + MathHelper.func_76142_g(pitch - mc.field_71439_g.field_70125_A));
   }

   public static boolean isEating() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && mc.field_71439_g.field_70173_aa > 20) {
         RayTraceResult result = mc.field_71476_x;
         if (result.field_72313_a == Type.BLOCK) {
            BlockPos pos = mc.field_71476_x.func_178782_a();
            if (BlockUtil.blackList.contains(BlockUtil.getBlock(pos)) && !ColorMain.INSTANCE.sneaking) {
               return false;
            }
         }

         return mc.field_71439_g.func_184587_cr() && (mc.field_71439_g.func_184607_cu().func_77973_b() instanceof ItemFood || mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemFood);
      } else {
         return false;
      }
   }

   public static boolean invalid(Entity entity, double range) {
      return entity == null || isDead(entity) || entity.equals(mc.field_71439_g) || entity instanceof EntityPlayer && SocialManager.isFriend(entity.func_70005_c_()) || mc.field_71439_g.func_70068_e(entity) > MathUtil.square(range);
   }

   public static BlockPos getEntityPos(Entity target) {
      return new BlockPos(target.field_70165_t, target.field_70163_u + 0.5D, target.field_70161_v);
   }

   public static boolean isLiving(Entity entity) {
      return entity instanceof EntityLivingBase;
   }

   public static Vec3d[] getVarOffsets(int x, int y, int z) {
      List<Vec3d> offsets = getVarOffsetList(x, y, z);
      Vec3d[] array = new Vec3d[offsets.size()];
      return (Vec3d[])offsets.toArray(array);
   }

   public static BlockPos getPlayerPos(EntityPlayer player) {
      return player == null ? null : new BlockPos(Math.floor(player.field_70165_t), Math.floor(player.field_70163_u) + 0.5D, Math.floor(player.field_70161_v));
   }

   public static List<Vec3d> getVarOffsetList(int x, int y, int z) {
      ArrayList<Vec3d> offsets = new ArrayList();
      offsets.add(new Vec3d((double)x, (double)y, (double)z));
      return offsets;
   }

   public static BlockPos getRoundedBlockPos(Entity entity) {
      return new BlockPos(MathUtil.roundVec(entity.field_181017_ao, 0));
   }

   public static boolean isAlive(Entity entity) {
      return isLiving(entity) && !entity.field_70128_L && ((EntityLivingBase)entity).func_110143_aJ() > 0.0F;
   }

   public static boolean isOnLiquid() {
      double y = mc.field_71439_g.field_70163_u - 0.03D;

      for(int x = MathHelper.func_76128_c(mc.field_71439_g.field_70165_t); x < MathHelper.func_76143_f(mc.field_71439_g.field_70165_t); ++x) {
         for(int z = MathHelper.func_76128_c(mc.field_71439_g.field_70161_v); z < MathHelper.func_76143_f(mc.field_71439_g.field_70161_v); ++z) {
            BlockPos pos = new BlockPos(x, MathHelper.func_76128_c(y), z);
            if (mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockLiquid) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean isDead(Entity entity) {
      return !isAlive(entity);
   }

   public static float getHealth(Entity entity) {
      if (isLiving(entity)) {
         EntityLivingBase livingBase = (EntityLivingBase)entity;
         return livingBase.func_110143_aJ() + livingBase.func_110139_bj();
      } else {
         return 0.0F;
      }
   }

   public static boolean isPassive(Entity e) {
      if (e instanceof EntityWolf && ((EntityWolf)e).func_70919_bu()) {
         return false;
      } else if (!(e instanceof EntityAgeable) && !(e instanceof EntityAmbientCreature) && !(e instanceof EntitySquid)) {
         return e instanceof EntityIronGolem && ((EntityIronGolem)e).func_70643_av() == null;
      } else {
         return true;
      }
   }

   public static Vec3d[] getOffsets(int y, boolean floor, boolean face) {
      List<Vec3d> offsets = getOffsetList(y, floor, face);
      Vec3d[] array = new Vec3d[offsets.size()];
      return (Vec3d[])offsets.toArray(array);
   }

   public static boolean isSafe(Entity entity, int height, boolean floor) {
      return getUnsafeBlocks(entity, height, floor).size() == 0;
   }

   public static Vec3d[] getUnsafeBlockArray(Entity entity, int height, boolean floor) {
      List<Vec3d> list = getUnsafeBlocks(entity, height, floor);
      Vec3d[] array = new Vec3d[list.size()];
      return (Vec3d[])list.toArray(array);
   }

   public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor) {
      return getUnsafeBlocksFromVec3d(entity.func_174791_d(), height, floor);
   }

   public static Vec3d[] getUnsafeBlockArrayFromVec3d(Vec3d pos, int height, boolean floor) {
      List<Vec3d> list = getUnsafeBlocksFromVec3d(pos, height, floor);
      Vec3d[] array = new Vec3d[list.size()];
      return (Vec3d[])list.toArray(array);
   }

   public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
      ArrayList<Vec3d> vec3ds = new ArrayList();
      Vec3d[] var4 = getOffsets(height, floor);
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Vec3d vector = var4[var6];
         BlockPos targetPos = (new BlockPos(pos)).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
         Block block = mc.field_71441_e.func_180495_p(targetPos).func_177230_c();
         if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
            vec3ds.add(vector);
         }
      }

      return vec3ds;
   }

   public static List<Vec3d> getOffsetList(int y, boolean floor) {
      ArrayList<Vec3d> offsets = new ArrayList();
      offsets.add(new Vec3d(-1.0D, (double)y, 0.0D));
      offsets.add(new Vec3d(1.0D, (double)y, 0.0D));
      offsets.add(new Vec3d(0.0D, (double)y, -1.0D));
      offsets.add(new Vec3d(0.0D, (double)y, 1.0D));
      if (floor) {
         offsets.add(new Vec3d(0.0D, (double)(y - 1), 0.0D));
      }

      return offsets;
   }

   public static Vec3d[] getOffsets(int y, boolean floor) {
      List<Vec3d> offsets = getOffsetList(y, floor);
      Vec3d[] array = new Vec3d[offsets.size()];
      return (Vec3d[])offsets.toArray(array);
   }

   public static List<Vec3d> getOffsetList(int y, boolean floor, boolean face) {
      ArrayList<Vec3d> offsets = new ArrayList();
      if (face) {
         offsets.add(new Vec3d(-1.0D, (double)y, 0.0D));
         offsets.add(new Vec3d(1.0D, (double)y, 0.0D));
         offsets.add(new Vec3d(0.0D, (double)y, -1.0D));
         offsets.add(new Vec3d(0.0D, (double)y, 1.0D));
      } else {
         offsets.add(new Vec3d(-1.0D, (double)y, 0.0D));
      }

      if (floor) {
         offsets.add(new Vec3d(0.0D, (double)(y - 1), 0.0D));
      }

      return offsets;
   }

   public static Vec3d interpolateEntity(Entity entity, float time) {
      return new Vec3d(entity.field_70142_S + (entity.field_70165_t - entity.field_70142_S) * (double)time, entity.field_70137_T + (entity.field_70163_u - entity.field_70137_T) * (double)time, entity.field_70136_U + (entity.field_70161_v - entity.field_70136_U) * (double)time);
   }

   public static Block isColliding(double posX, double posY, double posZ) {
      Block block = null;
      if (mc.field_71439_g != null) {
         AxisAlignedBB bb = mc.field_71439_g.func_184187_bx() != null ? mc.field_71439_g.func_184187_bx().func_174813_aQ().func_191195_a(0.0D, 0.0D, 0.0D).func_72317_d(posX, posY, posZ) : mc.field_71439_g.func_174813_aQ().func_191195_a(0.0D, 0.0D, 0.0D).func_72317_d(posX, posY, posZ);
         int y = (int)bb.field_72338_b;

         for(int x = MathHelper.func_76128_c(bb.field_72340_a); x < MathHelper.func_76128_c(bb.field_72336_d) + 1; ++x) {
            for(int z = MathHelper.func_76128_c(bb.field_72339_c); z < MathHelper.func_76128_c(bb.field_72334_f) + 1; ++z) {
               block = mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
            }
         }
      }

      return block;
   }

   public static boolean isPlayerValid(EntityPlayer player, float range) {
      return player != mc.field_71439_g && mc.field_71439_g.func_70032_d(player) < range && !player.field_70128_L && !SocialManager.isFriend(player.func_70005_c_());
   }

   public static boolean isInLiquid() {
      if (mc.field_71439_g == null) {
         return false;
      } else if (mc.field_71439_g.field_70143_R >= 3.0F) {
         return false;
      } else {
         boolean inLiquid = false;
         AxisAlignedBB bb = mc.field_71439_g.func_184187_bx() != null ? mc.field_71439_g.func_184187_bx().func_174813_aQ() : mc.field_71439_g.func_174813_aQ();
         int y = (int)bb.field_72338_b;

         for(int x = MathHelper.func_76128_c(bb.field_72340_a); x < MathHelper.func_76128_c(bb.field_72336_d) + 1; ++x) {
            for(int z = MathHelper.func_76128_c(bb.field_72339_c); z < MathHelper.func_76128_c(bb.field_72334_f) + 1; ++z) {
               Block block = mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
               if (!(block instanceof BlockAir)) {
                  if (!(block instanceof BlockLiquid)) {
                     return false;
                  }

                  inLiquid = true;
               }
            }
         }

         return inLiquid;
      }
   }

   public static void setTimer(float speed) {
      TimerUtils.setTickLength(50.0F / speed);
   }

   public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
      return getInterpolatedAmount(entity, ticks, ticks, ticks);
   }

   public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
      return (new Vec3d(entity.field_70142_S, entity.field_70137_T, entity.field_70136_U)).func_178787_e(getInterpolatedAmount(entity, (double)ticks));
   }

   public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
      return new Vec3d((entity.field_70165_t - entity.field_70142_S) * x, (entity.field_70163_u - entity.field_70137_T) * y, (entity.field_70161_v - entity.field_70136_U) * z);
   }

   public static float clamp(float val, float min, float max) {
      if (val <= min) {
         val = min;
      }

      if (val >= max) {
         val = max;
      }

      return val;
   }

   public static List<BlockPos> getSphere(BlockPos loc, Double r, Double h, boolean hollow, boolean sphere, int plus_y) {
      List<BlockPos> circleBlocks = new ArrayList();
      double cx = (double)loc.func_177958_n();
      double cy = (double)loc.func_177956_o();
      double cz = (double)loc.func_177952_p();

      for(double x = cx - r; x <= cx + r; ++x) {
         for(double z = cz - r; z <= cz + r; ++z) {
            for(double y = sphere ? cy - r : cy - h; y < (sphere ? cy + r : cy + h); ++y) {
               double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0.0D);
               if (dist < r * r && (!hollow || !(dist < (r - 1.0D) * (r - 1.0D)))) {
                  BlockPos l = new BlockPos(x, y + (double)plus_y, z);
                  circleBlocks.add(l);
               }
            }
         }
      }

      return circleBlocks;
   }

   public static List<BlockPos> getFlatSphere(BlockPos loc, Double r, Double h, boolean hollow, boolean sphere, int plus_y) {
      List<BlockPos> circleBlocks = new ArrayList();
      double cx = (double)loc.func_177958_n();
      double cy = (double)loc.func_177956_o();
      double cz = (double)loc.func_177952_p();

      for(double y = sphere ? cy - r : cy - h; y < (sphere ? cy + r : cy + h); ++y) {
         for(double x = cx - r; x <= cx + r; ++x) {
            for(double z = cz - r; z <= cz + r; ++z) {
               double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0.0D);
               if (dist < r * r && (!hollow || !(dist < (r - 1.0D) * (r - 1.0D)))) {
                  BlockPos l = new BlockPos(x, y + (double)plus_y, z);
                  circleBlocks.add(l);
               }
            }
         }
      }

      return circleBlocks;
   }

   public static List<BlockPos> getSquare(BlockPos pos1, BlockPos pos2) {
      List<BlockPos> squareBlocks = new ArrayList();
      int x1 = pos1.func_177958_n();
      int y1 = pos1.func_177956_o();
      int z1 = pos1.func_177952_p();
      int x2 = pos2.func_177958_n();
      int y2 = pos2.func_177956_o();
      int z2 = pos2.func_177952_p();

      for(int x = Math.min(x1, x2); x <= Math.max(x1, x2); ++x) {
         for(int z = Math.min(z1, z2); z <= Math.max(z1, z2); ++z) {
            for(int y = Math.min(y1, y2); y <= Math.max(y1, y2); ++y) {
               squareBlocks.add(new BlockPos(x, y, z));
            }
         }
      }

      return squareBlocks;
   }

   public static double[] calculateLookAt(double px, double py, double pz, Entity me) {
      double dirx = me.field_70165_t - px;
      double diry = me.field_70163_u - py;
      double dirz = me.field_70161_v - pz;
      double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
      dirx /= len;
      diry /= len;
      dirz /= len;
      double pitch = Math.asin(diry);
      double yaw = Math.atan2(dirz, dirx);
      pitch = pitch * 180.0D / 3.141592653589793D;
      yaw = yaw * 180.0D / 3.141592653589793D;
      yaw += 90.0D;
      return new double[]{yaw, pitch};
   }

   public static boolean basicChecksEntity(EntityPlayer pl) {
      return pl == null || pl.func_70005_c_().equals(mc.field_71439_g.func_70005_c_()) || SocialManager.isFriend(pl.func_70005_c_()) || pl.field_70128_L || pl.func_110143_aJ() + pl.func_110139_bj() <= 0.0F;
   }

   public static BlockPos getPosition(Entity pl) {
      return new BlockPos(Math.floor(pl.field_70165_t), Math.floor(pl.field_70163_u + 0.5D), Math.floor(pl.field_70161_v));
   }

   public static List<BlockPos> getBlocksIn(Entity pl) {
      List<BlockPos> blocks = new ArrayList();
      AxisAlignedBB bb = pl.func_174813_aQ();

      for(double x = Math.floor(bb.field_72340_a); x < Math.ceil(bb.field_72336_d); ++x) {
         for(double y = Math.floor(bb.field_72338_b); y < Math.ceil(bb.field_72337_e); ++y) {
            for(double z = Math.floor(bb.field_72339_c); z < Math.ceil(bb.field_72334_f); ++z) {
               blocks.add(new BlockPos(x, y, z));
            }
         }
      }

      return blocks;
   }

   public static boolean isMobAggressive(Entity entity) {
      if (entity instanceof EntityPigZombie) {
         if (((EntityPigZombie)entity).func_184734_db() || ((EntityPigZombie)entity).func_175457_ck()) {
            return true;
         }
      } else {
         if (entity instanceof EntityWolf) {
            return ((EntityWolf)entity).func_70919_bu() && !Wrapper.getPlayer().equals(((EntityWolf)entity).func_70902_q());
         }

         if (entity instanceof EntityEnderman) {
            return ((EntityEnderman)entity).func_70823_r();
         }
      }

      return isHostileMob(entity);
   }

   public static boolean isNeutralMob(Entity entity) {
      return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
   }

   public static boolean isFriendlyMob(Entity entity) {
      return entity.isCreatureType(EnumCreatureType.CREATURE, false) && !isNeutralMob(entity) || entity.isCreatureType(EnumCreatureType.AMBIENT, false) || entity instanceof EntityVillager || entity instanceof EntityIronGolem || isNeutralMob(entity) && !isMobAggressive(entity);
   }

   public static boolean isHostileMob(Entity entity) {
      return entity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(entity);
   }

   public static List<Vec3d> targets(Vec3d vec3d, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
      ArrayList<Vec3d> placeTargets = new ArrayList();
      if (antiDrop) {
         Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiDropOffsetList));
      }

      if (platform) {
         Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, platformOffsetList));
      }

      if (legs) {
         Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, legOffsetList));
      }

      Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, OffsetList));
      if (antiStep) {
         Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiStepOffsetList));
      } else {
         List<Vec3d> vec3ds = getUnsafeBlocksFromVec3d(vec3d, 2, false);
         if (vec3ds.size() == 4) {
            Iterator var9 = vec3ds.iterator();

            while(var9.hasNext()) {
               Vec3d vector = (Vec3d)var9.next();
               BlockPos position = (new BlockPos(vec3d)).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
               switch(BlockUtil.isPositionPlaceable(position, raytrace)) {
               case -1:
               case 1:
               case 2:
                  break;
               case 3:
                  placeTargets.add(vec3d.func_178787_e(vector));
               case 0:
               default:
                  if (antiScaffold) {
                     Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));
                  }

                  return placeTargets;
               }
            }
         }
      }

      if (antiScaffold) {
         Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));
      }

      return placeTargets;
   }

   public static boolean isTrapped(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      return getUntrappedBlocks(player, antiScaffold, antiStep, legs, platform, antiDrop).isEmpty();
   }

   public static boolean isTrappedExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
      return getUntrappedBlocksExtended(extension, player, antiScaffold, antiStep, legs, platform, antiDrop, raytrace).isEmpty();
   }

   public static List<Vec3d> getUntrappedBlocks(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      ArrayList<Vec3d> vec3ds = new ArrayList();
      if (!antiStep && getUnsafeBlocks(player, 2, false).size() == 4) {
         vec3ds.addAll(getUnsafeBlocks(player, 2, false));
      }

      for(int i = 0; i < getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop).length; ++i) {
         Vec3d vector = getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop)[i];
         BlockPos targetPos = (new BlockPos(player.func_174791_d())).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
         Block block = mc.field_71441_e.func_180495_p(targetPos).func_177230_c();
         if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
            vec3ds.add(vector);
         }
      }

      return vec3ds;
   }

   public static List<Vec3d> getUntrappedBlocksExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
      ArrayList<Vec3d> placeTargets = new ArrayList();
      Iterator var10;
      Vec3d vec3d;
      if (extension == 1) {
         placeTargets.addAll(targets(player.func_174791_d(), antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
      } else {
         int extend = 1;

         for(var10 = MathUtil.getBlockBlocks(player).iterator(); var10.hasNext(); ++extend) {
            vec3d = (Vec3d)var10.next();
            if (extend > extension) {
               break;
            }

            placeTargets.addAll(targets(vec3d, antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
         }
      }

      ArrayList<Vec3d> removeList = new ArrayList();
      var10 = placeTargets.iterator();

      while(var10.hasNext()) {
         vec3d = (Vec3d)var10.next();
         BlockPos pos = new BlockPos(vec3d);
         if (BlockUtil.isPositionPlaceable(pos, raytrace) == -1) {
            removeList.add(vec3d);
         }
      }

      var10 = removeList.iterator();

      while(var10.hasNext()) {
         vec3d = (Vec3d)var10.next();
         placeTargets.remove(vec3d);
      }

      return placeTargets;
   }

   public static Vec3d[] getTrapOffsets(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      List<Vec3d> offsets = getTrapOffsetsList(antiScaffold, antiStep, legs, platform, antiDrop);
      Vec3d[] array = new Vec3d[offsets.size()];
      return (Vec3d[])offsets.toArray(array);
   }

   public static List<Vec3d> getTrapOffsetsList(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      ArrayList<Vec3d> offsets = new ArrayList(getOffsetList(1, false));
      offsets.add(new Vec3d(0.0D, 2.0D, 0.0D));
      if (antiScaffold) {
         offsets.add(new Vec3d(0.0D, 3.0D, 0.0D));
      }

      if (antiStep) {
         offsets.addAll(getOffsetList(2, false));
      }

      if (legs) {
         offsets.addAll(getOffsetList(0, false));
      }

      if (platform) {
         offsets.addAll(getOffsetList(-1, false));
         offsets.add(new Vec3d(0.0D, -1.0D, 0.0D));
      }

      if (antiDrop) {
         offsets.add(new Vec3d(0.0D, -2.0D, 0.0D));
      }

      return offsets;
   }
}
