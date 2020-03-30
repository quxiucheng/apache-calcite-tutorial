package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.JoinToMultiJoinRule;
import org.apache.calcite.rel.rules.MultiJoinOptimizeBushyRule;

/**
 * @author quxiucheng
 * @date 2019-02-19 14:48:00
 */
public class MultiJoinOptimizeBushyRuleTest {
    public static void main(String[] args) {
        String sql = "select e.name as ename,d.name as dname from hr.emps e join hr.depts d on e.deptno = d.deptno where e.name = '1'";

        RuleTester.printProcessRule(sql, JoinToMultiJoinRule.INSTANCE, MultiJoinOptimizeBushyRule.INSTANCE);

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


    规则:JoinToMultiJoinRule
    LogicalProject(ename=[$2], dname=[$6])
      LogicalFilter(condition=[=($2, '1')])
        MultiJoin(joinFilter=[=($1, $5)], isFullOuterJoin=[false], joinTypes=[[INNER, INNER]], outerJoinConditions=[[NULL, NULL]], projFields=[[ALL, ALL]])
          EnumerableTableScan(table=[[hr, emps]])
          EnumerableTableScan(table=[[hr, depts]])


    规则:MultiJoinOptimizeBushyRule
    LogicalProject(ename=[$2], dname=[$6])
      LogicalFilter(condition=[=($2, '1')])
        LogicalJoin(condition=[=($1, $5)], joinType=[inner])
          EnumerableTableScan(table=[[hr, emps]])
          EnumerableTableScan(table=[[hr, depts]])
     */
}
