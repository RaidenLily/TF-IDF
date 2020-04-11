package com.company.bean;

import com.company.entiy.WordInform;
import lombok.Data;

import java.util.List;

@Data
public class WbMessage{
    private int id;
    private String content;
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
