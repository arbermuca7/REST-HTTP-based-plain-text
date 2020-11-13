import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.RequestMethods;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestMethodsTest {

    @Test
    @DisplayName("Test if we recieve the correct Method")
    void getValueTest(){

        String expected = "GET";
        String actual = RequestMethods.GET.getVal();

        assertEquals(expected,actual,"it is defined correctly");

    }

    @Test
    @DisplayName("Looks if the method we are looking for is implemented")
    void hasMethodTest(){
        //ask if it contains method DELETE
        boolean expected = true;
        boolean actual = RequestMethods.hasMethod("DELETE");
        boolean expected_next = false;
        boolean actual_next = RequestMethods.hasMethod("HEAD");

        assertEquals(expected, actual, "First Test with a containing method works");
        assertEquals(expected_next,actual_next,"Second Test with a not implemented Method works");
    }

}
