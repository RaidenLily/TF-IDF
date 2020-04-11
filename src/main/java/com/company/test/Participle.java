package com.company.test;

import com.company.bean.IDFAndWb;
import com.company.bean.WbMessage;
import com.company.entiy.ResultAndWb;
import com.company.entiy.WordInform;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Participle {

    static WbMessage[] doAll(ArrayList<WbMessage> wbMessageArrayList, WbMessage wb, int sortCount) throws InterruptedException {
        HashMap<String,IDFAndWb> b=new HashMap<>();
        LinkedList linkedList=new LinkedList();
        int[] lock=new int[0];
        int msgCount=wbMessageArrayList.size();

        participleOne(wb,b);

        //要开更多线程可以修改其他数量，默认为4个
        ExecutorService executors = Executors.newFixedThreadPool(4);
        executors.submit(new SplitThread(wbMessageArrayList,linkedList));
        executors.submit(new SplitThread(wbMessageArrayList,linkedList));
        executors.submit(new SplitThread(wbMessageArrayList,linkedList));
        executors.submit(new CountTFThread(linkedList,b,lock));

        //必须等待所有结果运算完毕才可以
        synchronized (lock){
            lock.wait();
        }
        executors.shutdown();
        countIDF(b, msgCount);
        return countCos(b, wb,sortCount);
    }

        private static void participleOne(WbMessage wb, HashMap<String, IDFAndWb> b){

            HashMap<String,Integer> temp=new HashMap<>();

            Result a= ToAnalysis.parse(wb.getContent());

            int wordNumber=0;

            for (Term tm : a) {
                String strNs = tm.getNatureStr();//获取词性
                if (strNs.equals("null")) continue;
                char cns = strNs.charAt(0);//取词性第一个字母
                // cns=='t' || cns=='s' ||cns=='a' || cns=='r' || strNs.equals("mq") || cns=='q' || cns=='d' || cns=='y'  || cns=='x' ||strNs.equals("en")cns=='v' ||
                //http://nlpchina.github.io/ansj_seg/content.html?name=词性说明
                if (cns == 'n' || //名词、时间词、处所词
                        cns == 'f' || //方位词、动词、形容词
                        cns == 'b' || cns == 'z'//区别词、状态词、代词
                    //数词、数量词、副词
                ) {//语气词、字符串x、英文
                    //介词p、连词c、助词u、叹词e、拟声词o、标点符号w、前缀h、后缀k不获取 ，数词m只获取其中mq数量词
                    String strNm = tm.getName();
                    if (strNm.length() <= 1)
                        continue;
                    wordNumber++;

                    if (temp.containsKey(strNm)) {
                        int count = temp.get(strNm);
                        count++;
                        temp.put(strNm, count);
                    } else {
                        temp.put(strNm, 1);
                        if (!b.containsKey(strNm)) {
                            List<WbMessage> arrayList = new ArrayList<>();
                            IDFAndWb idfAndWb = new IDFAndWb();
                            idfAndWb.setWbMessageList(arrayList);
                            b.put(strNm, idfAndWb);
                        }
                    }
                }
            }
            Iterator<Map.Entry<String, Integer>> iterator = temp.entrySet().iterator();
            List<WordInform> wordInformList=new ArrayList<>();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();
                WordInform wd=new WordInform();
                wd.setWord(entry.getKey());

                wd.setDF((float) entry.getValue()/wordNumber);
                wordInformList.add(wd);
            }
            wb.setWordInforms(wordInformList);
        }

    private static void countIDF(HashMap<String, IDFAndWb> b, int count){
        for (Map.Entry<String, IDFAndWb> entry : b.entrySet()) {
            IDFAndWb idfAndWb = entry.getValue();
            idfAndWb.setIDF((float) Math.log((float) count / (idfAndWb.getWbMessageList().size())));
        }
    }

    private static WbMessage[] countCos(HashMap<String, IDFAndWb> b, WbMessage wbMessage, int sortCount){
        if(sortCount<=0)
            throw new RuntimeException("排序的数量必须大于0");
        WbMessage[] noWbMessage =new WbMessage[sortCount];
        Iterator<Map.Entry<String, IDFAndWb>> iterator = b.entrySet().iterator();
        float[] oneMsg =new float[b.size()];
        for (int i=0;i<b.size();i++){
            WordInform wdInform = wbMessage.getWordInforms().get(i);
            float DFIDF=b.get(wdInform.getWord()).getIDF()*wdInform.getDF();
            wdInform.setDFIDF(DFIDF);
            oneMsg[i]=DFIDF;
        }

        List<WbMessage> unique=new ArrayList<>(b.size()*20);

        while (iterator.hasNext()) {
            Map.Entry<String, IDFAndWb> entry = iterator.next();
            List<WbMessage> list = entry.getValue().getWbMessageList();
            for (WbMessage message : list) {
                if (!unique.contains(message)) {
                    unique.add(message);
                }
            }
        }

        for (WbMessage message : unique) {
            float[] AllMsg = new float[b.size()];
            if (message.getId() == wbMessage.getId()) {
                continue;
            } else {
                ArrayList<WordInform> wordInformList = (ArrayList<WordInform>) message.getWordInforms();
                for (int t = 0; t < b.size(); t++) {
                    WordInform wdInform = wbMessage.getWordInforms().get(t);
                    int index = wordInformList.indexOf(wdInform);
                    if (index == -1) {
                        AllMsg[t] = 0;
                    } else {
                        AllMsg[t] = b.get(wdInform.getWord()).getIDF() * wordInformList.get(index).getDF();
                    }
                }
            }

            float sumUp = 0f;
            float sumDown01 = 0f;
            float sumDown02 = 0f;
            for (int k = 0; k < AllMsg.length; k++) {
                sumUp = oneMsg[k] * AllMsg[k] + sumUp;
                sumDown01 = (float) (sumDown01 + Math.pow(oneMsg[k], 2));
                sumDown02 = (float) (sumDown02 + Math.pow(AllMsg[k], 2));
            }

            message.setCos((float) (sumUp / (Math.sqrt(sumDown01) * Math.sqrt(sumDown02))));
            sort(message, noWbMessage);
        }

        return noWbMessage;
    }

    private static void sort(WbMessage wbMessage,WbMessage[] noWbMessage){
            if (noWbMessage[noWbMessage.length-1]==null){
                for (int i=0;i<noWbMessage.length;i++){
                    if (noWbMessage[i]==null){
                        noWbMessage[i]=wbMessage;
                        if (i==noWbMessage.length-1){
                            for (int t = 1; t < noWbMessage.length; t++) {
                                for (int k = t; k > 0; k--) {
                                    if (noWbMessage[k].getCos() > noWbMessage[k - 1].getCos()) {
                                        WbMessage temp = noWbMessage[k];
                                        noWbMessage[k] = noWbMessage[k - 1];
                                        noWbMessage[k - 1] = temp;
                                    }
                                }
                            }
                        }else {
                            break;
                        }
                    }
                }
            }else {
                if (wbMessage.getCos() > noWbMessage[noWbMessage.length-1].getCos()) {
                    noWbMessage[noWbMessage.length-1] = wbMessage;
                    for (int k = noWbMessage.length-1; k > 0; k--) {
                        if (noWbMessage[k].getCos() > noWbMessage[k - 1].getCos()) {
                            WbMessage temp2 = noWbMessage[k - 1];
                            noWbMessage[k - 1] = noWbMessage[k];
                            noWbMessage[k] = temp2;
                        } else {
                            break;
                        }
                    }
                }
            }
    }
    private static class SplitThread implements Runnable {

        private ArrayList<WbMessage> wbMessageArrayList;
        private WbMessage wbMessage;
        private LinkedList list;

        SplitThread(ArrayList<WbMessage> wbMessageArrayList, LinkedList list) {
            this.wbMessageArrayList = wbMessageArrayList;
            this.list = list;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (wbMessageArrayList) {
                    if(wbMessageArrayList.size()==0){
                        ResultAndWb rs = new ResultAndWb(true);
                        synchronized (list){
                            list.add(rs);
                            list.notifyAll();
                        }break;
                    }
                    wbMessage = wbMessageArrayList.get(wbMessageArrayList.size() - 1);
                    wbMessageArrayList.remove(wbMessageArrayList.size() - 1);
                }
                Result a = ToAnalysis.parse(wbMessage.getContent());
                ResultAndWb resultAndWb = new ResultAndWb(a, wbMessage,false);
                synchronized (list) {
                    while (list.size() == 50) {
                        try {
                            list.notifyAll();
                            list.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    list.add(resultAndWb);
                    list.notifyAll();
                }
            }
        }
    }

    private static class CountTFThread implements Runnable {

        private LinkedList list;
        private Result a;
        private ResultAndWb resultAndWb;
        private WbMessage wbMessage;
        private HashMap<String, IDFAndWb> b;
        private int t = 1;
        private int[] lock;

        CountTFThread(LinkedList list, HashMap<String, IDFAndWb> b, int[] lock) {
            this.list = list;
            this.b = b;
            this.lock = lock;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (list) {
                    while (list.isEmpty()) {
                        try {
                            list.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    resultAndWb = (ResultAndWb) list.get(0);
                    if (resultAndWb.isEnd()) {
                        list.remove(0);
                        if (t++ == 3) {
                            synchronized (lock) {
                                lock.notify();
                            }
                            break;
                        }
                        continue;
                    } else {
                        list.remove(0);
                        list.notify();
                    }
                }

                a = resultAndWb.getResult();
                wbMessage = resultAndWb.getWbMessage();

                int wordNumber = 0;

                HashMap<String, Integer> temp = new HashMap<>();

                for (Term tm : a) {
                    String strNs = tm.getNatureStr();//获取词性
                    if (strNs.equals("null")) continue;
                    char cns = strNs.charAt(0);//取词性第一个字母
                    // cns=='t' || cns=='s' ||cns=='a' || cns=='r' || strNs.equals("mq") || cns=='q' || cns=='d' || cns=='y'  || cns=='x' ||strNs.equals("en")cns=='v' ||
                    //http://nlpchina.github.io/ansj_seg/content.html?name=词性说明
                    if (cns == 'n' || //名词、时间词、处所词
                            cns == 'f' || //方位词、动词、形容词
                            cns == 'b' || cns == 'z'//区别词、状态词、代词
                        //数词、数量词、副词
                    ) {//语气词、字符串x、英文
                        //介词p、连词c、助词u、叹词e、拟声词o、标点符号w、前缀h、后缀k不获取 ，数词m只获取其中mq数量词
                        String strNm = tm.getName();
                        if (strNm.length() <= 1)
                            continue;
                        wordNumber++;

                        if (b.containsKey(strNm)) {
                            if (temp.containsKey(strNm)) {
                                int count = temp.get(strNm);
                                count++;
                                temp.put(strNm, count);
                            } else {
                                temp.put(strNm, 1);
                                b.get(strNm).getWbMessageList().add(wbMessage);
                            }
                        }
                    }
                }

                Iterator<Map.Entry<String, Integer>> iterator = temp.entrySet().iterator();
                List<WordInform> wordInformList = new ArrayList<>();
                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> entry = iterator.next();
                    WordInform wd = new WordInform();
                    wd.setWord(entry.getKey());
                    wd.setDF((float) entry.getValue() / wordNumber);
                    wordInformList.add(wd);
                }
                wbMessage.setWordInforms(wordInformList);
            }
        }
    }
}
