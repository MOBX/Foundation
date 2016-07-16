package com.lamfire.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lamfire.logger.Logger;

public class URLUtils {
	private static final Logger logger = Logger.getLogger(URLUtils.class.getName());

	public static String encode(String data, String charset) {
		try {
			return URLEncoder.encode(data, charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String decode(String data, String charset) {
		try {
			return URLDecoder.decode(data, charset);
		} catch (UnsupportedEncodingException e) {
			logger.warn(e.getMessage(),e);
			throw new RuntimeException(e);
		}
	}

	public static String buildQueryString(Map<String, String> map, String charset) {
		if (map == null || map.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder();
		int i = 0;

		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (i > 0) {
				sb.append("&");
			}
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(encode(entry.getValue(), charset));
			i++;
		}

		return sb.toString();
	}
	
	public static boolean isUrl(String path){
		Pattern pattern = Pattern.compile("^(http://|https://){1}[\\w\\.\\-/:]+");
		Matcher matcher = pattern.matcher(path);
		return matcher.find();
	}
	
	public static void downloadToFile(URL url,File saveAs) throws IOException{
		FileUtils.copyURLToFile(url, saveAs);
	}
	
	public static byte[] downloadToBytes(URL source) throws IOException {
		InputStream input = source.openStream();
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			try {
				IOUtils.copy(input, output);
			} finally {
				IOUtils.closeQuietly(output);
			}
			return output.toByteArray();
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

    public static List<String> extractAllUrl(String body){
        List<String> list = Lists.newArrayList();
        Pattern pattern = Pattern.compile("(http://|https://){1}[\\w\\.\\-/:/?/=]+");
        Matcher matcher = pattern.matcher(body);
        while(matcher.find()){
            list.add(matcher.group());
        }
        return list;
    }

    public static String extractUrl(String body){
        List<String> list = Lists.newArrayList();
        Pattern pattern = Pattern.compile("(http://|https://){1}[\\w\\.\\-/:/?/=]+");
        Matcher matcher = pattern.matcher(body);
        if(matcher.find()){
            return (matcher.group());
        }
        return null;
    }

    public static List<String> extractURL(String body,String domain){
        List<String> list = Lists.newArrayList();
        Pattern pattern = Pattern.compile("(http://|https://){1}"+domain+"[\\w\\.\\-/:/?/=]+");
        Matcher matcher = pattern.matcher(body);
        while(matcher.find()){
            list.add(matcher.group());
        }
        return list;
    }

    public static String getHostAddress(URL url) throws UnknownHostException {
        InetAddress addr = InetAddress.getByName(url.getHost());
        return (addr.getHostAddress());
    }

    public static String getHostAddress(String url) throws UnknownHostException, MalformedURLException {
        return getHostAddress(new URL(url));
    }

    private static String switchToIPAddressUrl(String urlAddr) {
        try{
            URL url = new URL(urlAddr);
            InetAddress addr = InetAddress.getByName(url.getHost());
            String ipAddr =  addr.getHostAddress();
            return StringUtils.replace(urlAddr,url.getHost(),ipAddr);
        }catch (Exception e){
            logger.error("[host name to host address failed] : " + e.getMessage(),e);
            return urlAddr;
        }
    }
}
