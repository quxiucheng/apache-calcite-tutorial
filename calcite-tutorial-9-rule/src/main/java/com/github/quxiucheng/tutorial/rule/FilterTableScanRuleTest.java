package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.FilterTableScanRule;

/**
 * 这个目前没有看到测试实例
 * 它将FilterableTable或 ProjectableFilterableTable表上的filter转换为 Bindables.BindableTableScan.
 * @author quxiucheng
 * @date 2019-02-02 15:28:00
 * TODO:
 */
public class FilterTableScanRuleTest {
    public static void main(String[] args) {
        String sql = "select * from hr.emps where name='abcd'";
        RuleTester.printOriginalCompare(sql, FilterTableScanRule.INSTANCE);
    }
}
