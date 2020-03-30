package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.ValuesReduceRule;

/**
 * @author quxiucheng
 * @date 2019-02-20 11:56:00
 *
 */
public class ValuesReduceRuleTest {
    public static void main(String[] args) {
        String sql = "select a, b from (values (10, 'x'), (20, 'y')) as t(a, b) where a < 15";
        RuleTester.printProcessRule(sql, ValuesReduceRule.FILTER_INSTANCE);
    }
    /**
    sql:
    select a, b from (values (10, 'x'), (20, 'y')) as t(a, b) where a < 15

    原始:
    LogicalProject(a=[$0], b=[$1])
      LogicalFilter(condition=[<($0, 15)])
        LogicalValues(tuples=[[{ 10, 'x' }, { 20, 'y' }]])


    规则:ValuesReduceRule(Filter)
    LogicalProject(a=[$0], b=[$1])
      LogicalValues(tuples=[[{ 10, 'x' }]])
     */
}
