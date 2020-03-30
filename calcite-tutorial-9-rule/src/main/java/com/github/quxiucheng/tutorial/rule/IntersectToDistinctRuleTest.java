package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.IntersectToDistinctRule;

/**
 * 它将一个不同的交集Intersect转换为一组由Union、Aggregate等组成的操作符。
 * @author quxiucheng
 * @date 2019-02-02 15:39:00
 */
public class IntersectToDistinctRuleTest {
    public static void main(String[] args) {
        String sql =
                "select name from hr.emps " +
                "intersect " +
                "select name from hr.emps ";

        RuleTester.printOriginalCompare(sql, IntersectToDistinctRule.INSTANCE);
    }
    /**
     sql:
    select name from hr.emps intersect select name from hr.emps

    原始:
    LogicalIntersect(all=[false])
      LogicalProject(name=[$2])
        EnumerableTableScan(table=[[hr, emps]])
      LogicalProject(name=[$2])
        EnumerableTableScan(table=[[hr, emps]])


    优化后:
    LogicalProject(name=[$0])
      LogicalFilter(condition=[=($1, 2)])
        LogicalAggregate(group=[{0}], agg#0=[COUNT()])
          LogicalUnion(all=[true])
            LogicalAggregate(group=[{0}], agg#0=[COUNT()])
              LogicalProject(name=[$2])
                EnumerableTableScan(table=[[hr, emps]])
            LogicalAggregate(group=[{0}], agg#0=[COUNT()])
              LogicalProject(name=[$2])
                EnumerableTableScan(table=[[hr, emps]])

     */
}
