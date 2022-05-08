package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.CoreRules;

/**
 * 将project下推到sort中
 *
 * 必须是sort class不能是LogicalSort
 * @author quxiucheng
 * @date 2019-02-19 15:45:00
 */
public class ProjectSortTransposeRuleTest {
    public static void main(String[] args) {
        String sql = "select * from hr.emps order by name";
        RuleTester.printProcessRule(sql,
                CoreRules.SORT_PROJECT_TRANSPOSE);
    }
}
