package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

public class NewRenderEntityEvent extends LemonClientEvent {
   public ModelBase modelBase;
   public Entity entityIn;
   public float limbSwing;
   public float limbSwingAmount;
   public float ageInTicks;
   public float netHeadYaw;
   public float headPitch;
   public float scale;

   public NewRenderEntityEvent(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.modelBase = modelBase;
      this.entityIn = entityIn;
      this.limbSwing = limbSwing;
      this.limbSwingAmount = limbSwingAmount;
      this.ageInTicks = ageInTicks;
      this.netHeadYaw = netHeadYaw;
      this.headPitch = headPitch;
      this.scale = scale;
   }

   public boolean isCancelable() {
      return true;
   }
}
