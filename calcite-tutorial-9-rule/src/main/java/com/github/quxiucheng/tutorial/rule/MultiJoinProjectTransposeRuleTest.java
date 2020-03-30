package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.JoinToMultiJoinRule;
import org.apache.calcite.rel.rules.MultiJoinProjectTransposeRule;
import org.apache.calcite.rel.rules.ProjectJoinTransposeRule;

/**
 * @author quxiucheng
 * @date 2019-02-19 14:51:00
 * description 报错
 */
public class MultiJoinProjectTransposeRuleTest {
    public static void main(String[] args) {
        String sql = "select e.name from hr.emps e left join hr.depts d on e.deptno = d.deptno";

        RuleTester.printProcessRule(sql,
                // project下推大到join中
                ProjectJoinTransposeRule.INSTANCE,
                JoinToMultiJoinRule.INSTANCE,
                MultiJoinProjectTransposeRule.MULTI_LEFT_PROJECT);

    }
}
