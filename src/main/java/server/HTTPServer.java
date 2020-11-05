package server;
//import request.*;
//import response.*;

import request.RequestMethods;
import response.ResponseStatusCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class HTTPServer {

    public static void main(String[] args) throws Exception {
        //open a tcp socket
        try(ServerSocket serverSocket = new ServerSocket(8080)){
            while (true){
                //accept the client connection
                try(Socket cli = serverSocket.accept()){
                    handleClient(cli);
                }
            }
        }

    }

    private static void handleClient(Socket cli) throws IOException {
        System.out.println("new Client: "+cli.toString());
        //read the request
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(cli.getInputStream()));
        StringBuilder requestBuilder = new StringBuilder();
        String line;
        //request end with one empty line
        //we read it until an empty line comes
        while(!(line= bufferedReader.readLine()).isBlank()){
            requestBuilder.append(line + "\r\n");
        }
        String request = requestBuilder.toString();
        //parse the request
        String[] requestLines = request.split("\r\n");
        String[] requestLine  = requestLines[0].split(" ");
        String method = requestLine[0];
        String path = requestLine[1];
        String version = requestLine[2];
        String host = requestLines[1].split(" ")[1];
        //list for the header
        List<String> _headers = new ArrayList<>();
        //save the header in the List
        //we start at 2 because the first line is (method, path and version)
        //and second line is the host
        for(int i = 2; i<requestLines.length; i++){
            String header = requestLines[i];
            _headers.add(header);
        }

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

        String access = String.format("Client %s, method %s, path %s, version %s, host %s, headers %s",
                cli.toString(),method,path,version,host, _headers.toString());
        System.out.println(access);

        OutputStream clientOutput = cli.getOutputStream();
        clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
        clientOutput.write(("ContentType: text/html\r\n").getBytes());
        clientOutput.write("\r\n".getBytes());
        clientOutput.write("<b>It works!</b>".getBytes());
        clientOutput.write("\r\n\r\n".getBytes());
        clientOutput.flush();
        cli.close();

        //if the path exists
        /*Path filepath = filePath(path);
        if(Files.exists(filepath)){
                String conTyp = findContentTyp(filepath);
                sendRespond(cli, ResponseStatusCode.getDesc(200),conTyp,version,Files.readAllBytes(filepath));
            }*/
    }
    private static void sendRespond(Socket cli, String status, String version, String contentTyp, byte[] content) throws IOException {
        //send the response to the client's output stream
        OutputStream cliOut = cli.getOutputStream();
        cliOut.write((version + status+"\r\n").getBytes());
        cliOut.write(("ContentType:" + contentTyp + "\r\n").getBytes());
        cliOut.write("\r\n".getBytes());
        cliOut.write(content);
        cliOut.write("\r\n\r\n".getBytes());
        cliOut.flush();
        cli.close();

    }
    private static String findContentTyp(Path path) throws IOException {
        return Files.probeContentType(path);
    }

    private static Path filePath(String path){
        if("/".equals(path)){
            path = "/index.html";
        }
        return Paths.get("REST_HTTP",path);
    }

 }