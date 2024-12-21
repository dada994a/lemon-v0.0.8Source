package com.lemonclient.client.module.modules.dev;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.TotemPopEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.api.util.player.RotationUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.combat.DamageUtil;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.GameType;

@Module.Declaration(
   name = "FakePlayerDev",
   category = Category.Dev
)
public class FakePlayerDev extends Module {
   private final ItemStack[] armors;
   StringSetting nameFakePlayer;
   BooleanSetting copyInventory;
   BooleanSetting playerStacked;
   BooleanSetting onShift;
   BooleanSetting simulateDamage;
   IntegerSetting vulnerabilityTick;
   IntegerSetting resetHealth;
   IntegerSetting tickRegenVal;
   IntegerSetting startHealth;
   ModeSetting moving;
   DoubleSetting speed;
   DoubleSetting range;
   BooleanSetting followPlayer;
   BooleanSetting resistance;
   BooleanSetting pop;
   int incr;
   boolean beforePressed;
   ArrayList<FakePlayerDev.playerInfo> listPlayers;
   FakePlayerDev.movingManager manager;
   @EventHandler
   private final Listener<PacketEvent.Receive> packetReceiveListener;

   public FakePlayerDev() {
      this.armors = new ItemStack[]{new ItemStack(Items.field_151175_af), new ItemStack(Items.field_151173_ae), new ItemStack(Items.field_151163_ad), new ItemStack(Items.field_151161_ac)};
      this.nameFakePlayer = this.registerString("Name FakePlayer", "NotLazyOfLazys");
      this.copyInventory = this.registerBoolean("Copy Inventory", false);
      this.playerStacked = this.registerBoolean("Player Stacked", false);
      this.onShift = this.registerBoolean("On Shift", false);
      this.simulateDamage = this.registerBoolean("Simulate Damage", false);
      this.vulnerabilityTick = this.registerInteger("Vulnerability Tick", 4, 0, 10);
      this.resetHealth = this.registerInteger("Reset Health", 10, 0, 36);
      this.tickRegenVal = this.registerInteger("Tick Regen", 4, 0, 30);
      this.startHealth = this.registerInteger("Start Health", 20, 0, 30);
      this.moving = this.registerMode("Moving", Arrays.asList("None", "Line", "Circle", "Random"), "None");
      this.speed = this.registerDouble("Speed", 0.36D, 0.0D, 4.0D);
      this.range = this.registerDouble("Range", 3.0D, 0.0D, 14.0D);
      this.followPlayer = this.registerBoolean("Follow Player", true);
      this.resistance = this.registerBoolean("Resistance", true);
      this.pop = this.registerBoolean("Pop", true);
      this.listPlayers = new ArrayList();
      this.manager = new FakePlayerDev.movingManager();
      this.packetReceiveListener = new Listener((event) -> {
         if ((Boolean)this.simulateDamage.getValue()) {
            Packet<?> packet = event.getPacket();
            if (packet instanceof SPacketSoundEffect) {
               SPacketSoundEffect packetSoundEffect = (SPacketSoundEffect)packet;
               if (packetSoundEffect.func_186977_b() == SoundCategory.BLOCKS && packetSoundEffect.func_186978_a() == SoundEvents.field_187539_bB) {
                  Iterator var4 = (new ArrayList(mc.field_71441_e.field_72996_f)).iterator();

                  while(true) {
                     Entity entity;
                     do {
                        do {
                           if (!var4.hasNext()) {
                              return;
                           }

                           entity = (Entity)var4.next();
                        } while(!(entity instanceof EntityEnderCrystal));
                     } while(!(entity.func_70092_e(packetSoundEffect.func_149207_d(), packetSoundEffect.func_149211_e(), packetSoundEffect.func_149210_f()) <= 36.0D));

                     Iterator var6 = mc.field_71441_e.field_73010_i.iterator();

                     while(var6.hasNext()) {
                        EntityPlayer entityPlayer = (EntityPlayer)var6.next();
                        if (entityPlayer.func_70005_c_().split(this.nameFakePlayer.getText()).length == 2) {
                           Optional<FakePlayerDev.playerInfo> temp = this.listPlayers.stream().filter((e) -> {
                              return e.name.equals(entityPlayer.func_70005_c_());
                           }).findAny();
                           if (temp.isPresent() && ((FakePlayerDev.playerInfo)temp.get()).canPop()) {
                              float damage = DamageUtil.calculateDamage(entityPlayer, packetSoundEffect.func_149207_d(), packetSoundEffect.func_149211_e(), packetSoundEffect.func_149210_f(), 6.0F, "Default");
                              if (damage > entityPlayer.func_110143_aJ()) {
                                 entityPlayer.func_70606_j((float)(Integer)this.resetHealth.getValue());
                                 if ((Boolean)this.pop.getValue()) {
                                    mc.field_71452_i.func_191271_a(entityPlayer, EnumParticleTypes.TOTEM, 30);
                                    mc.field_71441_e.func_184134_a(entityPlayer.field_70165_t, entityPlayer.field_70163_u, entityPlayer.field_70161_v, SoundEvents.field_191263_gW, entity.func_184176_by(), 1.0F, 1.0F, false);
                                 }

                                 LemonClient.EVENT_BUS.post(new TotemPopEvent(entityPlayer));
                              } else {
                                 entityPlayer.func_70606_j(entityPlayer.func_110143_aJ() - damage);
                              }

                              ((FakePlayerDev.playerInfo)temp.get()).tickPop = 0;
                           }
                        }
                     }
                  }
               }
            }
         }

      }, new Predicate[0]);
   }

