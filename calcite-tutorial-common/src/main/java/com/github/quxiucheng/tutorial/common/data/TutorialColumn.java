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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

/**
 * @author quxiucheng
 * @date 2019-01-12 09:57:00
 */
@Data
@AllArgsConstructor
public class TutorialColumn implements Serializable {

    private static final long serialVersionUID = -5914383625394884307L;

    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段类型名称
     */
    private String sqlTypeName;

    private boolean primary = false;

    /**
     * 精度
     */
    private Integer precision;

    /**
     * 小数
     */
    private Integer scale;

    public TutorialColumn(@NonNull String name, @NonNull String sqlTypeName) {
        this.name = name;
        this.sqlTypeName = sqlTypeName.toUpperCase();
    }

    public TutorialColumn(String name, String sqlTypeName, boolean primary) {
        this(name, sqlTypeName);
        this.primary = primary;
    }

    public TutorialColumn(String name, String sqlTypeName, int precision) {
        this(name, sqlTypeName);
        this.precision = precision;
    }
}
