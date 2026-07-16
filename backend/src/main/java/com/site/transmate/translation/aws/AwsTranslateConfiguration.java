package com.site.transmate.translation.aws;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AwsTranslateProperties.class)
public class AwsTranslateConfiguration {

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        return DefaultAWSCredentialsProviderChain.getInstance();
    }

    @Bean
    public AmazonTranslate amazonTranslate(
            AwsTranslateProperties properties,
            AWSCredentialsProvider credentialsProvider
    ) {
        return AmazonTranslateClient.builder()
                .withCredentials(credentialsProvider)
                .withRegion(properties.region())
                .build();
    }
}
