package io.lonmstalker.tgkit.core.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;

/** Проверка опросов и викторин. */
public class PollQuizTest {
    @Test
    void pollAndQuiz() {
        SendPoll poll = (SendPoll) BotDSL.poll(TestUtils.request(1), "Q")
                .option("A").option("B").chat(1).build();
        assertThat(poll.getQuestion()).isEqualTo("Q");
        SendPoll quiz = (SendPoll) BotDSL.quiz(TestUtils.request(1), "2+2", 1)
                .option("3").option("4").chat(1).build();
        assertThat(quiz.getCorrectOptionId()).isEqualTo(1);
    }
}
