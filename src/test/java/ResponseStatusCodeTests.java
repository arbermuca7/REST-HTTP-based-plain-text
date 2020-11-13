import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import response.ResponseStatusCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class ResponseStatusCodeTests {
    @Test
    @DisplayName("Test the Method to get the description of a status code")
    void getDescTest(){
        int number = 201;
        String desc = "Created";
        String expected = number + " " + desc;
        String actual = ResponseStatusCode.getDesc(number);

        assertEquals(expected,actual);

    }

}
