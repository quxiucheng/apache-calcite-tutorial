package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.FilterProjectTransposeRule;

/**
 * 把filter下推到project中
 * @author quxiucheng
 * @date 2019-02-02 11:24:00
 */
public class FilterProjectTransposeRuleTest {
    public static void main(String[] args) {
        String sql = "select * from " +
                "(select * from hr.emps where name = 'a') t " +
                "where t.deptno=1";
        RuleTester.printOriginalCompare(sql, FilterProjectTransposeRule.INSTANCE);
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


    优化后:
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
        LogicalFilter(condition=[=($1, 1)])
          LogicalFilter(condition=[=($2, 'a')])
            EnumerableTableScan(table=[[hr, emps]])
     */
}
