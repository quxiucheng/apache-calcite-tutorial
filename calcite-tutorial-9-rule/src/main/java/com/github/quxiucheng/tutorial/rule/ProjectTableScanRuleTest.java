package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.ProjectTableScanRule;

/**
 * 有问题,没有看明白
 * @author quxiucheng
 * @date 2019-02-19 15:53:00
 */
public class ProjectTableScanRuleTest {
    public static void main(String[] args) {
        String sql = "select * from hr.emps";
        RuleTester.printProcessRule(sql, ProjectTableScanRule.INSTANCE);
    }
}
