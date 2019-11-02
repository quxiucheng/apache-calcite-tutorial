package com.github.quxiucheng.calcite.parser.tutorial.sample;

import com.github.quxiucheng.calcite.parser.tutorial.CustomSqlParserImpl;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.parser.SqlParser;

/**
 * @author quxiucheng
 * @date 2019-04-24 15:47:00
 */
public abstract class AbstractSample {
    public static SqlParser.Config mysqlConfig = SqlParser.configBuilder()
            // 定义解析工厂
            .setParserFactory(CustomSqlParserImpl.FACTORY)
            .setLex(Lex.MYSQL)
            .build();
    public static SqlParser parser = SqlParser.create("", mysqlConfig);

}
