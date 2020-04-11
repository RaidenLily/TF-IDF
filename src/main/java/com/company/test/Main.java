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

//测试
public class Main {

    public static void main(String[] args) throws InterruptedException {

        WbMessageMapper wbMessageMapper = SQLConnect.getMapper();

        //预先加载分词插件，不然很慢
        ToAnalysis.parse("哈哈");

        //从数据库中获取微博数据
        long startTime=System.currentTimeMillis();
        ArrayList<WbMessage> wbMessagesList = (ArrayList<WbMessage>) wbMessageMapper.getAllWbMessage();

        //目标微博
        WbMessage oneWb=wbMessageMapper.getWbMessageById(347);
        long endTime=System.currentTimeMillis();

        //输出从数据库获取数据的时间
        System.out.println("获取数据时间： "+(endTime-startTime)+"ms");

        long startTime1 = System.currentTimeMillis();

        //进行相似分析分析
        WbMessage[] noWbMessage = Participle.doAll(wbMessagesList,oneWb,5);

        long endTime1 = System.currentTimeMillis();

        //输出分析的时间
        System.out.println("分析时间： " + (endTime1 - startTime1) + "ms");

        for (int t=0;t<noWbMessage.length;t++){
            System.out.println("id:"+noWbMessage[t]);
        }
    }
}