   public void onEnable() {
      this.incr = 0;
      this.beforePressed = false;
      if (mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (!(Boolean)this.onShift.getValue()) {
            this.spawnPlayer();
         }

      } else {
         this.disable();
      }
   }

   void spawnPlayer() {
      EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP(mc.field_71441_e, new GameProfile(UUID.fromString("fdee323e-7f0c-4c15-8d1c-0f277442342a"), this.nameFakePlayer.getText()));
      clonedPlayer.func_82149_j(mc.field_71439_g);
      clonedPlayer.field_70759_as = mc.field_71439_g.field_70759_as;
      clonedPlayer.field_70177_z = mc.field_71439_g.field_70177_z;
      clonedPlayer.field_70125_A = mc.field_71439_g.field_70125_A;
      clonedPlayer.func_71033_a(GameType.SURVIVAL);
      clonedPlayer.func_70606_j((float)(Integer)this.startHealth.getValue());
      mc.field_71441_e.func_73027_a(-1234 + this.incr, clonedPlayer);
      ++this.incr;
      if ((Boolean)this.copyInventory.getValue()) {
         clonedPlayer.field_71071_by.func_70455_b(mc.field_71439_g.field_71071_by);
      } else if ((Boolean)this.playerStacked.getValue()) {
         for(int i = 0; i < 4; ++i) {
            ItemStack item = this.armors[i];
            item.func_77966_a(i == 3 ? Enchantments.field_185297_d : Enchantments.field_180310_c, 4);
            clonedPlayer.field_71071_by.field_70460_b.set(i, item);
         }
      }

      if ((Boolean)this.resistance.getValue()) {
         clonedPlayer.func_70690_d(new PotionEffect(Potion.func_188412_a(11), 123456789, 0));
      }

      clonedPlayer.func_70030_z();
      this.listPlayers.add(new FakePlayerDev.playerInfo(clonedPlayer.func_70005_c_()));
      if (!((String)this.moving.getValue()).equals("None")) {
         this.manager.addPlayer(clonedPlayer.field_145783_c, (String)this.moving.getValue(), (Double)this.speed.getValue(), ((String)this.moving.getValue()).equals("Line") ? this.getDirection() : -1, (Double)this.range.getValue(), (Boolean)this.followPlayer.getValue());
      }

   }

   public void onUpdate() {
      if ((Boolean)this.onShift.getValue() && mc.field_71474_y.field_74311_E.func_151468_f() && !this.beforePressed) {
         this.beforePressed = true;
         this.spawnPlayer();
      } else {
         this.beforePressed = false;
      }

      for(int i = 0; i < this.listPlayers.size(); ++i) {
         if (((FakePlayerDev.playerInfo)this.listPlayers.get(i)).update()) {
            Optional<EntityPlayer> temp = mc.field_71441_e.field_73010_i.stream().filter((e) -> {
               return e.func_70005_c_().equals(((FakePlayerDev.playerInfo)this.listPlayers.get(i)).name);
            }).findAny();
            if (temp.isPresent() && ((EntityPlayer)temp.get()).func_110143_aJ() < 20.0F) {
               ((EntityPlayer)temp.get()).func_70606_j(((EntityPlayer)temp.get()).func_110143_aJ() + 1.0F);
            }
         }
      }

      this.manager.update();
   }

   int getDirection() {
      int yaw = (int)RotationUtil.normalizeAngle(mc.field_71439_g.func_189653_aC().field_189983_j);
      if (yaw < 0) {
         yaw += 360;
      }

      yaw += 22;
      yaw %= 360;
      return yaw / 45;
   }

   public void onDisable() {
      if (mc.field_71441_e != null) {
         for(int i = 0; i < this.incr; ++i) {
            mc.field_71441_e.func_73028_b(-1234 + i);
         }
      }

      this.listPlayers.clear();
      this.manager.remove();
   }

