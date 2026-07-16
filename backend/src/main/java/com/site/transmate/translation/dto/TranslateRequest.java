package com.site.transmate.translation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TranslateRequest(
        @JsonProperty("Text") String text,
        @JsonProperty("TerminologyNames") String terminologyNames,
        @JsonProperty("SourceLanguageCode") String sourceLanguageCode,
        @JsonProperty("TargetLanguageCode") String targetLanguageCode
) {
}
