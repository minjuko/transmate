package com.site.ssk;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/*
 * import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClient;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
*/

@Controller
public class HelloController {
    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "Hello ssk@###!!";
    }
    
    /*
    @GetMapping("/translate")
    @ResponseBody
    public String test() {
    	
    	AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();
        
        AmazonTranslate translate = AmazonTranslateClient.builder()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds.getCredentials()))
                .withRegion("ap-northeast-2")
                .build();
 
        TranslateTextRequest request = new TranslateTextRequest()
                .withText("Actionable Subsidies are not prohibited")
                .withTerminologyNames("PORT")
                .withSourceLanguageCode("en")
                .withTargetLanguageCode("ko");
        TranslateTextResult result  = translate.translateText(request);
        
        return result.getTranslatedText();
    }
    */
}