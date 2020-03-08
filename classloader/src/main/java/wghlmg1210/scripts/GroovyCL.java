package wghlmg1210.scripts;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class GroovyCL {

    private GroovyClassLoader loader;

    public GroovyCL(ClassLoader parent, URL[] urls) {
        this.loader = new GroovyClassLoader(parent);

        for (URL url : urls) {
            this.loader.addURL(url);
        }
    }

    public Object compileFromFile(File file) throws IOException, IllegalAccessException, InstantiationException {
        Class clazz = loader.parseClass(file);
        return clazz.newInstance();
    }

    public Object compileFromContent(String content) throws IllegalAccessException, InstantiationException {
        Class clazz = loader.parseClass(content);
        return clazz.newInstance();
    }

}
