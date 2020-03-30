package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.JoinProjectTransposeRule;
import org.apache.calcite.rel.rules.ProjectJoinTransposeRule;

/**
 *  join下推到project中
 *
 * BOTH_PROJECT
 * LEFT_PROJECT
 * RIGHT_PROJECT
 * BOTH_PROJECT_INCLUDE_OUTER
 * LEFT_PROJECT_INCLUDE_OUTER
 * RIGHT_PROJECT_INCLUDE_OUTER
 * @author quxiucheng
 * @date 2019-02-03 11:23:00
 */
public class JoinProjectTransposeRuleTest {
    public static void main(String[] args) {
        String sql = "select e.name from hr.emps e left join hr.depts d on e.deptno = d.deptno";
        RuleTester.printProcessRule(sql,
                // project下推大到join中
                ProjectJoinTransposeRule.INSTANCE,
                // join下推到project中
                JoinProjectTransposeRule.LEFT_PROJECT);
    }
    /**
     sql:
     select e.name from hr.emps e left join hr.depts d on e.deptno = d.deptno

     原始:
    LogicalProject(name=[$2])
      LogicalJoin(condition=[=($1, $5)], joinType=[left])
        EnumerableTableScan(table=[[hr, emps]])
        EnumerableTableScan(table=[[hr, depts]])


    规则:ProjectJoinTransposeRule
    LogicalProject(name=[$1])
      LogicalJoin(condition=[=($0, $2)], joinType=[left])
        LogicalProject(deptno=[$1], name=[$2])
          EnumerableTableScan(table=[[hr, emps]])
        LogicalProject(deptno=[$0])
          EnumerableTableScan(table=[[hr, depts]])


    规则:JoinProjectTransposeRule(Project-Other)
    LogicalProject(name=[$1])
      LogicalProject(deptno=[$1], name=[$2], deptno0=[$5])
        LogicalJoin(condition=[=($1, $5)], joinType=[left])
          EnumerableTableScan(table=[[hr, emps]])
          LogicalProject(deptno=[$0])
            EnumerableTableScan(table=[[hr, depts]])
     */
}
