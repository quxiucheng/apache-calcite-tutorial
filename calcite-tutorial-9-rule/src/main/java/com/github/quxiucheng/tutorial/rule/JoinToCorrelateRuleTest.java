package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.JoinToCorrelateRule;

/**
 * 将join 转换为 corelate (nested-loop join)
 * 不适用right join,full join 等
 * @author quxiucheng
 * @date 2019-02-02 11:02:00
 */
public class JoinToCorrelateRuleTest {
    public static void main(String[] args) {
        String sql = "select e.name as ename,d.name as dname from hr.emps e join hr.depts d on e.deptno = d.deptno";
        RuleTester.printOriginalCompare(sql, JoinToCorrelateRule.INSTANCE);
    }

    /**
    sql:
    select e.name as ename,d.name as dname from hr.emps e join hr.depts d on e.deptno = d.deptno

    原始:
    LogicalProject(ename=[$2], dname=[$6])
      LogicalJoin(condition=[=($1, $5)], joinType=[inner])
        EnumerableTableScan(table=[[hr, emps]])
        EnumerableTableScan(table=[[hr, depts]])


    优化后:
    LogicalProject(ename=[$2], dname=[$6])
      LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{1}])
        EnumerableTableScan(table=[[hr, emps]])
        LogicalFilter(condition=[=($cor0.deptno, $0)])
          EnumerableTableScan(table=[[hr, depts]])
     */
}
