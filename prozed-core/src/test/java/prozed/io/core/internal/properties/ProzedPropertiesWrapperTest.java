package prozed.io.core.internal.properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import prozed.io.test.utils.RandomUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ProzedPropertiesWrapperTest {

    @Test
    void testGetDefaultProperties() {
        // given + then
        int servicePort = ProzedPropertiesWrapper.getServicePort();
        String scanPackage = ProzedPropertiesWrapper.getScanPackage();
        String dummyValue = RandomUtils.randomAlphbetString(10);
        String dummyPropertyValue = ProzedPropertiesWrapper.getProperty("DUMMY", dummyValue);

        // then
        assertEquals(8080, servicePort);
        assertEquals(dummyPropertyValue, dummyValue);
        assertNull(scanPackage);
        assertEquals(3, Constants.class.getDeclaredFields().length, "Verify to test all Constant class fields");
    }

}