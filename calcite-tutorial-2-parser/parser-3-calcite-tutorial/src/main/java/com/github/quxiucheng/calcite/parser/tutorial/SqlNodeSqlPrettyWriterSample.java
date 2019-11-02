package com.github.quxiucheng.calcite.parser.tutorial;

import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.dialect.MysqlSqlDialect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.pretty.SqlPrettyWriter;

/**
 * @author quxiucheng
 * @date 2019-04-23 11:03:00
 */
public class SqlNodeSqlPrettyWriterSample {

    public static void main(String[] args) throws SqlParseException {
        SqlNodeSqlPrettyWriterSample sample = new SqlNodeSqlPrettyWriterSample();
        sample.format1();
        sample.format2();
    }

    public void format1() throws SqlParseException {
        SqlPrettyWriter prettyWriter = new SqlPrettyWriter(MysqlSqlDialect.DEFAULT);
        // Sql语句
        String sql = "select * from emps where id = 1";
        SqlParser.Config mysqlConfig = SqlParser.configBuilder().setLex(Lex.MYSQL).build();

        SqlParser parser = SqlParser.create("", mysqlConfig);
        // 解析sql
        SqlNode sqlNode = parser.parseQuery(sql);
        String format = prettyWriter.format(sqlNode);
        // 还原某个方言的SQL
        System.out.println(format);
    }

    public void format2() throws SqlParseException {
        SqlPrettyWriter prettyWriter = new SqlPrettyWriter(MysqlSqlDialect.DEFAULT);
        // Sql语句
        String sql = "select * from emps where id = 1";
        SqlParser.Config mysqlConfig = SqlParser.configBuilder().setLex(Lex.MYSQL).build();

        SqlParser parser = SqlParser.create("", mysqlConfig);
        // 解析sql
        SqlNode sqlNode = parser.parseQuery(sql);
        sqlNode.unparse(prettyWriter, 0, 0);
        System.out.println(prettyWriter.toSqlString());
    }
}
