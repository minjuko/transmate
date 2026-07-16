package com.site.transmate.translation.aws;

import static org.assertj.core.api.Assertions.assertThat;

import com.amazonaws.ClientConfiguration;
import org.junit.jupiter.api.Test;

class AwsTranslateConfigurationTest {

    @Test
    void appliesConfiguredTimeoutsAndRetryCount() {
        AwsTranslateProperties properties = new AwsTranslateProperties(
                "ap-northeast-2",
                3_000,
                10_000,
                2
        );
        AwsTranslateConfiguration configuration = new AwsTranslateConfiguration();

        ClientConfiguration clientConfiguration =
                configuration.awsTranslateClientConfiguration(properties);

        assertThat(clientConfiguration.getConnectionTimeout()).isEqualTo(3_000);
        assertThat(clientConfiguration.getSocketTimeout()).isEqualTo(10_000);
        assertThat(clientConfiguration.getMaxErrorRetry()).isEqualTo(2);
    }
}
