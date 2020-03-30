package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.ProjectSetOpTransposeRule;

/**
 * 将project(投影) 下推到 SetOp(例如:union ,minus, except)
 * @author quxiucheng
 * @date 2019-02-01 14:24:00
 */
public class ProjectSetOpTransposeRuleTest {
    public static void main(String[] args) {
        String sql = "select salary from " +
                "(select * from hr.emps e1 " +
                "union all " +
                "select * from hr.emps e2) ";
        RuleTester.printOriginalCompare(sql, ProjectSetOpTransposeRule.INSTANCE);
    }

    /**
     sql:
    select salary from (select * from hr.emps e1 union all select * from hr.emps e2)

    原始:
    LogicalProject(salary=[$3])
      LogicalUnion(all=[true])
        LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
          EnumerableTableScan(table=[[hr, emps]])
        LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
          EnumerableTableScan(table=[[hr, emps]])


    优化后:
    LogicalUnion(all=[true])
      LogicalProject(salary=[$3])
        LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
          EnumerableTableScan(table=[[hr, emps]])
      LogicalProject(salary=[$3])
        LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
          EnumerableTableScan(table=[[hr, emps]])

     */
}
