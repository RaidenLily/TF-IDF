package com.company.bean;

import com.company.entiy.WordInform;
import lombok.Data;

import java.util.List;

@Data
public class WbMessage{
    private int id;
/*    private WbAuthor authorName;
    private Keyword keyword;*/
    private String content;
/*    private Integer posOrNeg;
    private double mark;
    private double score;
    private Integer grade;*/
    private float cos;

    public String toString(){
        return id+ ": "+cos;
    }
    private List<WordInform> wordInforms;

    @Override
    public boolean equals(Object obj) {
        return ((WbMessage) obj).getId()==this.id;
    }

}
