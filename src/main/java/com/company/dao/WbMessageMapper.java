package com.company.dao;



import com.company.bean.WbMessage;

import java.util.List;

public interface WbMessageMapper {
    /*public Page<WbMessage> getAllWbMessage();*/
    public List<WbMessage> getAllWbMessage();

    public WbMessage getWbMessageById(int id);

    public int getMsgCount();

/*    public void modifyWbMsg(WbMessage msg);

    public void batchDeleteMsg(int[] array);

    public void deleteMsg(int id);

    public int getMsgCount();*/
}
