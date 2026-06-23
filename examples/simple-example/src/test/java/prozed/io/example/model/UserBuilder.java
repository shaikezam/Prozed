package prozed.io.example.model;

import prozed.io.test.utils.RandomUtils;

public class UserBuilder {
    private int id = RandomUtils.randomInt();
    private String name = RandomUtils.randomAlphbetString(10);

    public UserBuilder() {}

    public UserBuilder withId(int id) {
        this.id = id;

        return this;
    }

    public UserBuilder withName(String name) {
        this.name = name;

        return this;
    }

    public User build() {
        return new User(this.id, this.name);
    }
}
