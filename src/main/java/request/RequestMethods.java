package request;

import java.util.ArrayList;
import java.util.List;


public enum RequestMethods {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH");

    String method;
    private static List<String> val;

    RequestMethods(String method){
        this.method = method;
    }

    public String getVal() {
        return this.method;
    }

    private static void initList(){
        val = new ArrayList<>();
        for (RequestMethods method : RequestMethods.values()){
            val.add(method.getVal());
        }
    }

    private static boolean hasMethod(String method){
        if(val == null){
            initList();
        }
        return val.contains(method);
    }
}
