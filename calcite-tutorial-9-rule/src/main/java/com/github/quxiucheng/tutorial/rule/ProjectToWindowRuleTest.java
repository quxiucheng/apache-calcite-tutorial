package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.ProjectToWindowRule;

/**
 * project中提取window相关
 * @author quxiucheng
 * @date 2019-02-19 17:34:00
 */
public class ProjectToWindowRuleTest {
    public static void main(String[] args) {
        String sql = "select count(*) over(partition by deptno order by name) as count1 from hr.emps";
        RuleTester.printOriginalCompare(sql, ProjectToWindowRule.PROJECT);
    }
    /**
     sql:
    select count(*) over(partition by deptno order by name) as count1 from hr.emps

    原始:
    LogicalProject(count1=[COUNT() OVER (PARTITION BY $1 ORDER BY $2 RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)])
      EnumerableTableScan(table=[[hr, emps]])


    优化后:
    LogicalProject($0=[$5])
      LogicalWindow(window#0=[window(partition {1} order by [2] range between UNBOUNDED PRECEDING and CURRENT ROW aggs [COUNT()])])
        EnumerableTableScan(table=[[hr, emps]])

     */
}
