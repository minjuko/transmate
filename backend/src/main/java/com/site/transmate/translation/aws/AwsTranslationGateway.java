package com.site.transmate.translation.aws;

import java.util.List;
import java.util.Optional;

import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.translate.model.AppliedTerminology;
import com.amazonaws.services.translate.model.Term;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import com.site.transmate.translation.TranslationCommand;
import com.site.transmate.translation.TranslationGateway;
import com.site.transmate.translation.TranslationProviderException;
import com.site.transmate.translation.TranslationRequestException;
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
                .withSourceLanguageCode(command.sourceLanguageCode())
                .withTargetLanguageCode(command.targetLanguageCode());
        if (command.terminologyName() != null && !command.terminologyName().isBlank()) {
            request.withTerminologyNames(command.terminologyName());
        }

        TranslateTextResult result;
        try {
            result = amazonTranslate.translateText(request);
        } catch (AmazonServiceException exception) {
            if (isInvalidRequest(exception)) {
                throw new TranslationRequestException(
                        "번역 요청을 처리할 수 없습니다.",
                        exception
                );
            }
            throw providerUnavailable(exception);
        } catch (AmazonClientException exception) {
            throw providerUnavailable(exception);
        }

        return new TranslationResult(
                result.getTranslatedText(),
                appliedTerms(result)
        );
    }

    private boolean isInvalidRequest(AmazonServiceException exception) {
        return exception.getStatusCode() >= 400 && exception.getStatusCode() < 500
                && exception.getStatusCode() != 401
                && exception.getStatusCode() != 403
                && exception.getStatusCode() != 429;
    }

    private TranslationProviderException providerUnavailable(Exception cause) {
        return new TranslationProviderException(
                "번역 서비스를 일시적으로 사용할 수 없습니다.",
                cause
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
