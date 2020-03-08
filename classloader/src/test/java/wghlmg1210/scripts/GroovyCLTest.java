package wghlmg1210.scripts;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class GroovyCLTest {

    private String externalJar;
    private boolean testParent;

    @Parameterized.Parameters
    public static Collection prepareData() {
        Object[][] object = {
                {"/Users/han.qishu/.m2/repository/redis/clients/jedis/3.1.0/jedis-3.1.0.jar", true},
                {"/Users/han.qishu/.m2/repository/redis/clients/jedis/3.1.0/jedis-3.1.0", true},
                {"/Users/han.qishu/.m2/repository/redis/clients/jedis/3.1.0/jedis-3.1.0.jar", false},
                {"/Users/han.qishu/.m2/repository/redis/clients/jedis/3.1.0/jedis-3.1.0", false}
        };
        return Arrays.asList(object);
    }

    public GroovyCLTest(String externalJar, boolean testParent) {
        this.externalJar = externalJar;
        this.testParent = testParent;
    }

    @Test
    public void testGroovyCL() throws MalformedURLException {
        System.out.println(this.externalJar + " " + this.testParent);
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        ClassPathTools.showUrls(current);
        MultiClassLoader parent = new MultiClassLoader(new URL[0], current);

        GroovyCLBuilder builder = GroovyCLBuilder.builder().setParentCL(parent);
        URL externalURL = new File(externalJar).toURI().toURL();
        if (testParent) {
            parent.addURL(externalURL);
        } else {
            builder.addUrl(externalURL);
        }

        GroovyCL classLoader = builder.build();
        newInstance(classLoader);
    }

    private void newInstance(GroovyCL classLoader) {
        Object obj = null;
        try {
            obj = classLoader.compileFromFile(new File("scripts/ExternalDep.groovy"));
        } catch (IOException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        IFoo foo = (IFoo) obj;
        assert foo != null;
        System.out.println(foo.run(""));
    }

}
