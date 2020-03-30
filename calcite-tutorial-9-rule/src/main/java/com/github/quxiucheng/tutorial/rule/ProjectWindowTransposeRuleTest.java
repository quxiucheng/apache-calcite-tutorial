package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.ProjectToWindowRule;
import org.apache.calcite.rel.rules.ProjectWindowTransposeRule;

/**
 * 将project下推到window中
 * @author quxiucheng
 * @date 2019-02-19 17:39:00
 */
public class ProjectWindowTransposeRuleTest {
    public static void main(String[] args) {
        String sql = "select count(*) over(partition by deptno order by name) as count1 from hr.emps";
        RuleTester.printProcessRule(sql, ProjectToWindowRule.PROJECT, ProjectWindowTransposeRule.INSTANCE);

    }
    /**
     sql:
    select count(*) over(partition by deptno order by name) as count1 from hr.emps

    原始:
    LogicalProject(count1=[COUNT() OVER (PARTITION BY $1 ORDER BY $2 RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)])
      EnumerableTableScan(table=[[hr, emps]])


    规则:ProjectToWindowRule:project
    LogicalProject($0=[$5])
      LogicalWindow(window#0=[window(partition {1} order by [2] range between UNBOUNDED PRECEDING and CURRENT ROW aggs [COUNT()])])
        EnumerableTableScan(table=[[hr, emps]])


    规则:ProjectWindowTransposeRule
    LogicalProject($0=[$2])
      LogicalWindow(window#0=[window(partition {0} order by [1] range between UNBOUNDED PRECEDING and CURRENT ROW aggs [COUNT()])])
        LogicalProject(deptno=[$1], name=[$2])
          EnumerableTableScan(table=[[hr, emps]])
     */
}
