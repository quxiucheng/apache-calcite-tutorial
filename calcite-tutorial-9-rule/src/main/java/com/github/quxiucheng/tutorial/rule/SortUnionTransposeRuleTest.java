package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.ProjectSetOpTransposeRule;
import org.apache.calcite.rel.rules.SortUnionTransposeRule;

/**
 * sort移动到union下
 * @author quxiucheng
 * @date 2019-02-20 11:12:00
 */
public class SortUnionTransposeRuleTest {
    public static void main(String[] args) {
        final String sql = "select a.name,a.deptno from hr.depts a\n"
                + "union all\n"
                + "select b.name,b.deptno from hr.depts b\n"
                + "order by name";
        RuleTester.printProcessRule(sql,
                ProjectSetOpTransposeRule.INSTANCE,
                SortUnionTransposeRule.MATCH_NULL_FETCH);

    }
    /**
     sql:
    select a.name,a.deptno from hr.depts a
    union all
    select b.name,b.deptno from hr.depts b
    order by name

    原始:
    LogicalSort(sort0=[$0], dir0=[ASC])
      LogicalProject(name=[$0], deptno=[$1])
        LogicalUnion(all=[true])
          LogicalProject(name=[$1], deptno=[$0])
            EnumerableTableScan(table=[[hr, depts]])
          LogicalProject(name=[$1], deptno=[$0])
            EnumerableTableScan(table=[[hr, depts]])


    规则:ProjectSetOpTransposeRule
    LogicalSort(sort0=[$0], dir0=[ASC])
      LogicalUnion(all=[true])
        LogicalProject(name=[$1], deptno=[$0])
          EnumerableTableScan(table=[[hr, depts]])
        LogicalProject(name=[$1], deptno=[$0])
          EnumerableTableScan(table=[[hr, depts]])


    规则:SortUnionTransposeRule:default
    LogicalSort(sort0=[$0], dir0=[ASC])
      LogicalUnion(all=[true])
        LogicalSort(sort0=[$0], dir0=[ASC])
          LogicalProject(name=[$1], deptno=[$0])
            EnumerableTableScan(table=[[hr, depts]])
        LogicalSort(sort0=[$0], dir0=[ASC])
          LogicalProject(name=[$1], deptno=[$0])
            EnumerableTableScan(table=[[hr, depts]])
     */
}
