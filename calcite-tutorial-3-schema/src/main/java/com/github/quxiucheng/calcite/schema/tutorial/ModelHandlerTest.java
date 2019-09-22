package com.github.quxiucheng.calcite.schema.tutorial;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.apache.calcite.model.JsonRoot;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author quxiucheng
 * @date 2019-04-28 17:43:00
 */
public class ModelHandlerTest {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper()
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
            .configure(JsonParser.Feature.ALLOW_COMMENTS, true);

    private static final ObjectMapper YAML_MAPPER = new YAMLMapper();

    public static void main(String[] args) throws SQLException, IOException {
        String uri = "calcite-tutorial-3-schema/src/main/resources/model.json";
        JsonRoot root = JSON_MAPPER.readValue(new File(uri), JsonRoot.class);
        System.out.println(root);

    }
}
