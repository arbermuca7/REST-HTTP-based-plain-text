package server;
/**
 * Github: https://github.com/arbermuca7/REST-HTTP-based-plain-text
 * */

import request.RequestMethods;
import response.ResponseStatusCode;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class HTTPServer implements Runnable {
    Socket cli;
    List<String> messages;
    public HTTPServer(Socket s, List<String> msg){

        cli = s;
        messages = msg;
    }

    public static void main(String[] args){
        List<String> messages = new ArrayList<>();
        try{
            //open a tcp socket
            ServerSocket serverSocket = new ServerSocket(8080);

            while (true){
                //accept the client connection
                HTTPServer httpServer = new HTTPServer(serverSocket.accept(),messages);
                Thread thread = new Thread(httpServer);
                thread.start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void run() {
        //read the request
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream contentSend = null;
        String inputStream;
        try{
            //read the characters from the client
            in = new BufferedReader(new InputStreamReader(cli.getInputStream()));
            //we send or get the character output stream to the client -> for header
            out = new PrintWriter(cli.getOutputStream());
            //we send or get the binary output stream to the client -> for body
            contentSend = new BufferedOutputStream(cli.getOutputStream());
            //get the first line of the request
            inputStream = in.readLine();

            if (inputStream != null) {

                String headerPart = readFirstLineHeader(inputStream);

                String[] headerPartSplit = headerPart.split(",");
                //get the method
                String method = headerPartSplit[0];
                //get the path
                String path = headerPartSplit[1];
                //get the version
                String version = headerPartSplit[2];
                //save the content-type
                String contentType = null;
                if (!RequestMethods.hasMethod(method)){
                    //send a response 501 not implemented, if we haven't implement the asked Method
                    sendRespond(out,contentSend, ResponseStatusCode.getDesc(501),version,contentType,"Wrong method chosen".getBytes());
                }
                else if (method.equals(RequestMethods.GET.getVal())) {

                    String msgID = pathSpliter(path);

                    if (path.equals("/messages")) {
                        //take all messages in the List in a single string
                        String allMessages = listAllElements(messages);
                        //send the respond to the client
                        sendRespond(out, contentSend, ResponseStatusCode.getDesc(200), version, contentType, allMessages.getBytes());

                    }
                    else if(path.equals("/messages/"+msgID)){
                            //return the given ID from String to int
                            int msgId = Integer.parseInt(msgID);
                            //send the respond to the client
                            String specificMsg = messages.indexOf(messages.get(msgId-1))+1+": "+messages.get(msgId-1);
                            sendRespond(out,contentSend, ResponseStatusCode.getDesc(200),version,contentType,specificMsg.getBytes());
                    }
                    else{
                        sendRespond(out,contentSend, ResponseStatusCode.getDesc(404),version,contentType,"Wrong URI".getBytes());
                    }
                }
                else if (method.equals(RequestMethods.POST.getVal())) {

                    String bodyReq = takeTheBody(in);

                    String[] bodySplit = bodyReq.split(",");

                    String bodyContent = bodySplit[0];
                    contentType = bodySplit[1];

                    messages.add(bodyContent);

                    //send the response on the client
                    String messageContent =messages.indexOf(messages.get(messages.size()-1))+1+": "+messages.get(messages.size()-1);
                    sendRespond(out,contentSend, ResponseStatusCode.getDesc(201),version,contentType,messageContent.getBytes());
                }
                //update a message in the list
                else if(method.equals(RequestMethods.PUT.getVal())) {

                    String bodyReq = takeTheBody(in);

                    String[] bodySplit = bodyReq.split(",");

                    String bodyContent = bodySplit[0];
                    contentType = bodySplit[1];

                    String msgID = pathSpliter(path);

                    if (path.equals("/messages/" + msgID)) {
                        //return the given ID from String to int
                        int msgId = Integer.parseInt(msgID);
                        //if the input with that values doesnt exist then add a new one.
                        if (bodyContent == null) {
                            sendRespond(out, contentSend, ResponseStatusCode.getDesc(204), version, contentType, "no content".getBytes());
                        } else if (messages.size() <= msgId-1) {
                            messages.add(bodyContent);
                            //send the response on the client
                            String messageContent = messages.indexOf(messages.get(messages.size() - 1)) + 1 + ": " + messages.get(messages.size() - 1);
                            sendRespond(out, contentSend, ResponseStatusCode.getDesc(201), version, contentType, messageContent.getBytes());
                        } else {
                            messages.set(msgId - 1, bodyContent);
                            String specificMsgUpdate = messages.indexOf(messages.get(msgId - 1)) + 1 + ": " + messages.get(msgId - 1);
                            sendRespond(out, contentSend, ResponseStatusCode.getDesc(200), version, contentType, specificMsgUpdate.getBytes());
                        }
                    } else {
                        sendRespond(out, contentSend, ResponseStatusCode.getDesc(404), version, contentType, "You can only update a specific message".getBytes());
                    }
                }
                //delete a certain message from the list
                else if(method.equals(RequestMethods.DELETE.getVal())){

                    String msgID = pathSpliter(path);

                    if (path.equals("/messages/"+msgID)){
                        int msgId=Integer.parseInt(msgID);
                        if (msgId-1 >= messages.size()){
                            sendRespond(out, contentSend, ResponseStatusCode.getDesc(404), version, contentType, "ID not founded".getBytes());
                        }else{
                            messages.remove(msgId-1);
                            sendRespond(out, contentSend, ResponseStatusCode.getDesc(200), version, contentType, "OK".getBytes());
                        }

                    }else{
                        sendRespond(out, contentSend, ResponseStatusCode.getDesc(405), version, contentType, "You aren't allowed to delete the entire List".getBytes());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                in.close();
                out.close();
                contentSend.close();
                cli.close();
            } catch (IOException e) {
                System.err.println("Error closing stream : " + e.getMessage());
            }
        }
    }

    public static String readFirstLineHeader(String input){
        //parse the request
        //StringTokenizer st = new StringTokenizer(inputStream);
        StringTokenizer st = new StringTokenizer(input);
        //get the method
        String method = st.nextToken().toUpperCase();
        //get the path
        String path = st.nextToken().toLowerCase();
        //get the version
        String version = st.nextToken().toUpperCase();

        String returnLine = method + "," + path + "," +version;
        return returnLine;
    }
    public static String pathSpliter(String path){
        String msgID = null;
        String[] pathSplited = path.split("/");
        if (pathSplited.length == 3){
            msgID  = pathSplited[2];
            return msgID;
        }
        return null;
    }
    public static String listAllElements(List<String> messages){

        int i = 0;
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append("{ ");
        while (i < messages.size() - 1) {
            msgBuilder.append(messages.indexOf(messages.get(i))+1+":"+messages.get(i) + " , ");
            i++;
        }
        msgBuilder.append(messages.indexOf(messages.get(i))+1+":"+messages.get(i)+" }");
        String allMessages = msgBuilder.toString();

        return allMessages;
    }
    private static void sendRespond(PrintWriter out,BufferedOutputStream contentSend, String status, String version, String contentTyp, byte[] content) throws IOException {
        //send the response to the client's output stream
        out.println(version +" "+ status);
        out.println("Content-Type: "+ contentTyp);
        out.println(); // blank line between headers and content
        out.flush(); // flush character output stream buffer
        contentSend.write(content);
        contentSend.flush();
    }
    public static String takeTheBody(BufferedReader in) throws IOException {
        //take the other part of the header + the body
        StringBuilder requestBody = new StringBuilder();
        //divide every field of the header and the last part(the body) with a newline
        while (in.ready()) {
            char line = (char) in.read();
            requestBody.append(line);
        }
        String body = requestBody.toString();

        String[] requestLines = body.split("\r\n");
        //split the line of the content type where a space is
        String[] requestLine = requestLines[0].split(" ");
        //take the content type
        String contentType = requestLine[1];
        //list for the header
        List<String> _headers = new ArrayList<String>();
        //save the header in the List
        for (int i = 1; i < requestLines.length; i++) {
            String header = requestLines[i];
            _headers.add(header);
        }
        String bodyContent = null;
        //save the body into a string
        if (_headers != null && !_headers.isEmpty()) {
            bodyContent = _headers.get(_headers.size() - 1);
        }

        String bodyPlusContentTyp= bodyContent+","+contentType;

        return bodyPlusContentTyp;
    }
}