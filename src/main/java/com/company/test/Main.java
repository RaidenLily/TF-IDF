package com.company.test;

import com.company.bean.IDFAndWb;
import com.company.bean.WbMessage;
import com.company.dao.WbMessageMapper;
import com.company.service.SQLConnect;
import org.ansj.domain.Result;
import org.ansj.domain.Term;

import org.ansj.library.AmbiguityLibrary;
import org.ansj.library.DicLibrary;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.MyStaticValue;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.domain.Value;
import org.nlpcn.commons.lang.tire.library.Library;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;


public class Main {

    static private HashMap<String,IDFAndWb> b=new HashMap<>();

    public static void main(String[] args) {
        WbMessageMapper wbMessageMapper = SQLConnect.getMapper();
        //从数据库中获取目标关键字的所有数据
        long startTime=System.currentTimeMillis();
        List<WbMessage> wbMessagesList = wbMessageMapper.getAllWbMessage();
        WbMessage oneWb=wbMessageMapper.getWbMessageById(347);
        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("获取数据时间： "+(endTime-startTime)+"ms");

            long startTime1 = System.currentTimeMillis();
            int msgCount = wbMessageMapper.getMsgCount();
            Iterator<WbMessage> wbMessageIterator = wbMessagesList.iterator();
            Participle.participleOne(oneWb, b);
            while (wbMessageIterator.hasNext()) {
                WbMessage wb = wbMessageIterator.next();
                if (wb.getId() == 347) {
                    continue;
                }
                Participle.participleAll(wb, b);
            }
        Participle.countIDF(b, msgCount);
        Participle.countCos(b, oneWb);
        long endTime1 = System.currentTimeMillis(); //获取结束时间
        System.out.println("分析时间： " + (endTime1 - startTime1) + "ms");
    }
}
