package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.SortProjectTransposeRule;

/**
 * 将sort下推到project中
 * @author quxiucheng
 * @date 2019-02-19 15:48:00
 */
public class SortProjectTransposeRuleTest {
    public static void main(String[] args) {
        String sql = "select * from hr.emps order by name";
        RuleTester.printOriginalCompare(sql, SortProjectTransposeRule.INSTANCE);
    }
    /**
     sql:
    select * from hr.emps order by name

    原始:
    LogicalSort(sort0=[$2], dir0=[ASC])
      LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
        EnumerableTableScan(table=[[hr, emps]])


    优化后:
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalSort(sort0=[$2], dir0=[ASC])
        EnumerableTableScan(table=[[hr, emps]])

     */
}
