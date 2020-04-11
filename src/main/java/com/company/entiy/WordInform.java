package com.company.entiy;

import lombok.Data;

@Data
public class WordInform {
    private String word;
    private float DF;
    private float IDF;
    private float DFIDF;

    @Override
    public String toString() {
        return word+" "+DF;
    }

    @Override
    public boolean equals(Object obj) {
        return this.word.equals(((WordInform) obj).getWord());
    }
}
