package com.site.transmate.translation;

import com.site.transmate.translation.dto.TranslateRequest;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

        List<TranslationTerm> terms = usableTerms(initialResult.terms());
        if (terms.isEmpty()) {
            return initialResult.translatedText();
        }

        String markedSourceText = markSourceTerms(request.text(), terms);
        if (markedSourceText.equals(request.text())) return initialResult.translatedText();

        TranslationResult markedResult = translationGateway.translate(
                command.withText(markedSourceText)
        );

        return removeTermMarkers(markedResult.translatedText(), terms);
    }

    private List<TranslationTerm> usableTerms(List<TranslationTerm> terms) {
        return terms.stream()
                .filter(term -> term.sourceText() != null && !term.sourceText().isBlank())
                .filter(term -> term.targetText() != null && !term.targetText().isBlank())
                .sorted(Comparator.comparingInt(
                        (TranslationTerm term) -> term.sourceText().length()
                ).reversed())
                .toList();
    }

    private String markSourceTerms(String text, List<TranslationTerm> terms) {
        String alternatives = terms.stream()
                .map(TranslationTerm::sourceText)
                .distinct()
                .map(Pattern::quote)
                .reduce((left, right) -> left + "|" + right)
                .orElseThrow();
        Matcher matcher = Pattern.compile(alternatives).matcher(text);
        return matcher.replaceAll(result ->
                Matcher.quoteReplacement("[" + result.group() + "]"));
    }

    private String removeTermMarkers(String text, List<TranslationTerm> terms) {
        String result = text;
        for (TranslationTerm term : terms) {
            result = result.replace("[" + term.targetText() + "]", term.targetText());
        }
        return result;
    }
}
