package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.CalcSplitRule;
import org.apache.calcite.rel.rules.FilterToCalcRule;
import org.apache.calcite.rel.rules.ProjectToCalcRule;

/**
 *  将Calc转换为project和filter的Planner规则。
 *  默认情况下不启用，因为它与通常的流程(将project和filter转换为Calc)相反，但是对于特定的任务非常有用，比如在调用org.apache.calcite.interpreter.Interpreter之前进行优化。
 * @author quxiucheng
 * @date 2019-02-02 09:42:00
 */
public class CalcSplitRuleTest {
    public static void main(String[] args) {
        String sql = "select name as ename from hr.emps where name='abcd'";
        RuleTester.printProcessRule(sql, FilterToCalcRule.INSTANCE,
                ProjectToCalcRule.INSTANCE,
                // 可有可无,
                // CalcMergeRule.INSTANCE,
                CalcSplitRule.INSTANCE);

    }

    /**
     sql:
    select name as ename from hr.emps where name='abcd'

    原始:
    LogicalProject(ename=[$2])
      LogicalFilter(condition=[=($2, 'abcd')])
        EnumerableTableScan(table=[[hr, emps]])


    规则:FilterToCalcRule
    LogicalProject(ename=[$2])
      LogicalCalc(expr#0..4=[{inputs}], expr#5=['abcd'], expr#6=[=($t2, $t5)], proj#0..4=[{exprs}], $condition=[$t6])
        EnumerableTableScan(table=[[hr, emps]])


    规则:ProjectToCalcRule
    LogicalCalc(expr#0..4=[{inputs}], ename=[$t2])
      LogicalCalc(expr#0..4=[{inputs}], expr#5=['abcd'], expr#6=[=($t2, $t5)], proj#0..4=[{exprs}], $condition=[$t6])
        EnumerableTableScan(table=[[hr, emps]])


    规则:CalcSplitRule
    LogicalProject(ename=[$2])
      LogicalFilter(condition=[=($2, 'abcd')])
        EnumerableTableScan(table=[[hr, emps]])
     */
}
