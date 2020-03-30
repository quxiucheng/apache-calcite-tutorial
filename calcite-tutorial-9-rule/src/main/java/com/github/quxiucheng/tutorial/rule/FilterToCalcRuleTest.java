package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.FilterToCalcRule;

/**
 * Filter转换成Calc
 *
 * @author quxiucheng
 * @date 2019-02-01 17:31:00
 */
public class FilterToCalcRuleTest {
    public static void main(String[] args) {
        String sql = "select * from hr.emps where name='abcd'";
        RuleTester.printOriginalCompare(sql, FilterToCalcRule.INSTANCE);
    }

    /**
     *
    sql:
    select * from hr.emps where name='abcd'

    原始:
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalFilter(condition=[=($2, 'abcd')])
        EnumerableTableScan(table=[[hr, emps]])


    优化后:
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalCalc(expr#0..4=[{inputs}], expr#5=['abcd'], expr#6=[=($t2, $t5)], proj#0..4=[{exprs}], $condition=[$t6])
        EnumerableTableScan(table=[[hr, emps]])
     */
}
