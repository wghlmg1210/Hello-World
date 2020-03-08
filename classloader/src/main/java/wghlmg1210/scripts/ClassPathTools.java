package wghlmg1210.scripts;

import java.net.URL;
import java.net.URLClassLoader;

public class ClassPathTools {

    public static void showUrls(ClassLoader loader) {
        URLClassLoader cl = loader instanceof URLClassLoader ? ((URLClassLoader) loader) : null;

        if (cl == null) return;

        for (URL url : cl.getURLs()) {
            String path = url.getFile();
            if (!path.contains("jdk1.8.0_"))
                System.out.println(path);
        }
    }

}
