/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.lonmstalker.tgkit.plugin.sort;

import io.lonmstalker.tgkit.plugin.PluginException;
import java.util.*;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;

/** Утилита для топологической сортировки по зависимостям. */
public final class TopoSorter {
  private TopoSorter() {}

  /**
   * Выполняет topological sort.
   *
   * @param items коллекция элементов
   * @param idFn функция получения ID элемента
   * @param depsFn функция получения списка зависимостей по ID
   * @param <T> тип элемента
   * @return список элементов в порядке без нарушения зависимостей
   * @throws PluginException при наличии циклических зависимостей или отсутствующем депенденси
   */
  public static <T> @NonNull List<T> sort(
      @NonNull Collection<T> items,
      @NonNull Function<T, String> idFn,
      @NonNull Function<T, Collection<String>> depsFn) {
    Map<String, T> idMap = new HashMap<>();
    Map<String, Integer> inDegree = new HashMap<>();
    Map<String, List<String>> graph = new HashMap<>();

    for (T item : items) {
      String id = idFn.apply(item);
      idMap.put(id, item);
      inDegree.put(id, 0);
      graph.put(id, new ArrayList<>());
    }

    for (T item : items) {
      String id = idFn.apply(item);
      for (String dep : depsFn.apply(item)) {
        if (!idMap.containsKey(dep)) {
          throw new PluginException("Missing dependency: " + dep + " for plugin " + id);
        }
        graph.get(dep).add(id);
        inDegree.compute(id, (k, v) -> v + 1);
      }
    }

    Deque<String> queue = new ArrayDeque<>();
    for (var e : inDegree.entrySet()) {
      if (e.getValue() == 0) {
        queue.add(e.getKey());
      }
    }

    List<T> sorted = new ArrayList<>();
    while (!queue.isEmpty()) {
      String id = queue.remove();
      sorted.add(idMap.get(id));
      for (String nxt : graph.get(id)) {
        int deg = inDegree.compute(nxt, (k, v) -> v - 1);
        if (deg == 0) {
          queue.add(nxt);
        }
      }
    }

    if (sorted.size() < items.size()) {
      List<String> cycle =
          inDegree.entrySet().stream()
              .filter(e -> e.getValue() > 0)
              .map(Map.Entry::getKey)
              .toList();
      throw new PluginException("Cyclic dependency detected: " + cycle);
    }

    return sorted;
  }
}
