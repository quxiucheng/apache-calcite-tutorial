package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.FilterJoinRule;
import org.apache.calcite.rel.rules.JoinAddRedundantSemiJoinRule;
import org.apache.calcite.rel.rules.SemiJoinFilterTransposeRule;

/**
 * @author quxiucheng
 * @date 2019-02-19 17:43:00
 */
public class SemiJoinFilterTransposeRuleTest {
    public static void main(String[] args) {
        String sql = "select e.name as ename,d.name as dname from hr.emps e join hr.depts d on e.deptno = d.deptno" +
                " where e.name = 'abc'";
        RuleTester.printProcessRule(sql,
                // filter下推到join
                FilterJoinRule.FILTER_ON_JOIN,
                // 转成成semiJoin
                JoinAddRedundantSemiJoinRule.INSTANCE,
                // semiJoin下推到filter
                SemiJoinFilterTransposeRule.INSTANCE);

    }
}
