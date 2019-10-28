package database.memory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.google.common.collect.Lists;
import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MemTable extends AbstractTable implements ScannableTable {

   private List<Map<String, Object>> list = Lists.newLinkedList();

   public MemTable(Object list) {
      if (list instanceof List) {
         ((List)list).forEach(o -> {
            this.list.add(
                    JSON.parseObject(JSON.toJSONString(o),
                            new TypeReference<Map<String, Object>>() {},
                            Feature.OrderedField));
         });
      }
   }

   @Override
   public Enumerable<Object[]> scan(DataContext dataContext) {
      return new AbstractEnumerable<Object[]>() {
         @Override
         public Enumerator<Object[]> enumerator() {
            return new MemEnumerator<Object[]>(list);
         }
      };
   }

   @Override
   public RelDataType getRowType(RelDataTypeFactory relDataTypeFactory) {
      JavaTypeFactory typeFactory = (JavaTypeFactory)relDataTypeFactory;

      List<String> names = Lists.newLinkedList();
      List<RelDataType> types = Lists.newLinkedList();

      if (list.size() != 0) {
         list.get(0).forEach((key, value) -> {
            names.add(key);
            types.add(typeFactory.createSqlType(SqlTypeName.get("VARCHAR")));
         });
      }
      return typeFactory.createStructType(Pair.zip(names, types));
   }
}