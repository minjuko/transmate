package com.site.transmate.translation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import com.site.transmate.translation.dto.TranslateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TranslateServiceTest {

    @Mock
    private TranslationGateway translationGateway;

    @InjectMocks
    private TranslateService translateService;

    @Test
    void returnsInitialTranslationWhenNoTerminologyWasApplied() {
        TranslationCommand command = command("Hello");
        when(translationGateway.translate(command))
                .thenReturn(new TranslationResult("안녕하세요", List.of()));

        String result = translateService.translate(request("Hello"));

        assertThat(result).isEqualTo("안녕하세요");
        verify(translationGateway).translate(command);
    }

    @Test
    void marksAppliedTerminologyAndRemovesMarkersFromFinalTranslation() {
        TranslationCommand initialCommand = command("Use Amazon Translate");
        TranslationCommand markedCommand = command("Use [Amazon Translate]");
        when(translationGateway.translate(initialCommand))
                .thenReturn(new TranslationResult(
                        "Amazon Translate를 사용하세요",
                        List.of(new TranslationTerm("Amazon Translate", "Amazon 번역"))
                ));
        when(translationGateway.translate(markedCommand))
                .thenReturn(new TranslationResult(
                        "[Amazon 번역]을 사용하세요",
                        List.of()
                ));

        String result = translateService.translate(request("Use Amazon Translate"));

        assertThat(result).isEqualTo("Amazon 번역을 사용하세요");
        ArgumentCaptor<TranslationCommand> commandCaptor =
                ArgumentCaptor.forClass(TranslationCommand.class);
        verify(translationGateway, org.mockito.Mockito.times(2))
                .translate(commandCaptor.capture());
        assertThat(commandCaptor.getAllValues())
                .containsExactly(initialCommand, markedCommand);
    }

    @Test
    void handlesMultipleAndOverlappingAppliedTermsInOnePass() {
        TranslationCommand initialCommand = command(
                "Use Amazon Translate and Translate"
        );
        TranslationCommand markedCommand = command(
                "Use [Amazon Translate] and [Translate]"
        );
        when(translationGateway.translate(initialCommand))
                .thenReturn(new TranslationResult(
                        "Amazon Translate와 Translate를 사용하세요",
                        List.of(
                                new TranslationTerm("Translate", "번역"),
                                new TranslationTerm("Amazon Translate", "Amazon 번역")
                        )
                ));
        when(translationGateway.translate(markedCommand))
                .thenReturn(new TranslationResult(
                        "[Amazon 번역]과 [번역]을 사용하세요",
                        List.of()
                ));

        String result = translateService.translate(request(
                "Use Amazon Translate and Translate"
        ));

        assertThat(result).isEqualTo("Amazon 번역과 번역을 사용하세요");
        verify(translationGateway).translate(initialCommand);
        verify(translationGateway).translate(markedCommand);
    }

    @Test
    void returnsInitialResultWhenAppliedTermsCannotBeMarked() {
        TranslationCommand command = command("Hello");
        when(translationGateway.translate(command))
                .thenReturn(new TranslationResult(
                        "안녕하세요",
                        List.of(
                                new TranslationTerm("", "빈 용어"),
                                new TranslationTerm("missing", "누락")
                        )
                ));

        String result = translateService.translate(request("Hello"));

        assertThat(result).isEqualTo("안녕하세요");
        verify(translationGateway).translate(command);
        verifyNoMoreInteractions(translationGateway);
    }

    private TranslateRequest request(String text) {
        return new TranslateRequest(text, "category", "en", "ko");
    }

    private TranslationCommand command(String text) {
        return new TranslationCommand(text, "category", "en", "ko");
    }
}
