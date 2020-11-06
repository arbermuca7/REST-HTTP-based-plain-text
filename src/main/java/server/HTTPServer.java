package server;
import request.RequestMethods;
import response.ResponseStatusCode;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class HTTPServer implements Runnable {

    Socket cli;
    HTTPServer(Socket s){
        cli = s;
    }

    public static void main(String[] args){
        try{
            //open a tcp socket
            ServerSocket serverSocket = new ServerSocket(8080);

            while (true){
                //accept the client connection
                HTTPServer httpServer = new HTTPServer(serverSocket.accept());
                Thread thread = new Thread(httpServer);
                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void run() {
        System.out.println("Client: "+cli.toString());
        //read the request
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream contentSend = null;
        String inputStream = null;
        try{
            in = new BufferedReader(new InputStreamReader(cli.getInputStream()));
            out = new PrintWriter(cli.getOutputStream());
            contentSend = new BufferedOutputStream(cli.getOutputStream());
            inputStream = in.readLine();
            StringTokenizer parse = null;

            if (inputStream != null){
                parse = new StringTokenizer(inputStream);
            }

            String method = parse.nextToken().toUpperCase();
            System.out.println("methode: "+method);
            String path = parse.nextToken().toLowerCase();
            System.out.println("path: "+path);
            String version = parse.nextToken().toUpperCase();
            System.out.println("version: "+version);

            if(method.equals(RequestMethods.GET.getVal())){
                out.println("HTTP/1.1 200 OK\r\n");
                out.println(("ContentType: text/html\r\n"));
                out.println("\r\n");
                out.println("<b>It works!</b>");
                out.println(); // blank line between headers and content, very important !
                out.flush(); // flush character output stream buffer
                if(path.equals("/messages")){
                    contentSend.write("<h1>Du hast es geschaft</h1>".getBytes());
                    contentSend.flush();
                }

                //if the path exists
                /*Path filepath = filePath(path);
                if(Files.exists(filepath)){
                        String conTyp = findContentTyp(filepath);
                        sendRespond(cli, ResponseStatusCode.getDesc(200),conTyp,version,Files.readAllBytes(filepath));
                }*/
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






    private static void handleClient(Socket cli) throws IOException {
       /*
        //read the request
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(cli.getInputStream()));
        String input = bufferedReader.readLine();
        StringTokenizer parse = new StringTokenizer(input);
        String method = parse.nextToken().toUpperCase();
        System.out.println("methode: "+method);
        String file = parse.nextToken().toLowerCase();
        System.out.println("file: "+file);


        //StringBuilder requestBuilder = new StringBuilder();
        String line;
        //request end with one empty line
        //we read it until an empty line comes
        List <String> req = new ArrayList<>();

        while((line= bufferedReader.readLine())!=null && !line.isBlank()){
            //requestBuilder.append(line + "\r\n");
            req.add(line + "\r\n");
        }
        System.out.println("List: "+ req);
        //String request = requestBuilder.toString();
        //parse the request
        //String[] requestLines = request.split("\r\n");
        String[] requestLine  = req.get(0).split(" ");
        String method = requestLine[0];
        System.out.println("methode: "+method);
        String path = requestLine[1];
        System.out.println("path: "+path);
        String version = requestLine[2];
        System.out.println("version: "+version);
        String host = req.get(1).split(" ")[1];
        System.out.println("host: "+host);
        //list for the header
        List<String> _headers = new ArrayList<>();
        //save the header in the List
        //we start at 2 because the first line is (method, path and version)
        //and second line is the host
        //for(int i = 2; i<requestLines.length; i++){
            //String header = requestLines[i];
        for(int i = 2; i<req.size(); i++){
            String header = req.get(i);
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
        cli.close();*/


    }
}