package prozed.io.core.internal.web.caster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import prozed.io.core.internal.web.HttpException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class DoubleCasterTest {

    @InjectMocks
    private DoubleCaster doubleCaster;

    @Test
    void testFromWithNullInput() {
        // given + when + then
        assertThrows(
                NullPointerException.class,
                () -> doubleCaster.from(null)
        );
    }

    @Test
    void testFromWithTextInput() {
        // given + when + then
        HttpException exception = assertThrows(
                HttpException.class,
                () -> doubleCaster.from("blabla")
        );
        assertTrue(exception.getMessage().contains("Cannot convert blabla to Double"));
    }

    @Test
    void testFromWithObjectInput() {
        // given + when + then
        HttpException exception = assertThrows(
                HttpException.class,
                () -> doubleCaster.from(new Object())
        );
        assertTrue(exception.getMessage().contains("Cannot convert java.lang.Object"));
    }

}
