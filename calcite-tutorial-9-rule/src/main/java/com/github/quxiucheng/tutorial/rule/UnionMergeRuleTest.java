package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.UnionMergeRule;

/**
 * 将多个union合并到一起
 * @author quxiucheng
 * @date 2019-02-20 11:44:00
 */
public class UnionMergeRuleTest {
    public static void main(String[] args) {
        final String sql = "select * from hr.emps where deptno = 10\n"
                + "union all\n"
                + "select * from hr.emps where deptno = 20\n"
                + "union all\n"
                + "select * from hr.emps where deptno = 30\n";
        RuleTester.printProcessRule(sql, UnionMergeRule.INSTANCE);
    }
    /**
     sql:
    select * from hr.emps where deptno = 10
    union all
    select * from hr.emps where deptno = 20
    union all
    select * from hr.emps where deptno = 30


    原始:
    LogicalUnion(all=[true])
      LogicalUnion(all=[true])
        LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
          LogicalFilter(condition=[=($1, 10)])
            EnumerableTableScan(table=[[hr, emps]])
        LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
          LogicalFilter(condition=[=($1, 20)])
            EnumerableTableScan(table=[[hr, emps]])
      LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
        LogicalFilter(condition=[=($1, 30)])
          EnumerableTableScan(table=[[hr, emps]])


    规则:UnionMergeRule
    LogicalUnion(all=[true])
      LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
        LogicalFilter(condition=[=($1, 10)])
          EnumerableTableScan(table=[[hr, emps]])
      LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
        LogicalFilter(condition=[=($1, 20)])
          EnumerableTableScan(table=[[hr, emps]])
      LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
        LogicalFilter(condition=[=($1, 30)])
          EnumerableTableScan(table=[[hr, emps]])
     */
}
