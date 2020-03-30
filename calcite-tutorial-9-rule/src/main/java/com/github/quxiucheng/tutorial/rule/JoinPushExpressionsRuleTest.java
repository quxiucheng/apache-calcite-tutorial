package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.FilterJoinRule;
import org.apache.calcite.rel.rules.JoinPushExpressionsRule;

/**
 * 下推 等值链接的join中的表达式到project中
 * @author quxiucheng
 * @date 2019-02-03 15:25:00
 *
 */
public class JoinPushExpressionsRuleTest {
    public static void main(String[] args) {
        String sql = "select e.deptno from hr.emps e left join hr.emps d on e.deptno = d.deptno where e.deptno +2 = d.deptno *2";

        RuleTester.printProcessRule(sql,
                FilterJoinRule.FILTER_ON_JOIN,
                // join下推到project中
                JoinPushExpressionsRule.INSTANCE
        );

    }

    /**
     sql:
    select e.deptno from hr.emps e left join hr.emps d on e.deptno = d.deptno where e.deptno +2 = d.deptno *2

    原始:
    LogicalProject(deptno=[$1])
      LogicalFilter(condition=[=(+($1, 2), *($6, 2))])
        LogicalJoin(condition=[=($1, $6)], joinType=[left])
          EnumerableTableScan(table=[[hr, emps]])
          EnumerableTableScan(table=[[hr, emps]])


    规则:FilterJoinRule:FilterJoinRule:filter
    LogicalProject(deptno=[$1])
      LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4], empid0=[CAST($5):INTEGER], deptno0=[CAST($6):INTEGER], name0=[CAST($7):VARCHAR(10) CHARACTER SET "ISO-8859-1" COLLATE "ISO-8859-1$en_US$primary"], salary0=[CAST($8):FLOAT], commission0=[CAST($9):INTEGER])
        LogicalJoin(condition=[AND(=($1, $6), =(+($1, 2), *($6, 2)))], joinType=[inner])
          EnumerableTableScan(table=[[hr, emps]])
          EnumerableTableScan(table=[[hr, emps]])


    规则:JoinPushExpressionsRule
    LogicalProject(deptno=[$1])
      LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4], empid0=[CAST($5):INTEGER], deptno0=[CAST($6):INTEGER], name0=[CAST($7):VARCHAR(10) CHARACTER SET "ISO-8859-1" COLLATE "ISO-8859-1$en_US$primary"], salary0=[CAST($8):FLOAT], commission0=[CAST($9):INTEGER])
        LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4], empid0=[$6], deptno0=[$7], name0=[$8], salary0=[$9], commission0=[$10])
          LogicalJoin(condition=[AND(=($1, $7), =($5, $11))], joinType=[inner])
            LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4], $f5=[+($1, 2)])
              EnumerableTableScan(table=[[hr, emps]])
            LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4], $f5=[*($1, 2)])
              EnumerableTableScan(table=[[hr, emps]])
     */
}
