package server;
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
    HTTPServer(Socket s, List<String> msg){

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
        //List of all Messages

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
                //parse the request
                StringTokenizer st = new StringTokenizer(inputStream);
                //get the method
                String method = st.nextToken().toUpperCase();
                //get the path
                String path = st.nextToken().toLowerCase();
                //get the version
                String version = st.nextToken().toUpperCase();
                String body;
                String contentType = null;

                if (!method.equals(RequestMethods.GET.getVal()) && !method.equals(RequestMethods.POST.getVal()) && !method.equals(RequestMethods.PUT.getVal()) && !method.equals(RequestMethods.DELETE.getVal())){
                    //send a response 501 not implemented, if we haven't implement the asked Method
                    sendRespond(out,contentSend, ResponseStatusCode.getDesc(501),version,contentType,"Wrong Method chosen".getBytes());
                }
                else if (method.equals(RequestMethods.GET.getVal())) {
                    String msgID = null;
                    String[] pathSplited = path.split("/");
                    //String datei = pathSplited[1];
                    if (pathSplited.length == 3){
                        msgID  = pathSplited[2];
                    }
                    if (path.equals("/messages")) {
                        int i = 0;
                        StringBuilder msgBuilder = new StringBuilder();
                        while (i < messages.size() - 1) {
                            msgBuilder.append(messages.indexOf(messages.get(i))+1+":"+messages.get(i) + "--|--");
                            i++;
                        }
                        msgBuilder.append(messages.indexOf(messages.get(i))+1+":"+messages.get(i));
                        String allMessages = msgBuilder.toString();
                        //send the respond to the client
                        sendRespond(out, contentSend, ResponseStatusCode.getDesc(200), version, contentType, allMessages.getBytes());

                    }
                    else if(path.equals("/messages/"+msgID)){
                            //return the given ID from String to int
                            int msgId=Integer.parseInt(msgID);
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
                    System.out.println("in post: "+messages);
                    //send the response on the client
                    String messageContent =messages.indexOf(messages.get(messages.size()-1))+1+": "+messages.get(messages.size()-1);
                    sendRespond(out,contentSend, ResponseStatusCode.getDesc(201),version,contentType,messageContent.getBytes());
                }
                //update a message in the list
                else if(method.equals(RequestMethods.PUT.getVal())){

                    String bodyReq = takeTheBody(in);
                    String[] bodySplit = bodyReq.split(",");
                    String bodyContent = bodySplit[0];
                    contentType = bodySplit[1];

                    String msgID=null;
                    String[] pathSplited = path.split("/");
                    if (pathSplited.length == 3){
                        msgID  = pathSplited[2];
                        System.out.println("id:"+msgID);
                    }
                    if (path.equals("/messages/"+msgID)){
                        //return the given ID from String to int
                        int msgId=Integer.parseInt(msgID);
                        //if the input with that values doesnt exist then add a new one.
                        if(bodyContent == null){
                            sendRespond(out,contentSend, ResponseStatusCode.getDesc(204),version,contentType,"no content".getBytes());
                        }
                        else if(messages.size()<=msgId){
                            messages.add(bodyContent);
                            System.out.println("list inpost if not exist the id:"+messages);
                            //send the response on the client
                            String messageContent =messages.indexOf(messages.get(messages.size()-1))+1+": "+messages.get(messages.size()-1);
                            //System.out.println("MSG: "+messageContent);
                            sendRespond(out,contentSend, ResponseStatusCode.getDesc(201),version,contentType,messageContent.getBytes());
                        }
                        else{
                            messages.set(msgId-1,bodyContent);
                            String specificMsgUpdate = messages.indexOf(messages.get(msgId-1))+1+": "+messages.get(msgId-1);
                            sendRespond(out,contentSend, ResponseStatusCode.getDesc(200),version,contentType,specificMsgUpdate.getBytes());
                            System.out.println("list:"+messages);
                        }
                    }else{
                        sendRespond(out,contentSend, ResponseStatusCode.getDesc(404),version,contentType,"You can only update a specific message".getBytes());
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
                //cli.close();
            } catch (IOException e) {
                System.err.println("Error closing stream : " + e.getMessage());
            }
        }
    }

    private static void sendRespond(PrintWriter out,BufferedOutputStream contentSend, String status, String version, String contentTyp, byte[] content) throws IOException {
        //send the response to the client's output stream
        out.println(version +" "+ status);
        out.println("ContentType: "+ contentTyp);
        out.println(); // blank line between headers and content, very important !
        out.flush(); // flush character output stream buffer
        contentSend.write(content);
        contentSend.flush();
    }
    private static String takeTheBody(BufferedReader in) throws IOException {
        //take the other part of the header + the body
        StringBuilder requestBody = new StringBuilder();
        //divide every field of the header and the last part(the body) with a newline
        while (in.ready()) {
            char temp = (char) in.read();
            requestBody.append(temp);
            //String line = in.readLine();
            //requestBody.append(line + "\r\n");
            System.out.println("Line in while: "+temp);
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

    /*
    private static void handleClient(Socket cli) throws IOException {
        //read the request
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(cli.getInputStream()));
        StringBuilder requestBuilder = new StringBuilder();
        String line;
        //request end with one empty line
        //we read it until an empty line comes
        while((line= bufferedReader.readLine())!=null && !line.equals("")){
            requestBuilder.append(line);
           requestBuilder.append("\r\n");
        }
        String request = requestBuilder.toString();
        //parse the request
        String[] requestLines = request.split("\r\n");
        String[] requestLine  = requestLines[0].split(" ");
        String method = requestLine[0];
        System.out.println("methode: "+method);
        String path = requestLine[1];
        System.out.println("path: "+path);
        String version = requestLine[2];
        System.out.println("version: "+version);
        String host = requestLines[1].split(" ")[1];
        System.out.println("host: "+host);
        //list for the header
         List<String> _headers = new ArrayList<>();
        //save the header in the List
        //we start at 2 because the first line is (method, path and version)
        //and second line is the host
        for(int i = 2; i<requestLines.length; i++){
            String header = requestLines[i];
            //for(int i = 2; i<req.size(); i++){
            //String header = req.get(i);
            _headers.add(header);
        }

        String access = String.format("Client --> %s, method --> %s, path --> %s, version --> %s, host --> %s, headers --> %s",
                cli.toString(),method,path,version,host, _headers.toString());
        System.out.println(access);

        //if the method is post
        String body = null;
        if (method.equals(RequestMethods.POST.getVal())) {
            StringBuilder requestBody = new StringBuilder();
            while (bufferedReader.ready()) {
                char temp = (char) bufferedReader.read();
                requestBody.append(temp);
            }
            body = requestBody.toString();
        }

      OutputStream clientOutput = cli.getOutputStream();
        clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
        clientOutput.write(("ContentType: text/html\r\n").getBytes());
        clientOutput.write("\r\n".getBytes());
        clientOutput.write("<b>It works!</b>".getBytes());
        clientOutput.write("\r\n\r\n".getBytes());
        clientOutput.flush();
        cli.close();


    }*/
}