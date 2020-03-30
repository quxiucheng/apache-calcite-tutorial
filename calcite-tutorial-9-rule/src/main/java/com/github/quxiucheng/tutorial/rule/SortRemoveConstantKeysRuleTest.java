package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.SortRemoveConstantKeysRule;

/**
 * 移除sort中的常量值
 * @author quxiucheng
 * @date 2019-02-20 10:32:00
 */
public class SortRemoveConstantKeysRuleTest {
    public static void main(String[] args) {
        String sql = "select name from hr.emps where name = 'abc' order by name";
        RuleTester.printOriginalCompare(sql, SortRemoveConstantKeysRule.INSTANCE);
    }
    /**
    sql:
    select name from hr.emps where name = 'abc' order by name

    原始:
    LogicalSort(sort0=[$0], dir0=[ASC])
      LogicalProject(name=[$2])
        LogicalFilter(condition=[=($2, 'abc')])
          EnumerableTableScan(table=[[hr, emps]])


    优化后:
    LogicalProject(name=[$2])
      LogicalFilter(condition=[=($2, 'abc')])
        EnumerableTableScan(table=[[hr, emps]])
     */
}
