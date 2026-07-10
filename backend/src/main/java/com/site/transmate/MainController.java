package com.site.transmate;

import java.util.Map;

import org.springframework.web.bind.annotation.PatchMapping;
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

    @PatchMapping("/translate")
    public String translate(@RequestBody Map<String, String> requestData) {

        translate translateRequest = new translate();

        requestData.forEach((key, value) -> {

            if ("TerminologyNames".equals(key)) {
                translateRequest.TerminologyNames = value;
            }

            if ("SourceLanguageCode".equals(key)) {
                translateRequest.SourceLanguageCode = value;
            }

            if ("TargetLanguageCode".equals(key)) {
                translateRequest.TargetLanguageCode = value;
            }

            if ("Text".equals(key)) {
                translateRequest.Text = value;
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
                        .withText(translateRequest.Text)
                        .withTerminologyNames(
                                translateRequest.TerminologyNames
                        )
                        .withSourceLanguageCode(
                                translateRequest.SourceLanguageCode
                        )
                        .withTargetLanguageCode(
                                translateRequest.TargetLanguageCode
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

        String markedSourceText = translateRequest.Text.replace(
                sourceTerm,
                "[" + sourceTerm + "]"
        );

        System.out.println(markedSourceText);

        TranslateTextRequest markedTranslateRequest =
                new TranslateTextRequest()
                        .withText(markedSourceText)
                        .withTerminologyNames(
                                translateRequest.TerminologyNames
                        )
                        .withSourceLanguageCode(
                                translateRequest.SourceLanguageCode
                        )
                        .withTargetLanguageCode(
                                translateRequest.TargetLanguageCode
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