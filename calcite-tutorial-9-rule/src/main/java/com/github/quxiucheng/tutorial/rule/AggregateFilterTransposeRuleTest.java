package com.github.quxiucheng.tutorial.rule;

import com.google.common.collect.Lists;
import org.apache.calcite.rel.rules.AggregateFilterTransposeRule;
import org.apache.calcite.rel.rules.FilterAggregateTransposeRule;

/**
 * 将聚合下推到filter中
 * @author quxiucheng
 * @date 2019-01-31 17:17:00
 */
public class AggregateFilterTransposeRuleTest {
    public static void main(String[] args) {
        String sql = "select * from (select name, count(0) as cnt from hr.emps group by name ) t where name='1' ";
        RuleTester.printRuleCompare(sql,
                // 执行聚合
                Lists.newArrayList(FilterAggregateTransposeRule.INSTANCE),
                // 提取投影 project
                Lists.newArrayList(AggregateFilterTransposeRule.INSTANCE));
    }
    /**
     *
     sql:
     select * from (select name, count(0) as cnt from hr.emps group by name ) t where name='1'

     原始:
     LogicalProject(name=[$0], cnt=[$1])
      LogicalFilter(condition=[=(CAST($0):VARCHAR CHARACTER SET "ISO-8859-1" COLLATE "ISO-8859-1$en_US$primary", '1')])
       ↓ ↑
       LogicalAggregate(group=[{0}], cnt=[COUNT()])
        LogicalProject(name=[$2], $f1=[0])
         EnumerableTableScan(table=[[hr, emps]])


     pre执行:
     LogicalProject(name=[$0], cnt=[$1])
      LogicalAggregate(group=[{0}], cnt=[COUNT()])
        ↓ ↑
       LogicalFilter(condition=[=(CAST($0):VARCHAR CHARACTER SET "ISO-8859-1" COLLATE "ISO-8859-1$en_US$primary", '1')])
        LogicalProject(name=[$2], $f1=[0])
        EnumerableTableScan(table=[[hr, emps]])


     after执行:
     LogicalProject(name=[$0], cnt=[$1])
      LogicalFilter(condition=[=(CAST($0):VARCHAR CHARACTER SET "ISO-8859-1" COLLATE "ISO-8859-1$en_US$primary", '1')])
       LogicalAggregate(group=[{0}], cnt=[COUNT()])
        LogicalProject(name=[$2], $f1=[0])
         EnumerableTableScan(table=[[hr, emps]])
     */
}
