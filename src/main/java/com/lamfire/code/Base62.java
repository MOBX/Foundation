package com.lamfire.code;

public class Base62 {  
    public static String encode(long number) {  
        return Radixes.encode(number,62);  
    }  
  
    public static long decode(String base62) {  
        return Radixes.decode(base62,62);  
    }  

}  
