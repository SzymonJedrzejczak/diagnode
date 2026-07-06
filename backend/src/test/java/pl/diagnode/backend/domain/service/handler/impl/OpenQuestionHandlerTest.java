package pl.diagnode.backend.domain.service.handler.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import pl.diagnode.backend.domain.exception.ValidationException;
import pl.diagnode.backend.domain.model.InterviewContext;
import pl.diagnode.backend.domain.model.nodes.InterludeNode;
import pl.diagnode.backend.domain.model.nodes.Node;
import pl.diagnode.backend.domain.model.nodes.OpenQuestionNode;
import pl.diagnode.backend.domain.model.enums.Category;
import pl.diagnode.backend.domain.port.AiAnalysisClient;
import pl.diagnode.backend.domain.service.handler.input.ChoiceAnswer;
import pl.diagnode.backend.domain.service.handler.input.OpenAnswer;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static pl.diagnode.backend.domain.model.enums.NodeType.OPEN_QUESTION;

class OpenQuestionHandlerTest {

    private final AiAnalysisClient dummyAiClient = input -> Map.of();
    private final OpenQuestionHandler handler = new OpenQuestionHandler(dummyAiClient);

    @Test
    void shouldReturnOpenQuestionAsSupportedType() {
        // expect
        assertThat(handler.getSupportedType()).isEqualTo(OPEN_QUESTION);
    }

    @Test
    void shouldThrowAnExceptionWhenReceivedInvalidInputType() {
        // given
        Node node = Node.builder(new OpenQuestionNode()).build();
        InterviewContext context = InterviewContext.builder().build();

        // when & then
        assertThatExceptionOfType(IllegalStateException.class)
                .as("Odpowiedź na pytanie otwarte musi być typu OpenAnswer")
                .isThrownBy(() -> handler.handle(node, context, new ChoiceAnswer("")))
                .withMessageContaining("Nieoczekiwany typ inputu");
    }

    @ParameterizedTest(name = "Zgłasza wyjątek walidacji dla wejścia: ''{0}''")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    void shouldThrowAnExceptionWhenReceivedBlankInput(String invalidInput) {
        // given
        Node node = Node.builder(new OpenQuestionNode()).build();
        InterviewContext context = InterviewContext.builder().build();

        // when & then
        assertThatExceptionOfType(ValidationException.class)
                .as("Odpowiedź na pytanie otwarte nie może być pusta")
                .isThrownBy(() -> handler.handle(node, context, new OpenAnswer(invalidInput)))
                .withMessage("Odpowiedź nie może być pusta");
    }

    @Test
    void shouldSaveProfileEntryAndAdvanceToNextNodeWhenCategoryIsEmpty() {
        // given
        UUID nodeId = UUID.randomUUID();
        UUID nextId = UUID.randomUUID();
        Node next = Node.builder(new InterludeNode()).id(nextId).build();

        Node node = Node.builder(new OpenQuestionNode())
                .id(nodeId)
                .mappingKey("firstName")
                .nextNode(next)
                .build();

        InterviewContext context = InterviewContext.builder().userId("user1").build();

        // when
        InterviewContext result = handler.handle(node, context, new OpenAnswer("Jan"));

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.profileData())
                    .as("Profil powinien zawierać dodany wpis")
                    .containsEntry("firstName", "Jan");

            softly.assertThat(result.currentNodeId())
                    .as("Kontekst powinien przejść do następnego węzła")
                    .isEqualTo(nextId);
        });
    }

    @Test
    void shouldStoreAnswerAndNotCallAi_whenCategoryPresentAndAiConsentFalse() {
        // given
        UUID nodeId = UUID.randomUUID();
        UUID nextId = UUID.randomUUID();
        Node next = Node.builder(new InterludeNode()).id(nextId).build();

        Node node = Node.builder(new OpenQuestionNode())
                .id(nodeId)
                .category(Category.ANXIETY)
                .nextNode(next)
                .build();

        AiAnalysisClient failingAi = userInput -> {
            throw new AssertionError("AI nie powinno być wołane przy braku zgody (aiConsent = false)!");
        };
        OpenQuestionHandler handlerWithFailingAi = new OpenQuestionHandler(failingAi);

        InterviewContext context = InterviewContext.builder()
                .userId("user-2")
                .aiConsentGiven(false)
                .build();

        // when
        InterviewContext result = handlerWithFailingAi.handle(node, context, new OpenAnswer("Czuję się źle"));

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.answers())
                    .as("Odpowiedź powinna zostać zapisana w mapie answers pod kluczem ID węzła")
                    .containsEntry(nodeId.toString(), "Czuję się źle");

            softly.assertThat(result.collectedPoints())
                    .as("Punkty diagnostyczne nie powinny zostać zaktualizowane bez zgody na AI")
                    .isEmpty();

            softly.assertThat(result.currentNodeId())
                    .as("Kontekst powinien przejść do następnego węzła")
                    .isEqualTo(nextId);
        });
    }

    @Test
    void shouldCallAiAndMergeCollectedPoints_whenAiConsentGiven() {
        // given
        UUID nodeId = UUID.randomUUID();
        UUID nextId = UUID.randomUUID();
        Node next = Node.builder(new InterludeNode()).id(nextId).build();

        Node node = Node.builder(new OpenQuestionNode())
                .id(nodeId)
                .category(Category.ANXIETY)
                .nextNode(next)
                .build();

        AiAnalysisClient aiStub = userInput -> Map.of("Anxiety", 3);
        OpenQuestionHandler handlerWithAi = new OpenQuestionHandler(aiStub);

        InterviewContext context = InterviewContext.builder()
                .userId("user-3")
                .aiConsentGiven(true)
                .build();

        // when
        InterviewContext result = handlerWithAi.handle(node, context, new OpenAnswer("Bardzo się martwię"));

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.answers())
                    .as("Odpowiedź powinna być zapisana pod kluczem ID węzła")
                    .containsEntry(nodeId.toString(), "Bardzo się martwię");

            softly.assertThat(result.collectedPoints())
                    .as("Wynik analizy AI powinien zostać dopisany do zebranych punktów (collectedPoints)")
                    .containsEntry("Anxiety", 3);

            softly.assertThat(result.currentNodeId())
                    .as("Kontekst powinien przejść do następnego węzła")
                    .isEqualTo(nextId);
        });
    }
}
