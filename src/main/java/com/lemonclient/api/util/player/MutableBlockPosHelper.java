package com.lemonclient.api.util.player;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;

public class MutableBlockPosHelper {
   public MutableBlockPos mutablePos = new MutableBlockPos();

   public static MutableBlockPos set(MutableBlockPos mutablePos, double x, double y, double z) {
      return mutablePos.func_189532_c(x, y, z);
   }

   public static MutableBlockPos set(MutableBlockPos mutablePos, BlockPos pos) {
      return mutablePos.func_181079_c(pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p());
   }

   public static MutableBlockPos set(MutableBlockPos mutablePos, BlockPos pos, double x, double y, double z) {
      return mutablePos.func_189532_c((double)pos.func_177958_n() + x, (double)pos.func_177956_o() + y, (double)pos.func_177952_p() + z);
   }

   public static MutableBlockPos set(MutableBlockPos mutablePos, BlockPos pos, int x, int y, int z) {
      return mutablePos.func_181079_c(pos.func_177958_n() + x, pos.func_177956_o() + y, pos.func_177952_p() + z);
   }

   public static MutableBlockPos set(MutableBlockPos mutablePos, int x, int y, int z) {
      return mutablePos.func_181079_c(x, y, z);
   }

   public static MutableBlockPos setAndAdd(MutableBlockPos mutablePos, int x, int y, int z) {
      return mutablePos.func_181079_c(mutablePos.func_177958_n() + x, mutablePos.func_177956_o() + y, mutablePos.func_177952_p() + z);
   }

   public static MutableBlockPos setAndAdd(MutableBlockPos mutablePos, double x, double y, double z) {
      return mutablePos.func_189532_c((double)mutablePos.func_177958_n() + x, (double)mutablePos.func_177956_o() + y, (double)mutablePos.func_177952_p() + z);
   }

   public static MutableBlockPos setAndAdd(MutableBlockPos mutablePos, BlockPos pos) {
      return mutablePos.func_181079_c(mutablePos.func_177958_n() + pos.func_177958_n(), mutablePos.func_177956_o() + pos.func_177956_o(), mutablePos.func_177952_p() + pos.func_177952_p());
   }

   public static MutableBlockPos setAndAdd(MutableBlockPos mutablePos, BlockPos pos, double x, double y, double z) {
      return mutablePos.func_189532_c((double)(mutablePos.func_177958_n() + pos.func_177958_n()) + x, (double)(mutablePos.func_177956_o() + pos.func_177956_o()) + y, (double)(mutablePos.func_177952_p() + pos.func_177952_p()) + z);
   }

   public MutableBlockPos set(double x, double y, double z) {
      return this.mutablePos.func_189532_c(x, y, z);
   }

   public MutableBlockPos set(BlockPos pos) {
      return this.mutablePos.func_181079_c(pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p());
   }

   public MutableBlockPos set(BlockPos pos, double x, double y, double z) {
      return this.mutablePos.func_189532_c((double)pos.func_177958_n() + x, (double)pos.func_177956_o() + y, (double)pos.func_177952_p() + z);
   }

   public MutableBlockPos set(BlockPos pos, int x, int y, int z) {
      return this.mutablePos.func_181079_c(pos.func_177958_n() + x, pos.func_177956_o() + y, pos.func_177952_p() + z);
   }

   public MutableBlockPos set(int x, int y, int z) {
      return this.mutablePos.func_181079_c(x, y, z);
   }

   public MutableBlockPos setAndAdd(int x, int y, int z) {
      return this.mutablePos.func_181079_c(this.mutablePos.func_177958_n() + x, this.mutablePos.func_177956_o() + y, this.mutablePos.func_177952_p() + z);
   }

   public MutableBlockPos setAndAdd(double x, double y, double z) {
      return this.mutablePos.func_189532_c((double)this.mutablePos.func_177958_n() + x, (double)this.mutablePos.func_177956_o() + y, (double)this.mutablePos.func_177952_p() + z);
   }

   public MutableBlockPos setAndAdd(BlockPos pos) {
      return this.mutablePos.func_181079_c(this.mutablePos.func_177958_n() + pos.func_177958_n(), this.mutablePos.func_177956_o() + pos.func_177956_o(), this.mutablePos.func_177952_p() + pos.func_177952_p());
   }

   public MutableBlockPos setAndAdd(BlockPos pos, double x, double y, double z) {
      return this.mutablePos.func_189532_c((double)(this.mutablePos.func_177958_n() + pos.func_177958_n()) + x, (double)(this.mutablePos.func_177956_o() + pos.func_177956_o()) + y, (double)(this.mutablePos.func_177952_p() + pos.func_177952_p()) + z);
   }
}
