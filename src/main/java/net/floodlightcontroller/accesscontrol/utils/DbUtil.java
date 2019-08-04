package net.floodlightcontroller.accesscontrol.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class DbUtil {
    //定义一个sqlSessionFactory对象
    private static SqlSessionFactory sqlSessionFactory = null;

    //初始化对象
    static {
        try {
            InputStream inputStream = Resources.getResourceAsStream("net/floodlightcontroller/accesscontrol/config/mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SqlSession getSqlSession() {
        return sqlSessionFactory.openSession(true);
    }
}
