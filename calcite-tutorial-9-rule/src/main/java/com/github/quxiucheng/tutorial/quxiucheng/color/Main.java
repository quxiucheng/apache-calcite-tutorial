
package com.github.quxiucheng.tutorial.quxiucheng.color;

import com.github.quxiucheng.tutorial.quxiucheng.color.physical.ColoredRel;
import com.github.quxiucheng.tutorial.quxiucheng.color.physical.LeafRel;
import com.github.quxiucheng.tutorial.quxiucheng.color.rule.RuleForGreen;
import com.github.quxiucheng.tutorial.quxiucheng.color.trait.Colors;
import com.github.quxiucheng.tutorial.quxiucheng.color.traitdef.ColorDef;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.sql.SqlExplainFormat;
import org.apache.calcite.sql.SqlExplainLevel;
import org.apache.calcite.sql.type.SqlTypeFactoryImpl;

/**
 * @author quxiucheng
 * @date 2022-05-08 09:40:00
 */
public class Main {
    public static void main(String[] args) {
        // System.out.println(RuleForGreen.PRECEDED_BY_RULE.getClass());
        // System.out.println("aaaa");
        // RelOptCluster cluster =
        VolcanoPlanner volcanoPlanner = new VolcanoPlanner();
        volcanoPlanner.addRelTraitDef(new ColorDef());

        SqlTypeFactoryImpl sqlTypeFactory = new SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
        RexBuilder rexBuilder = new RexBuilder(sqlTypeFactory);
        RelOptCluster relOptCluster = RelOptCluster.create(volcanoPlanner, rexBuilder);

        RelTraitSet redTraitSet = RelTraitSet.createEmpty().plus(Colors.Red);
         RelTraitSet greenTraitSet = RelTraitSet.createEmpty().plus(Colors.Green);
         RelTraitSet blueTraitSet = RelTraitSet.createEmpty().plus(Colors.Blue);
         RelTraitSet none = RelTraitSet.createEmpty().plus(Colors.None);

        // relOptCluster.createCorrel()

        LeafRel leafRel = new LeafRel(relOptCluster, blueTraitSet);
        ColoredRel greenRel = new ColoredRel(relOptCluster, greenTraitSet, leafRel);
        ColoredRel redRel = new ColoredRel(relOptCluster, redTraitSet, greenRel);

        HepProgramBuilder hepProgramBuilder = new HepProgramBuilder();
        // hepProgramBuilder.addRuleInstance(RuleForGreen.PRECEDED_BY_RULE);
        // hepProgramBuilder.addRuleInstance(RuleForGreen.SUCCEEDED_BY_RULE);
        hepProgramBuilder.addRuleInstance(RuleForGreen.REPLACED_BY_RULE);
        HepPlanner hepPlanner = new HepPlanner(hepProgramBuilder.build());
        hepPlanner.setRoot(redRel);
        RelNode bestExp = hepPlanner.findBestExp();

        String bestStr = RelOptUtil.dumpPlan("Result", bestExp, SqlExplainFormat.TEXT, SqlExplainLevel.EXPPLAN_ATTRIBUTES);
        String input = RelOptUtil.dumpPlan("Result", redRel, SqlExplainFormat.TEXT, SqlExplainLevel.EXPPLAN_ATTRIBUTES);
        System.out.println(input);
        System.out.println("===============");
        System.out.println(bestStr);

        // ColoredRel(Color=[{"color":"RED"}])
        //     ColoredRel(Color=[{"color":"GREEN"}])
        //         LeafRel(Color=[{"color":"BLUE"}])
    }

}
