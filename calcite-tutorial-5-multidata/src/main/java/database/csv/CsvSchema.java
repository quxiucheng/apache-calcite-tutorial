package database.csv;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.apache.calcite.util.Source;
import org.apache.calcite.util.Sources;

import java.net.URL;
import java.util.Map;

public class CsvSchema extends AbstractSchema {
   private Map<String, Table> tableMap;
   private String dataFile;

   public CsvSchema(String dataFile) {
      this.dataFile = dataFile;
   }

   @Override
   protected Map<String, Table> getTableMap() {
      URL url = Resources.getResource(dataFile);
      Source source = Sources.of(url);
      if (tableMap == null) {
         final ImmutableMap.Builder<String, Table> builder = ImmutableMap.builder();
         builder.put(this.dataFile.split("\\.")[0],new CsvTable(source));
         // 一个数据库有多个表名，这里初始化，大小写要注意了,TEST01是表名。
         tableMap = builder.build();
      }
      return tableMap;
   }
}
