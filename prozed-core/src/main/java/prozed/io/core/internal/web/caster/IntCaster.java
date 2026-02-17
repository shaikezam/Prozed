package prozed.io.core.internal.web.caster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.web.HttpCode;
import prozed.io.core.internal.web.HttpException;

import java.util.Objects;

public class IntCaster implements Castable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(IntCaster.class);

    @Override
    public Integer from(Object value) {
        Objects.requireNonNull(value);
        if (value instanceof String number) {
            try {
                return Integer.parseInt(number);
            } catch (NumberFormatException ex) {
                String errorMessage = "Cannot convert %s to Integer".formatted(number);
                logger.error(errorMessage, ex);
                throw new HttpException(errorMessage, HttpCode.BAD_REQUEST, ex);
            }
        } else if (value instanceof Number number) {
            return number.intValue();
        }
        String errorMessage = "Cannot convert %s to Integer".formatted(value);
        logger.error(errorMessage);
        throw new HttpException(errorMessage, HttpCode.BAD_REQUEST);

    }

    @Override
    public Object to(Integer value) {
        return value;
    }
}
