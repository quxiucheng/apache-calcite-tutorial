package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.PruneEmptyRules;
import org.apache.calcite.rel.rules.ReduceExpressionsRule;

/**
 * 删除查询计划中已知永远不会生成任何行的部分的规则的集合
 *
 * 通常，表示空关系表达式的方法是使用没有元组的值。
 *
 * AGGREGATE_INSTANCE
 * FILTER_INSTANCE
 * INTERSECT_INSTANCE
 * JOIN_LEFT_INSTANCE
 * JOIN_RIGHT_INSTANCE
 * MINUS_INSTANCE
 * PROJECT_INSTANCE
 * SORT_FETCH_ZERO_INSTANCE
 * SORT_INSTANCE
 * UNION_INSTANCE
 *
 * @author quxiucheng
 * @date 2019-02-01 16:46:00
 */
public class PruneEmptyRulesTest {
    public static void main(String[] args) {
        String sql = "select * from hr.emps where 1=2";
        RuleTester.printProcessRule(sql, ReduceExpressionsRule.FILTER_INSTANCE, PruneEmptyRules.PROJECT_INSTANCE);

    }
    /**
     *
    sql:
    select * from hr.emps where 1=2

    原始:
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalFilter(condition=[=(1, 2)])
        EnumerableTableScan(table=[[hr, emps]])


    规则:ReduceExpressionsRule(Filter)
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalValues(tuples=[[]])


    规则:PruneEmptyProject
    LogicalValues(tuples=[[]])
     */
}
