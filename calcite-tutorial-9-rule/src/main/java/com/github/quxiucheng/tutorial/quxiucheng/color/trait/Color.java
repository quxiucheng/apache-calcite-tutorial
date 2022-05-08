package com.github.quxiucheng.tutorial.quxiucheng.color.trait;

import com.github.quxiucheng.tutorial.quxiucheng.color.traitdef.ColorDef;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelTrait;
import org.apache.calcite.plan.RelTraitDef;

import java.util.List;
import java.util.Map;

/**
 * @author quxiucheng
 * @date 2022-05-08 09:28:00
 */
public class Color extends ColorTrait {

    public Color(String color) {
        super(color);
    }

    @Override
    public RelTraitDef getTraitDef() {
        return ColorDef.INSTANCE;
    }

    @Override
    public boolean satisfies(RelTrait trait) {
        ColorTrait colorTrait = null;
        if (trait instanceof ColorTrait) {
            colorTrait = (ColorTrait) trait;
        }
        boolean result = (trait instanceof ColorTrait || (
                isColorTrait(trait) && colorTrait != null && satisfies(this.color, colorTrait.getColor())));
        return result;
    }

    @Override
    public void register(RelOptPlanner planner) {

    }

    public boolean isColorTrait(RelTrait relTrait) {
        return relTrait instanceof ColorTrait;

    }

    public boolean satisfies(String colorA, String colorB) {
        List<String> strings = satisfyCondition.get(colorA);
        return strings.contains(colorB);
    }

    public static Map<String, List<String>> satisfyCondition = Maps.newHashMap();

    static {
        satisfyCondition.put("BLUE", Lists.newArrayList("NONE", "RED", "GREEN", "BLUE"));
        satisfyCondition.put("GREEN", Lists.newArrayList("NONE", "RED", "GREEN"));
        satisfyCondition.put("RED", Lists.newArrayList("NONE", "RED"));
        satisfyCondition.put("NONE", Lists.newArrayList());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"color\":\"")
                .append(color).append('\"');
        sb.append('}');
        return sb.toString();
    }


}
