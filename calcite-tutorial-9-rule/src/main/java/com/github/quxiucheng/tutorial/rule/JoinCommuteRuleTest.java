package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.rules.JoinCommuteRule;

/**
 * 交换链接的顺序
 * 为了保持输出行中列的顺序，该规则添加了一个投影 project
 * @author quxiucheng
 * @date 2019-02-03 10:15:00
 */
public class JoinCommuteRuleTest {
    public static void main(String[] args) {
        HepProgramBuilder hepProgramBuilder = new HepProgramBuilder();
        hepProgramBuilder.addMatchLimit(1);
        String sql = "select e.name from hr.emps e right join hr.depts d on e.deptno = d.deptno";
        RuleTester.printOriginalCompare(sql, hepProgramBuilder, JoinCommuteRule.SWAP_OUTER);
    }

    /**
    sql:
    select e.name from hr.emps e right join hr.depts d on e.deptno = d.deptno

    原始:
    LogicalProject(name=[$2])
      LogicalJoin(condition=[=($1, $5)], joinType=[right])
        EnumerableTableScan(table=[[hr, emps]])
        EnumerableTableScan(table=[[hr, depts]])


    优化后:
    LogicalProject(name=[$2])
      LogicalProject(empid=[$3], deptno=[$4], name=[$5], salary=[$6], commission=[$7], deptno0=[$0], name0=[$1], create_time=[$2])
        LogicalJoin(condition=[=($4, $0)], joinType=[left])
          EnumerableTableScan(table=[[hr, depts]])
          EnumerableTableScan(table=[[hr, emps]])
     */
}
