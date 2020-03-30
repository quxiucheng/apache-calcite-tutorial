package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.AggregateJoinTransposeRule;
import org.apache.calcite.rel.rules.AggregateProjectMergeRule;

/**
 * 将 聚合 下推到 join中
 * @author quxiucheng
 * @date 2019-01-31 17:33:00
 */
public class AggregateJoinTransposeRuleTest {
    public static void main(String[] args) {
        String sql = "select e.deptno,d.name "
                + "from hr.emps as e "
                + "join hr.depts as d on e.deptno = d.deptno "
                + "group by e.deptno,d.name";
        RuleTester.printOriginalCompare(sql,
                // 将分组字段投影到project中,若不投影则无法下推
                AggregateProjectMergeRule.INSTANCE,
                // 包括聚合函数
                AggregateJoinTransposeRule.EXTENDED);
    }
    /**
     sql:
     select e.deptno,d.name from hr.emps as e join hr.depts as d on e.deptno = d.deptno group by e.deptno,d.name

     原始:
     LogicalAggregate(group=[{0, 1}])               - 合并 AggregateProjectMergeRule.INSTANCE
      LogicalProject(deptno=[$1], name=[$6])        - 合并
       LogicalJoin(condition=[=($1, $5)], joinType=[inner])
        EnumerableTableScan(table=[[hr, emps]])
        EnumerableTableScan(table=[[hr, depts]])

     中间过度:
     LogicalAggregate(group=[{1, 6}])                       - 下推 AggregateJoinTransposeRule.EXTENDED
      LogicalJoin(condition=[=($1, $5)], joinType=[inner])
       EnumerableTableScan(table=[[hr, emps]])
       EnumerableTableScan(table=[[hr, depts]])

     优化后:
     LogicalProject(deptno=[$0], name=[$2])
      LogicalJoin(condition=[=($0, $1)], joinType=[inner])
       LogicalAggregate(group=[{1}])
        EnumerableTableScan(table=[[hr, emps]])
       LogicalAggregate(group=[{0, 1}])
        EnumerableTableScan(table=[[hr, depts]])
     */
}
