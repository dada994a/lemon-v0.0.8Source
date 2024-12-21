package com.lemonclient.client.clickgui;

import com.lemonclient.api.setting.Setting;
import com.lemonclient.api.setting.SettingsManager;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.api.util.font.FontUtil;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.HUDModule;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ClickGuiModule;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.base.SettingsAnimation;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.component.IFixedComponentProxy;
import com.lukflug.panelstudio.component.IResizable;
import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.container.IContainer;
import com.lukflug.panelstudio.hud.HUDGUI;
import com.lukflug.panelstudio.layout.CSGOLayout;
import com.lukflug.panelstudio.layout.ChildUtil;
import com.lukflug.panelstudio.layout.ComponentGenerator;
import com.lukflug.panelstudio.layout.IComponentAdder;
import com.lukflug.panelstudio.layout.IComponentGenerator;
import com.lukflug.panelstudio.layout.ILayout;
import com.lukflug.panelstudio.layout.PanelAdder;
import com.lukflug.panelstudio.layout.PanelLayout;
import com.lukflug.panelstudio.mc12.MinecraftGUI;
import com.lukflug.panelstudio.mc12.MinecraftHUDGUI;
import com.lukflug.panelstudio.popup.CenteredPositioner;
import com.lukflug.panelstudio.popup.MousePositioner;
import com.lukflug.panelstudio.popup.PanelPositioner;
import com.lukflug.panelstudio.popup.PopupTuple;
import com.lukflug.panelstudio.setting.IBooleanSetting;
import com.lukflug.panelstudio.setting.ICategory;
import com.lukflug.panelstudio.setting.IClient;
import com.lukflug.panelstudio.setting.IColorSetting;
import com.lukflug.panelstudio.setting.IEnumSetting;
import com.lukflug.panelstudio.setting.IKeybindSetting;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.IModule;
import com.lukflug.panelstudio.setting.INumberSetting;
import com.lukflug.panelstudio.setting.ISetting;
import com.lukflug.panelstudio.setting.IStringSetting;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.IColorScheme;
import com.lukflug.panelstudio.theme.ITheme;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.ColorPickerComponent;
import com.lukflug.panelstudio.widget.TextField;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class LemonClientGUI extends MinecraftHUDGUI {
   public static LemonClientGUI INSTANCE;
   public static final int WIDTH = 100;
   public static final int HEIGHT = 12;
   public static final int FONT_HEIGHT = 9;
   public static final int DISTANCE = 10;
   public static final int HUD_BORDER = 2;
   public static IClient client;
   public static MinecraftGUI.GUIInterface guiInterface;
   public static HUDGUI gui;
   private final ITheme theme;
   private ITheme clearTheme;

   public LemonClientGUI() {
      INSTANCE = this;
      final ClickGuiModule clickGuiModule = (ClickGuiModule)ModuleManager.getModule(ClickGuiModule.class);
      final ColorMain colorMain = (ColorMain)ModuleManager.getModule(ColorMain.class);
      guiInterface = new MinecraftGUI.GUIInterface(true) {
         public void drawString(Point pos, int height, String s, Color c) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b((float)pos.x, (float)pos.y, 0.0F);
            double scale = (double)height / (double)(FontUtil.getFontHeight((Boolean)colorMain.customFont.getValue()) + ((Boolean)colorMain.customFont.getValue() ? 1 : 0));
            this.end(false);
            FontUtil.drawStringWithShadow((Boolean)colorMain.customFont.getValue(), s, 0.0F, 0.0F, new GSColor(c));
            this.begin(false);
            GlStateManager.func_179139_a(scale, scale, 1.0D);
            GlStateManager.func_179121_F();
         }

         public int getFontWidth(int height, String s) {
            double scale = (double)height / (double)(FontUtil.getFontHeight((Boolean)colorMain.customFont.getValue()) + ((Boolean)colorMain.customFont.getValue() ? 1 : 0));
            return (int)Math.round((double)FontUtil.getStringWidth((Boolean)colorMain.customFont.getValue(), s) * scale);
         }

         public double getScreenWidth() {
            return super.getScreenWidth();
         }

         public double getScreenHeight() {
            return super.getScreenHeight();
         }

         public String getResourcePrefix() {
            return "lemonclient:gui/";
         }
      };
      this.clearTheme = new Theme(new LemonClientGUI.GSColorScheme("clear", () -> {
         return true;
      }), colorMain.Title.getValue(), colorMain.Enabled.getValue(), colorMain.Disabled.getValue(), colorMain.Background.getValue(), colorMain.Font.getValue(), colorMain.ScrollBar.getValue(), colorMain.Highlight.getValue(), () -> {
         return (Boolean)clickGuiModule.gradient.getValue();
      }, 9, 3, 1, ": " + TextFormatting.GRAY);
      this.theme = () -> {
         return this.clearTheme;
      };
      client = () -> {
         return Arrays.stream(Category.values()).sorted(Comparator.comparing(Enum::toString)).map((category) -> {
            return new ICategory() {
               public String getDisplayName() {
                  return category.toString();
               }

               public Stream<IModule> getModules() {
                  return ModuleManager.getModulesInCategory(category).stream().sorted(Comparator.comparing(Module::getName)).map((module) -> {
                     return new IModule() {
                        public String getDisplayName() {
                           return module.getName();
                        }

                        public IToggleable isEnabled() {
                           return new IToggleable() {
                              public boolean isOn() {
                                 return module.isEnabled();
                              }

                              public void toggle() {
                                 module.toggle();
                              }
                           };
                        }

                        public Stream<ISetting<?>> getSettings() {
                           Stream temp = SettingsManager.getSettingsForModule(module).stream().map((setting) -> {
                              return LemonClientGUI.this.createSetting(setting);
                           });
                           return Stream.concat(temp, Stream.concat(Stream.of(new IBooleanSetting() {
                              public String getDisplayName() {
                                 return "Toggle Msgs";
                              }

                              public void toggle() {
                                 module.setToggleMsg(!module.isToggleMsg());
                              }

                              public boolean isOn() {
                                 return module.isToggleMsg();
                              }
                           }), Stream.of(new IKeybindSetting() {
                              public String getDisplayName() {
                                 return "Keybind";
                              }

                              public int getKey() {
                                 return module.getBind();
                              }

                              public void setKey(int key) {
                                 module.setBind(key);
                              }

                              public String getKeyName() {
                                 return Keyboard.getKeyName(module.getBind());
                              }
                           })));
                        }
                     };
                  });
               }
            };
         });
      };
      final IToggleable guiToggle = new SimpleToggleable(false);
      IToggleable hudToggle = new SimpleToggleable(false) {
         public boolean isOn() {
            return guiToggle.isOn() && super.isOn() ? (Boolean)clickGuiModule.showHUD.getValue() : super.isOn();
         }
      };
      gui = new HUDGUI(guiInterface, this.theme.getDescriptionRenderer(), new MousePositioner(new Point(10, 10)), guiToggle, hudToggle);
      final BiFunction<Context, Integer, Integer> scrollHeight = (context, componentHeight) -> {
         return ((String)clickGuiModule.scrolling.getValue()).equals("Screen") ? componentHeight : Math.min(componentHeight, Math.max(48, this.field_146295_m - context.getPos().y - 12));
      };
      Supplier<Animation> animation = () -> {
         return new SettingsAnimation(() -> {
            return (Integer)clickGuiModule.animationSpeed.getValue();
         }, () -> {
            return guiInterface.getTime();
         });
      };
      PopupTuple popupType = new PopupTuple(new PanelPositioner(new Point(0, 0)), false, new IScrollSize() {
         public int getScrollHeight(Context context, int componentHeight) {
            return (Integer)scrollHeight.apply(context, componentHeight);
         }
      });
      Iterator var8 = ModuleManager.getModules().iterator();

      while(var8.hasNext()) {
         final Module module = (Module)var8.next();
         if (module instanceof HUDModule) {
            ((HUDModule)module).populate(this.theme);
            gui.addHUDComponent(((HUDModule)module).getComponent(), new IToggleable() {
               public boolean isOn() {
                  return module.isEnabled();
               }

               public void toggle() {
                  module.toggle();
               }
            }, (Animation)animation.get(), this.theme, 2);
         }
      }

      IComponentAdder classicPanelAdder = new PanelAdder(new IContainer<IFixedComponent>() {
         public boolean addComponent(final IFixedComponent component) {
            return LemonClientGUI.gui.addComponent((IFixedComponent)(new IFixedComponentProxy<IFixedComponent>() {
               public void handleScroll(Context context, int diff) {
                  IFixedComponentProxy.super.handleScroll(context, diff);
                  if (((String)clickGuiModule.scrolling.getValue()).equals("Screen")) {
                     Point p = this.getPosition(LemonClientGUI.guiInterface);
                     p.translate(0, -diff);
                     this.setPosition(LemonClientGUI.guiInterface, p);
                  }

               }

               public IFixedComponent getComponent() {
                  return component;
               }
            }));
         }

         public boolean addComponent(final IFixedComponent component, IBoolean visible) {
            return LemonClientGUI.gui.addComponent((IFixedComponent)(new IFixedComponentProxy<IFixedComponent>() {
               public void handleScroll(Context context, int diff) {
                  IFixedComponentProxy.super.handleScroll(context, diff);
                  if (((String)clickGuiModule.scrolling.getValue()).equals("Screen")) {
                     Point p = this.getPosition(LemonClientGUI.guiInterface);
                     p.translate(0, -diff);
                     this.setPosition(LemonClientGUI.guiInterface, p);
                  }

               }

               public IFixedComponent getComponent() {
                  return component;
               }
            }), visible);
         }

         public boolean removeComponent(IFixedComponent component) {
            return LemonClientGUI.gui.removeComponent(component);
         }
      }, false, () -> {
         return !(Boolean)clickGuiModule.csgoLayout.getValue();
      }, (title) -> {
         return title;
      }) {
         protected IScrollSize getScrollSize(IResizable size) {
            return new IScrollSize() {
               public int getScrollHeight(Context context, int componentHeight) {
                  return (Integer)scrollHeight.apply(context, componentHeight);
               }
            };
         }
      };
      IComponentGenerator generator = new ComponentGenerator((scancode) -> {
         return scancode == 211;
      }, (character) -> {
         return character >= 32;
      }, new TextFieldKeys()) {
         public IComponent getColorComponent(IColorSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
            return new ColorPickerComponent(setting, theme);
         }

         public IComponent getStringComponent(IStringSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
            return new TextField(setting, this.keys, 0, new SimpleToggleable(false), theme.getTextRenderer(false, isContainer)) {
               public boolean allowCharacter(char character) {
                  return charFilter.test(character) && character != 127;
               }
            };
         }
      };
      ILayout classicPanelLayout = new PanelLayout(100, new Point(10, 10), 55, 22, animation, (level) -> {
         return ChildUtil.ChildMode.DOWN;
      }, (level) -> {
         return ChildUtil.ChildMode.DOWN;
      }, popupType);
      classicPanelLayout.populateGUI(classicPanelAdder, generator, client, this.theme);
      PopupTuple colorPopup = new PopupTuple(new CenteredPositioner(() -> {
         return new Rectangle(new Point(0, 0), guiInterface.getWindowSize());
      }), true, new IScrollSize() {
      });
      IComponentAdder horizontalCSGOAdder = new PanelAdder(gui, true, () -> {
         return (Boolean)clickGuiModule.csgoLayout.getValue();
      }, (title) -> {
         return title;
      });
      ILayout horizontalCSGOLayout = new CSGOLayout(new Labeled("LemonClient", (String)null, () -> {
         return true;
      }), new Point(100, 100), 480, 100, animation, "Enabled", true, true, 2, ChildUtil.ChildMode.DOWN, colorPopup) {
         public int getScrollHeight(Context context, int componentHeight) {
            return 320;
         }

         protected boolean isUpKey(int key) {
            return key == 200;
         }

         protected boolean isDownKey(int key) {
            return key == 208;
         }

         protected boolean isLeftKey(int key) {
            return key == 203;
         }

         protected boolean isRightKey(int key) {
            return key == 205;
         }
      };
      horizontalCSGOLayout.populateGUI(horizontalCSGOAdder, generator, client, this.theme);
   }

   protected HUDGUI getGUI() {
      return gui;
   }

   private ISetting<?> createSetting(final Setting<?> setting) {
      if (setting instanceof BooleanSetting) {
         return new IBooleanSetting() {
            public String getDisplayName() {
               return setting.getName();
            }

            public IBoolean isVisible() {
               return () -> {
                  return setting.isVisible();
               };
            }

            public void toggle() {
               ((BooleanSetting)setting).setValue(!(Boolean)((BooleanSetting)setting).getValue());
            }

            public boolean isOn() {
               return (Boolean)((BooleanSetting)setting).getValue();
            }

            public Stream<ISetting<?>> getSubSettings() {
               return setting.getSubSettings().count() == 0L ? null : setting.getSubSettings().map((subSetting) -> {
                  return LemonClientGUI.this.createSetting(subSetting);
               });
            }
         };
      } else if (setting instanceof IntegerSetting) {
         return new INumberSetting() {
            public String getDisplayName() {
               return setting.getName();
            }

            public IBoolean isVisible() {
               return () -> {
                  return setting.isVisible();
               };
            }

            public double getNumber() {
               return (double)(Integer)((IntegerSetting)setting).getValue();
            }

            public void setNumber(double value) {
               ((IntegerSetting)setting).setValue((int)Math.round(value));
            }

            public double getMaximumValue() {
               return (double)((IntegerSetting)setting).getMax();
            }

            public double getMinimumValue() {
               return (double)((IntegerSetting)setting).getMin();
            }

            public int getPrecision() {
               return 0;
            }

            public Stream<ISetting<?>> getSubSettings() {
               return setting.getSubSettings().count() == 0L ? null : setting.getSubSettings().map((subSetting) -> {
                  return LemonClientGUI.this.createSetting(subSetting);
               });
            }
         };
      } else if (setting instanceof DoubleSetting) {
         return new INumberSetting() {
            public String getDisplayName() {
               return setting.getName();
            }

            public IBoolean isVisible() {
               return () -> {
                  return setting.isVisible();
               };
            }

            public double getNumber() {
               return (Double)((DoubleSetting)setting).getValue();
            }

            public void setNumber(double value) {
               ((DoubleSetting)setting).setValue(value);
            }

            public double getMaximumValue() {
               return ((DoubleSetting)setting).getMax();
            }

            public double getMinimumValue() {
               return ((DoubleSetting)setting).getMin();
            }

            public int getPrecision() {
               return 2;
            }

            public Stream<ISetting<?>> getSubSettings() {
               return setting.getSubSettings().count() == 0L ? null : setting.getSubSettings().map((subSetting) -> {
                  return LemonClientGUI.this.createSetting(subSetting);
               });
            }
         };
      } else if (setting instanceof ModeSetting) {
         return new IEnumSetting() {
            private final ILabeled[] states = (ILabeled[])((ModeSetting)setting).getModes().stream().map((mode) -> {
               return new Labeled(mode, (String)null, () -> {
                  return true;
               });
            }).toArray((x$0) -> {
               return new ILabeled[x$0];
            });

            public String getDisplayName() {
               return setting.getName();
            }

            public IBoolean isVisible() {
               return () -> {
                  return setting.isVisible();
               };
            }

            public void increment() {
               ((ModeSetting)setting).increment();
            }

            public void decrement() {
               ((ModeSetting)setting).decrement();
            }

            public String getValueName() {
               return (String)((ModeSetting)setting).getValue();
            }

            public int getValueIndex() {
               return ((ModeSetting)setting).getModes().indexOf(this.getValueName());
            }

            public void setValueIndex(int index) {
               ((ModeSetting)setting).setValue(((ModeSetting)setting).getModes().get(index));
            }

            public ILabeled[] getAllowedValues() {
               return this.states;
            }

            public Stream<ISetting<?>> getSubSettings() {
               return setting.getSubSettings().count() == 0L ? null : setting.getSubSettings().map((subSetting) -> {
                  return LemonClientGUI.this.createSetting(subSetting);
               });
            }
         };
      } else if (setting instanceof ColorSetting) {
         return new IColorSetting() {
            public String getDisplayName() {
               return TextFormatting.BOLD + setting.getName();
            }

            public IBoolean isVisible() {
               return () -> {
                  return setting.isVisible();
               };
            }

            public Color getValue() {
               return ((ColorSetting)setting).getValue();
            }

            public void setValue(Color value) {
               ((ColorSetting)setting).setValue(new GSColor(value));
            }

            public Color getColor() {
               return ((ColorSetting)setting).getColor();
            }

            public boolean getRainbow() {
               return ((ColorSetting)setting).getRainbow();
            }

            public void setRainbow(boolean rainbow) {
               ((ColorSetting)setting).setRainbow(rainbow);
            }

            public boolean hasAlpha() {
               return ((ColorSetting)setting).alphaEnabled();
            }

            public boolean allowsRainbow() {
               return ((ColorSetting)setting).rainbowEnabled();
            }

            public boolean hasHSBModel() {
               return ((String)((ColorMain)ModuleManager.getModule(ColorMain.class)).colorModel.getValue()).equalsIgnoreCase("HSB");
            }

            public Stream<ISetting<?>> getSubSettings() {
               Stream<ISetting<?>> temp = setting.getSubSettings().map((subSetting) -> {
                  return LemonClientGUI.this.createSetting(subSetting);
               });
               return Stream.concat(temp, Stream.of(new IBooleanSetting() {
                  public String getDisplayName() {
                     return "Sync Color";
                  }

                  public IBoolean isVisible() {
                     return () -> {
                        return setting != ((ColorMain)ModuleManager.getModule(ColorMain.class)).enabledColor;
                     };
                  }

                  public void toggle() {
                     ((ColorSetting)setting).setValue(((ColorMain)ModuleManager.getModule(ColorMain.class)).enabledColor.getColor());
                     ((ColorSetting)setting).setRainbow(((ColorMain)ModuleManager.getModule(ColorMain.class)).enabledColor.getRainbow());
                  }

                  public boolean isOn() {
                     return ((ColorMain)ModuleManager.getModule(ColorMain.class)).enabledColor.getColor().equals(((ColorSetting)setting).getColor());
                  }
               }));
            }
         };
      } else {
         return (ISetting)(setting instanceof StringSetting ? new IStringSetting() {
            public String getValue() {
               return ((StringSetting)setting).getText();
            }

            public void setValue(String string) {
               ((StringSetting)setting).setText(string);
            }

            public String getDisplayName() {
               return setting.getName();
            }
         } : new ISetting<Void>() {
            public String getDisplayName() {
               return setting.getName();
            }

            public IBoolean isVisible() {
               return () -> {
                  return setting.isVisible();
               };
            }

            public Void getSettingState() {
               return null;
            }

            public Class<Void> getSettingClass() {
               return Void.class;
            }

            public Stream<ISetting<?>> getSubSettings() {
               return setting.getSubSettings().count() == 0L ? null : setting.getSubSettings().map((subSetting) -> {
                  return LemonClientGUI.this.createSetting(subSetting);
               });
            }
         });
      }
   }

   public static void renderItem(ItemStack item, Point pos) {
      LemonClient.INSTANCE.gameSenseGUI.getInterface().end(false);
      GlStateManager.func_179098_w();
      GlStateManager.func_179132_a(true);
      GL11.glPushAttrib(524288);
      GL11.glDisable(3089);
      GlStateManager.func_179086_m(256);
      GL11.glPopAttrib();
      GlStateManager.func_179126_j();
      GlStateManager.func_179118_c();
      GlStateManager.func_179094_E();
      Minecraft.func_71410_x().func_175599_af().field_77023_b = -150.0F;
      RenderHelper.func_74520_c();
      Minecraft.func_71410_x().func_175599_af().func_180450_b(item, pos.x, pos.y);
      Minecraft.func_71410_x().func_175599_af().func_175030_a(Minecraft.func_71410_x().field_71466_p, item, pos.x, pos.y);
      RenderHelper.func_74518_a();
      Minecraft.func_71410_x().func_175599_af().field_77023_b = 0.0F;
      GlStateManager.func_179121_F();
      GlStateManager.func_179097_i();
      GlStateManager.func_179132_a(false);
      LemonClient.INSTANCE.gameSenseGUI.getInterface().begin(false);
   }

   public static void renderItemTest(ItemStack item, Point pos) {
      GlStateManager.func_179098_w();
      GlStateManager.func_179132_a(true);
      GL11.glPushAttrib(524288);
      GL11.glDisable(3089);
      GlStateManager.func_179086_m(256);
      GL11.glPopAttrib();
      GlStateManager.func_179126_j();
      GlStateManager.func_179118_c();
      GlStateManager.func_179094_E();
      Minecraft.func_71410_x().func_175599_af().field_77023_b = -150.0F;
      RenderHelper.func_74520_c();
      Minecraft.func_71410_x().func_175599_af().func_180450_b(item, pos.x, pos.y);
      Minecraft.func_71410_x().func_175599_af().func_175030_a(Minecraft.func_71410_x().field_71466_p, item, pos.x, pos.y);
      RenderHelper.func_74518_a();
      Minecraft.func_71410_x().func_175599_af().field_77023_b = 0.0F;
      GlStateManager.func_179121_F();
      GlStateManager.func_179097_i();
      GlStateManager.func_179132_a(false);
   }

   public static void renderEntity(EntityLivingBase entity, Point pos, int scale) {
      LemonClient.INSTANCE.gameSenseGUI.getInterface().end(false);
      GlStateManager.func_179098_w();
      GlStateManager.func_179132_a(true);
      GL11.glPushAttrib(524288);
      GL11.glDisable(3089);
      GlStateManager.func_179086_m(256);
      GL11.glPopAttrib();
      GlStateManager.func_179126_j();
      GlStateManager.func_179118_c();
      GlStateManager.func_179094_E();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GuiInventory.func_147046_a(pos.x, pos.y, scale, 28.0F, 60.0F, entity);
      GlStateManager.func_179121_F();
      GlStateManager.func_179097_i();
      GlStateManager.func_179132_a(false);
      LemonClient.INSTANCE.gameSenseGUI.getInterface().begin(false);
   }

   protected MinecraftGUI.GUIInterface getInterface() {
      return guiInterface;
   }

   protected int getScrollSpeed() {
      return (Integer)((ClickGuiModule)ModuleManager.getModule(ClickGuiModule.class)).scrollSpeed.getValue();
   }

   public void refresh() {
      ClickGuiModule clickGuiModule = (ClickGuiModule)ModuleManager.getModule(ClickGuiModule.class);
      ColorMain colorMain = (ColorMain)ModuleManager.getModule(ColorMain.class);
      this.clearTheme = new Theme(new LemonClientGUI.GSColorScheme("clear", () -> {
         return true;
      }), colorMain.Title.getValue(), colorMain.Enabled.getValue(), colorMain.Disabled.getValue(), colorMain.Background.getValue(), colorMain.Font.getValue(), colorMain.ScrollBar.getValue(), colorMain.Highlight.getValue(), () -> {
         return (Boolean)clickGuiModule.gradient.getValue();
      }, 9, 3, 1, ": " + TextFormatting.GRAY);
   }

   private static final class GSColorScheme implements IColorScheme {
      private final String configName;
      private final Supplier<Boolean> isVisible;

      public GSColorScheme(String configName, Supplier<Boolean> isVisible) {
         this.configName = configName;
         this.isVisible = isVisible;
      }

      public void createSetting(ITheme theme, String name, String description, boolean hasAlpha, boolean allowsRainbow, Color color, boolean rainbow) {
      }

      public Color getColor(String name) {
         return new Color(255, 255, 255);
      }
   }
}
