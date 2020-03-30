package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.FilterAggregateTransposeRule;

/**
 * 将filter相关条件下推到group by之后
 * @author quxiucheng
 * @date 2019-01-31 17:09:00
 */
public class FilterAggregateTransposeRuleTest {
    public static void main(String[] args) {
        String sql = "select * from (select name, count(0) as cnt from hr.emps group by name ) t where name='1' ";
        RuleTester.printOriginalCompare(sql, FilterAggregateTransposeRule.INSTANCE);

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


     优化后:
     LogicalProject(name=[$0], cnt=[$1])
      LogicalAggregate(group=[{0}], cnt=[COUNT()])
        LogicalFilter(condition=[=(CAST($0):VARCHAR CHARACTER SET "ISO-8859-1" COLLATE "ISO-8859-1$en_US$primary", '1')])
         LogicalProject(name=[$2], $f1=[0])
          EnumerableTableScan(table=[[hr, emps]])
     */
}
