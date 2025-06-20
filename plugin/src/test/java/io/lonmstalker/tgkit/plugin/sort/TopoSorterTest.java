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
package io.lonmstalker.tgkit.plugin.internal.sort;

import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.plugin.PluginException;
import java.util.List;
import org.junit.jupiter.api.Test;

class TopoSorterTest {

  private static class Node {
    final String id;
    final List<String> deps;

    Node(String id, List<String> deps) {
      this.id = id;
      this.deps = deps;
    }

    String getId() {
      return id;
    }

    List<String> getDeps() {
      return deps;
    }
  }

  @Test
  void testSimpleChain() {
    Node a = new Node("A", List.of("B"));
    Node b = new Node("B", List.of("C"));
    Node c = new Node("C", List.of());
    var sorted = TopoSorter.sort(List.of(a, b, c), Node::getId, Node::getDeps);
    assertEquals(List.of("C", "B", "A"), sorted.stream().map(Node::getId).toList());
  }

  @Test
  void testEmptyDependencies() {
    Node x = new Node("X", List.of());
    Node y = new Node("Y", List.of());
    Node z = new Node("Z", List.of());
    var sorted = TopoSorter.sort(List.of(x, y, z), Node::getId, Node::getDeps);
    assertEquals(3, sorted.size());
    assertTrue(sorted.stream().map(Node::getId).toList().containsAll(List.of("X", "Y", "Z")));
  }

  @Test
  void testMissingDependency() {
    Node a = new Node("A", List.of("X"));
    Node b = new Node("B", List.of());
    var ex =
        assertThrows(
            PluginException.class,
            () -> TopoSorter.sort(List.of(a, b), Node::getId, Node::getDeps));
    assertTrue(ex.getMessage().contains("Missing dependency: X"));
  }

  @Test
  void testCyclicDependency() {
    Node a = new Node("A", List.of("B"));
    Node b = new Node("B", List.of("A"));
    var ex =
        assertThrows(
            PluginException.class,
            () -> TopoSorter.sort(List.of(a, b), Node::getId, Node::getDeps));
    assertTrue(ex.getMessage().contains("Cyclic dependency detected"));
    assertTrue(ex.getMessage().contains("A"));
    assertTrue(ex.getMessage().contains("B"));
  }
}
