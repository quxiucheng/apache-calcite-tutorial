package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.JoinToCorrelateRule;
import org.apache.calcite.rel.rules.ProjectCorrelateTransposeRule;

/**
 * Project 下推到 correlate中
 * 投影           关联
 * @author quxiucheng
 * @date 2019-02-19 15:03:00
 */
public class ProjectCorrelateTransposeRuleTest {
    public static void main(String[] args) {
        String sql = "select e.name as ename,d.name as dname from hr.emps e join hr.depts d on e.deptno = d.deptno";
        RuleTester.printProcessRule(sql,
                // join转correlateRule
                JoinToCorrelateRule.INSTANCE,
                ProjectCorrelateTransposeRule.INSTANCE);
    }
    /**
     sql:
    select e.name as ename,d.name as dname from hr.emps e join hr.depts d on e.deptno = d.deptno

    原始:
    LogicalProject(ename=[$2], dname=[$6])
      LogicalJoin(condition=[=($1, $5)], joinType=[inner])
        EnumerableTableScan(table=[[hr, emps]])
        EnumerableTableScan(table=[[hr, depts]])


    规则:JoinToCorrelateRule
    LogicalProject(ename=[$2], dname=[$6])
      LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{1}])
        EnumerableTableScan(table=[[hr, emps]])
        LogicalFilter(condition=[=($cor0.deptno, $0)])
          EnumerableTableScan(table=[[hr, depts]])


    规则:ProjectCorrelateTransposeRule
    LogicalProject(ename=[$1], dname=[$2])
      LogicalCorrelate(correlation=[$cor1], joinType=[inner], requiredColumns=[{0}])
        LogicalProject(deptno=[$1], name=[$2])
          EnumerableTableScan(table=[[hr, emps]])
        LogicalProject(name=[$1])
          LogicalFilter(condition=[=($cor1.deptno, $0)])
            EnumerableTableScan(table=[[hr, depts]])
     */
}
