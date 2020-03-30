package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.JoinToMultiJoinRule;

/**
 * join转换为MultiJoin
 * 规则如下
 * A JOIN B → MJ(A, B)
 * A JOIN B JOIN C → MJ(A, B, C)
 * A LEFT JOIN B → MJ(A, B), left outer join on input#1
 * A RIGHT JOIN B → MJ(A, B), right outer join on input#0
 * A FULL JOIN B → MJ[full](A, B)
 * A LEFT JOIN (B JOIN C) → MJ(A, MJ(B, C))), left outer join on input#1 in the outermost MultiJoin
 * (A JOIN B) LEFT JOIN C → MJ(A, B, C), left outer join on input#2
 * (A LEFT JOIN B) JOIN C → MJ(MJ(A, B), C), left outer join on input#1 of the inner MultiJoin TODO
 * A LEFT JOIN (B FULL JOIN C) → MJ(A, MJ[full](B, C)), left outer join on input#1 in the outermost MultiJoin
 * (A LEFT JOIN B) FULL JOIN (C RIGHT JOIN D) → MJ[full](MJ(A, B), MJ(C, D)), left outer join on input #1 in the first inner MultiJoin and right outer join on input#0 in the second inner MultiJoin

 * @author quxiucheng
 * @date 2019-02-02 11:37:00
 */
public class JoinToMultiJoinRuleTest {
    public static void main(String[] args) {
        String sql = "select e.name as ename,d.name as dname from hr.emps e join hr.depts d on e.deptno = d.deptno where e.name = '1'";
        // HepMatchOrder.BOTTOM_UP
        RuleTester.printProcessRule(sql, JoinToMultiJoinRule.INSTANCE);
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
     */
}
