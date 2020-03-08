package wghlmg1210.scripts;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GroovyCLBuilder {

    private ClassLoader parent = null;

    private List<URL> urls = new ArrayList<>();

    public static GroovyCLBuilder builder() {
        return new GroovyCLBuilder();
    }

    public GroovyCLBuilder setParentCL(ClassLoader parent) {
        this.parent = parent;
        return this;
    }

    public GroovyCLBuilder addUrl(URL url) {
        this.urls.add(url);
        return this;
    }

    public GroovyCL build() {
        return new GroovyCL(parent, urls.toArray(new URL[urls.size()]));
    }

}
