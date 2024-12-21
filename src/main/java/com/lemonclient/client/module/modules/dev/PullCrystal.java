package com.lemonclient.client.module.modules.dev;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.lemonclient.api.event.Phase;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.misc.CrystalUtil;
import com.lemonclient.api.util.misc.MathUtil;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerPacket;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.util.render.RenderUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.combat.DamageUtil;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.lemonclient.client.module.modules.qwq.AutoEz;
import com.lemonclient.mixin.mixins.accessor.AccessorCPacketVehicleMove;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "PullCrystal",
   category = Category.Dev
)
public class PullCrystal extends Module {
   public static PullCrystal INSTANCE;
   public boolean autoCrystal;
   ModeSetting page = this.registerMode("Page", Arrays.asList("Calc", "General", "Render"), "Calc");
   IntegerSetting maxTarget = this.registerInteger("Max Target", 1, 1, 10, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   DoubleSetting range = this.registerDouble("Range", 6.0D, 0.0D, 10.0D, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   IntegerSetting maxY = this.registerInteger("MaxY", 3, 1, 5, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   IntegerSetting delay = this.registerInteger("Delay", 20, 0, 100, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   IntegerSetting baseDelay = this.registerInteger("Base Delay", 0, 0, 100, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   IntegerSetting startBreakDelay = this.registerInteger("Start Break Delay", 0, 0, 100, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   IntegerSetting breakDelay = this.registerInteger("Break Delay", 0, 0, 100, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   BooleanSetting alwaysCalc = this.registerBoolean("Loop Calc", false, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   BooleanSetting pistonCheck = this.registerBoolean("Piston Check", false, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   BooleanSetting entityCheck = this.registerBoolean("Crystal Check", false, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   BooleanSetting base = this.registerBoolean("Base", true, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   BooleanSetting pushTarget = this.registerBoolean("Push Target", false, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   BooleanSetting fiveB = this.registerBoolean("5b Mode", false, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   BooleanSetting push = this.registerBoolean("Push To Block", false, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   BooleanSetting crystal = this.registerBoolean("Crystal Detect", false, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   BooleanSetting fire = this.registerBoolean("Fire", true, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   BooleanSetting different = this.registerBoolean("Different Pos", false, () -> {
      return ((String)this.page.getValue()).equals("Calc");
   });
   IntegerSetting maxPos = this.registerInteger("Max Pos", 10, 1, 25, () -> {
      return (Boolean)this.different.getValue() && ((String)this.page.getValue()).equals("Calc");
   });
   ModeSetting redstone = this.registerMode("Redstone", Arrays.asList("Block", "Torch", "Both"), "Block", () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting packetPlace = this.registerBoolean("Packet Place", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting packet = this.registerBoolean("Packet Crystal", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting packetBreak = this.registerBoolean("Packet Break", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting antiWeakness = this.registerBoolean("Anti Weakness", false, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting swingArm = this.registerBoolean("Swing Arm", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting silentSwitch = this.registerBoolean("Switch Back", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting crystalBypass = this.registerBoolean("Crystal Bypass", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting force = this.registerBoolean("Force Bypass", false, () -> {
      return (Boolean)this.crystalBypass.getValue() && ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting strict = this.registerBoolean("Strict", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting forceRotate = this.registerBoolean("Piston ForceRotate", false, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting rotate = this.registerBoolean("Rotate", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting pistonRotate = this.registerBoolean("Piston Rotate", true, () -> {
      return (Boolean)this.rotate.getValue() && ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting raytrace = this.registerBoolean("RayTrace", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting baseRaytrace = this.registerBoolean("Base RayTrace", true, () -> {
      return (Boolean)this.base.getValue() && (Boolean)this.raytrace.getValue() && ((String)this.page.getValue()).equals("General");
   });
   DoubleSetting forceRange = this.registerDouble("Force Range", 3.0D, 0.0D, 6.0D, () -> {
      return (Boolean)this.raytrace.getValue() && ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting pauseEat = this.registerBoolean("Pause When Eating", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting pause1 = this.registerBoolean("Pause When Burrow", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   DoubleSetting maxSelfSpeed = this.registerDouble("Max Self Speed", 10.0D, 0.0D, 50.0D, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   DoubleSetting maxTargetSpeed = this.registerDouble("Max Target Speed", 10.0D, 0.0D, 50.0D, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting bypass = this.registerBoolean("Bypass", false, () -> {
      return (Boolean)this.silentSwitch.getValue() && ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting dance = this.registerBoolean("Hotbar Dance (?", false, () -> {
      return (Boolean)this.silentSwitch.getValue() && (Boolean)this.bypass.getValue() && ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting render = this.registerBoolean("Render", false, () -> {
      return ((String)this.page.getValue()).equals("Render");
   });
   BooleanSetting fireRender = this.registerBoolean("Fire Render", false, () -> {
      return (Boolean)this.render.getValue() && ((String)this.page.getValue()).equals("Render");
   });
   BooleanSetting box = this.registerBoolean("Box", false, () -> {
      return (Boolean)this.render.getValue() && ((String)this.page.getValue()).equals("Render");
   });
   BooleanSetting outline = this.registerBoolean("Outline", false, () -> {
      return (Boolean)this.render.getValue() && ((String)this.page.getValue()).equals("Render");
   });
   BooleanSetting iq = this.registerBoolean("IQ", false, () -> {
      return (Boolean)this.render.getValue() && ((String)this.page.getValue()).equals("Render");
   });
   DoubleSetting speed = this.registerDouble("Speed", 0.5D, 0.01D, 1.0D, () -> {
      return (Boolean)this.render.getValue() && (Boolean)this.iq.getValue() && ((String)this.page.getValue()).equals("Render");
   });
   BooleanSetting hud = this.registerBoolean("HUD", false, () -> {
      return ((String)this.page.getValue()).equals("Render");
   });
   Vec3d movingPistonNow = new Vec3d(-1.0D, -1.0D, -1.0D);
   BlockPos lastBestPiston = null;
   Vec3d movingCrystalNow = new Vec3d(-1.0D, -1.0D, -1.0D);
   BlockPos lastBestCrystal = null;
   Vec3d movingRedstoneNow = new Vec3d(-1.0D, -1.0D, -1.0D);
   BlockPos lastBestRedstone = null;
   public static EntityPlayer target = null;
   public BlockPos targetPos;
   public BlockPos pistonPos;
   public BlockPos crystalPos;
   public BlockPos redStonePos;
   public BlockPos firePos;
   public BlockPos lastTargetPos;
   public int pistonSlot;
   public int crystalSlot;
   public int redStoneSlot;
   public int obbySlot = -1;
   public Timing timer = new Timing();
   public Timing baseTimer = new Timing();
   public Timing startBreakTimer = new Timing();
   public Timing breakTimer = new Timing();
   public boolean preparedSpace;
   public boolean placedPiston;
   public boolean placedCrystal;
   public boolean placedRedstone;
   public boolean brokeCrystal;
   int oldSlot;
   boolean useBlock;
   boolean boom;
   boolean burrowed;
   boolean moving;
   boolean first;
   Vec2f rotation;
   BlockPos[] saveArray = new BlockPos[25];
   Vec3d[] sides = new Vec3d[]{new Vec3d(0.24D, 0.0D, 0.24D), new Vec3d(-0.24D, 0.0D, 0.24D), new Vec3d(0.24D, 0.0D, -0.24D), new Vec3d(-0.24D, 0.0D, -0.24D)};
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && this.crystalPos != null) {
         if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
            if (packet.func_186977_b() == SoundCategory.BLOCKS && packet.func_186978_a() == SoundEvents.field_187539_bB && this.crystalPos.func_177954_c(packet.func_149207_d(), packet.func_149211_e(), packet.func_149210_f()) <= 9.0D) {
               this.boom = true;
            }
         }

      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.Send> sendListener = new Listener((event) -> {
      if (this.rotation != null) {
         if (event.getPacket() instanceof Rotation) {
            ((Rotation)event.getPacket()).field_149476_e = this.rotation.field_189982_i;
            ((Rotation)event.getPacket()).field_149473_f = 0.0F;
         }

         if (event.getPacket() instanceof PositionRotation) {
            ((PositionRotation)event.getPacket()).field_149476_e = this.rotation.field_189982_i;
            ((PositionRotation)event.getPacket()).field_149473_f = 0.0F;
         }

         if (event.getPacket() instanceof CPacketVehicleMove) {
            ((AccessorCPacketVehicleMove)event.getPacket()).setYaw(this.rotation.field_189982_i);
         }
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener((event) -> {
      if (this.rotation != null && event.getPhase() == Phase.PRE) {
         PlayerPacket packet = new PlayerPacket(this, new Vec2f(this.rotation.field_189982_i, 0.0F));
         PlayerPacketManager.INSTANCE.addPacket(packet);
      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.Receive> listener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
            if (packet.func_186977_b() == SoundCategory.BLOCKS && packet.func_186978_a() == SoundEvents.field_187539_bB) {
               Iterator var3 = (new ArrayList(mc.field_71441_e.field_72996_f)).iterator();

               while(var3.hasNext()) {
                  Entity crystal = (Entity)var3.next();
                  if (crystal instanceof EntityEnderCrystal && crystal.func_70011_f(packet.func_149207_d(), packet.func_149211_e(), packet.func_149210_f()) <= (Double)this.range.getValue() + 5.0D) {
                     crystal.func_70106_y();
                  }
               }
            }
         }

      }
   }, new Predicate[0]);

   public PullCrystal() {
      INSTANCE = this;
   }

   public void onEnable() {
      this.lastBestPiston = this.lastBestCrystal = this.lastBestRedstone = null;
      this.movingPistonNow = this.movingCrystalNow = this.movingRedstoneNow = new Vec3d(-1.0D, -1.0D, -1.0D);
      this.saveArray = new BlockPos[25];
      this.first = true;
      this.reset();
   }

   public void onTick() {
      if (!this.autoCrystal) {
         this.doPA();
      }
   }

   public void doPA() {
      this.moving = false;
      this.burrowed = false;
      BlockPos originalPos = PlayerUtil.getPlayerPos();
      Block block = BlockUtil.getBlock(originalPos);
      if (block == Blocks.field_150357_h || block == Blocks.field_150343_Z || block == Blocks.field_150477_bB) {
         this.burrowed = true;
      }

      if (!(Boolean)this.pause1.getValue() || !this.burrowed) {
         if (!(Boolean)this.pauseEat.getValue() || !EntityUtil.isEating()) {
            if (!(LemonClient.speedUtil.getPlayerSpeed(mc.field_71439_g) > (Double)this.maxSelfSpeed.getValue())) {
               this._doPA();
            }
         }
      }
   }

   public void _doPA() {
      if (!(Boolean)this.forceRotate.getValue()) {
         this.rotation = null;
      }

      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         try {
            if (this.findMaterials()) {
               if ((Boolean)this.alwaysCalc.getValue() || this.boom || target == null || !EntityUtil.isAlive(target)) {
                  PullCrystal.PistonAuraPos pos = this.findSpace();
                  if (pos == null) {
                     this.first = true;
                     target = null;
                     this.targetPos = this.pistonPos = this.redStonePos = this.crystalPos = null;
                     this.rotation = null;
                     return;
                  }

                  target = pos.target;
                  this.targetPos = pos.targetPos;
                  this.pistonPos = pos.piston;
                  this.redStonePos = pos.redstone;
                  this.crystalPos = pos.crystal;
               }

               if (this.targetPos != null && this.pistonPos != null && this.redStonePos != null && this.crystalPos != null) {
                  if (!(PlayerUtil.getDistanceI(this.pistonPos) > (Double)this.range.getValue()) && !(PlayerUtil.getDistanceI(this.redStonePos) > (Double)this.range.getValue()) && !(PlayerUtil.getDistanceI(this.crystalPos) > (Double)this.range.getValue())) {
                     AutoEz.INSTANCE.addTargetedPlayer(target.func_70005_c_());
                     this.lastTargetPos = new BlockPos(this.targetPos.func_177958_n(), this.crystalPos.func_177956_o() + 2, this.targetPos.func_177952_p());
                     this.oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
                     BlockPos offset = new BlockPos(this.crystalPos.func_177958_n() - this.targetPos.func_177958_n(), 0, this.crystalPos.func_177952_p() - this.targetPos.func_177952_p());
                     BlockPos headPos = this.pistonPos.func_177982_a(offset.func_177958_n(), 0, offset.func_177952_p());
                     Block block = BlockUtil.getBlock(headPos);
                     if (block == Blocks.field_150357_h || block == Blocks.field_150343_Z || block == Blocks.field_150477_bB || this.checkPos(headPos)) {
                        this.reset();
                     } else {
                        this.placedCrystal = this.getCrystal(this.crystalPos.func_177984_a()) != null && this.getCrystal(new BlockPos(this.targetPos.func_177958_n(), this.crystalPos.func_177956_o() + 2, this.targetPos.func_177952_p())) != null;
                        if (this.placedCrystal) {
                           this.placedPiston = this.placedRedstone = true;
                        } else {
                           Block piston = BlockUtil.getBlock(this.pistonPos);
                           this.placedPiston = piston instanceof BlockPistonBase;
                           this.placedRedstone = this.hasRedstone(this.pistonPos) || ColorMain.INSTANCE.breakList.contains(this.redStonePos);
                        }

                        if (this.breakTimer.passedDms((double)(Integer)this.breakDelay.getValue())) {
                           this.breakCrystal(this.placedCrystal);
                        }

                        float[] angle = MathUtil.calcAngle(new Vec3d(this.targetPos), new Vec3d(this.crystalPos));
                        this.rotation = new Vec2f(angle[0] + 180.0F, angle[1]);
                        if (!this.preparedSpace) {
                           this.preparedSpace = this.canPlace(this.pistonPos) || this.canPlace(this.redStonePos);
                           if (!this.preparedSpace) {
                              if (!(Boolean)this.base.getValue()) {
                                 this.preparedSpace = true;
                              } else if (this.baseTimer.passedDms((double)(Integer)this.baseDelay.getValue())) {
                                 this.baseTimer.reset();
                                 this.preparedSpace = this.prepareSpace();
                              }
                           }

                           this.timer.reset();
                        }

                        if (this.preparedSpace && this.first) {
                           if (!(Boolean)this.forceRotate.getValue()) {
                              this.timer.setMs(1000000000L);
                           }

                           this.first = false;
                        }

                        if (this.timer.passedDms((double)(Integer)this.delay.getValue())) {
                           this.timer.reset();
                           if (!this.placedPiston && !this.canPlace(this.pistonPos) && this.canPlace(this.redStonePos)) {
                              this.placeRedstone(this.preparedSpace && !this.placedRedstone);
                           }

                           this.placePiston(this.preparedSpace && !this.placedPiston);
                           this.placeRedstone(this.preparedSpace && !this.placedRedstone);
                           this.placeCrystal(!this.placedCrystal && block == Blocks.field_150332_K);
                        }

                        this.restoreItem();
                     }
                  } else {
                     this.lastTargetPos = null;
                     this.reset();
                  }
               } else {
                  if (this.breakTimer.passedDms((double)(Integer)this.breakDelay.getValue()) && this.lastTargetPos != null) {
                     if ((Boolean)this.packetBreak.getValue()) {
                        CrystalUtil.breakCrystalPacket(this.lastTargetPos, (Boolean)this.swingArm.getValue());
                     } else {
                        CrystalUtil.breakCrystal(this.lastTargetPos, (Boolean)this.swingArm.getValue());
                     }

                     this.breakTimer.reset();
                  }

                  this.reset();
               }
            }
         } catch (Exception var5) {
         }
      }
   }

   private void placePiston(boolean work) {
      if (work) {
         this.setItem(this.pistonSlot, false);
         mc.field_71439_g.field_71174_a.func_147297_a(new Rotation(this.rotation.field_189982_i, this.rotation.field_189983_j, true));
         this.placedPiston = this.placeBlock(this.pistonPos, (Boolean)this.packetPlace.getValue());
         if (!(Boolean)this.dance.getValue()) {
            this.setItem(this.pistonSlot, true);
         }

         this.startBreakTimer.reset();
         this.breakTimer.reset();
      }
   }

   private void placeCrystal(boolean work) {
      if (work) {
         EnumHand hand = this.crystalSlot != 999 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
         int slot;
         if ((Boolean)this.crystalBypass.getValue() && (this.crystalSlot >= 9 || (Boolean)this.force.getValue()) && hand == EnumHand.MAIN_HAND) {
            slot = this.crystalSlot;
            if (slot < 9) {
               slot += 36;
            }

            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketClickWindow(0, slot, mc.field_71439_g.field_71071_by.field_70461_c, ClickType.SWAP, ItemStack.field_190927_a, mc.field_71439_g.field_71069_bz.func_75136_a(mc.field_71439_g.field_71071_by)));
            this.placedCrystal = CrystalUtil.placeCrystal(this.crystalPos, hand, (Boolean)this.packet.getValue(), (Boolean)this.rotate.getValue(), (Boolean)this.swingArm.getValue());
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketClickWindow(0, slot, mc.field_71439_g.field_71071_by.field_70461_c, ClickType.SWAP, Items.field_185158_cP.func_190903_i(), mc.field_71439_g.field_71069_bz.func_75136_a(mc.field_71439_g.field_71071_by)));
         } else {
            this.setItem(this.crystalSlot, false);
            this.placedCrystal = CrystalUtil.placeCrystal(this.crystalPos, hand, (Boolean)this.packet.getValue(), (Boolean)this.rotate.getValue(), (Boolean)this.swingArm.getValue());
            if (!(Boolean)this.dance.getValue()) {
               this.setItem(this.crystalSlot, true);
            }
         }

         this.startBreakTimer.reset();
         this.breakTimer.reset();
         if (this.placedCrystal) {
            if ((Boolean)this.fire.getValue()) {
               slot = BurrowUtil.findHotbarBlock(Items.field_151033_d.getClass());
               if (slot != -1) {
                  this.setItem(slot, false);
                  this.firePos = this.crystalPos.func_177984_a();
                  this.placeBlock(this.firePos, (Boolean)this.packetPlace.getValue());
                  if (!(Boolean)this.dance.getValue()) {
                     this.setItem(slot, true);
                  }
               }
            }

            mc.field_71442_b.func_180512_c(this.redStonePos, EnumFacing.UP);
         }

      }
   }

   private void placeRedstone(boolean work) {
      if (work) {
         this.setItem(this.redStoneSlot, false);
         this.placedRedstone = BlockUtil.placeBlockBoolean(this.redStonePos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packetPlace.getValue(), (Boolean)this.strict.getValue(), this.needRaytrace(this.redStonePos), (Boolean)this.swingArm.getValue());
         if (!(Boolean)this.dance.getValue()) {
            this.setItem(this.redStoneSlot, true);
         }

         this.startBreakTimer.reset();
         this.breakTimer.reset();
      }
   }

   private void breakCrystal(boolean work) {
      if (work) {
         if (this.startBreakTimer.passedDms((double)(Integer)this.startBreakDelay.getValue())) {
            Entity crystal = (Entity)mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(this.crystalPos.func_177981_b(2))).stream().filter((e) -> {
               return e instanceof EntityEnderCrystal;
            }).min(Comparator.comparing((e) -> {
               return this.getDistance(target, e);
            })).orElse((Object)null);
            if (crystal != null) {
               this.breakTimer.reset();
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
                     this.setItem(newSlot, false);
                  }
               }

               if ((Boolean)this.packetBreak.getValue()) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketUseEntity(crystal));
               } else {
                  mc.field_71442_b.func_78764_a(mc.field_71439_g, crystal);
               }

               if ((Boolean)this.swingArm.getValue()) {
                  mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
               }

               if ((Boolean)this.silentSwitch.getValue()) {
                  this.setItem(oldSlot, false);
               }
            }

         }
      }
   }

   public boolean prepareSpace() {
      BlockPos piston = this.pistonPos.func_177982_a(0, -1, 0);
      if (this.isPos2(piston, this.redStonePos)) {
         piston = piston.func_177977_b();
      }

      BlockPos redstone = this.redStonePos.func_177982_a(0, -1, 0);
      if (!this.canPlace(this.pistonPos)) {
         if (this.intersectsWithEntity(this.pistonPos)) {
            this.reset();
         } else {
            this.setItem(this.obbySlot, false);
            if (this.canPlace(piston) && BlockUtil.canReplace(piston) && !this.isPos2(piston, this.redStonePos)) {
               if (this.intersectsWithEntity(piston)) {
                  this.reset();
               } else {
                  BlockUtil.placeBlock(piston, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packetPlace.getValue(), (Boolean)this.strict.getValue(), (Boolean)this.baseRaytrace.getValue(), (Boolean)this.swingArm.getValue());
               }
            } else {
               this.reset();
            }

            if (!(Boolean)this.dance.getValue()) {
               this.setItem(this.obbySlot, true);
            }
         }

         return false;
      } else if ((!this.canPlace(this.redStonePos) || !this.useBlock && this.redStonePos.func_177956_o() == this.pistonPos.func_177956_o()) && this.canPlace(redstone) && !this.isPos2(redstone, this.pistonPos)) {
         if (this.intersectsWithEntity(redstone)) {
            this.reset();
         } else {
            this.setItem(this.obbySlot, false);
            BlockUtil.placeBlock(redstone, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packetPlace.getValue(), (Boolean)this.strict.getValue(), (Boolean)this.baseRaytrace.getValue(), (Boolean)this.swingArm.getValue());
            if (!(Boolean)this.dance.getValue()) {
               this.setItem(this.obbySlot, true);
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public PullCrystal.PistonAuraPos findSpace() {
      List<PullCrystal.PistonAuraPos> list = new ArrayList();
      Iterator var2 = PlayerUtil.getNearPlayers((Double)this.range.getValue() + 4.0D, (Integer)this.maxTarget.getValue()).iterator();

      while(true) {
         EntityPlayer target;
         do {
            if (!var2.hasNext()) {
               PullCrystal.PistonAuraPos best = (PullCrystal.PistonAuraPos)list.stream().min(Comparator.comparing(PullCrystal.PistonAuraPos::range)).orElse((Object)null);
               if (best == null) {
                  this.saveArray = new BlockPos[25];
                  return null;
               }

               return best;
            }

            target = (EntityPlayer)var2.next();
         } while(LemonClient.speedUtil.getPlayerSpeed(target) > (Double)this.maxTargetSpeed.getValue());

         List<PullCrystal.PistonAuraPos> sideList = new ArrayList();
         Vec3d[] var5 = this.sides;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Vec3d vec3d = var5[var7];
            BlockPos targetPos = new BlockPos(target.field_70165_t + vec3d.field_72450_a, target.field_70163_u + 0.5D, target.field_70161_v + vec3d.field_72449_c);
            BlockPos cPos = null;
            Iterator var11 = mc.field_71441_e.field_72996_f.iterator();

            int y;
            while(var11.hasNext()) {
               Entity entity = (Entity)var11.next();
               if (entity instanceof EntityEnderCrystal) {
                  cPos = new BlockPos(entity.field_70165_t, entity.field_70163_u - 1.0D, entity.field_70161_v);
                  int x = Math.abs(cPos.func_177958_n() - targetPos.func_177958_n());
                  y = cPos.field_177960_b - targetPos.field_177960_b;
                  int z = Math.abs(cPos.func_177952_p() - targetPos.func_177952_p());
                  if (x <= 1 && y <= 5 && y >= 0 && z <= 1) {
                     break;
                  }

                  cPos = null;
               }
            }

            BlockPos[] offsets = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
            boolean calc = false;
            List<PullCrystal.PistonAuraPos> can = new ArrayList();

            for(y = 0; y <= (Integer)this.maxY.getValue(); ++y) {
               boolean cantPlace = false;
               boolean block = false;

               for(int high = y + 1; high >= 0; --high) {
                  BlockPos pos = targetPos.func_177981_b(high);
                  if (DamageUtil.isResistant(BlockUtil.getState(pos))) {
                     if (high < y + 1) {
                        cantPlace = true;
                     } else if (!(Boolean)this.push.getValue()) {
                        cantPlace = true;
                     } else {
                        block = true;
                     }
                  }
               }

               if (!cantPlace) {
                  BlockPos[] var45 = offsets;
                  int var46 = offsets.length;

                  for(int var19 = 0; var19 < var46; ++var19) {
                     BlockPos side = var45[var19];
                     if (!(Boolean)this.crystal.getValue()) {
                        cPos = null;
                     }

                     BlockPos offset = cPos == null ? side : new BlockPos(cPos.func_177958_n() - targetPos.func_177958_n(), 0, cPos.func_177952_p() - targetPos.func_177952_p());
                     if (cPos != null && this.isPos2(new BlockPos(-offset.func_177958_n(), 0, -offset.func_177952_p()), side)) {
                        cPos = null;
                     }

                     if (cPos == null) {
                        offset = side;
                     } else if (calc) {
                        continue;
                     }

                     BlockPos crystalPos = cPos == null ? targetPos.func_177982_a(offset.func_177958_n(), y, offset.func_177952_p()) : cPos;
                     if (cPos != null || (BlockUtil.getBlock(crystalPos) == Blocks.field_150343_Z || BlockUtil.getBlock(crystalPos) == Blocks.field_150357_h) && mc.field_71441_e.func_175623_d(crystalPos.func_177984_a()) && mc.field_71441_e.func_175623_d(crystalPos.func_177981_b(2)) && !(PlayerUtil.getDistanceI(crystalPos) > (Double)this.range.getValue()) && mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(crystalPos.func_177984_a())).isEmpty() && mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(crystalPos.func_177981_b(2))).isEmpty()) {
                        BlockPos normal = targetPos.func_177982_a(offset.func_177958_n() * -1, y, offset.func_177952_p() * -1);
                        BlockPos side0 = normal.func_177982_a(offset.func_177952_p(), 0, offset.func_177958_n());
                        BlockPos side1 = normal.func_177982_a(offset.func_177952_p() * -1, 0, offset.func_177958_n() * -1);
                        BlockPos side2 = side0.func_177971_a(offset);
                        BlockPos side3 = side1.func_177971_a(offset);
                        BlockPos side4 = side2.func_177971_a(offset);
                        BlockPos side5 = side3.func_177971_a(offset);
                        BlockPos side6 = side4.func_177971_a(offset);
                        BlockPos side7 = side5.func_177971_a(offset);
                        BlockPos side8 = crystalPos.func_177971_a(offset);
                        List<BlockPos> pistons = new ArrayList();
                        if ((Boolean)this.pushTarget.getValue()) {
                           this.add(pistons, normal);
                        }

                        this.add(pistons, side0);
                        this.add(pistons, side1);
                        this.add(pistons, side2);
                        this.add(pistons, side3);
                        this.add(pistons, side4);
                        this.add(pistons, side5);
                        if (!(Boolean)this.fire.getValue() || BurrowUtil.findHotbarBlock(Items.field_151033_d.getClass()) == -1) {
                           this.add(pistons, side6);
                           this.add(pistons, side7);
                           this.add(pistons, side8);
                        }

                        pistons.removeIf((p) -> {
                           if (!(Boolean)this.different.getValue()) {
                              return false;
                           } else {
                              boolean same = false;
                              BlockPos[] var3 = this.saveArray;
                              int var4 = var3.length;

                              for(int var5 = 0; var5 < var4; ++var5) {
                                 BlockPos savePos = var3[var5];
                                 if (this.isPos2(savePos, p)) {
                                    same = true;
                                    break;
                                 }
                              }

                              return same;
                           }
                        });
                        if (!pistons.isEmpty()) {
                           List<BlockPos> pistonList = (List)pistons.stream().filter((p) -> {
                              if ((Boolean)this.fiveB.getValue() && BlockUtil.getBlock(p.func_177982_a(offset.func_177958_n() * -1, 0, offset.func_177952_p() * -1)) == Blocks.field_150357_h) {
                                 return false;
                              } else {
                                 BlockPos headPos = p.func_177971_a(offset);
                                 if (!ColorMain.INSTANCE.breakList.contains(headPos) && !ColorMain.INSTANCE.breakList.contains(p)) {
                                    Block headBlock = BlockUtil.getBlock(headPos);
                                    if (headBlock != Blocks.field_150357_h && headBlock != Blocks.field_150343_Z && headBlock != Blocks.field_150477_bB && !this.checkPos(headPos)) {
                                       boolean isPiston = BlockUtil.getBlock(p) instanceof BlockPistonBase;
                                       if (isPiston) {
                                          if ((Boolean)this.pistonCheck.getValue() && !this.isFacing(p, headPos)) {
                                             return false;
                                          }
                                       } else {
                                          if (!this.canPlace(p)) {
                                             return false;
                                          }

                                          if (mc.field_71439_g.func_70011_f((double)p.func_177958_n() + 0.5D, (double)p.func_177956_o() + 0.5D, (double)p.func_177952_p() + 0.5D) > (Double)this.range.getValue()) {
                                             return false;
                                          }

                                          double feetY = mc.field_71439_g.field_70163_u;
                                          if (PlayerUtil.getDistanceI(p) < 0.8D + (double)p.func_177956_o() - feetY && (double)p.func_177956_o() > feetY + 1.0D || PlayerUtil.getDistanceI(p) < 1.8D + feetY - (double)p.func_177956_o() && (double)p.func_177956_o() < feetY) {
                                             return false;
                                          }
                                       }

                                       BlockPos redstonePos = this.getRedStonePos(crystalPos, p, offset);
                                       if (redstonePos == null) {
                                          return false;
                                       } else {
                                          return isPiston || BlockUtil.canPlaceWithoutBase(p, (Boolean)this.strict.getValue(), this.needRaytrace(p), ((Boolean)this.base.getValue() || this.canPlace(redstonePos.func_177977_b())) && this.obbySlot != -1 || this.canPlace(redstonePos) || BlockUtil.getBlock(p) instanceof BlockPistonBase);
                                       }
                                    } else {
                                       return false;
                                    }
                                 } else {
                                    return false;
                                 }
                              }
                           }).collect(Collectors.toList());
                           if (pistonList.isEmpty()) {
                              pistonList.addAll(pistons);
                           }

                           BlockPos piston = (BlockPos)pistonList.stream().min(Comparator.comparing(this::blockLevel)).orElse((Object)null);
                           PullCrystal.PistonAuraPos pos = new PullCrystal.PistonAuraPos(crystalPos, piston, this.getRedStonePos(crystalPos, piston, offset), offset, target, targetPos, block);
                           can.add(pos);
                           if (cPos != null) {
                              calc = true;
                           }
                        }
                     }
                  }
               }
            }

            List<PullCrystal.PistonAuraPos> paList = (List)can.stream().filter((p) -> {
               return !p.block || p.offset.field_177961_c == 1;
            }).collect(Collectors.toList());
            if (paList.isEmpty()) {
               paList.addAll(can);
            }

            PullCrystal.PistonAuraPos best = (PullCrystal.PistonAuraPos)paList.stream().min(Comparator.comparing(PullCrystal.PistonAuraPos::range)).orElse((Object)null);
            if (best != null) {
               sideList.add(best);
            }
         }

         if (!sideList.isEmpty()) {
            list.add(sideList.stream().min(Comparator.comparing(PullCrystal.PistonAuraPos::range)).orElse((Object)null));
         }
      }
   }

   public boolean isFacing(BlockPos pos, BlockPos facingPos) {
      ImmutableMap<IProperty<?>, Comparable<?>> properties = mc.field_71441_e.func_180495_p(pos).func_177228_b();
      UnmodifiableIterator var4 = properties.keySet().iterator();

      IProperty prop;
      do {
         do {
            if (!var4.hasNext()) {
               return false;
            }

            prop = (IProperty)var4.next();
         } while(prop.func_177699_b() != EnumFacing.class);
      } while(!prop.func_177701_a().equals("facing") && !prop.func_177701_a().equals("rotation"));

      BlockPos pushPos = pos.func_177972_a((EnumFacing)properties.get(prop));
      return this.isPos2(facingPos, pushPos);
   }

   public BlockPos getRedStonePos(BlockPos crystalPos, BlockPos pistonPos, BlockPos offset) {
      BlockPos pos = this.hasRedstoneBlock(pistonPos);
      if (pos != null) {
         return pos;
      } else {
         List<BlockPos> redstone = new ArrayList();
         BlockPos pistonPush = pistonPos.func_177982_a(offset.func_177958_n(), 0, offset.func_177952_p());
         int var9;
         if (this.useBlock) {
            EnumFacing[] var7 = EnumFacing.field_82609_l;
            int var8 = var7.length;

            for(var9 = 0; var9 < var8; ++var9) {
               EnumFacing facing = var7[var9];
               redstone.add(pistonPos.func_177972_a(facing));
            }
         } else {
            BlockPos[] offsets = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
            BlockPos[] var15 = offsets;
            var9 = offsets.length;

            for(int var16 = 0; var16 < var9; ++var16) {
               BlockPos offs = var15[var16];

               for(int i = 0; i < 2; ++i) {
                  BlockPos torch = pistonPos.func_177979_c(i).func_177971_a(offs);
                  if (i != 1 || !BlockUtil.isBlockUnSolid(torch.func_177984_a())) {
                     redstone.add(torch);
                  }
               }
            }
         }

         return (BlockPos)redstone.stream().filter((p) -> {
            return !ColorMain.INSTANCE.breakList.contains(p) && (p.func_177958_n() != crystalPos.func_177958_n() || p.func_177952_p() != crystalPos.func_177952_p()) && (p.func_177958_n() != pistonPush.func_177958_n() || p.func_177952_p() != pistonPush.func_177952_p()) && mc.field_71439_g.func_70011_f((double)p.func_177958_n() + 0.5D, (double)p.func_177956_o() + 0.5D, (double)p.func_177952_p() + 0.5D) <= (Double)this.range.getValue() && BlockUtil.canPlaceWithoutBase(p, (Boolean)this.strict.getValue(), this.needRaytrace(p), (Boolean)this.base.getValue());
         }).min(Comparator.comparing(this::blockLevel)).orElse((Object)null);
      }
   }

   public boolean hasRedstone(BlockPos pos) {
      return this.hasRedstoneBlock(pos) != null;
   }

   public BlockPos hasRedstoneBlock(BlockPos pos) {
      List<BlockPos> redstone = new ArrayList();
      BlockPos[] offsets = new BlockPos[]{new BlockPos(0, -1, 0), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
      BlockPos[] var4 = offsets;
      int var5 = offsets.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         BlockPos redstonePos = var4[var6];
         redstone.add(pos.func_177971_a(redstonePos));
      }

      if (this.useBlock) {
         redstone.add(pos.func_177982_a(0, 1, 0));
      }

      return (BlockPos)redstone.stream().filter((p) -> {
         return BlockUtil.getBlock(p) == Blocks.field_150429_aA || BlockUtil.getBlock(p) == Blocks.field_150451_bX;
      }).min(Comparator.comparing(PlayerUtil::getDistanceI)).orElse((Object)null);
   }

   private boolean isPos2(BlockPos pos1, BlockPos pos2) {
      if (pos1 != null && pos2 != null) {
         return pos1.field_177962_a == pos2.field_177962_a && pos1.field_177960_b == pos2.field_177960_b && pos1.field_177961_c == pos2.field_177961_c;
      } else {
         return false;
      }
   }

   private double getDistance(EntityPlayer player, Entity entity) {
      double x = player.field_70165_t - entity.field_70165_t;
      double z = player.field_70161_v - entity.field_70161_v;
      return Math.sqrt(x * x + z * z);
   }

   private boolean canPlace(BlockPos pos) {
      return BlockUtil.getFirstFacing(pos, (Boolean)this.strict.getValue(), this.needRaytrace(pos)) != null && !this.intersectsWithEntity(pos);
   }

   private Entity getCrystal(BlockPos pos) {
      Iterator var2 = mc.field_71441_e.field_72996_f.iterator();

      Entity entity;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         entity = (Entity)var2.next();
      } while(!(entity instanceof EntityEnderCrystal) || !(new AxisAlignedBB(pos)).func_72326_a(entity.func_174813_aQ()));

      return entity;
   }

   private boolean intersectsWithEntity(BlockPos pos) {
      Iterator var2 = mc.field_71441_e.field_72996_f.iterator();

      Entity entity;
      do {
         do {
            do {
               do {
                  do {
                     do {
                        do {
                           if (!var2.hasNext()) {
                              return false;
                           }

                           entity = (Entity)var2.next();
                        } while(entity.field_70128_L);
                     } while(entity instanceof EntityItem);
                  } while(entity instanceof EntityXPOrb);
               } while(entity instanceof EntityExpBottle);
            } while(entity instanceof EntityArrow);
         } while(!(Boolean)this.entityCheck.getValue() && entity instanceof EntityEnderCrystal);
      } while(!(new AxisAlignedBB(pos)).func_72326_a(entity.func_174813_aQ()));

      return true;
   }

   public void add(List<BlockPos> pistons, BlockPos pos) {
      pistons.add(pos.func_177982_a(0, 1, 0));
      pistons.add(pos.func_177982_a(0, 2, 0));
   }

   public static int findHotbarBlock(Block blockIn) {
      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock && ((ItemBlock)stack.func_77973_b()).func_179223_d() == blockIn) {
            return i;
         }
      }

      return -1;
   }

   private int getItemHotbar() {
      for(int i = 0; i < ((Boolean)this.crystalBypass.getValue() ? 36 : 9); ++i) {
         Item item = mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
         if (Item.func_150891_b(item) == Item.func_150891_b(Items.field_185158_cP)) {
            return i;
         }
      }

      return -1;
   }

   public boolean findMaterials() {
      this.pistonSlot = findHotbarBlock(Blocks.field_150331_J);
      this.obbySlot = findHotbarBlock(Blocks.field_150343_Z);
      this.crystalSlot = this.getItemHotbar();
      if (this.pistonSlot == -1) {
         this.pistonSlot = findHotbarBlock(Blocks.field_150320_F);
      }

      if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP) {
         this.crystalSlot = 999;
      }

      int block = findHotbarBlock(Blocks.field_150451_bX);
      int torch = findHotbarBlock(Blocks.field_150429_aA);
      if (((String)this.redstone.getValue()).equals("Block")) {
         this.redStoneSlot = block;
      }

      if (((String)this.redstone.getValue()).equals("Torch")) {
         this.redStoneSlot = torch;
      }

      if (((String)this.redstone.getValue()).equals("Both")) {
         if (block != -1) {
            this.redStoneSlot = block;
         } else {
            this.redStoneSlot = torch;
         }
      }

      this.useBlock = this.redStoneSlot == block;
      return this.pistonSlot != -1 && this.crystalSlot != -1 && this.redStoneSlot != -1;
   }

   private void reset() {
      int i;
      for(i = this.saveArray.length - 1; i > 0; --i) {
         this.saveArray[i] = this.saveArray[i - 1];
      }

      if (this.pistonPos != null) {
         this.saveArray[0] = this.pistonPos;
      }

      for(i = 0; i < this.saveArray.length; ++i) {
         if (i >= (Integer)this.maxPos.getValue()) {
            this.saveArray[i] = null;
         }
      }

      if (!(Boolean)this.different.getValue()) {
         this.saveArray = new BlockPos[25];
      }

      target = null;
      this.targetPos = null;
      this.pistonPos = null;
      this.crystalPos = null;
      this.redStonePos = null;
      this.firePos = null;
      this.pistonSlot = -1;
      this.crystalSlot = -1;
      this.redStoneSlot = -1;
      this.obbySlot = -1;
      this.baseTimer = new Timing();
      this.timer = new Timing();
      this.startBreakTimer = new Timing();
      this.breakTimer = new Timing();
      this.preparedSpace = false;
      this.placedPiston = false;
      this.placedCrystal = false;
      this.placedRedstone = false;
      this.brokeCrystal = false;
      this.boom = false;
   }

   public boolean checkPos(BlockPos pos) {
      BlockPos myPos = PlayerUtil.getPlayerPos();
      return pos.func_177958_n() == myPos.func_177958_n() && pos.func_177952_p() == myPos.func_177952_p() && (myPos.func_177956_o() == pos.func_177956_o() || myPos.func_177956_o() + 1 == pos.func_177956_o());
   }

   public void setItem(int slot, boolean back) {
      if (slot != 999) {
         if ((Boolean)this.bypass.getValue()) {
            this.bypassSwitch(slot);
         } else if (!back) {
            this.normalSwitch(slot);
         }

      }
   }

   private void normalSwitch(int slot) {
      if ((Boolean)this.packetSwitch.getValue()) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
      } else {
         mc.field_71439_g.field_71071_by.field_70461_c = slot;
      }

   }

   private void bypassSwitch(int slot) {
      mc.field_71442_b.func_187098_a(0, slot + 36, mc.field_71439_g.field_71071_by.field_70461_c, ClickType.SWAP, mc.field_71439_g);
   }

   public void restoreItem() {
      if ((Boolean)this.silentSwitch.getValue() && !(Boolean)this.bypass.getValue()) {
         if ((Boolean)this.packetSwitch.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(this.oldSlot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
            mc.field_71442_b.func_78765_e();
         }
      }

   }

   private boolean placeBlock(BlockPos pos, boolean packet) {
      if (!BlockUtil.canReplace(pos)) {
         return false;
      } else {
         EnumFacing side = BlockUtil.getFirstFacing(pos, (Boolean)this.strict.getValue(), this.needRaytrace(pos));
         if (side == null) {
            return false;
         } else {
            BlockPos neighbour = pos.func_177972_a(side);
            EnumFacing opposite = side.func_176734_d();
            if (!BlockUtil.canBeClicked(neighbour)) {
               return false;
            } else {
               Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
               boolean sneak = false;
               if (!ColorMain.INSTANCE.sneaking && BlockUtil.blackList.contains(BlockUtil.getBlock(neighbour))) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
                  mc.field_71439_g.func_70095_a(true);
                  sneak = true;
               }

               if (packet) {
                  rightClickBlock(neighbour, hitVec, EnumHand.MAIN_HAND, opposite);
               } else {
                  mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
               }

               if ((Boolean)this.swingArm.getValue()) {
                  mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
               }

               if ((Boolean)this.rotate.getValue() && (Boolean)this.pistonRotate.getValue()) {
                  BlockUtil.faceVector(hitVec);
               }

               if (sneak) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
                  mc.field_71439_g.func_70095_a(false);
               }

               return true;
            }
         }
      }
   }

   public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction) {
      float f = (float)(vec.field_72450_a - (double)pos.func_177958_n());
      float f1 = (float)(vec.field_72448_b - (double)pos.func_177956_o());
      float f2 = (float)(vec.field_72449_c - (double)pos.func_177952_p());
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
   }

   private int blockLevel(BlockPos pos) {
      return pos.func_177956_o() * 10000;
   }

   private boolean needRaytrace(BlockPos pos) {
      return mc.field_71439_g.func_70011_f((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D) > (Double)this.forceRange.getValue() && (Boolean)this.raytrace.getValue();
   }

   public void onWorldRender(RenderEvent event) {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         if ((Boolean)this.render.getValue()) {
            if (this.firePos != null && (Boolean)this.fireRender.getValue()) {
               this.drawBoxMain((double)this.firePos.field_177962_a, (double)this.firePos.field_177960_b, (double)this.firePos.field_177961_c, 255, 160, 0);
            }

            this.lastBestPiston = this.pistonPos;
            this.lastBestCrystal = this.crystalPos;
            this.lastBestRedstone = this.redStonePos;
            if ((Boolean)this.iq.getValue()) {
               if (this.lastBestPiston != null) {
                  if (this.movingPistonNow.field_72450_a == -1.0D && this.movingPistonNow.field_72448_b == -1.0D && this.movingPistonNow.field_72449_c == -1.0D) {
                     this.movingPistonNow = new Vec3d((double)((float)this.lastBestPiston.func_177958_n()), (double)((float)this.lastBestPiston.func_177956_o()), (double)((float)this.lastBestPiston.func_177952_p()));
                  }

                  this.movingPistonNow = new Vec3d(this.movingPistonNow.field_72450_a + ((double)this.lastBestPiston.func_177958_n() - this.movingPistonNow.field_72450_a) * (double)((Double)this.speed.getValue()).floatValue(), this.movingPistonNow.field_72448_b + ((double)this.lastBestPiston.func_177956_o() - this.movingPistonNow.field_72448_b) * (double)((Double)this.speed.getValue()).floatValue(), this.movingPistonNow.field_72449_c + ((double)this.lastBestPiston.func_177952_p() - this.movingPistonNow.field_72449_c) * (double)((Double)this.speed.getValue()).floatValue());
                  this.drawBoxMain(this.movingPistonNow.field_72450_a, this.movingPistonNow.field_72448_b, this.movingPistonNow.field_72449_c, 255, 255, 150);
                  if (Math.abs(this.movingPistonNow.field_72450_a - (double)this.lastBestPiston.func_177958_n()) <= 0.125D && Math.abs(this.movingPistonNow.field_72448_b - (double)this.lastBestPiston.func_177956_o()) <= 0.125D && Math.abs(this.movingPistonNow.field_72449_c - (double)this.lastBestPiston.func_177952_p()) <= 0.125D) {
                     this.lastBestPiston = null;
                  }
               }

               if (this.lastBestCrystal != null) {
                  if (this.movingCrystalNow.field_72450_a == -1.0D && this.movingCrystalNow.field_72448_b == -1.0D && this.movingCrystalNow.field_72449_c == -1.0D) {
                     this.movingCrystalNow = new Vec3d((double)((float)this.lastBestCrystal.func_177958_n()), (double)((float)this.lastBestCrystal.func_177956_o()), (double)((float)this.lastBestCrystal.func_177952_p()));
                  }

                  this.movingCrystalNow = new Vec3d(this.movingCrystalNow.field_72450_a + ((double)this.lastBestCrystal.func_177958_n() - this.movingCrystalNow.field_72450_a) * (double)((Double)this.speed.getValue()).floatValue(), this.movingCrystalNow.field_72448_b + ((double)this.lastBestCrystal.func_177956_o() - this.movingCrystalNow.field_72448_b) * (double)((Double)this.speed.getValue()).floatValue(), this.movingCrystalNow.field_72449_c + ((double)this.lastBestCrystal.func_177952_p() - this.movingCrystalNow.field_72449_c) * (double)((Double)this.speed.getValue()).floatValue());
                  this.drawBoxMain(this.movingCrystalNow.field_72450_a, this.movingCrystalNow.field_72448_b, this.movingCrystalNow.field_72449_c, 255, 255, 255);
                  if (Math.abs(this.movingCrystalNow.field_72450_a - (double)this.lastBestCrystal.func_177958_n()) <= 0.125D && Math.abs(this.movingCrystalNow.field_72448_b - (double)this.lastBestCrystal.func_177956_o()) <= 0.125D && Math.abs(this.movingCrystalNow.field_72449_c - (double)this.lastBestCrystal.func_177952_p()) <= 0.125D) {
                     this.lastBestCrystal = null;
                  }
               }

               if (this.lastBestRedstone != null) {
                  if (this.movingRedstoneNow.field_72450_a == -1.0D && this.movingRedstoneNow.field_72448_b == -1.0D && this.movingRedstoneNow.field_72449_c == -1.0D) {
                     this.movingRedstoneNow = new Vec3d((double)((float)this.lastBestRedstone.func_177958_n()), (double)((float)this.lastBestRedstone.func_177956_o()), (double)((float)this.lastBestRedstone.func_177952_p()));
                  }

                  this.movingRedstoneNow = new Vec3d(this.movingRedstoneNow.field_72450_a + ((double)this.lastBestRedstone.func_177958_n() - this.movingRedstoneNow.field_72450_a) * (double)((Double)this.speed.getValue()).floatValue(), this.movingRedstoneNow.field_72448_b + ((double)this.lastBestRedstone.func_177956_o() - this.movingRedstoneNow.field_72448_b) * (double)((Double)this.speed.getValue()).floatValue(), this.movingRedstoneNow.field_72449_c + ((double)this.lastBestRedstone.func_177952_p() - this.movingRedstoneNow.field_72449_c) * (double)((Double)this.speed.getValue()).floatValue());
                  this.drawBoxMain(this.movingRedstoneNow.field_72450_a, this.movingRedstoneNow.field_72448_b, this.movingRedstoneNow.field_72449_c, 225, 50, 50);
                  if (Math.abs(this.movingRedstoneNow.field_72450_a - (double)this.lastBestRedstone.func_177958_n()) <= 0.125D && Math.abs(this.movingRedstoneNow.field_72448_b - (double)this.lastBestRedstone.func_177956_o()) <= 0.125D && Math.abs(this.movingRedstoneNow.field_72449_c - (double)this.lastBestRedstone.func_177952_p()) <= 0.125D) {
                     this.lastBestRedstone = null;
                  }
               }
            } else if (this.pistonPos != null && this.crystalPos != null && this.redStonePos != null) {
               this.drawBoxMain((double)this.pistonPos.field_177962_a, (double)this.pistonPos.field_177960_b, (double)this.pistonPos.field_177961_c, 255, 255, 150);
               this.drawBoxMain((double)this.crystalPos.field_177962_a, (double)this.crystalPos.field_177960_b, (double)this.crystalPos.field_177961_c, 255, 255, 255);
               this.drawBoxMain((double)this.redStonePos.field_177962_a, (double)this.redStonePos.field_177960_b, (double)this.redStonePos.field_177961_c, 225, 50, 50);
            }
         }

      }
   }

   void drawBoxMain(double x, double y, double z, int r, int g, int b) {
      AxisAlignedBB box = this.getBox(x, y, z);
      if ((Boolean)this.box.getValue()) {
         RenderUtil.drawBox(box, false, 1.0D, new GSColor(r, g, b, 25), 63);
      }

      if ((Boolean)this.outline.getValue()) {
         RenderUtil.drawBoundingBox(box, 1.0D, new GSColor(r, g, b, 255));
      }

   }

   AxisAlignedBB getBox(double x, double y, double z) {
      double maxX = x + 1.0D;
      double maxZ = z + 1.0D;
      return new AxisAlignedBB(x, y, z, maxX, y + 1.0D, maxZ);
   }

   public String getHudInfo() {
      return (Boolean)this.hud.getValue() && target != null ? "[" + ChatFormatting.WHITE + target.func_70005_c_() + ChatFormatting.GRAY + "]" : "";
   }

   public static class PistonAuraPos {
      public BlockPos targetPos;
      public BlockPos crystal;
      public BlockPos piston;
      public BlockPos redstone;
      public BlockPos offset;
      EntityPlayer target;
      boolean block;

      public PistonAuraPos(BlockPos crystal, BlockPos piston, BlockPos redstone, BlockPos offset, EntityPlayer target, BlockPos targetPos, boolean block) {
         this.crystal = crystal;
         this.piston = piston;
         this.redstone = redstone;
         this.offset = offset;
         this.targetPos = targetPos;
         this.target = target;
         this.block = block;
      }

      public double range() {
         double crystalRange = PlayerUtil.getDistanceL(this.crystal);
         double pistonRange = PlayerUtil.getDistanceL(this.piston);
         return Math.max(pistonRange, crystalRange);
      }
   }
}
