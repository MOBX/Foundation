package com.lamfire.code;

public class Radixes {
	static final String SS ="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static final int MAXRADIX = SS.length();
	
	public static String digest62(long value){
		return encode(value,MAXRADIX);
	}

	public static String digest32(long value){
		return encode(value,32);
	}

	public static String digest16(long value){
		return encode(value,16);
	}
	
	public static String encode(long value,int radix){
		if(radix > MAXRADIX){
			throw new IllegalArgumentException("The parameter 'radix' cannot greater than [" + MAXRADIX +"]");
		}
		
		boolean revert = false;
		if(value < 0){
			value = Math.abs(value);
			revert = true;
		}
		
		String result = value==0?"0":"";
		while(value > 0){
			int v = (int)(value % radix);
			value = value / radix;
			result = revert?(SS.charAt(radix - v -1)) + result:(SS.charAt(v)) + result;
		}
		
		return revert?SS.charAt(radix-1)+result:result;
	}
	
	public static long decode(String text,int radix){
		if(text == null){
			return 0;
		}
		if(radix <= 36){
			text = text.toLowerCase();
		}
		long result = 0,multiplier = 1;  
        for (int pos = text.length(); pos > 0; pos--) {  
            result += ((SS.indexOf(text.substring(pos - 1, pos))) * multiplier);  
            multiplier *= radix;  
        }  
        return result;  
	}
	
	public static long decodeNegative (String text,int radix){
		if(text == null){
			return 0;
		}
		long result = 0,multiplier = 1;;  
        for (int pos = text.length(); pos > 0; pos--) {  
            result += ((SS.indexOf(text.substring(pos - 1, pos)) - radix +1) * multiplier);  
            multiplier *= radix;  
        }  
        return result;  
	}
}
