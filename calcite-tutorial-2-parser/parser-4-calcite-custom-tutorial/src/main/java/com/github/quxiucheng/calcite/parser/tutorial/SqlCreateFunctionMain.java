package com.github.quxiucheng.calcite.parser.tutorial;

import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.dialect.OracleSqlDialect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;

/**
 * @author quxiucheng
 * @date 2019-04-23 20:34:00
 */
public class SqlCreateFunctionMain {
    public static void main(String[] args) throws SqlParseException {
        // 解析配置 - mysql设置
        SqlParser.Config mysqlConfig = SqlParser.configBuilder()
                // 定义解析工厂
                .setParserFactory(CustomSqlParserImpl.FACTORY)
                .setLex(Lex.MYSQL)
                .build();
        // 创建解析器
        SqlParser parser = SqlParser.create("", mysqlConfig);
        // Sql语句
        String sql = "create function " +
                "hr.custom_function as 'com.github.quxiucheng.calcite.func.CustomFunction' " +
                "method 'eval'  " +
                "property ('a'='b','c'='1') ";
        // 解析sql
        SqlNode sqlNode = parser.parseQuery(sql);
        // 还原某个方言的SQL
        System.out.println(sqlNode.toSqlString(OracleSqlDialect.DEFAULT));
    }
}
