package io.lonmstalker.tgkit.doc.emitter;

import static org.assertj.core.api.Assertions.assertThat;

import io.lonmstalker.tgkit.doc.mapper.OperationInfo;
import io.swagger.v3.oas.models.OpenAPI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class OpenApiEmitterTest {
  @Test
  void buildsAndWritesYaml() throws Exception {
    OpenApiEmitter emitter = new OpenApiEmitter();
    List<OperationInfo> ops = List.of(new OperationInfo("getMe", "desc"));
    OpenAPI api = emitter.toOpenApi(ops);
    Path tmp = Files.createTempFile("openapi", ".yaml");
    emitter.write(api, tmp);
    String yaml = Files.readString(tmp);
    assertThat(yaml).contains("/getMe");
  }
}
