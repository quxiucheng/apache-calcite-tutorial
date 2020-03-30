package com.github.quxiucheng.tutorial.common.cost;

import org.apache.calcite.plan.RelOptCost;

/**
 * @author quxiucheng
 * @date 2019-01-31 15:13:00
 */
public class TutorialRelOptCost implements RelOptCost {

    @Override
    public boolean equals(Object obj) {
        return this == obj
                || obj instanceof TutorialRelOptCost
                && equals((TutorialRelOptCost) obj);
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public double getCpu() {
        return 0;
    }

    @Override
    public boolean isInfinite() {
        return false;
    }

    @Override
    public double getIo() {
        return 0;
    }

    @Override
    public boolean isLe(RelOptCost cost) {
        return true;
    }

    @Override
    public boolean isLt(RelOptCost cost) {
        return false;
    }

    @Override
    public double getRows() {
        return 0;
    }

    @Override
    public boolean equals(RelOptCost cost) {
        return true;
    }

    @Override
    public boolean isEqWithEpsilon(RelOptCost cost) {
        return true;
    }

    @Override
    public RelOptCost minus(RelOptCost cost) {
        return this;
    }

    @Override
    public RelOptCost multiplyBy(double factor) {
        return this;
    }

    @Override
    public double divideBy(RelOptCost cost) {
        return 1;
    }

    @Override
    public RelOptCost plus(RelOptCost cost) {
        return this;
    }

    @Override
    public String toString() {
        return "TutorialRelOptCost(0)";
    }
}
