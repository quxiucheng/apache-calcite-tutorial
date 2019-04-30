package com.github.quxiucheng.calcite.schema.tutorial;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

/**
 * 列信息
 * @author quxiucheng
 * @date 2019-04-26 11:18:00
 */
@Data
public class TutorialColumn implements Serializable {
    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段类型名称
     */
    private String type;

}
