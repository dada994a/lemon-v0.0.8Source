package com.lukflug.panelstudio.setting;

import java.util.stream.Stream;

@FunctionalInterface
public interface IClient {
   Stream<ICategory> getCategories();
}
