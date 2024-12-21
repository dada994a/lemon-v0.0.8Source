package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.util.render.Interpolation;
import com.lemonclient.api.util.render.animation.AnimationMode;
import com.lemonclient.api.util.render.animation.TimeAnimation;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import org.lwjgl.opengl.GL11;

@Module.Declaration(
   name = "Trails",
   category = Category.Render
)
public class Trails extends Module {
   BooleanSetting arrows = this.registerBoolean("Arrows", false);
   BooleanSetting pearls = this.registerBoolean("Pearls", false);
   BooleanSetting snowballs = this.registerBoolean("Snowballs", false);
   IntegerSetting time = this.registerInteger("Time", 1, 1, 10);
   ColorSetting color = this.registerColor("Color", new GSColor(255, 0, 0, 255));
   IntegerSetting alpha = this.registerInteger("Alpha", 255, 1, 255);
   DoubleSetting width = this.registerDouble("Width", 1.600000023841858D, 0.10000000149011612D, 10.0D);
   Map<Integer, TimeAnimation> ids = new ConcurrentHashMap();
   Map<Integer, List<Trails.Trace>> traceLists = new ConcurrentHashMap();
   Map<Integer, Trails.Trace> traces = new ConcurrentHashMap();
   public static final Vec3d ORIGIN = new Vec3d(8.0D, 64.0D, 8.0D);
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if (mc.field_71441_e != null) {
         if (event.getPacket() instanceof SPacketSpawnObject) {
            SPacketSpawnObject packet = (SPacketSpawnObject)event.getPacket();
            if ((Boolean)this.pearls.getValue() && packet.func_148993_l() == 65 || (Boolean)this.arrows.getValue() && packet.func_148993_l() == 60 || (Boolean)this.snowballs.getValue() && packet.func_148993_l() == 61) {
               TimeAnimation animation = new TimeAnimation((long)((Integer)this.time.getValue() * 1000), 0.0D, (double)(Integer)this.alpha.getValue(), false, AnimationMode.LINEAR);
               animation.stop();
               this.ids.put(packet.func_149001_c(), animation);
               this.traceLists.put(packet.func_149001_c(), new ArrayList());
               this.traces.put(packet.func_149001_c(), new Trails.Trace(0, (String)null, mc.field_71441_e.field_73011_w.func_186058_p(), new Vec3d(packet.func_186880_c(), packet.func_186882_d(), packet.func_186881_e()), new ArrayList()));
            }
         }

         if (event.getPacket() instanceof SPacketDestroyEntities) {
            int[] var6 = ((SPacketDestroyEntities)event.getPacket()).func_149098_c();
            int var7 = var6.length;

            for(int var4 = 0; var4 < var7; ++var4) {
               int id = var6[var4];
               if (this.ids.containsKey(id)) {
                  ((TimeAnimation)this.ids.get(id)).play();
               }
            }
         }

      }
   }, new Predicate[0]);

   public void onTick() {
      if (mc.field_71441_e != null) {
         if (!this.ids.keySet().isEmpty()) {
            Iterator var1 = this.ids.keySet().iterator();

            while(true) {
               Integer id;
               do {
                  if (!var1.hasNext()) {
                     return;
                  }

                  id = (Integer)var1.next();
               } while(id == null);

               if (mc.field_71441_e.field_72996_f == null) {
                  return;
               }

               if (mc.field_71441_e.field_72996_f.isEmpty()) {
                  return;
               }

               Trails.Trace idTrace = (Trails.Trace)this.traces.get(id);
               Entity entity = mc.field_71441_e.func_73045_a(id);
               if (entity != null) {
                  Vec3d vec = entity.func_174791_d();
                  if (vec.equals(ORIGIN)) {
                     continue;
                  }

                  if (!this.traces.containsKey(id) || idTrace == null) {
                     this.traces.put(id, new Trails.Trace(0, (String)null, mc.field_71441_e.field_73011_w.func_186058_p(), vec, new ArrayList()));
                     idTrace = (Trails.Trace)this.traces.get(id);
                  }

                  List<Trails.Trace.TracePos> trace = idTrace.getTrace();
                  Vec3d vec3d = ((List)trace).isEmpty() ? vec : ((Trails.Trace.TracePos)((List)trace).get(((List)trace).size() - 1)).getPos();
                  if (!((List)trace).isEmpty() && (vec.func_72438_d(vec3d) > 100.0D || idTrace.getType() != mc.field_71441_e.field_73011_w.func_186058_p())) {
                     ((List)this.traceLists.get(id)).add(idTrace);
                     trace = new ArrayList();
                     this.traces.put(id, new Trails.Trace(((List)this.traceLists.get(id)).size() + 1, (String)null, mc.field_71441_e.field_73011_w.func_186058_p(), vec, new ArrayList()));
                  }

                  if (((List)trace).isEmpty() || !vec.equals(vec3d)) {
                     ((List)trace).add(new Trails.Trace.TracePos(vec));
                  }
               }

               TimeAnimation animation = (TimeAnimation)this.ids.get(id);
               if (entity instanceof EntityArrow && (entity.field_70122_E || entity.field_70132_H || !entity.field_70160_al)) {
                  animation.play();
               }

               if (animation != null && (double)(Integer)this.alpha.getValue() - animation.getCurrent() <= 0.0D) {
                  animation.stop();
                  this.ids.remove(id);
                  this.traceLists.remove(id);
                  this.traces.remove(id);
               }
            }
         }
      }
   }

   public void onWorldRender(RenderEvent event) {
      for(Iterator var2 = this.traceLists.entrySet().iterator(); var2.hasNext(); GL11.glEnd()) {
         Entry<Integer, List<Trails.Trace>> entry = (Entry)var2.next();
         GL11.glLineWidth(((Double)this.width.getValue()).floatValue());
         TimeAnimation animation = (TimeAnimation)this.ids.get(entry.getKey());
         animation.add();
         GL11.glColor4f((float)this.color.getColor().getRed(), (float)this.color.getColor().getGreen(), (float)this.color.getColor().getBlue(), MathHelper.func_76131_a((float)((double)(Integer)this.alpha.getValue() - animation.getCurrent() / 255.0D), 0.0F, 255.0F));
         ((List)entry.getValue()).forEach((tracex) -> {
            GL11.glBegin(3);
            tracex.getTrace().forEach(this::renderVec);
            GL11.glEnd();
         });
         GL11.glColor4f((float)this.color.getColor().getRed(), (float)this.color.getColor().getGreen(), (float)this.color.getColor().getBlue(), MathHelper.func_76131_a((float)((double)(Integer)this.alpha.getValue() - animation.getCurrent() / 255.0D), 0.0F, 255.0F));
         GL11.glBegin(3);
         Trails.Trace trace = (Trails.Trace)this.traces.get(entry.getKey());
         if (trace != null) {
            trace.getTrace().forEach(this::renderVec);
         }
      }

   }

   private void renderVec(Trails.Trace.TracePos tracePos) {
      double x = tracePos.getPos().field_72450_a - Interpolation.getRenderPosX();
      double y = tracePos.getPos().field_72448_b - Interpolation.getRenderPosY();
      double z = tracePos.getPos().field_72449_c - Interpolation.getRenderPosZ();
      GL11.glVertex3d(x, y, z);
   }

   public static class Trace {
      private String name;
      private int index;
      private Vec3d pos;
      private final List<Trails.Trace.TracePos> trace;
      private DimensionType type;

      public Trace(int index, String name, DimensionType type, Vec3d pos, List<Trails.Trace.TracePos> trace) {
         this.index = index;
         this.name = name;
         this.type = type;
         this.pos = pos;
         this.trace = trace;
      }

      public int getIndex() {
         return this.index;
      }

      public DimensionType getType() {
         return this.type;
      }

      public List<Trails.Trace.TracePos> getTrace() {
         return this.trace;
      }

      public String getName() {
         return this.name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public void setPos(Vec3d pos) {
         this.pos = pos;
      }

      public void setIndex(int index) {
         this.index = index;
      }

      public Vec3d getPos() {
         return this.pos;
      }

      public void setType(DimensionType type) {
         this.type = type;
      }

      public static class TracePos {
         private final Vec3d pos;

         public TracePos(Vec3d pos) {
            this.pos = pos;
         }

         public Vec3d getPos() {
            return this.pos;
         }
      }
   }
}
