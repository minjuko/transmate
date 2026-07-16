package com.site.transmate.translation;

public record TranslationCommand(
        String text,
        String terminologyName,
        String sourceLanguageCode,
        String targetLanguageCode
) {

    public TranslationCommand withText(String replacementText) {
        return new TranslationCommand(
                replacementText,
                terminologyName,
                sourceLanguageCode,
                targetLanguageCode
        );
    }
}
