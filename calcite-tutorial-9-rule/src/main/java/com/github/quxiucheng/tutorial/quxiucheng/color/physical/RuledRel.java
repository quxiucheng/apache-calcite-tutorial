package com.github.quxiucheng.tutorial.quxiucheng.color.physical;

import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import com.github.quxiucheng.tutorial.quxiucheng.color.traitdef.ColorDef;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelWriter;
import org.apache.calcite.rel.SingleRel;

import java.util.List;

/**
 * @author quxiucheng
 * @date 2022-05-08 09:57:00
 */
public class RuledRel extends SingleRel {

    public RuledRel(RelOptCluster cluster, RelTraitSet traits, RelNode input) {
        super(cluster, traits, input);
    }

    @Override
    public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
        return new RuledRel(getCluster(), traitSet, sole(inputs));
    }

    @Override
    public RelWriter explainTerms(RelWriter pw) {
        RelWriter relWriter = super.explainTerms(pw);
        return relWriter.item("Color", getTraitSet().getTrait(ColorDef.INSTANCE));
    }


}

