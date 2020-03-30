package com.github.quxiucheng.tutorial.common.function;

import org.apache.calcite.rel.type.RelProtoDataType;
import org.apache.calcite.sql.type.TableFunctionReturnTypeInference;

import java.util.List;

/**
 * @author quxiucheng
 * @date 2019-02-02 15:14:00
 */
public class DedupTableFunctionReturnTypeInference extends TableFunctionReturnTypeInference {
    public DedupTableFunctionReturnTypeInference(RelProtoDataType unexpandedOutputType, List<String> paramNames, boolean isPassthrough) {
        super(unexpandedOutputType, paramNames, isPassthrough);
    }
}
