package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.FilterCorrelateRule;
import org.apache.calcite.rel.rules.JoinToCorrelateRule;

/**
 * @author quxiucheng
 * @date 2019-02-02 10:54:00
 *
 * 将filter下推到Correlate
 */
public class FilterCorrelateRuleTest {
    public static void main(String[] args) {
        String sql = "select e.name as ename,d.name as dname from hr.emps e join hr.depts d on e.deptno = d.deptno where e.name = '1'";
        RuleTester.printProcessRule(sql, JoinToCorrelateRule.INSTANCE, FilterCorrelateRule.INSTANCE);
    }
    /**
     sql:
    select e.name as ename,d.name as dname from hr.emps e join hr.depts d on e.deptno = d.deptno where e.name = '1'

    原始:
    LogicalProject(ename=[$2], dname=[$6])
      LogicalFilter(condition=[=($2, '1')])
        LogicalJoin(condition=[=($1, $5)], joinType=[inner])
          EnumerableTableScan(table=[[hr, emps]])
          EnumerableTableScan(table=[[hr, depts]])


    规则:JoinToCorrelateRule
    LogicalProject(ename=[$2], dname=[$6])
      LogicalFilter(condition=[=($2, '1')])
        LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{1}])
          EnumerableTableScan(table=[[hr, emps]])
          LogicalFilter(condition=[=($cor0.deptno, $0)])
            EnumerableTableScan(table=[[hr, depts]])


    规则:FilterCorrelateRule
    LogicalProject(ename=[$2], dname=[$6])
      LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{1}])
        LogicalFilter(condition=[=($2, '1')])
          EnumerableTableScan(table=[[hr, emps]])
        LogicalFilter(condition=[=($cor0.deptno, $0)])
          EnumerableTableScan(table=[[hr, depts]])
     */
}
