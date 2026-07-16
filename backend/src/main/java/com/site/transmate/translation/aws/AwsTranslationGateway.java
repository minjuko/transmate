package com.site.transmate.translation.aws;

import java.util.List;
import java.util.Optional;

import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.model.AppliedTerminology;
import com.amazonaws.services.translate.model.Term;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import com.site.transmate.translation.TranslationCommand;
import com.site.transmate.translation.TranslationGateway;
import com.site.transmate.translation.TranslationResult;
import com.site.transmate.translation.TranslationTerm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AwsTranslationGateway implements TranslationGateway {

    private final AmazonTranslate amazonTranslate;

    @Override
    public TranslationResult translate(TranslationCommand command) {
        TranslateTextRequest request = new TranslateTextRequest()
                .withText(command.text())
                .withTerminologyNames(command.terminologyName())
                .withSourceLanguageCode(command.sourceLanguageCode())
                .withTargetLanguageCode(command.targetLanguageCode());

        TranslateTextResult result = amazonTranslate.translateText(request);

        return new TranslationResult(
                result.getTranslatedText(),
                appliedTerms(result)
        );
    }

    private List<TranslationTerm> appliedTerms(TranslateTextResult result) {
        return Optional.ofNullable(result.getAppliedTerminologies())
                .orElseGet(List::of)
                .stream()
                .map(AppliedTerminology::getTerms)
                .filter(terms -> terms != null)
                .flatMap(List::stream)
                .map(this::toTranslationTerm)
                .toList();
    }

    private TranslationTerm toTranslationTerm(Term term) {
        return new TranslationTerm(term.getSourceText(), term.getTargetText());
    }
}
