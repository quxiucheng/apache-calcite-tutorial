package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.rel.logical.LogicalUnion;
import org.apache.calcite.rel.rules.CoerceInputsRule;

/**
 * 将输入强制转换为特定类型
 *
 * @author quxiucheng
 * @date 2019-02-02 09:52:00
 */
public class CoerceInputsRuleTest {
    public static void main(String[] args) {
        // emps name varchar 10
        // depts name varchar 20
        String sql =
                "select name from hr.emps " +
                 "union all " +
                 "select name from hr.depts";

        CoerceInputsRule coerceInputsRule = new CoerceInputsRule(LogicalUnion.class, false, RelFactories.LOGICAL_BUILDER);
        RuleTester.printOriginalCompare(sql, coerceInputsRule);

    }
    /**
     sql:
    select name from hr.emps union all select name from hr.depts

    原始:
    LogicalUnion(all=[true])
      LogicalProject(name=[$2])
        EnumerableTableScan(table=[[hr, emps]])
      LogicalProject(name=[$1])
        EnumerableTableScan(table=[[hr, depts]])


    优化后:
    LogicalUnion(all=[true])
      LogicalProject(name=[CAST($0):VARCHAR(20) CHARACTER SET "ISO-8859-1" COLLATE "ISO-8859-1$en_US$primary" NOT NULL])
        LogicalProject(name=[$2])
          EnumerableTableScan(table=[[hr, emps]])
      LogicalProject(name=[$1])
        EnumerableTableScan(table=[[hr, depts]])
     */
}
