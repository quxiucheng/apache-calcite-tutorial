package com.github.quxiucheng.tutorial.rule;

import com.github.quxiucheng.tutorial.common.parser.SqlParserBase;
import com.github.quxiucheng.tutorial.common.sql2rel.SqlToRelConverterBase;
import com.github.quxiucheng.tutorial.common.sql2rel.TutorialSqlToRelConverter;
import com.google.common.collect.Lists;
import org.apache.calcite.config.Lex;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgram;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.metadata.DefaultRelMetadataProvider;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql2rel.SqlToRelConverter;

import java.util.List;

/**
 * @author quxiucheng
 * @date 2019-01-31 15:43:00
 */
public class RuleTester {

    /**
     * sql默认解析和优化后的规则对比
     *
     * @param sql
     * @param rules
     */
    public static void printOriginalCompare(String sql, RelOptRule... rules) {
        HepProgramBuilder hepProgramBuilder = new HepProgramBuilder();
        printOriginalCompare(sql, hepProgramBuilder, rules);
    }

    public static void printOriginalCompare(String sql, HepProgramBuilder builder, RelOptRule... rules) {
        try {
            SqlParser.Config mysql = SqlParser.configBuilder().setLex(Lex.MYSQL).setCaseSensitive(false).build();
            SqlToRelConverter.Config aDefault = SqlToRelConverterBase.DEFAULT;

            SqlParserBase sqlParserBase = new SqlParserBase();
            SqlNode sqlNode = sqlParserBase.parseQuery(sql);
            SqlToRelConverter sqlToRelConverter = TutorialSqlToRelConverter.createSqlToRelConverter(mysql, aDefault);
            RelRoot relRoot = sqlToRelConverter.convertQuery(sqlNode, true, true);
            RelNode original = relRoot.rel;
            for (RelOptRule rule : rules) {
                builder.addRuleInstance(rule);
            }
            HepProgram program = builder.build();
            HepPlanner hepPlanner = new HepPlanner(program);
            hepPlanner.registerMetadataProviders(Lists.newArrayList(DefaultRelMetadataProvider.INSTANCE));
            hepPlanner.setRoot(original);
            // hepPlanner优化
            RelNode bestExp = hepPlanner.findBestExp();
            System.out.println("sql:");
            System.out.println(sql);
            System.out.println();
            System.out.println("原始:");
            System.out.println(RelOptUtil.toString(original));
            System.out.println();
            System.out.println("优化后:");
            System.out.println(RelOptUtil.toString(bestExp));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 对比两个rule
     * @param sql
     * @param preRules
     * @param rules
     */
    public static void printRuleCompare(String sql, List<RelOptRule> preRules, List<RelOptRule> rules) {
        try {
            SqlParser.Config mysql = SqlParser.configBuilder().setLex(Lex.MYSQL).setCaseSensitive(false).build();
            SqlToRelConverter.Config aDefault = SqlToRelConverterBase.DEFAULT;

            SqlParserBase sqlParserBase = new SqlParserBase();
            SqlNode sqlNode = sqlParserBase.parseQuery(sql);
            SqlToRelConverter sqlToRelConverter = TutorialSqlToRelConverter.createSqlToRelConverter(mysql, aDefault);
            RelRoot relRoot = sqlToRelConverter.convertQuery(sqlNode, true, true);
            // 原始
            RelNode original = relRoot.rel;

            // pre
            HepProgramBuilder preBuilder = HepProgram.builder();
            for (RelOptRule preRule : preRules) {
                preBuilder.addRuleInstance(preRule);
            }
            HepProgram preProgram = preBuilder.build();
            HepPlanner preHepPlanner = new HepPlanner(preProgram);
            preHepPlanner.setRoot(original);
            RelNode preBestExp = preHepPlanner.findBestExp();
            // after
            HepProgramBuilder afterBuilder = HepProgram.builder();
            for (RelOptRule rule : rules) {
                afterBuilder.addRuleInstance(rule);
            }
            HepProgram afterProgram = afterBuilder.build();
            HepPlanner afterHepPlanner = new HepPlanner(afterProgram);
            afterHepPlanner.setRoot(preBestExp);
            // hepPlanner优化
            RelNode afterBestExp = afterHepPlanner.findBestExp();
            System.out.println("sql:");
            System.out.println(sql);
            System.out.println();
            System.out.println("原始:");
            System.out.println(RelOptUtil.toString(original));
            System.out.println();
            System.out.println("pre执行:");
            System.out.println(RelOptUtil.toString(preBestExp));
            System.out.println();
            System.out.println("after执行:");
            System.out.println(RelOptUtil.toString(afterBestExp));
            System.out.println();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 根据传入的规则一个一个优化
     * @param sql
     * @param rules
     */
    public static void printProcessRule(String sql, RelOptRule... rules) {

        try {
            System.out.println("sql:");
            System.out.println(sql);
            System.out.println();
            SqlParser.Config mysql = SqlParser.configBuilder().setLex(Lex.MYSQL).setCaseSensitive(false).build();
            SqlToRelConverter.Config aDefault = SqlToRelConverterBase.DEFAULT;

            SqlParserBase sqlParserBase = new SqlParserBase();
            SqlNode sqlNode = sqlParserBase.parseQuery(sql);
            SqlToRelConverter sqlToRelConverter = TutorialSqlToRelConverter.createSqlToRelConverter(mysql, aDefault);
            RelRoot relRoot = sqlToRelConverter.convertQuery(sqlNode, true, true);
            // 原始
            RelNode original = relRoot.rel;
            System.out.println("原始:");
            System.out.println(RelOptUtil.toString(original));
            System.out.println();
            RelNode temp = original;
            for (RelOptRule rule : rules) {
                HepProgramBuilder builder = HepProgram.builder();
                builder.addRuleInstance(rule);
                HepProgram program = builder.build();
                HepPlanner hepPlanner = new HepPlanner(program);
                hepPlanner.setRoot(temp);
                temp = hepPlanner.findBestExp();
                System.out.println("规则:" + rule.toString());
                System.out.println(RelOptUtil.toString(temp));
                System.out.println();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
