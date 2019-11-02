# fmpp 具体代码实例
FMPP是以freemarker为模板的模板生成器

## 1.添加Maven依赖
```xml
<plugin>
    <configuration>
        <!--配置文件地址-->
        <cfgFile>src/main/codegen/config.fmpp</cfgFile>
        <!--文件输出目录-->
        <outputDirectory>target/generated-sources/fmpp/</outputDirectory>
        <!--文件模板存放目录-->
        <templateDirectory>src/main/codegen/templates</templateDirectory>
    </configuration>
    <groupId>com.googlecode.fmpp-maven-plugin</groupId>
    <artifactId>fmpp-maven-plugin</artifactId>
    <version>1.0</version>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>org.freemarker</groupId>
            <artifactId>freemarker</artifactId>
            <version>2.3.28</version>
        </dependency>
        <dependency>
            <groupId>net.sourceforge.fmpp</groupId>
            <artifactId>fmpp</artifactId>
            <version>0.9.16</version>
            <exclusions>
                <exclusion>
                <groupId>org.freemarker</groupId>
                <artifactId>freemarker</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>
</plugin>
```

## fmpp配置文件

fmpp配置文件 config.fmpp
```
# 用data标示为变量
# 一般变量替换为 ${one} or ${two.three} ,具体语法请参考freemarker语法
# include 指令插入另个freemarker模板
data: {
     one:1,
     two: {
        three: 3
     }
     implementationFiles: [
             "parserImpls.ftl"
      ]
}

#
freemarkerLinks: {
    includes: includes/
}

```

## freemarker模板1

模板1 Main.ftl
```
public class Main {
    public static void main(String[] args){
        System.out.println(${one} + ${two.three});
    }
    /**
     * 额外附加代码
     */
    <#list implementationFiles as file>
        <#include "/@includes/"+file />
    </#list>
}
```
## freemarker模板2

模板2 parserImpls.ftl
```
static {
    System.out.println(${one});
    System.out.println(${two.three});
}
```

## 执行maven插件

执行maven命令
```shell
mvn fmpp:generate
```
## 生成文件

生成如下文件
```java
public class Main {
    public static void main(String[] args){
        System.out.println(1 + 3);
    }
    /**
     * 额外附加代码
     */
static {
    System.out.println(1);
    System.out.println(3);
}}
```


