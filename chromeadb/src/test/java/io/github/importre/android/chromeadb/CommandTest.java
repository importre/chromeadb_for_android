package io.github.importre.android.chromeadb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class CommandTest {

    @Test
    public void testMoveCommand() throws Exception {
        HashMap<String, Boolean> testCases = new HashMap<String, Boolean>();
        testCases.put("move 1", false);
        testCases.put("move 1,1", true);
        testCases.put("move  1,1", false);
        testCases.put("move 1,1,1", false);
        testCases.put("move 1,1,1,1", true);
        testCases.put("move 1,a,1,1", false);
        testCases.put("   move 1,1,1,1,1,1   ", true);
        testCases.put("", false);
        testCases.put(null, false);
        testCases.put("test", false);

        for (String key : testCases.keySet()) {
            boolean res = Command.getCoordinates(key) != null;
            assertThat(res).isEqualTo(testCases.get(key));
        }
    }
}
