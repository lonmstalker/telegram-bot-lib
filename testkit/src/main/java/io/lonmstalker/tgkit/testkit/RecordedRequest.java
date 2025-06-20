package io.lonmstalker.tgkit.testkit;

import java.util.List;
import java.util.Map;

/** Информация о запросе, полученном {@link TelegramMockServer}. */
public record RecordedRequest(
    String method, String path, Map<String, List<String>> headers, String body) {}
