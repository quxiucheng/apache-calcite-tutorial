package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.UnionToDistinctRule;

/**
 * 和union去重
 * @author quxiucheng
 * @date 2019-02-20 11:53:00
 */
public class UnionToDistinctRuleTest {
    public static void main(String[] args) {
        final String sql = "select * from hr.depts union select * from hr.depts";
        RuleTester.printProcessRule(sql, UnionToDistinctRule.INSTANCE);
    }
    /**
     sql:
    select * from hr.depts union select * from hr.depts

    原始:
    LogicalUnion(all=[false])
      LogicalProject(deptno=[$0], name=[$1], create_time=[$2])
        EnumerableTableScan(table=[[hr, depts]])
      LogicalProject(deptno=[$0], name=[$1], create_time=[$2])
        EnumerableTableScan(table=[[hr, depts]])


    规则:UnionToDistinctRule
    LogicalAggregate(group=[{0, 1, 2}])
      LogicalUnion(all=[true])
        LogicalProject(deptno=[$0], name=[$1], create_time=[$2])
          EnumerableTableScan(table=[[hr, depts]])
        LogicalProject(deptno=[$0], name=[$1], create_time=[$2])
          EnumerableTableScan(table=[[hr, depts]])
     */
}
