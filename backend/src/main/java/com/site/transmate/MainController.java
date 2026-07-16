package com.site.transmate;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClient;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController // Rest API용 컨트롤러, 데이터(JSON) 반환
public class MainController {

    @PostMapping("/translate")
    public String translate(@RequestBody Map<String, String> requestData) {

        TranslateRequest translateRequest = new TranslateRequest();

        requestData.forEach((key, value) -> {

            if ("TerminologyNames".equals(key)) {
                translateRequest.setTerminologyNames(value);
            }

            if ("SourceLanguageCode".equals(key)) {
                translateRequest.setSourceLanguageCode(value);
            }

            if ("TargetLanguageCode".equals(key)) {
                translateRequest.setTargetLanguageCode(value);
            }

            if ("Text".equals(key)) {
                translateRequest.setText(value);
            }
        });

        AWSCredentialsProvider awsCredentialsProvider =
                DefaultAWSCredentialsProviderChain.getInstance();

        AmazonTranslate amazonTranslateClient = AmazonTranslateClient.builder()
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                awsCredentialsProvider.getCredentials()
                        )
                )
                .withRegion("ap-northeast-2")
                .build();

        TranslateTextRequest initialTranslateRequest =
                new TranslateTextRequest()
                        .withText(translateRequest.getText())
                        .withTerminologyNames(
                                translateRequest.getTerminologyNames()
                        )
                        .withSourceLanguageCode(
                                translateRequest.getSourceLanguageCode()
                        )
                        .withTargetLanguageCode(
                                translateRequest.getTargetLanguageCode()
                        );

        TranslateTextResult initialTranslateResult =
                amazonTranslateClient.translateText(
                        initialTranslateRequest
                );

        System.out.println(
                initialTranslateResult.getTranslatedText()
        );

        System.out.println(
                initialTranslateResult
                        .getAppliedTerminologies()
                        .get(0)
                        .getTerms()
                        .get(0)
                        .getSourceText()
        );

        String sourceTerm = initialTranslateResult
                .getAppliedTerminologies()
                .get(0)
                .getTerms()
                .get(0)
                .getSourceText();

        String translatedTargetTerm = initialTranslateResult
                .getAppliedTerminologies()
                .get(0)
                .getTerms()
                .get(0)
                .getTargetText();

        String markedSourceText = translateRequest.getText().replace(
                sourceTerm,
                "[" + sourceTerm + "]"
        );

        System.out.println(markedSourceText);

        TranslateTextRequest markedTranslateRequest =
                new TranslateTextRequest()
                        .withText(markedSourceText)
                        .withTerminologyNames(
                                translateRequest.getTerminologyNames()
                        )
                        .withSourceLanguageCode(
                                translateRequest.getSourceLanguageCode()
                        )
                        .withTargetLanguageCode(
                                translateRequest.getTargetLanguageCode()
                        );

        TranslateTextResult markedTranslateResult =
                amazonTranslateClient.translateText(
                        markedTranslateRequest
                );

        String markedTranslatedText =
                markedTranslateResult.getTranslatedText();

        int targetTermIndex =
                markedTranslatedText.indexOf(translatedTargetTerm);

        String finalTranslatedText =
                markedTranslatedText.substring(
                        0,
                        targetTermIndex - 1
                )
                        + translatedTargetTerm
                        + markedTranslatedText.substring(
                        targetTermIndex
                                + translatedTargetTerm.length()
                                + 2
                );

        return finalTranslatedText;
    }
}