   static class movingManager {
      private final ArrayList<FakePlayerDev.movingPlayer> players = new ArrayList();

      void addPlayer(int id, String type, double speed, int direction, double range, boolean follow) {
         this.players.add(new FakePlayerDev.movingPlayer(id, type, speed, direction, range, follow));
      }

      void update() {
         this.players.forEach(FakePlayerDev.movingPlayer::move);
      }

      void remove() {
         this.players.clear();
      }
   }

   static class movingPlayer {
      private final int id;
      private final String type;
      private final double speed;
      private final int direction;
      private final double range;
      private final boolean follow;
      int rad = 0;

      public movingPlayer(int id, String type, double speed, int direction, double range, boolean follow) {
         this.id = id;
         this.type = type;
         this.speed = speed;
         this.direction = Math.abs(direction);
         this.range = range;
         this.follow = follow;
      }

      void move() {
         Entity player = FakePlayerDev.mc.field_71441_e.func_73045_a(this.id);
         if (player != null) {
            String var2 = this.type;
            byte var3 = -1;
            switch(var2.hashCode()) {
            case -1854418717:
               if (var2.equals("Random")) {
                  var3 = 2;
               }
               break;
            case 2368532:
               if (var2.equals("Line")) {
                  var3 = 0;
               }
               break;
            case 2018617584:
               if (var2.equals("Circle")) {
                  var3 = 1;
               }
            }

            switch(var3) {
            case 0:
               double posX = this.follow ? FakePlayerDev.mc.field_71439_g.field_70165_t : player.field_70165_t;
               double posY = this.follow ? FakePlayerDev.mc.field_71439_g.field_70163_u : player.field_70163_u;
               double posZ = this.follow ? FakePlayerDev.mc.field_71439_g.field_70161_v : player.field_70161_v;
               switch(this.direction) {
               case 0:
                  posZ += this.speed;
                  break;
               case 1:
                  posX -= this.speed / 2.0D;
                  posZ += this.speed / 2.0D;
                  break;
               case 2:
                  posX -= this.speed / 2.0D;
                  break;
               case 3:
                  posZ -= this.speed / 2.0D;
                  posX -= this.speed / 2.0D;
                  break;
               case 4:
                  posZ -= this.speed;
                  break;
               case 5:
                  posX += this.speed / 2.0D;
                  posZ -= this.speed / 2.0D;
                  break;
               case 6:
                  posX += this.speed;
                  break;
               case 7:
                  posZ += this.speed / 2.0D;
                  posX += this.speed / 2.0D;
               }

               int i;
               if (BlockUtil.getBlock(posX, posY, posZ) instanceof BlockAir) {
                  for(i = 0; i < 5 && BlockUtil.getBlock(posX, posY - 1.0D, posZ) instanceof BlockAir; ++i) {
                     --posY;
                  }
               } else {
                  for(i = 0; i < 5 && !(BlockUtil.getBlock(posX, posY, posZ) instanceof BlockAir); ++i) {
                     ++posY;
                  }
               }

               player.func_70634_a(posX, posY, posZ);
               break;
            case 1:
               double posXCir = Math.cos((double)this.rad / 100.0D) * this.range + FakePlayerDev.mc.field_71439_g.field_70165_t;
               double posZCir = Math.sin((double)this.rad / 100.0D) * this.range + FakePlayerDev.mc.field_71439_g.field_70161_v;
               double posYCir = FakePlayerDev.mc.field_71439_g.field_70163_u;
               int i;
               if (BlockUtil.getBlock(posXCir, posYCir, posZCir) instanceof BlockAir) {
                  for(i = 0; i < 5 && BlockUtil.getBlock(posXCir, posYCir - 1.0D, posZCir) instanceof BlockAir; ++i) {
                     --posYCir;
                  }
               } else {
                  for(i = 0; i < 5 && !(BlockUtil.getBlock(posXCir, posYCir, posZCir) instanceof BlockAir); ++i) {
                     ++posYCir;
                  }
               }

               player.func_70634_a(posXCir, posYCir, posZCir);
               this.rad = (int)((double)this.rad + this.speed * 10.0D);
            case 2:
            }
         }

      }
   }

   class playerInfo {
      final String name;
      int tickPop = -1;
      int tickRegen = 0;

      public playerInfo(String name) {
         this.name = name;
      }

      boolean update() {
         if (this.tickPop != -1 && ++this.tickPop >= (Integer)FakePlayerDev.this.vulnerabilityTick.getValue()) {
            this.tickPop = -1;
         }

         if (++this.tickRegen >= (Integer)FakePlayerDev.this.tickRegenVal.getValue()) {
            this.tickRegen = 0;
            return true;
         } else {
            return false;
         }
      }

      boolean canPop() {
         return this.tickPop == -1;
      }
   }
}
