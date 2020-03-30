package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.FilterMergeRule;
import org.apache.calcite.rel.rules.FilterProjectTransposeRule;

/**
 * 合并filter
 * @author quxiucheng
 * @date 2019-02-02 11:28:00
 */
public class FilterMergeRuleTest {
    public static void main(String[] args) {
        String sql = "select * from " +
                "(select * from hr.emps where name = 'a') t " +
                "where t.deptno=1";
        RuleTester.printProcessRule(sql,
                // 下推fitler到project
                FilterProjectTransposeRule.INSTANCE,
                FilterMergeRule.INSTANCE);
    }
    /**
     sql:
    select * from (select * from hr.emps where name = 'a') t where t.deptno=1

    原始:
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalFilter(condition=[=($1, 1)])
        LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
          LogicalFilter(condition=[=($2, 'a')])
            EnumerableTableScan(table=[[hr, emps]])


    规则:FilterProjectTransposeRule
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
        LogicalFilter(condition=[=($1, 1)])
          LogicalFilter(condition=[=($2, 'a')])
            EnumerableTableScan(table=[[hr, emps]])


    规则:FilterMergeRule
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
        LogicalFilter(condition=[AND(=($2, 'a'), =($1, 1))])
          EnumerableTableScan(table=[[hr, emps]])
     */
}
