package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.FilterTableFunctionTransposeRule;

/**
 * 将filter table下推到
 * @author quxiucheng
 * @date 2019-02-02 13:57:00
 */
public class FilterTableFunctionTransposeRuleTest {
    public static void main(String[] args) {
        String sql = "select * from table(dedup(cursor(select name from hr.emps), row(name))) where name = '1'";
        RuleTester.printProcessRule(sql, FilterTableFunctionTransposeRule.INSTANCE);
    }
    /**
     sql:
    select * from table(dedup(cursor(select name from hr.emps), row(name))) where name = '1'

    原始:
    LogicalProject(NAME=[$0])
      LogicalFilter(condition=[=($0, '1')])
        LogicalTableFunctionScan(invocation=[DEDUP(CAST($0):CURSOR NOT NULL, COLUMN_LIST('name'))], rowType=[RecordType(VARCHAR(1024) NAME)])
          LogicalProject(name=[$2])
            EnumerableTableScan(table=[[hr, emps]])


    规则:FilterTableFunctionTransposeRule
    LogicalProject(NAME=[$0])
      LogicalTableFunctionScan(invocation=[DEDUP(CAST($0):CURSOR NOT NULL, COLUMN_LIST('name'))], rowType=[RecordType(VARCHAR(1024) NAME)])
        LogicalFilter(condition=[=($0, '1')])
          LogicalProject(name=[$2])
            EnumerableTableScan(table=[[hr, emps]])
     */
}
