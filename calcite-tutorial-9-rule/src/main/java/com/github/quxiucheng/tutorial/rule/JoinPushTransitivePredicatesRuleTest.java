package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.FilterJoinRule;
import org.apache.calcite.rel.rules.JoinPushTransitivePredicatesRule;

/**
 * 从联接上推断谓词，如果可以将这些谓词推入其输入，则创建过滤器。
 * 例如等值链接后,有对等值条件的其他过滤条件
 * @author quxiucheng
 * @date 2019-02-18 20:50:00
 */
public class JoinPushTransitivePredicatesRuleTest {
    public static void main(String[] args) {
        String sql = "select 1 from hr.emps e inner join hr.emps d on d.deptno = e.deptno where e.deptno > 7";
        RuleTester.printProcessRule(sql,
                // filter下推到join
                FilterJoinRule.FILTER_ON_JOIN,
                JoinPushTransitivePredicatesRule.INSTANCE);
    }
    /**
     sql:
    select 1 from hr.emps e inner join hr.emps d on d.deptno = e.deptno where e.deptno > 7

    原始:
    LogicalProject(EXPR$0=[1])
      LogicalFilter(condition=[>($1, 7)])
        LogicalJoin(condition=[=($6, $1)], joinType=[inner])
          EnumerableTableScan(table=[[hr, emps]])
          EnumerableTableScan(table=[[hr, emps]])


    规则:FilterJoinRule:FilterJoinRule:filter
    LogicalProject(EXPR$0=[1])
      LogicalJoin(condition=[=($6, $1)], joinType=[inner])
        LogicalFilter(condition=[>($1, 7)])
          EnumerableTableScan(table=[[hr, emps]])
        EnumerableTableScan(table=[[hr, emps]])


    规则:JoinPushTransitivePredicatesRule
    LogicalProject(EXPR$0=[1])
      LogicalJoin(condition=[=($6, $1)], joinType=[inner])
        LogicalFilter(condition=[>($1, 7)])
          EnumerableTableScan(table=[[hr, emps]])
        LogicalFilter(condition=[>($1, 7)])
          EnumerableTableScan(table=[[hr, emps]])

     */
}
