package com.github.quxiucheng.tutorial.common.data;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.rel.RelCollation;
import org.apache.calcite.rel.RelDistribution;
import org.apache.calcite.rel.RelDistributionTraitDef;
import org.apache.calcite.rel.RelReferentialConstraint;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.util.ImmutableBitSet;

import java.util.List;

/**
 * @author quxiucheng
 * @date 2019-02-01 13:45:00
 */
public class TutorialStatistic implements Statistic {

    private TutorialTable tutorialTable;

    public TutorialStatistic(TutorialTable tutorialTable) {
        this.tutorialTable = tutorialTable;
    }

    @Override
    public Double getRowCount() {
        return null;
    }

    @Override
    public boolean isKey(ImmutableBitSet columns) {
        List<TutorialColumn> sqlExecuteColumnList = tutorialTable.getSqlExecuteColumnList();
        List<Integer> indexs = columns.toList();
        for (Integer index : indexs) {
            if (!sqlExecuteColumnList.get(index).isPrimary()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<RelReferentialConstraint> getReferentialConstraints() {
        return ImmutableList.of();
    }

    @Override
    public List<RelCollation> getCollations() {
        return ImmutableList.of();
    }

    @Override
    public RelDistribution getDistribution() {
        return RelDistributionTraitDef.INSTANCE.getDefault();
    }
}
