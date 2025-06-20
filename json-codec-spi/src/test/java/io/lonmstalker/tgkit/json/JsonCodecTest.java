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
package io.lonmstalker.tgkit.json;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Test;

class JsonCodecTest {

  private static final class JacksonCodec implements JsonCodec {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> void serialize(T obj, java.io.OutputStream out) throws Exception {
      mapper.writeValue(out, obj);
    }

    @Override
    public <T> T deserialize(java.io.InputStream in, Class<T> type) throws Exception {
      return mapper.readValue(in, type);
    }
  }

  @Test
  void serializes_and_deserializes() throws Exception {
    JacksonCodec codec = new JacksonCodec();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Sample sample = new Sample("test", 42);

    codec.serialize(sample, out);

    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    Sample copy = codec.deserialize(in, Sample.class);

    assertThat(copy).isEqualTo(sample);
  }

  private record Sample(String name, int value) {}
}
