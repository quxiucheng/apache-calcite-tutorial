package com.github.quxiucheng.tutorial.quxiucheng.color.rule;

import com.github.quxiucheng.tutorial.quxiucheng.color.physical.ColoredCommonRel;
import com.github.quxiucheng.tutorial.quxiucheng.color.physical.RuledRel;
import com.github.quxiucheng.tutorial.quxiucheng.color.trait.ColorTrait;
import com.github.quxiucheng.tutorial.quxiucheng.color.trait.Colors;
import com.google.common.collect.ImmutableList;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelRule;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.tools.RelBuilderFactory;

/**
 * @author quxiucheng
 * @date 2022-05-08 09:59:00
 */
public class RuleForGreen extends RelRule<RuleForGreen.Config> {

    public static RelOptRule PRECEDED_BY_RULE = withOperandFor(new MatchHandler<RuleForGreen>() {
        @Override
        public void accept(RuleForGreen ruleForGreen, RelOptRuleCall call) {
            RuleForGreen.addParent(ruleForGreen, call);
        }
    }).toRule();

    public static RelOptRule SUCCEEDED_BY_RULE = withOperandFor(new MatchHandler<RuleForGreen>() {
        @Override
        public void accept(RuleForGreen ruleForGreen, RelOptRuleCall call) {
            RuleForGreen.addChild(ruleForGreen, call);
        }
    }).toRule();

    public static RelOptRule REPLACED_BY_RULE = withOperandFor(new MatchHandler<RuleForGreen>() {
        @Override
        public void accept(RuleForGreen ruleForGreen, RelOptRuleCall call) {
            RuleForGreen.replace(ruleForGreen, call);
        }
    }).toRule();

    public RuleForGreen(Config config) {
        super(config);
    }

    @Override
    public void onMatch(RelOptRuleCall call) {
        config.matchHandler().accept(this, call);
    }

    public static RelRule.Config withOperandFor(MatchHandler<RuleForGreen> matchHandler) {
        return new RuleForGreen.Config()
                .withMatchHandler(matchHandler)
                .withRelBuilderFactory(RelFactories.LOGICAL_BUILDER)
                .withOperandSupplier(new OperandTransform() {
                                         @Override
                                         public Done apply(OperandBuilder b0) {
                                             return b0.operand(ColoredCommonRel.class).trait(Colors.Green).anyInputs();
                                         }
                                     });
    }


    public static void addParent(RuleForGreen rule, RelOptRuleCall call) {
        RelNode rel = call.rel(0);
        RelTraitSet originalTraits = rel.getTraitSet();
        RelTraitSet enforcedTraits = originalTraits.replace(new Colors.None());
        RuledRel enforce = new RuledRel(rel.getCluster(), originalTraits, rel.copy(enforcedTraits, rel.getInputs()));
        call.transformTo(enforce);
    }

    public static void addChild(RuleForGreen rule, RelOptRuleCall call) {
        RelNode rel = call.rel(0);
        RelTraitSet originalTraits = rel.getTraitSet();
        RelTraitSet enforcedTraits = originalTraits.replace( Colors.None);
        RuledRel enforce = new RuledRel(rel.getCluster(), originalTraits, rel.getInput(0));
        call.transformTo(rel.copy(enforcedTraits, ImmutableList.of(enforce)));
    }

    public static void replace(RuleForGreen rule, RelOptRuleCall call) {
        RelNode rel = call.rel(0);
        RelTraitSet desiredTraits = rel.getTraitSet().replace( Colors.None);
        RuledRel enforce = new RuledRel(rel.getCluster(), desiredTraits, rel.getInput(0));
        call.transformTo(enforce);
    }




    public static class Config implements RelRule.Config {
        private RelBuilderFactory factory = null;

        private String text = "Fires on green color";
        private OperandTransform supplier = null;
        private MatchHandler<RuleForGreen> _matchHandler = null;

        public MatchHandler<RuleForGreen> matchHandler() {
            return this._matchHandler;
        }

        public RelRule.Config withMatchHandler(MatchHandler<RuleForGreen> matchHandler) {
            this._matchHandler = matchHandler;
            return this;
        }

        @Override
        public RelOptRule toRule() {
            return new RuleForGreen(this);
        }

        @Override
        public RelBuilderFactory relBuilderFactory() {
            return this.factory;
        }

        @Override
        public RelRule.Config withRelBuilderFactory(RelBuilderFactory factory) {
            this.factory = factory;
            return this;
        }

        @Override
        public String description() {
            return this.text;
        }

        @Override
        public RelRule.Config withDescription(String description) {
            this.text = description;
            return this;
        }

        @Override
        public OperandTransform operandSupplier() {
            return this.supplier;
        }

        @Override
        public RelRule.Config withOperandSupplier(OperandTransform transform) {
            this.supplier = transform;
            return this;
        }
    }

}
