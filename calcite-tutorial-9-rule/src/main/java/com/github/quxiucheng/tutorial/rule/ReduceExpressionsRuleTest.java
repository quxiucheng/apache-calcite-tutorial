package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.ReduceExpressionsRule;

/**
 * @author quxiucheng
 * @date 2019-02-01 16:00:00
 *
 * 计算常量树表达式
 * 目前支持 cast,等值,不等值,and ,or等
 * 还不支持 + - / *
 *
 * CALC_INSTANCE
 * EXCLUSION_PATTERN
 * FILTER_INSTANCE
 * JOIN_INSTANCE
 * PROJECT_INSTANCE
 */
public class ReduceExpressionsRuleTest {
    public static void main(String[] args) {
        String sql = "select * from hr.emps where 1=2";
        RuleTester.printOriginalCompare(sql, ReduceExpressionsRule.FILTER_INSTANCE);
    }
    /**
     *
    sql:
    select * from hr.emps where 1=2

    原始:
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalFilter(condition=[=(1, 2)])
        EnumerableTableScan(table=[[hr, emps]])


    优化后:
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalValues(tuples=[[]])
     */
}
