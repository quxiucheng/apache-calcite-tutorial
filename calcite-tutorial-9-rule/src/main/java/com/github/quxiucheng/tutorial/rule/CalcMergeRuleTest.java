package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.CalcMergeRule;
import org.apache.calcite.rel.rules.FilterToCalcRule;
import org.apache.calcite.rel.rules.ProjectToCalcRule;

/**
 * 合并两个Calc
 * @author quxiucheng
 * @date 2019-02-01 17:38:00
 */
public class CalcMergeRuleTest {
    public static void main(String[] args) {
        String sql = "select * from hr.emps where name='abcd'";
        RuleTester.printProcessRule(sql, FilterToCalcRule.INSTANCE, ProjectToCalcRule.INSTANCE, CalcMergeRule.INSTANCE);
    }
    /**
     sql:
    select * from hr.emps where name='abcd'

    原始:
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalFilter(condition=[=($2, 'abcd')])
        EnumerableTableScan(table=[[hr, emps]])


    规则:FilterToCalcRule
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalCalc(expr#0..4=[{inputs}], expr#5=['abcd'], expr#6=[=($t2, $t5)], proj#0..4=[{exprs}], $condition=[$t6])
        EnumerableTableScan(table=[[hr, emps]])


    规则:ProjectToCalcRule
    LogicalCalc(expr#0..4=[{inputs}], proj#0..4=[{exprs}])
      LogicalCalc(expr#0..4=[{inputs}], expr#5=['abcd'], expr#6=[=($t2, $t5)], proj#0..4=[{exprs}], $condition=[$t6])
        EnumerableTableScan(table=[[hr, emps]])


    规则:CalcMergeRule
    LogicalCalc(expr#0..4=[{inputs}], expr#5=['abcd'], expr#6=[=($t2, $t5)], proj#0..4=[{exprs}], $condition=[$t6])
      EnumerableTableScan(table=[[hr, emps]])
     */
}
