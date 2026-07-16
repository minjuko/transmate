package com.site.transmate.translation.aws;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.translate.model.AppliedTerminology;
import com.amazonaws.services.translate.model.Term;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import com.site.transmate.translation.TranslationCommand;
import com.site.transmate.translation.TranslationResult;
import com.site.transmate.translation.TranslationTerm;
import com.site.transmate.translation.TranslationProviderException;
import com.site.transmate.translation.TranslationRequestException;
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

    @Test
    void omitsTerminologyFromAwsRequestWhenNameIsBlank() {
        when(amazonTranslate.translateText(any(TranslateTextRequest.class)))
                .thenReturn(new TranslateTextResult().withTranslatedText("번역 결과"));
        AwsTranslationGateway gateway = new AwsTranslationGateway(amazonTranslate);

        gateway.translate(new TranslationCommand("source text", "  ", "en", "ko"));

        ArgumentCaptor<TranslateTextRequest> requestCaptor =
                ArgumentCaptor.forClass(TranslateTextRequest.class);
        verify(amazonTranslate).translateText(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getTerminologyNames()).isNullOrEmpty();
    }

    @Test
    void mapsInvalidAwsRequestWithoutExposingSdkDetails() {
        AmazonServiceException awsFailure = serviceFailure(400);
        when(amazonTranslate.translateText(any(TranslateTextRequest.class)))
                .thenThrow(awsFailure);
        AwsTranslationGateway gateway = new AwsTranslationGateway(amazonTranslate);

        assertThatThrownBy(() -> gateway.translate(command()))
                .isInstanceOf(TranslationRequestException.class)
                .hasMessage("번역 요청을 처리할 수 없습니다.")
                .hasCause(awsFailure);
    }

    @Test
    void mapsAwsServiceOutageToProviderFailure() {
        AmazonServiceException awsFailure = serviceFailure(500);
        when(amazonTranslate.translateText(any(TranslateTextRequest.class)))
                .thenThrow(awsFailure);
        AwsTranslationGateway gateway = new AwsTranslationGateway(amazonTranslate);

        assertThatThrownBy(() -> gateway.translate(command()))
                .isInstanceOf(TranslationProviderException.class)
                .hasMessage("번역 서비스를 일시적으로 사용할 수 없습니다.")
                .hasCause(awsFailure);
    }

    @Test
    void mapsCredentialsOrNetworkFailureToProviderFailure() {
        AmazonClientException awsFailure =
                new AmazonClientException("sensitive credential or network details");
        when(amazonTranslate.translateText(any(TranslateTextRequest.class)))
                .thenThrow(awsFailure);
        AwsTranslationGateway gateway = new AwsTranslationGateway(amazonTranslate);

        assertThatThrownBy(() -> gateway.translate(command()))
                .isInstanceOf(TranslationProviderException.class)
                .hasMessage("번역 서비스를 일시적으로 사용할 수 없습니다.")
                .hasCause(awsFailure);
    }

    private AmazonServiceException serviceFailure(int statusCode) {
        AmazonServiceException exception =
                new AmazonServiceException("sensitive AWS details");
        exception.setStatusCode(statusCode);
        return exception;
    }

    private TranslationCommand command() {
        return new TranslationCommand("source text", "category", "en", "ko");
    }
}
