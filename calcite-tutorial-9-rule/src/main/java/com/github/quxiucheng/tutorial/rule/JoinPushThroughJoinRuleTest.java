package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.JoinProjectTransposeRule;
import org.apache.calcite.rel.rules.JoinPushThroughJoinRule;

/**
 *
 //        topJoin
 //        /     \
 //   bottomJoin  C
 //    /    \
 //   A      B

 // becomes
 //
 //        newTopJoin
 //        /        \
 //   newBottomJoin  B
 //    /    \
 //   A      C
 * @author quxiucheng
 * @date 2019-02-18 19:43:00
 */
public class JoinPushThroughJoinRuleTest {
    public static void main(String[] args) {
        // String sql = "select * from (select e.name as name ,d1.deptno as deptno from hr.emps e " +
        //         "inner join hr.depts d1 on  e.deptno = d1.deptno and  e.name = d1.name ) e2 ) " +
        //         "inner join hr.depts d on e2.deptno = d.deptno and  e2.name = d.name" +
        //         "";
        String sql = "select * from hr.emps as s join hr.depts as pc on true "
                +
                " join hr.depts2 as p on s.name = p.name "
                +
                " and p.deptno = pc.deptno"
                ;

        RuleTester.printProcessRule(sql,
                JoinProjectTransposeRule.LEFT_PROJECT,
                JoinPushThroughJoinRule.LEFT);
    }
}
