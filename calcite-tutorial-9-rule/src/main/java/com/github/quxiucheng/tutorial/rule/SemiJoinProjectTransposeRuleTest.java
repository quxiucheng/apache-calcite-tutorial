package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.rel.rules.FilterJoinRule;
import org.apache.calcite.rel.rules.JoinAddRedundantSemiJoinRule;
import org.apache.calcite.rel.rules.SemiJoinProjectTransposeRule;

import static org.apache.calcite.rel.rules.CoreRules.JOIN_ADD_REDUNDANT_SEMI_JOIN;
import static org.apache.calcite.rel.rules.CoreRules.SEMI_JOIN_PROJECT_TRANSPOSE;

/**
 * semiJoin下推到project
 * @author quxiucheng
 * @date 2019-02-20 10:04:00
 */
public class SemiJoinProjectTransposeRuleTest {
    public static void main(String[] args) {
        String sql = "select e.* from "
                + "(select name, deptno from hr.emps) e, hr.depts d "
                + "where e.deptno = d.deptno";
        RuleTester.printProcessRule(sql,
                // filter下推到join
                CoreRules.FILTER_INTO_JOIN,
                // 转成semiJoin
                JOIN_ADD_REDUNDANT_SEMI_JOIN,
                // semiJoin下推到project
                SEMI_JOIN_PROJECT_TRANSPOSE);
    }
    /**
    sql:
    select e.* from (select name, deptno from hr.emps) e, hr.depts d where e.deptno = d.deptno

    原始:
    LogicalProject(name=[$0], deptno=[$1])
      LogicalFilter(condition=[=($1, $2)])
        LogicalJoin(condition=[true], joinType=[inner])
          LogicalProject(name=[$2], deptno=[$1])
            EnumerableTableScan(table=[[hr, emps]])
          EnumerableTableScan(table=[[hr, depts]])


    规则:FilterJoinRule:FilterJoinRule:filter
    LogicalProject(name=[$0], deptno=[$1])
      LogicalJoin(condition=[=($1, $2)], joinType=[inner])
        LogicalProject(name=[$2], deptno=[$1])
          EnumerableTableScan(table=[[hr, emps]])
        EnumerableTableScan(table=[[hr, depts]])


    规则:JoinAddRedundantSemiJoinRule
    LogicalProject(name=[$0], deptno=[$1])
      LogicalJoin(condition=[=($1, $2)], joinType=[inner], semiJoinDone=[true])
        SemiJoin(condition=[=($1, $2)], joinType=[inner])
          LogicalProject(name=[$2], deptno=[$1])
            EnumerableTableScan(table=[[hr, emps]])
          EnumerableTableScan(table=[[hr, depts]])
        EnumerableTableScan(table=[[hr, depts]])


    规则:SemiJoinProjectTransposeRule
    LogicalProject(name=[$0], deptno=[$1])
      LogicalJoin(condition=[=($1, $2)], joinType=[inner], semiJoinDone=[true])
        LogicalProject(name=[$2], deptno=[$1])
          SemiJoin(condition=[=($1, $5)], joinType=[inner])
            EnumerableTableScan(table=[[hr, emps]])
            EnumerableTableScan(table=[[hr, depts]])
        EnumerableTableScan(table=[[hr, depts]])
     */
}
