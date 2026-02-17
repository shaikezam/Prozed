package prozed.io.core.internal.web.caster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.web.HttpCode;
import prozed.io.core.internal.web.HttpException;

import java.util.Objects;

public class FloatCaster implements Castable<Float> {

    private static final Logger logger = LoggerFactory.getLogger(FloatCaster.class);

    @Override
    public Float from(Object value) {
        Objects.requireNonNull(value);
        if (value instanceof String number) {
            try {
                return Float.parseFloat(number);
            } catch (NumberFormatException ex) {
                String errorMessage = "Cannot convert %s to Float".formatted(number);
                logger.error(errorMessage, ex);
                throw new HttpException(errorMessage, HttpCode.BAD_REQUEST, ex);
            }
        } else if (value instanceof Number number) {
            return number.floatValue();
        }
        String errorMessage = "Cannot convert %s to Float".formatted(value);
        logger.error(errorMessage);
        throw new HttpException(errorMessage, HttpCode.BAD_REQUEST);

    }

    @Override
    public Object to(Float number) {
        return number;
    }
}
