package com.company.service;

import com.company.dao.WbMessageMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SQLConnect {
    public static WbMessageMapper getMapper(){
        ApplicationContext context
                = new ClassPathXmlApplicationContext("applicationContext.xml");
        SqlSessionFactory sessionFactory = (SqlSessionFactory) context.getBean("sqlSessionFactory");
        SqlSession sqlSession = sessionFactory.openSession();
        WbMessageMapper wbMessageMapper = sqlSession.getMapper(WbMessageMapper.class);
        return wbMessageMapper;
    }
}
