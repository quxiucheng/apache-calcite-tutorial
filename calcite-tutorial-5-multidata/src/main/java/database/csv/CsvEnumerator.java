package database.csv;

import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.util.Source;

import java.io.BufferedReader;
import java.io.IOException;

public class CsvEnumerator<E> implements Enumerator<E> {

   private E current;

   private BufferedReader br;

   public CsvEnumerator(Source source) {
      try {
         this.br = new BufferedReader(source.reader());
         this.br.readLine();
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   @Override
   public E current() {
      return current;
   }

   @Override
   public boolean moveNext() {
      try {
         String line = br.readLine();
         if (line == null) {
            return false;
         }

         current = (E) line.split(",");    // 如果是多列，这里要多个值
      } catch (IOException e) {
         e.printStackTrace();
         return false;
      }
      return true;
   }

   /**
    * 出现异常走这里
    */
   @Override
   public void reset() {
      System.out.println("报错了兄弟，不支持此操作");
   }

   /**
    * InputStream流在这里关闭
    */
   @Override
   public void close() {

   }
}