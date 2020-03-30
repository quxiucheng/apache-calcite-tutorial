package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.JoinExtractFilterRule;

/**
 * 将内连接转换为笛卡尔积+filter
 * @author quxiucheng
 * @date 2019-02-03 11:16:00
 */
public class JoinExtractFilterRuleTest {
    public static void main(String[] args) {
        String sql = "select e.name from hr.emps e join hr.depts d on e.deptno = d.deptno";
        RuleTester.printOriginalCompare(sql, JoinExtractFilterRule.INSTANCE);
    }
    /**
    sql:
    select e.name from hr.emps e join hr.depts d on e.deptno = d.deptno

    原始:
    LogicalProject(name=[$2])
      LogicalJoin(condition=[=($1, $5)], joinType=[inner])
        EnumerableTableScan(table=[[hr, emps]])
        EnumerableTableScan(table=[[hr, depts]])


    优化后:
    LogicalProject(name=[$2])
      LogicalFilter(condition=[=($1, $5)])
        LogicalJoin(condition=[true], joinType=[inner])
          EnumerableTableScan(table=[[hr, emps]])
          EnumerableTableScan(table=[[hr, depts]])
     */
}
