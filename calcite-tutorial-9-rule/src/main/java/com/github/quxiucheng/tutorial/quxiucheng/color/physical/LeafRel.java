package com.github.quxiucheng.tutorial.quxiucheng.color.physical;

import com.github.quxiucheng.tutorial.quxiucheng.color.traitdef.ColorDef;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.AbstractRelNode;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelWriter;
import org.apache.calcite.rel.type.RelDataType;

import java.util.List;

/**
 * @author quxiucheng
 * @date 2022-05-08 16:19:00
 */
public class LeafRel extends AbstractRelNode implements ColoredCommonRel {


    public LeafRel(RelOptCluster cluster, RelTraitSet traitSet) {
        super(cluster, traitSet);
    }

    @Override
    public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
        if (inputs.isEmpty()) {
            return new LeafRel(getCluster(), traitSet);
        }
        return new ColoredRel(getCluster(), traitSet, sole(inputs));
    }

    @Override
    public RelWriter explainTerms(RelWriter pw) {
        RelWriter relWriter = super.explainTerms(pw);
        return relWriter.item("Color", getTraitSet().getTrait(ColorDef.INSTANCE));
    }

    @Override
    protected RelDataType deriveRowType() {
        return new JavaTypeFactoryImpl().createUnknownType();
    }

}