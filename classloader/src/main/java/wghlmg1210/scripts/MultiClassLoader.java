package wghlmg1210.scripts;

import java.net.URL;
import java.net.URLClassLoader;

public class MultiClassLoader extends URLClassLoader {

    public MultiClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected void addURL(URL url) {
        super.addURL(url);
    }

}
