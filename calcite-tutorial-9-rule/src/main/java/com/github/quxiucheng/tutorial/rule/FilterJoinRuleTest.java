package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.FilterJoinRule;

/**
 * filter下推到join
 *
 * FILTER_ON_JOIN
 * JOIN
 * @author quxiucheng
 * @date 2019-02-02 11:21:00
 */
public class FilterJoinRuleTest {
    public static void main(String[] args) {
        String sql = "select e.name as ename,d.name as dname from hr.emps e join hr.depts d on e.deptno = d.deptno where e.name = '1'";
        RuleTester.printOriginalCompare(sql, FilterJoinRule.FILTER_ON_JOIN);
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


    优化后:
    LogicalProject(ename=[$2], dname=[$6])
      LogicalJoin(condition=[=($1, $5)], joinType=[inner])
        LogicalFilter(condition=[=($2, '1')])
          EnumerableTableScan(table=[[hr, emps]])
        EnumerableTableScan(table=[[hr, depts]])
     */

}
