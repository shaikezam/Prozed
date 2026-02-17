package prozed.io.core.internal.web.caster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prozed.io.core.api.web.HttpCode;
import prozed.io.core.internal.web.HttpException;

import java.util.Objects;

public class DoubleCaster implements Castable<Double> {

    private static final Logger logger = LoggerFactory.getLogger(DoubleCaster.class);

    @Override
    public Double from(Object value) {
        Objects.requireNonNull(value);
        if (value instanceof String number) {
            try {
                return Double.parseDouble(number);
            } catch (NumberFormatException ex) {
                String errorMessage = "Cannot convert %s to Double".formatted(number);
                logger.error(errorMessage, ex);
                throw new HttpException(errorMessage, HttpCode.BAD_REQUEST, ex);
            }
        } else if (value instanceof Number number) {
            return number.doubleValue();
        }
        String errorMessage = "Cannot convert %s to Double".formatted(value);
        logger.error(errorMessage);
        throw new HttpException(errorMessage, HttpCode.BAD_REQUEST);

    }

    @Override
    public Object to(Double number) {
        return number;
    }
}
