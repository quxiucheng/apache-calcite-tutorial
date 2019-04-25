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