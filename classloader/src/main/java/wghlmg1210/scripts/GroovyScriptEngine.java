package wghlmg1210.scripts;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

import javax.script.*;
import java.io.File;
import java.util.Date;

public class GroovyScriptEngine {

    public static void main(String[] args) throws ScriptException, NoSuchMethodException {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("groovy");
        Invocable inv = (Invocable) engine;

        String fact = "def factorial(n, msg) { println(msg);return n == 1 ? 1 : n * factorial(n - 1, msg);}";
        engine.eval(fact);
        Object[] params = {5, "ssss"};
        Object result = inv.invokeFunction("factorial", params);
        System.out.println(result);

        Bindings binding = engine.createBindings();
        binding.put("date", new Date());
        engine.eval("def getTime(){return date.getTime();}", binding);
        Object time = inv.invokeFunction("getTime", (Object) null);
        System.out.println((Long) time);

        engine.eval("def sayHello(name,age){return 'Hello,I am ' + name + ',age ' + age;}");
        String message = (String) inv.invokeFunction("sayHello", "zhangsan", 12);
        System.out.println(message);


        try {
            GroovyClassLoader loader = new GroovyClassLoader();
            Class fileCreator = loader.parseClass(new File("GroovySimpleFileCreator.groovy"));
            GroovyObject object = (GroovyObject) fileCreator.newInstance();
            object.invokeMethod("createFile", "C:\\temp\\emptyFile.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
