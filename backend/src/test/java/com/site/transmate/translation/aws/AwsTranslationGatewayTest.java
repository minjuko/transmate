package com.site.transmate.translation.aws;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.model.AppliedTerminology;
import com.amazonaws.services.translate.model.Term;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import com.site.transmate.translation.TranslationCommand;
import com.site.transmate.translation.TranslationResult;
import com.site.transmate.translation.TranslationTerm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AwsTranslationGatewayTest {

    @Mock
    private AmazonTranslate amazonTranslate;

    @Test
    void mapsCommandAndAwsResultWithoutCallingRealAws() {
        TranslateTextResult awsResult = new TranslateTextResult()
                .withTranslatedText("번역 결과")
                .withAppliedTerminologies(new AppliedTerminology()
                        .withTerms(new Term()
                                .withSourceText("source term")
                                .withTargetText("대상 용어")));
        when(amazonTranslate.translateText(any(TranslateTextRequest.class)))
                .thenReturn(awsResult);
        AwsTranslationGateway gateway = new AwsTranslationGateway(amazonTranslate);

        TranslationResult result = gateway.translate(new TranslationCommand(
                "source text",
                "category",
                "en",
                "ko"
        ));

        ArgumentCaptor<TranslateTextRequest> requestCaptor =
                ArgumentCaptor.forClass(TranslateTextRequest.class);
        verify(amazonTranslate).translateText(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getText()).isEqualTo("source text");
        assertThat(requestCaptor.getValue().getTerminologyNames())
                .containsExactly("category");
        assertThat(requestCaptor.getValue().getSourceLanguageCode()).isEqualTo("en");
        assertThat(requestCaptor.getValue().getTargetLanguageCode()).isEqualTo("ko");
        assertThat(result.translatedText()).isEqualTo("번역 결과");
        assertThat(result.terms()).containsExactly(
                new TranslationTerm("source term", "대상 용어")
        );
    }
}
