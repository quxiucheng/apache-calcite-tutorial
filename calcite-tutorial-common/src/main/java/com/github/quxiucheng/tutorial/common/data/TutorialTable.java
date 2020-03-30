/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.quxiucheng.tutorial.common.data;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.Statistics;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author quxiucheng
 * @date 2019-01-12 09:57:00
 */
@Data
public class TutorialTable extends AbstractTable implements Serializable {

    private static final long serialVersionUID = 3700897039043139176L;

    @Getter
    private String name;

    @Getter
    private List<TutorialColumn> sqlExecuteColumnList;

    public TutorialTable(@NonNull String name, @NonNull List<TutorialColumn> sqlExecuteColumnList) {
        this.name = name;
        this.sqlExecuteColumnList = sqlExecuteColumnList;
    }


    public TutorialTable(@NonNull String name, @NonNull TutorialColumn... sqlExecuteColumns) {
        this.name = name;
        this.sqlExecuteColumnList = Lists.newArrayList(sqlExecuteColumns);
    }

    /**
     * 获取数据行类型
     *
     * @param typeFactory
     * @return
     */
    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        List<String> names = new ArrayList<>();
        List<RelDataType> types = new ArrayList<>();
        for (TutorialColumn sqlExecuteColumn : sqlExecuteColumnList) {
            names.add(sqlExecuteColumn.getName());

            RelDataType sqlType;
            if (sqlExecuteColumn.getPrecision() != null) {
                sqlType = typeFactory.createSqlType(SqlTypeName.VARCHAR, sqlExecuteColumn.getPrecision());
            } else {
                sqlType = typeFactory.createSqlType(SqlTypeName.get(sqlExecuteColumn.getSqlTypeName().toUpperCase()));
            }

            types.add(sqlType);
        }
        return typeFactory.createStructType(Pair.zip(names, types));
    }

    /**
     * 获取统计信息
     *
     * @return
     */
    @Override
    public Statistic getStatistic() {
        return new TutorialStatistic(this);
    }
}
