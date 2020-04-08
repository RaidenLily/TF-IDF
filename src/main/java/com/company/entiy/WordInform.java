package com.company.entiy;

import lombok.Data;

@Data
public class WordInform {
    private String word;
    private int count;
    private float DF;
    private int exitCount=1;
    private float IDF;
    private float DFIDF;

    @Override
    public String toString() {
/*        if (count==1){
            return null;
        }*/
        return word+" "+DF;
    }

    @Override
    public boolean equals(Object obj) {
        return this.word.equals(((WordInform) obj).getWord());
    }
}
