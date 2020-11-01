package response;

import java.util.HashMap;
import java.util.Map;

public enum ResponseStatusCode {
    CONTINUE(100, "Continue"),
    SWITCHING_PROTOCOL(101, "Switching Protocols"),
    PROCESSING(102, "Processing"),
    EARLY_HINTS(103, "Early Hints"),
    OK(200,"OK"),
    CREATED(201,"Created"),
    ACCEPTED(202,"Accepted"),
    NON_AUTHORITATIVE_INFORMATION(203,"Non-authoritative information"),
    NO_CONTENT(204,"No Content"),
    RESET_CONTENT(205,"Reset Content"),
    PARTIAL_CONTENT(206,"Partial Content"),
    MULTI_STATUS(207,"Multi-Status (WebDAV)"),
    ALREADY_REPORTED(208, "Already Reported (WebDAV)"),
    IM_USED(226, "IM Used"),
    MULTIPLE_CHOICES(300,"Multiple Choices"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302,"Found"),
    SEE_OTHER(303,"See Other"),
    NOT_MODIFIED(304,"Not Modified"),
    USE_PROXY(305,"Use Proxy (Deprecated)"),
    TEMPORARY_REDIRECT(307,"Temporary Redirect"),
    PERMANENT_REDIRECT(308,"Permanent Redirect (experimental)"),
    BAD_REQUEST(400,"Bad Request"),
    UNAUTHORISED(401,"Unauthorized"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403,"Forbidden"),
    NOT_FOUND(404,"Not Found"),
    METHOD_NOT_ALLOWED(405,"Method not allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    CONFLICT(409, "Conflict"),
    GONE(410, "Gone"),
    LENGTH_REQUIRED(411, "Length Required"),
    INTERNAL_SERVER_ERROR(500,"Internal Server Error"),
    NOT_IMPLEMENTED(501,"Not Implemented"),
    BAD_GATEWAY(502,"Bad Gateway"),
    SERVICE_UNAVALIABLE(503,"Service Unavaliable"),
    GATEWAY_TIMEOUT(504,"Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
    NOT_EXTENDED(510, "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED(511,"Network Authentication Required" );


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

    private void initMap(){
        val = new HashMap<>();
        for(ResponseStatusCode code: ResponseStatusCode.values()){
            val.put(code.num, code.desc);
        }
    }
    public String getDesc(int number){
        if(val == null){
            initMap();
        }
        if(val.containsKey(number)){
            return number + " " + val.get(number);
        }
        return "";
    }
}
