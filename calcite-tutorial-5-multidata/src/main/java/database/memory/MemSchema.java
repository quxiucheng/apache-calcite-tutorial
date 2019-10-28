package database.memory;

import com.google.common.collect.ImmutableMap;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;

import java.util.Map;

public class MemSchema extends AbstractSchema {
   private Map<String, Object> map;
   private Map<String, Table> tableMap;

   public MemSchema(Map<String, Object> map) {
      this.map = map;
   }

   @Override
   protected Map<String, Table> getTableMap() {
      if (tableMap == null) {
         final ImmutableMap.Builder<String, Table> builder = ImmutableMap.builder();
         map.forEach((key, value) -> {
            builder.put(key, new MemTable(value));
         });
         tableMap = builder.build();
      }

      return tableMap;
   }
}