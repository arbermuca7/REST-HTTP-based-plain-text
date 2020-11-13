import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.HTTPServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTTPServerTests {

    @Test
    @DisplayName("Test if the method take the first line of header")
    void readFirstLineHeaderTest(){
        //Socket s = new Socket();
        //List<String>messages = new ArrayList<>();
        //HTTPServer httpSrv = new HTTPServer(s,messages);

        String request = " GET /messages HTTP/1.1 \r\n Content-Type: text/plain \r\n Accept: */* \r\n\r\n hallo";

        String methodExp = "GET";
        String pathExp = "/messages";
        String versionExp = "HTTP/1.1";

        String readLine = HTTPServer.readFirstLineHeader(request);

        String[] headerPartSplit = readLine.split(",");
        //get the method
        String methodAct = headerPartSplit[0];
        //get the path
        String pathAct = headerPartSplit[1];
        //get the version
        String versionAct = headerPartSplit[2];

        assertEquals(methodExp,methodAct,"Same method!!!");
        assertEquals(pathExp,pathAct, "same path!!!");
        assertEquals(versionExp,versionAct, "Same version!!!");

    }

    @Test
    @DisplayName("Test if the path is spliten correctly to take the id ")
    void splitPathTest(){

        String path = "/messages/1";

        String ID_expected = "1";
        String ID_actual = HTTPServer.pathSpliter(path);

        assertEquals(ID_expected,ID_actual, "the ID correctly split!!");

    }

    @Test
    @DisplayName("Test if all the messages are returned at once ")
    void ListOfAllElemTest(){
        List<String> list = new ArrayList<>();
        list.add("hey");
        list.add("hello");
        list.add("bye");

        String listExpected = "{ 1:hey , 2:hello , 3:bye }";

        String listActual = HTTPServer.listAllElements(list);

        assertEquals(listExpected,listActual, "all messages are correctly returned");

    }

    @Test
    @DisplayName("Test if the method reads the body correct")
    void takeBodyTest() throws IOException {

        char[] req = {'C','o','n','t','e','n','t','-','T','y','p','e',':',' ', 't','e','x','t','/','p','l','a','i','n',' ',' ','\r','\n','\r','\n','h','e','l','l','o'};
        BufferedReader reader = new BufferedReader(new CharArrayReader(req));
        String contentType_expected = "text/plain";
        String body_expected = "hello";

        String bodyReq = HTTPServer.takeTheBody(reader);
        String[] bodySplit = bodyReq.split(",");
        String body_actual = bodySplit[0];
        String contentType_actual = bodySplit[1];

        assertEquals(body_expected,body_actual);
        assertEquals(contentType_expected,contentType_actual);

    }
}
