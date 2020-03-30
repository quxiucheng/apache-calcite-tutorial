package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.FilterCalcMergeRule;
import org.apache.calcite.rel.rules.ProjectToCalcRule;

/**
 * Filter和Calc合并到一起
 * @author quxiucheng
 * @date 2019-02-01 17:45:00
 */
public class FilterCalcMergeRuleTest {
    public static void main(String[] args) {
        String sql = "select * from (select * from hr.emps) t where name ='1'";
        RuleTester.printProcessRule(sql, ProjectToCalcRule.INSTANCE, FilterCalcMergeRule.INSTANCE);
    }
    /**
    sql:
    select * from (select * from hr.emps) t where name ='1'

    原始:
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalFilter(condition=[=($2, '1')])
        LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
          EnumerableTableScan(table=[[hr, emps]])


    规则:ProjectToCalcRule
    LogicalCalc(expr#0..4=[{inputs}], proj#0..4=[{exprs}])
      LogicalFilter(condition=[=($2, '1')])
        LogicalCalc(expr#0..4=[{inputs}], proj#0..4=[{exprs}])
          EnumerableTableScan(table=[[hr, emps]])


    规则:FilterCalcMergeRule
    LogicalCalc(expr#0..4=[{inputs}], proj#0..4=[{exprs}])
      LogicalCalc(expr#0..4=[{inputs}], expr#5=['1'], expr#6=[=($t2, $t5)], proj#0..4=[{exprs}], $condition=[$t6])
        EnumerableTableScan(table=[[hr, emps]])
     */
}
