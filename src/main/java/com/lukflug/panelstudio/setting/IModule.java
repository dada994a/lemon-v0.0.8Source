package com.lukflug.panelstudio.setting;

import com.lukflug.panelstudio.base.IToggleable;
import java.util.stream.Stream;

public interface IModule extends ILabeled {
   IToggleable isEnabled();

   Stream<ISetting<?>> getSettings();
}
