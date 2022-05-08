
package com.github.quxiucheng.tutorial.quxiucheng.color.traitdef;

import com.github.quxiucheng.tutorial.quxiucheng.color.trait.ColorTrait;
import com.github.quxiucheng.tutorial.quxiucheng.color.trait.Colors;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelTraitDef;
import org.apache.calcite.rel.RelNode;

/**
 * @author quxiucheng
 * @date 2022-05-08 09:18:00
 */
public class ColorDef extends RelTraitDef<ColorTrait> {

    public static final ColorDef INSTANCE = new ColorDef();

    @Override
    public Class<ColorTrait> getTraitClass() {
        return ColorTrait.class;
    }

    @Override
    public String getSimpleName() {
        return "ColorTraitDef";
    }

    @Override
    public ColorTrait getDefault() {
        return Colors.None;
    }

    @Override
    public RelNode convert(RelOptPlanner planner, RelNode rel, ColorTrait toTrait, boolean allowInfiniteCostConverters) {
        return rel;
    }

    @Override
    public boolean canConvert(RelOptPlanner planner, ColorTrait fromTrait, ColorTrait toTrait) {
        return fromTrait.satisfies(toTrait);
    }

}
