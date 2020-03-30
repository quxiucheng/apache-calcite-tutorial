package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.JoinUnionTransposeRule;

/**
 * 把join推入union中
 * @author quxiucheng
 * @date 2019-02-19 14:23:00
 */
public class JoinUnionTransposeRuleTest {
    public static void main(String[] args) {
        String sql = "select * from "
                + "(select * from hr.emps e1 union all select * from hr.emps e2) r1, "
                + "hr.emps r2";
        RuleTester.printOriginalCompare(sql, JoinUnionTransposeRule.LEFT_UNION);
    }
    /**
     sql:
    select * from (select * from hr.emps e1 union all select * from hr.emps e2) r1, hr.emps r2

    原始:
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4], empid0=[$5], deptno0=[$6], name0=[$7], salary0=[$8], commission0=[$9])
      LogicalJoin(condition=[true], joinType=[inner])
        LogicalUnion(all=[true])
          LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
            EnumerableTableScan(table=[[hr, emps]])
          LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
            EnumerableTableScan(table=[[hr, emps]])
        EnumerableTableScan(table=[[hr, emps]])


    优化后:
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4], empid0=[$5], deptno0=[$6], name0=[$7], salary0=[$8], commission0=[$9])
      LogicalUnion(all=[true])
        LogicalJoin(condition=[true], joinType=[inner])
          LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
            EnumerableTableScan(table=[[hr, emps]])
          EnumerableTableScan(table=[[hr, emps]])
        LogicalJoin(condition=[true], joinType=[inner])
          LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
            EnumerableTableScan(table=[[hr, emps]])
          EnumerableTableScan(table=[[hr, emps]])
     */
}
