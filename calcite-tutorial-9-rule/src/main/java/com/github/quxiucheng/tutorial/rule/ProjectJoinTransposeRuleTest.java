package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.ProjectJoinTransposeRule;

/**
 *  Project 下推到 join中
 * @author quxiucheng
 * @date 2019-02-19 15:08:00
 */
public class ProjectJoinTransposeRuleTest {
    public static void main(String[] args) {
        String sql = "select e.name as ename,d.name as dname from hr.emps e join hr.depts d on e.deptno = d.deptno";
        RuleTester.printProcessRule(sql,
                ProjectJoinTransposeRule.INSTANCE);
    }
    /**
     sql:
    select e.name as ename,d.name as dname from hr.emps e join hr.depts d on e.deptno = d.deptno

    原始:
    LogicalProject(ename=[$2], dname=[$6])
      LogicalJoin(condition=[=($1, $5)], joinType=[inner])
        EnumerableTableScan(table=[[hr, emps]])
        EnumerableTableScan(table=[[hr, depts]])


    规则:ProjectJoinTransposeRule
    LogicalProject(ename=[$1], dname=[$3])
      LogicalJoin(condition=[=($0, $2)], joinType=[inner])
        LogicalProject(deptno=[$1], name=[$2])
          EnumerableTableScan(table=[[hr, emps]])
        LogicalProject(deptno=[$0], name=[$1])
          EnumerableTableScan(table=[[hr, depts]])
     */
}
