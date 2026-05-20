package pl.diagnode.backend.domain.service.handler.impl;

import org.junit.jupiter.api.Test;
import pl.diagnode.backend.domain.exception.ValidationException;
import pl.diagnode.backend.domain.model.InterviewContext;
import pl.diagnode.backend.domain.model.nodes.Node;
import pl.diagnode.backend.domain.model.nodes.OpenQuestionNode;
import pl.diagnode.backend.domain.port.AiAnalysisClient;
import pl.diagnode.backend.domain.service.handler.input.ChoiceAnswer;
import pl.diagnode.backend.domain.service.handler.input.OpenAnswer;

import java.awt.*;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class OpenQuestionHandlerTest {

    AiAnalysisClient fakeAi = userInput -> Map.of();
    OpenQuestionHandler openQuestionHandler = new OpenQuestionHandler(fakeAi);

    @Test
    void shouldThrowAnExceptionWhenReceivedEmptyInput() {
        //given

        //when

        //then
        assertThatExceptionOfType(ValidationException.class)
                .as("Odpowiedź na pytanie otwarte nie może być pusta")
                .isThrownBy(() -> openQuestionHandler.handle(Node.builder(new OpenQuestionNode()).build(), InterviewContext.builder().build(), new OpenAnswer("")))
                .withMessage("Odpowiedź nie może być pusta");
    }

    @Test
    void shouldThrowAnExceptionWhenReceivedInvalidInputType() {
        //given

        //when

        //then
        assertThatExceptionOfType(IllegalStateException.class)
                .as("Odpowiedź na pytanie otwarte musi być typu OpenAnswer")
                .isThrownBy(() -> openQuestionHandler.handle(Node.builder(new OpenQuestionNode()).build(), InterviewContext.builder().build(), new ChoiceAnswer("")))
                .withMessageContaining("Nieoczekiwany typ inputu");
    }
}
