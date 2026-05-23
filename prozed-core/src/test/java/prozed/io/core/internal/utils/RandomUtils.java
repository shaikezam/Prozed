package prozed.io.core.internal.utils;

import java.util.Random;
import java.util.stream.IntStream;

public class RandomUtils {
    private final static Random RANDOM = new Random();

    public static int randomInt() {
        return RANDOM.nextInt();
    }

    public static long randomLong() {
        return RANDOM.nextLong();
    }

    public static double randomDouble() {
        return RANDOM.nextDouble();
    }

    public static float randomFloat() {
        return RANDOM.nextFloat();
    }

    public static boolean randomBoolean() {
        return RANDOM.nextBoolean();
    }

    public static <T> T randomEnum(Class<T> enumClass) {
        return enumClass.getEnumConstants()[RANDOM.nextInt(enumClass.getEnumConstants().length)];
    }

    public static char randomCharacter() {
        return randomAlphbetString(1).charAt(0);
    }

    public static String randomAlphbetString(int length) {
        return IntStream
                .range(0, length)
                .map(current -> {
                    int type = RANDOM.nextInt(0, 3);

                    return switch (type) {
                        case 0 -> RANDOM.nextInt(48, 58);   // 0-9
                        case 1 -> RANDOM.nextInt(65, 91);   // A-Z
                        case 2 -> RANDOM.nextInt(97, 123);  // a-z
                        default -> throw new IllegalStateException();
                    };
                })
                .collect(StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
    }
}
