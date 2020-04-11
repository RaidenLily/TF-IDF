package com.company.bean;

import lombok.Data;

import java.util.List;

@Data
public class IDFAndWb {
    private float IDF;
    private List<WbMessage> wbMessageList;
}
