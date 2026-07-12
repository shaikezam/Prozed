package prozed.io.core.internal.properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import prozed.io.test.utils.RandomUtils;

import java.util.Map;

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

    @Test
    void testResolveEnvironmentVariable() {
        // given: pick an env var that is present on the running machine
        Map.Entry<String, String> env = System.getenv().entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No environment variable available for test"));

        // when
        String resolved = ProzedPropertiesWrapper.resolve("prefix-${" + env.getKey() + "}-suffix");

        // then
        assertEquals("prefix-" + env.getValue() + "-suffix", resolved);
    }

    @Test
    void testResolveUsesInlineDefaultWhenUnset() {
        // given
        String key = "PROZED_MISSING_" + RandomUtils.randomAlphbetString(8);

        // when
        String resolved = ProzedPropertiesWrapper.resolve("${" + key + ":root}");

        // then
        assertEquals("root", resolved);
    }

    @Test
    void testResolveEmptyWhenUnsetAndNoDefault() {
        // given
        String key = "PROZED_MISSING_" + RandomUtils.randomAlphbetString(8);

        // when
        String resolved = ProzedPropertiesWrapper.resolve("prefix-${" + key + "}-suffix");

        // then
        assertEquals("prefix--suffix", resolved);
    }

    @Test
    void testResolveLeavesPlainValueUntouched() {
        // when
        String resolved = ProzedPropertiesWrapper.resolve("jdbc:mariadb://host:3306/db");

        // then
        assertEquals("jdbc:mariadb://host:3306/db", resolved);
    }

}