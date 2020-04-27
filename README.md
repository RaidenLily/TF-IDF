TF-IDF 
文本相似度计算Java版  
使用多线程进行分析计算，速度更快，默认使用四个线程。  
针对微博内容文本相似，当然还可以用于其他文本相似。

使用
==
主要类是Participle类，使用为test包下的Participle类的静态方法doAll，根据注释要求丢参数即可
入参主要为目标微博和所有微博  
微博格式为：  
微博id  
微博文本内容  
  
若要测试，可以使用blog.sql文件进行数据导入，数据库配置文件可能要按自己情况进行修改  
我这里是使用MySQL中存储数据的。

返回
==
这里对386条微博进行分析  
测试机器CPU为i5-7400

![](https://github.com/RaidenLily/TF-IDF/blob/master/result.jpg)

如果对你有帮助，请给颗星星，谢谢！
==
