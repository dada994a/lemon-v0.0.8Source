package com.lukflug.panelstudio.setting;

import java.util.stream.Stream;

public interface ICategory extends ILabeled {
   Stream<IModule> getModules();
}
