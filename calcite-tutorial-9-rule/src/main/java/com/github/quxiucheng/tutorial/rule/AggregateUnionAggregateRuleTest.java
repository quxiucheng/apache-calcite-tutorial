package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.AggregateUnionAggregateRule;

/**
 * union内的聚合和union外的聚合相同则删除
 * 如果考虑到效率，建议使用AGG_ON_FIRST_INPUT和AGG_ON_SECOND_INPU
 * @author quxiucheng
 * @date 2019-02-01 14:02:00
 */
public class AggregateUnionAggregateRuleTest {
    public static void main(String[] args) {
        String sql = "select name from" +
                "(select name from hr.emps group by name " +
                "union all " +
                "select name from hr.emps group by name) t " +
                "group by name";
        RuleTester.printOriginalCompare(sql, AggregateUnionAggregateRule.INSTANCE);
    }
    /**
     sql:
     select name from(select name from hr.emps group by name union all select name from hr.emps group by name) t group by name

     原始:
     LogicalAggregate(group=[{0}])
       LogicalUnion(all=[true])
         LogicalAggregate(group=[{0}])
           LogicalProject(name=[$2])
             EnumerableTableScan(table=[[hr, emps]])
         LogicalAggregate(group=[{0}])
           LogicalProject(name=[$2])
             EnumerableTableScan(table=[[hr, emps]])


     优化后:
     LogicalAggregate(group=[{0}])
       LogicalUnion(all=[true])
         LogicalProject(name=[$2])
           EnumerableTableScan(table=[[hr, emps]])
         LogicalProject(name=[$2])
           EnumerableTableScan(table=[[hr, emps]])
     */
}
