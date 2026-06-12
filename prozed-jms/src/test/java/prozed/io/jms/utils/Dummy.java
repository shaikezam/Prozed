package prozed.io.jms.utils;

import prozed.io.test.utils.RandomUtils;

public record Dummy(int val1, String val2) {
    public static class DummyBuilder {

        private int val1 = RandomUtils.randomInt();
        private String val2 = RandomUtils.randomAlphbetString(10);

        public DummyBuilder() {

        }

        public Dummy build() {
            return new Dummy(val1, val2);
        }
    }
}
