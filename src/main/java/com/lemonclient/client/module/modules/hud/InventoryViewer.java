package com.lemonclient.client.module.modules.hud;

import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.client.clickgui.LemonClientGUI;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.HUDModule;
import com.lemonclient.client.module.Module;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.hud.HUDComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

@Module.Declaration(
   name = "InventoryViewer",
   category = Category.HUD,
   drawn = false
)
@HUDModule.Declaration(
   posX = 0,
   posZ = 10
)
public class InventoryViewer extends HUDModule {
   ColorSetting fillColor = this.registerColor("Fill", new GSColor(0, 0, 0, 100));
   IntegerSetting fill = this.registerInteger("Fill Alpha", 100, 0, 255);
   ColorSetting outlineColor = this.registerColor("Outline", new GSColor(255, 0, 0, 255));
   IntegerSetting outline = this.registerInteger("Outline Alpha", 255, 0, 255);

   public void populate(ITheme theme) {
      this.component = new InventoryViewer.InventoryViewerComponent(theme);
   }

   private class InventoryViewerComponent extends HUDComponent {
      public InventoryViewerComponent(ITheme theme) {
         super(new Labeled(InventoryViewer.this.getName(), (String)null, () -> {
            return true;
         }), InventoryViewer.this.position, InventoryViewer.this.getName());
      }

      public void render(Context context) {
         super.render(context);
         Color bgcolor = new GSColor(InventoryViewer.this.fillColor.getValue(), (Integer)InventoryViewer.this.fill.getValue());
         context.getInterface().fillRect(context.getRect(), bgcolor, bgcolor, bgcolor, bgcolor);
         Color color = new GSColor(InventoryViewer.this.outlineColor.getValue(), (Integer)InventoryViewer.this.outline.getValue());
         context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(context.getSize().width, 1)), color, color, color, color);
         context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(1, context.getSize().height)), color, color, color, color);
         context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x + context.getSize().width - 1, context.getPos().y), new Dimension(1, context.getSize().height)), color, color, color, color);
         context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x, context.getPos().y + context.getSize().height - 1), new Dimension(context.getSize().width, 1)), color, color, color, color);
         NonNullList<ItemStack> items = Minecraft.func_71410_x().field_71439_g.field_71071_by.field_70462_a;
         int size = items.size();

         for(int item = 9; item < size; ++item) {
            int slotX = context.getPos().x + item % 9 * 18;
            int slotY = context.getPos().y + 2 + (item / 9 - 1) * 18;
            LemonClientGUI.renderItem((ItemStack)items.get(item), new Point(slotX, slotY));
         }

      }

      public Dimension getSize(IInterface inter) {
         return new Dimension(162, 56);
      }
   }
}
