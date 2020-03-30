package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.ProjectRemoveRule;
import org.apache.calcite.rel.rules.ProjectSetOpTransposeRule;

/**
 * 删除多余的project
 * @author quxiucheng
 * @date 2019-02-19 15:38:00
 */
public class ProjectRemoveRuleTest {
    public static void main(String[] args) {
        String sql = "select salary from " +
                "(select * from hr.emps e1 " +
                "union all " +
                "select * from hr.emps e2) ";
        RuleTester.printProcessRule(sql,
                // 将project(投影) 下推到 SetOp(例如:union ,minus, except)
                ProjectSetOpTransposeRule.INSTANCE,
                ProjectRemoveRule.INSTANCE);
    }
}
