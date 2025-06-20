package io.lonmstalker.tgkit.doc.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import io.lonmstalker.tgkit.doc.scraper.MethodDoc;
import org.junit.jupiter.api.Test;

class MethodDocMapperTest {
  @Test
  void mapsMethodDoc() {
    MethodDoc doc = new MethodDoc("getMe", "desc");
    OperationInfo info = MethodDocMapper.INSTANCE.toOperation(doc);
    assertThat(info.name()).isEqualTo("getMe");
  }
}
