package com.company.dao;

import com.company.bean.WbMessage;

import java.util.List;

public interface WbMessageMapper {

    public List<WbMessage> getAllWbMessage();

    public WbMessage getWbMessageById(int id);

}
