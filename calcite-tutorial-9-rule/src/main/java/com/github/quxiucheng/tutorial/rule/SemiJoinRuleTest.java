package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.SemiJoinRule;

/**
 * Join和LogicalAggregate 转化成SemiJoin
 * @author quxiucheng
 * @date 2019-02-20 10:16:00
 */
public class SemiJoinRuleTest {
    public static void main(String[] args) {
        String sql =  "select d.* from hr.depts d join ("
                + "  select distinct deptno from hr.emps"
                + "  ) using (deptno)";
        RuleTester.printProcessRule(sql,
                SemiJoinRule.PROJECT);
    }
    /**
    sql:
    select d.* from hr.depts d join (  select distinct deptno from hr.emps  ) using (deptno)

    原始:
    LogicalProject(deptno=[$0], name=[$1], create_time=[$2])
      LogicalJoin(condition=[=($0, $3)], joinType=[inner])
        EnumerableTableScan(table=[[hr, depts]])
        LogicalAggregate(group=[{0}])
          LogicalProject(deptno=[$1])
            EnumerableTableScan(table=[[hr, emps]])


    规则:SemiJoinRule:project
    SemiJoin(condition=[=($0, $3)], joinType=[inner])
      EnumerableTableScan(table=[[hr, depts]])
      LogicalProject(deptno=[$1])
        EnumerableTableScan(table=[[hr, emps]])
     */
}
