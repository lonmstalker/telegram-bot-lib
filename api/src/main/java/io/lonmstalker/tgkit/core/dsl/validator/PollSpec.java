package io.lonmstalker.tgkit.core.dsl.validator;


import java.util.List;

/* Вспомогательный record для опросов */
public record PollSpec(String question, List<String> options, Integer correct) {}