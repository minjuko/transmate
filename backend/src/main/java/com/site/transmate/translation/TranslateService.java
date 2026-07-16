package com.site.transmate.translation;

import com.site.transmate.translation.dto.TranslateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TranslateService {

    private final TranslationGateway translationGateway;

    public String translate(TranslateRequest request) {
        TranslationCommand command = new TranslationCommand(
                request.text(),
                request.terminologyNames(),
                request.sourceLanguageCode(),
                request.targetLanguageCode()
        );

        TranslationResult initialResult = translationGateway.translate(command);

        if (initialResult.terms().isEmpty()) {
            return initialResult.translatedText();
        }

        TranslationTerm term = initialResult.terms().get(0);
        String markedSourceText = request.text().replace(
                term.sourceText(),
                "[" + term.sourceText() + "]"
        );
        TranslationResult markedResult = translationGateway.translate(
                command.withText(markedSourceText)
        );

        return removeTermMarkers(markedResult.translatedText(), term.targetText());
    }

    private String removeTermMarkers(String text, String targetTerm) {
        return text.replace("[" + targetTerm + "]", targetTerm);
    }
}
