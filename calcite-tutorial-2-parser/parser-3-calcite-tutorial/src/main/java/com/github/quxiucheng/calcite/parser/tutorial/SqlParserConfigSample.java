package com.github.quxiucheng.calcite.parser.tutorial;

import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.avatica.util.Quoting;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParser;

/**
 * 大小写匹配 - sqlNode转relNode时生效
 * boolean caseSensitive();
 * sql模式
 * SqlConformance conformance();
 * 解析工厂
 * SqlParserImplFactory parserFactory();
 *
 * @author quxiucheng
 * @date 2019-04-22 14:04:00
 */
public class SqlParserConfigSample {


    public static void main(String[] args) throws Exception {
        SqlParserConfigSample sample = new SqlParserConfigSample();
        sample.unquotedCasing();
        sample.quotedCasing();
        sample.identifierMaxLength();
        sample.quoting();
    }
    /**
     * 转义字符外-大小写转换
     * @throws Exception
     */
    public void unquotedCasing() throws Exception {
        //  id因为在转义字符外面,所有转换成小写
        String sql = "select ID,`NAME` from hr.emp where dept_id=1";

        SqlParser.Config config = SqlParser.configBuilder()
                // 大小写转换
                .setUnquotedCasing(Casing.TO_LOWER)
                .setQuoting(Quoting.BACK_TICK)
                .build();
        SqlParser parser = SqlParser.create("", config);
        SqlNode sqlNode = parser.parseQuery(sql);
        System.out.println(sql);
        printSql(sqlNode);
    }

    /**
     * 转义字符内-大小写转换
     * @throws Exception
     */
    public void quotedCasing() throws Exception {
        // name因为在转义字符内,会转换成小写字母
        String sql = "select ID,`NAME` from hr.emp where dept_id=1";
        SqlParser.Config config =
                SqlParser.configBuilder()
                        // 大小写转换
                        .setQuotedCasing(Casing.TO_LOWER)
                        .setQuoting(Quoting.BACK_TICK)
                        .build();
        SqlParser parser = SqlParser.create("", config);
        SqlNode sqlNode = parser.parseQuery(sql);
        System.out.println(sql);
        printSql(sqlNode);
    }


    /**
     * 字段长度
     * @throws Exception
     */
    public void identifierMaxLength() throws Exception {
        // 设置字段长度最大个数
        try {
            String sql = "select id,NAME from hr.emp where dept_id=1";
            SqlParser.Config config =
                    SqlParser.configBuilder()
                            .setIdentifierMaxLength(3)
                            .build();
            SqlParser parser = SqlParser.create("", config);
            SqlNode sqlNode = parser.parseQuery(sql);
            System.out.println(sql);
            printSql(sqlNode);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }



    /**
     * 转义字符
     * @throws Exception
     */
    public void quoting() throws Exception {
        // 设置转义符号为``
        String sql = "select id,NAME from `hr.emp` where name=1";
        SqlParser.Config config =
                SqlParser.configBuilder()
                        .setQuoting(Quoting.BACK_TICK)
                        .build();
        SqlParser parser = SqlParser.create("", config);
        SqlNode sqlNode = parser.parseQuery(sql);
        System.out.println(sql);
        printSql(sqlNode);
    }

    public void printSql(SqlNode sqlNode) {
        System.out.println(sqlNode.toString().replace("\n", " "));
    }

}
