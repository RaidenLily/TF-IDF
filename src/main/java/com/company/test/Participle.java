package com.company.test;

import com.company.bean.IDFAndWb;
import com.company.bean.WbMessage;
import com.company.entiy.WordInform;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Participle {
    private WbMessage noWbMessage[]=new WbMessage[10];

        public void participleOne(WbMessage wb,HashMap<String, IDFAndWb> b){

            HashMap<String,Integer> temp=new HashMap<>();

            Result a= ToAnalysis.parse(wb.getContent());

            int wordNumber=0;
            //List wordNumber=new ArrayList<String>();

            Iterator<Term> segTerms=a.iterator();
            while (segTerms.hasNext()) {
                Term tm=segTerms.next();
                String strNs=tm.getNatureStr();//获取词性
                if(strNs=="null") continue;
                char cns=strNs.charAt(0);//取词性第一个字母
                // cns=='t' || cns=='s' ||cns=='a' || cns=='r' || strNs.equals("mq") || cns=='q' || cns=='d' || cns=='y'  || cns=='x' ||strNs.equals("en")cns=='v' ||
                //http://nlpchina.github.io/ansj_seg/content.html?name=词性说明
                if(cns=='n' || //名词、时间词、处所词
                        cns=='f' || //方位词、动词、形容词
                        cns=='b' || cns=='z'//区别词、状态词、代词
                    //数词、数量词、副词
                ){//语气词、字符串x、英文
                    //介词p、连词c、助词u、叹词e、拟声词o、标点符号w、前缀h、后缀k不获取 ，数词m只获取其中mq数量词
                    String strNm=tm.getName();
                    if (strNm.length()<=1)
                        continue;
                    wordNumber++;
                    //wordNumber.add(strNm);

/*                    WordInform wordInform=new WordInform();
                    wordInform.setWord(strNm);*/

                    if(temp.containsKey(strNm)){
                        int count = temp.get(strNm);
                        count++;
                        temp.put(strNm,count);
                    }else {
                        temp.put(strNm,1);
                        if(b.containsKey(strNm)){
                            continue;
                        }else {
                            List<WbMessage> arrayList=new ArrayList<>();
                            IDFAndWb idfAndWb=new IDFAndWb();
                            idfAndWb.setWbMessageList(arrayList);
                            b.put(strNm,idfAndWb);
                        }
                    }
                    /*b.sort((oneMsgStruct developer, oneMsgStruct compareDeveloper)->((Integer) developer.count).compareTo(compareDeveloper.count));*/
                }
            }
            Iterator<Map.Entry<String, Integer>> iterator = temp.entrySet().iterator();
            List<WordInform> wordInformList=new ArrayList<>();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();
                WordInform wd=new WordInform();
                wd.setWord(entry.getKey());

                wd.setDF((float) entry.getValue()/wordNumber);
                //wd.setDF((float) entry.getValue()/wordNumber.size());
                wordInformList.add(wd);
            }
            wb.setWordInforms(wordInformList);
        }

    public void participleAll(WbMessage wbMessage, HashMap<String,IDFAndWb> b){

        HashMap<String,Integer> temp=new HashMap<>();

        Result a= ToAnalysis.parse(wbMessage.getContent());

        int wordNumber=0;
        //List wordNumber=new ArrayList<String>();

        Iterator<Term> segTerms=a.iterator();

        while (segTerms.hasNext()) {
            Term tm=segTerms.next();
            String strNs=tm.getNatureStr();//获取词性
            if(strNs=="null") continue;
            char cns=strNs.charAt(0);//取词性第一个字母
            // cns=='t' || cns=='s' ||cns=='a' || cns=='r' || strNs.equals("mq") || cns=='q' || cns=='d' || cns=='y'  || cns=='x' ||strNs.equals("en")cns=='v' ||
            //http://nlpchina.github.io/ansj_seg/content.html?name=词性说明
            if(cns=='n' || //名词、时间词、处所词
                    cns=='f' || //方位词、动词、形容词
                    cns=='b' || cns=='z'//区别词、状态词、代词
                //数词、数量词、副词
            ){//语气词、字符串x、英文
                //介词p、连词c、助词u、叹词e、拟声词o、标点符号w、前缀h、后缀k不获取 ，数词m只获取其中mq数量词
                String strNm=tm.getName();
                if (strNm.length()<=1)
                    continue;
/*                System.out.println(strNm);*/
/*                WordInform wordInform=new WordInform();
                wordInform.setWord(strNm);*/
                wordNumber++;
                //wordNumber.add(strNm);

                if(!b.containsKey(strNm)){
                    continue;
                }else if (temp.containsKey(strNm)){
                    int count = temp.get(strNm);
                    count++;
                    temp.put(strNm,count);
                }else {
                    temp.put(strNm,1);
                    b.get(strNm).getWbMessageList().add(wbMessage);
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
            //wd.setDF((float) entry.getValue()/wordNumber.size());
            wordInformList.add(wd);
        }
/*        System.out.println(wordInformList);*/
        wbMessage.setWordInforms(wordInformList);
    }

    public void countIDF(HashMap<String, IDFAndWb> b,int count){
        Iterator<Map.Entry<String, IDFAndWb>> iterator = b.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, IDFAndWb> entry = iterator.next();
            IDFAndWb idfAndWb = entry.getValue();
            idfAndWb.setIDF((float) Math.log((float) count/(idfAndWb.getWbMessageList().size()+1)));
/*            System.out.println(entry.getKey()+" "+((float) count/(idfAndWb.getWbMessageList().size()+1)));*/
        }
    }

    public List<WbMessage> countCos(HashMap<String, IDFAndWb> b,WbMessage wbMessage){
        Iterator<Map.Entry<String, IDFAndWb>> iterator = b.entrySet().iterator();
        float[] oneMsg =new float[b.size()];
        for (int i=0;i<b.size();i++){
            WordInform wdInform = wbMessage.getWordInforms().get(i);
            float DFIDF=b.get(wdInform.getWord()).getIDF()*wdInform.getDF();
            wdInform.setDFIDF(DFIDF);
            oneMsg[i]=DFIDF;
        }

        List<WbMessage> unique=new ArrayList<>(b.size()*20);
        /*a.contains()*/
        while (iterator.hasNext()) {
            Map.Entry<String, IDFAndWb> entry = iterator.next();
            List<WbMessage> list = entry.getValue().getWbMessageList();
            for (WbMessage message : list) {
                if (!unique.contains(message)) {
                    unique.add(message);
                }
            }
/*            a.addAll(entry.getValue().getWbMessageList());*/
        }
/*        List<WbMessage> unique = a.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(WbMessage::getId))), ArrayList::new)
        );*/
        for (int i=0;i<unique.size();i++){
            float[] AllMsg =new float[b.size()];
            WbMessage wb=unique.get(i);
            if(wb.getId()==wbMessage.getId()){
                continue;
            }else {
                ArrayList<WordInform> wordInformList= (ArrayList<WordInform>) wb.getWordInforms();
                for(int t=0;t<b.size();t++){
                    WordInform wdInform = wbMessage.getWordInforms().get(t);
                    int index=wordInformList.indexOf(wdInform);
                    if(index==-1){
                        AllMsg[t]=0;
                    }else {
                        AllMsg[t]=b.get(wdInform.getWord()).getIDF()*wordInformList.get(index).getDF();
                    }
                }
            }

            float sumUp=0f;
            float sumDown01=0f;
            float sumDown02=0f;
            for(int k=0;k<AllMsg.length;k++){
                sumUp=oneMsg[k]*AllMsg[k]+sumUp;
                sumDown01= (float) (sumDown01+Math.pow(oneMsg[k],2));
                sumDown02= (float) (sumDown02+Math.pow(AllMsg[k],2));
            }

            wb.setCos((float) (sumUp/(Math.sqrt(sumDown01)*Math.sqrt(sumDown02))));
            sort(wb);
        }

        for (int i=0;i<noWbMessage.length;i++){
            System.out.println(noWbMessage[i]);
        }
        return unique;
    }

    private void sort(WbMessage wbMessage){
            if (noWbMessage[9]==null){
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
}
