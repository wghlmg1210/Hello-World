package wghlmg1210;

import org.apache.commons.text.StringSubstitutor;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void stringFormat() {


        Map<String, String> formatMap = new HashMap<>();
        formatMap.put("remote_addr", "111");
        formatMap.put("remote_user", "222");

        String formatter = "${remote_addr}\t${remote_user}";

        for (Map.Entry<String, String> entry : formatMap.entrySet()) {
            formatter = formatter.replace("%{" + entry.getKey() + "}", entry.getValue());
        }

        String message = StringSubstitutor.replace(formatter, formatMap);
        System.out.println(message);
    }

    @Test
    public void testGrok() throws FileNotFoundException {


    }

}
