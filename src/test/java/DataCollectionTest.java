import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by npakhomova on 3/8/16.
 */
public class DataCollectionTest {

    @Test
    public void buildUrlTest() {

        assertEquals("https://stars.macys.com/preview/02/98/01/04/final/2980104-", DataCollectionJobUtils.buildURL(2980104));

    }
}
