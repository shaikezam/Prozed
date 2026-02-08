package prozed.io.core.internal.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NodeRouterTest {

    @InjectMocks
    private NodeRouter router;

    @Test
    void test() {
        String path = "/api/v1/users";
        for(String seg: "/api/v1/users".split("/")) {

            System.out.println("/" + seg);
        }

        assertEquals(true, true);
    }
}