package database.memory;

import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaFactory;
import org.apache.calcite.schema.SchemaPlus;

import java.util.Map;


public class MemSchemaFactory implements SchemaFactory {
   @Override
   public Schema create(SchemaPlus schemaPlus, String dataName, Map<String, Object> map) {
      return new MemSchema(map);
   }
}