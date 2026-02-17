package prozed.io.core.internal.web.caster;

public interface Castable<T> {

    T from(Object value);

    Object to(T value);

}
