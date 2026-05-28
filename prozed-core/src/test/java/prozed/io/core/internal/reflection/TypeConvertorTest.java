package prozed.io.core.internal.reflection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import prozed.io.test.utils.RandomUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TypeConvertorTest {

    @Test
    void testConvertInteger() {
        // given
        Integer input = RandomUtils.randomInt();
        String parameter = Integer.toString(input);

        // when
        Integer actual = TypeConvertor.convert(parameter, Integer.class);

        // then
        assertEquals(input, actual);
    }

    @Test
    void testConvertInt() {
        // given
        int input = RandomUtils.randomInt();
        String parameter = Integer.toString(input);

        // when
        Integer actual = TypeConvertor.convert(parameter, int.class);

        // then
        assertEquals(input, actual);
    }

    @Test
    void testConvertDouble() {
        // given
        Double input = RandomUtils.randomDouble();
        String parameter = Double.toString(input);

        // when
        Double actual = TypeConvertor.convert(parameter, Double.class);

        // then
        assertEquals(input, actual);
    }

    @Test
    void testConvertDoublePrimitive() {
        // given
        double input = RandomUtils.randomDouble();
        String parameter = Double.toString(input);

        // when
        double actual = TypeConvertor.convert(parameter, double.class);

        // then
        assertEquals(input, actual);
    }

    @Test
    void testConvertFloat() {
        // given
        Float input = RandomUtils.randomFloat();
        String parameter = Double.toString(input);

        // when
        Float actual = TypeConvertor.convert(parameter, Float.class);

        // then
        assertEquals(input, actual);
    }

    @Test
    void testConvertFloatPrimitive() {
        // given
        float input = RandomUtils.randomFloat();
        String parameter = Float.toString(input);

        // when
        float actual = TypeConvertor.convert(parameter, float.class);

        // then
        assertEquals(input, actual);
    }

    @Test
    void testConvertLong() {
        // given
        Long input = RandomUtils.randomLong();
        String parameter = Long.toString(input);

        // when
        Long actual = TypeConvertor.convert(parameter, Long.class);

        // then
        assertEquals(input, actual);
    }

    @Test
    void testConvertLongPrimitive() {
        // given
        long input = RandomUtils.randomLong();
        String parameter = Long.toString(input);

        // when
        long actual = TypeConvertor.convert(parameter, long.class);

        // then
        assertEquals(input, actual);
    }

    @Test
    void testConvertBoolean() {
        // given
        Boolean input = RandomUtils.randomBoolean();
        String parameter = Boolean.toString(input);

        // when
        Boolean actual = TypeConvertor.convert(parameter, Boolean.class);

        // then
        assertEquals(input, actual);
    }

    @Test
    void testConvertBooleanPrimitive() {
        // given
        boolean input = RandomUtils.randomBoolean();
        String parameter = Boolean.toString(input);

        // when
        boolean actual = TypeConvertor.convert(parameter, boolean.class);

        // then
        assertEquals(input, actual);
    }

    @Test
    void testConvertCharacter() {
        // given
        Character input = RandomUtils.randomCharacter();
        String parameter = Character.toString(input);

        // when
        Character actual = TypeConvertor.convert(parameter, Character.class);

        // then
        assertEquals(input, actual);
    }

    @Test
    void testConvertCharacterPrimitive() {
        // given
        char input = RandomUtils.randomCharacter();
        String parameter = Character.toString(input);

        // when
        char actual = TypeConvertor.convert(parameter, char.class);

        // then
        assertEquals(input, actual);
    }

    @Test
    void testConvertString() {
        // given
        String input = RandomUtils.randomAlphbetString(10);

        // when
        String actual = TypeConvertor.convert(input, String.class);

        // then
        assertEquals(input, actual);
    }
}