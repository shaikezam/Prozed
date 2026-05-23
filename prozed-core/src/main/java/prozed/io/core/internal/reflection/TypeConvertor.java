package prozed.io.core.internal.reflection;

public class TypeConvertor {

    @SuppressWarnings("unchecked")
    public static <T> T convert(String value, Class<T> type) {
        if (Integer.class.equals(type) || int.class.equals(type)) {
            return (T) Integer.valueOf(value);
        } else if (Double.class.equals(type) || double.class.equals(type)) {
            return (T) Double.valueOf(value);
        } else if (Long.class.equals(type) || long.class.equals(type)) {
            return (T) Long.valueOf(value);
        } else if (Float.class.equals(type) || float.class.equals(type)) {
            return (T) Float.valueOf(value);
        } else if (Boolean.class.equals(type) || boolean.class.equals(type)) {
            return (T) Boolean.valueOf(value);
        } else if (Character.class.equals(type) || char.class.equals(type)) {
            return (T) Character.valueOf(value.charAt(0));
        } else if (String.class.equals(type)) {
            return (T) value;
        }
        throw new IllegalArgumentException("Type " + type.getName() + " is not supported");
    }
}
