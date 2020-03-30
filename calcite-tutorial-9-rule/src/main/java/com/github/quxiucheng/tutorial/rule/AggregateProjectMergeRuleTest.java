package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.AggregateProjectMergeRule;

/**
 * 将 聚合字段和投影(Project)的字段 Merge到一起
 *
 * @author quxiucheng
 * @date 2019-01-31 16:27:00
 */
public class AggregateProjectMergeRuleTest {

    public static void main(String[] args) {
        String sql = "select sum(salary) from hr.emps";
        RuleTester.printOriginalCompare(sql, AggregateProjectMergeRule.INSTANCE);
    }
    /**
     *
     sql:
     select sum(salary) from hr.emps

     原始:
     LogicalAggregate(group=[{}], EXPR$0=[SUM($0)])
      LogicalProject(salary=[$3])
       EnumerableTableScan(table=[[hr, emps]])


     优化后:
     LogicalAggregate(group=[{}], EXPR$0=[SUM($3)])
      EnumerableTableScan(table=[[hr, emps]])
     *
     */
}
