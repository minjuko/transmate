package com.site.transmate.translation;

import java.util.List;

public record TranslationResult(String translatedText, List<TranslationTerm> terms) {

    public TranslationResult {
        terms = terms == null ? List.of() : List.copyOf(terms);
    }
}
