package com.site.transmate.translation.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "transmate.aws.translate")
public record AwsTranslateProperties(String region) {
}
