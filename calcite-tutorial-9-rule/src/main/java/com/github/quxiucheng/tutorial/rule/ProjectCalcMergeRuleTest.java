package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.FilterToCalcRule;
import org.apache.calcite.rel.rules.ProjectCalcMergeRule;

/**
 * 合并project和Calc
 * @author quxiucheng
 * @date 2019-02-01 17:51:00
 */
public class ProjectCalcMergeRuleTest {
    public static void main(String[] args) {
        String sql = "select * from hr.emps where name ='1'";
        RuleTester.printProcessRule(sql, FilterToCalcRule.INSTANCE, ProjectCalcMergeRule.INSTANCE);
    }

    /**
     sql:
    select * from hr.emps where name ='1'

    原始:
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalFilter(condition=[=($2, '1')])
        EnumerableTableScan(table=[[hr, emps]])


    规则:FilterToCalcRule
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalCalc(expr#0..4=[{inputs}], expr#5=['1'], expr#6=[=($t2, $t5)], proj#0..4=[{exprs}], $condition=[$t6])
        EnumerableTableScan(table=[[hr, emps]])


    规则:ProjectCalcMergeRule
    LogicalCalc(expr#0..4=[{inputs}], expr#5=['1'], expr#6=[=($t2, $t5)], proj#0..4=[{exprs}], $condition=[$t6])
      EnumerableTableScan(table=[[hr, emps]])
     */
}