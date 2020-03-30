package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.AggregateValuesRule;
import org.apache.calcite.rel.rules.PruneEmptyRules;
import org.apache.calcite.rel.rules.ReduceExpressionsRule;

/**
 * 将聚合应用于值(当前仅为空值)的规则。
 * 因为PruneEmptyRules.AGGREGATE_INSTANCE不处理聚合,有些条件比较特殊,例如(例如COUNT是0,SUM是NULL)。
 *
 *
 * @author quxiucheng
 * @date 2019-02-01 15:45:00
 */
public class AggregateValuesRuleTest {
    public static void main(String[] args) {
        String sql = " SELECT sum(salary) FROM hr.emps WHERE 1 = 0";
        RuleTester.printProcessRule(sql,
                // 去除表达式
                ReduceExpressionsRule.FILTER_INSTANCE,
                // 去除空行
                PruneEmptyRules.PROJECT_INSTANCE,
                // 聚合值
                AggregateValuesRule.INSTANCE);

    }
    /**
     *
    sql:
     SELECT sum(salary) FROM hr.emps WHERE 1 = 0

    原始:
    LogicalAggregate(group=[{}], EXPR$0=[SUM($0)])
      LogicalProject(salary=[$3])
        LogicalFilter(condition=[=(1, 0)])
          EnumerableTableScan(table=[[hr, emps]])


    规则:ReduceExpressionsRule(Filter)
    LogicalAggregate(group=[{}], EXPR$0=[SUM($0)])
      LogicalProject(salary=[$3])
        LogicalValues(tuples=[[]])


    规则:PruneEmptyProject
    LogicalAggregate(group=[{}], EXPR$0=[SUM($0)])
      LogicalValues(tuples=[[]])


    规则:AggregateValuesRule
    LogicalValues(tuples=[[{ null }]])
     */
}
