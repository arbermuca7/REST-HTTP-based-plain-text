package response;

import java.util.HashMap;
import java.util.Map;

public enum ResponseStatusCode {
    OK(200,"OK"),
    CREATED(201,"Created"),
    ACCEPTED(202,"Accepted"),
    NO_CONTENT(204,"No Content"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    NOT_MODIFIED(304,"Not Modified"),
    BAD_REQUEST(400,"Bad Request"),
    UNAUTHORISED(401,"Unauthorized"),
    FORBIDDEN(403,"Forbidden"),
    NOT_FOUND(404,"Not Found"),
    INTERNAL_SERVER_ERROR(500,"Internal Server Error"),
    NOT_IMPLEMENTED(501,"Not Implemented"),
    BAD_GATEWAY(502,"Bad Gateway");


    private int num;
    private String desc;
    private static Map<Integer,String> val;

    ResponseStatusCode(int num, String desc){
        this.num = num;
        this.desc = desc;
    }

    public int getNum() {
        return num;
    }

    public String getVal() {
        return this.num + " " +this.desc;
    }

    private static void initMap(){
        val = new HashMap<>();
        for(ResponseStatusCode code: ResponseStatusCode.values()){
            val.put(code.num, code.desc);
        }
    }
    public static String getDesc(int number){
        if(val == null){
            initMap();
        }
        if(val.containsKey(number)){
            return number + " " + val.get(number);
        }
        return "";
    }
}
