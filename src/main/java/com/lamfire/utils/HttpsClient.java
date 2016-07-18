package com.lamfire.utils;

import java.io.*;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.*;

import com.lamfire.logger.Logger;

public class HttpsClient {

    private static final String BOUNDARY = "LamfireFormBoundaryucJiylDzwZWyoOSF";

    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static class ContentType {

        public static final String multipart_form_data               = "multipart/form-data; boundary=" + BOUNDARY;
        public static final String application_x_www_form_urlencoded = "application/x-www-form-urlencoded";
    }

    private static final Logger       LOGGER           = Logger.getLogger(HttpsClient.class.getName());
    private HttpsURLConnection        conn;
    private String                    charset          = "UTF-8";
    private String                    contentType      = ContentType.application_x_www_form_urlencoded;
    private String                    method           = "GET";
    private final Map<String, String> requestHeaderMap = new HashMap<String, String>();
    private int                       connectTimeOut   = 30000;
    private int                       readTimeOut      = 30000;
    private StringBuffer              queryBuffer;
    private InputStream               _inputStream;
    private OutputStream              _outputStream;

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setMethod(String method) {
        this.method = method.toUpperCase();
    }

    public String getMethod() {
        return method;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;

    }

    public void addPostParameter(String name, String value) {
        if (queryBuffer == null) {
            queryBuffer = new StringBuffer();
            queryBuffer.append(name + "=" + URLUtils.encode(value, charset));
            return;
        }
        queryBuffer.append("&");
        queryBuffer.append(name + "=" + URLUtils.encode(value, charset));
    }

    public void post() throws IOException {
        if (queryBuffer == null) {
            throw new RuntimeException(
                                       "The post parameter is empty.please first invoke 'addPostParameter(String name,String value)' try again.");
        }
        this.post(queryBuffer.toString().getBytes());
    }

    public void post(byte[] bytes) throws IOException {
        OutputStream output = getOutputStream();
        if (output == null) {
            throw new RuntimeException("method [" + this.method + "] output stream has not opened.");
        }

        output.write(bytes);
        output.flush();

    }

    public void setRequestHeader(String key, String value) {
        this.requestHeaderMap.put(key, value);
    }

    public void setConnectTimeout(int millis) {
        this.connectTimeOut = millis;
    }

    public void setReadTimeout(int millis) {
        this.readTimeOut = millis;
    }

    public void open(String url) throws Exception {
        open(new URL(url));
    }

    public void open(URL url) throws Exception {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());

        conn = (HttpsURLConnection) url.openConnection();

        conn.setSSLSocketFactory(sc.getSocketFactory());
        conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
        conn.setRequestMethod(method);
        conn.setRequestProperty("User-Agent",
                                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2");
        conn.setReadTimeout(readTimeOut);
        conn.setConnectTimeout(connectTimeOut);
        conn.setUseCaches(false);
        conn.setDoInput(true);

        boolean isGetMethod = "GET".equalsIgnoreCase(method);
        if (isGetMethod) {
            conn.setDoOutput(false);
            conn.setInstanceFollowRedirects(false);
        } else {
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", charset);
            conn.setRequestProperty("Content-Type", contentType);
        }

        for (Map.Entry<String, String> entry : requestHeaderMap.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }

        // ������
        conn.connect();
    }

    public byte[] read() throws IOException {
        InputStream input = getInputStream();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        IOUtils.copy(input, result);
        // ��ȡ���
        return result.toByteArray();
    }

    public synchronized InputStream getInputStream() {
        if (_inputStream != null) {
            return _inputStream;
        }
        try {
            this._inputStream = conn.getInputStream();
            return _inputStream;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized OutputStream getOutputStream() {
        if (_outputStream != null) {
            return _outputStream;
        }
        try {
            _outputStream = conn.getOutputStream();
            return _outputStream;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String readAsString() throws IOException {
        return readAsString(charset);
    }

    public String readAsString(String encoding) throws IOException {
        byte[] content = read();
        if (content == null) return null;
        return new String(content, encoding);
    }

    public String getResponseHeader(String key) {
        return conn.getHeaderField(key);
    }

    public Map<String, List<String>> getResponseHeaders() {
        return conn.getHeaderFields();
    }

    public String getContentEncoding() {
        return conn.getContentEncoding();
    }

    public int getContentLength() {
        return conn.getContentLength();
    }

    public int getResponseCode() throws IOException {
        return conn.getResponseCode();
    }

    public String getResponseMessage() throws IOException {
        return conn.getResponseMessage();
    }

    public void sendMultipartFormItem(String formItemName, String value) throws IOException {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\r\n--" + BOUNDARY + "\r\n");
        buffer.append("Content-Disposition: form-data; name=\"" + formItemName + "\"\r\n");
        buffer.append("Content-Type: text/plain;charset=" + charset + "\r\n\r\n");
        buffer.append(value);
        OutputStream output = getOutputStream();
        output.write(buffer.toString().getBytes());
        output.flush();
    }

    public void sendMultipartFileAsByteArray(String formItemName, String fileName, byte[] bytes) throws IOException {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\r\n--" + BOUNDARY + "\r\n");
        buffer.append("Content-Disposition: form-data; name=\"" + formItemName + "\"; filename=\"" + fileName
                      + "\"\r\n");
        buffer.append("Content-Type: application/octet-stream\r\n\r\n");
        OutputStream output = getOutputStream();
        output.write(buffer.toString().getBytes());
        output.write(bytes);
        output.flush();
    }

    public void sendMultipartFile(String formItemName, File file) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            sendMultipartFileAsStream(formItemName, file.getName(), fis);
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    public void sendMultipartFileAsStream(String formItemName, String fileName, InputStream input) throws IOException {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\r\n--" + BOUNDARY + "\r\n");
        buffer.append("Content-Disposition: form-data; name=\"" + formItemName + "\"; filename=\"" + fileName
                      + "\"\r\n");
        buffer.append("Content-Type: application/octet-stream\r\n\r\n");
        OutputStream output = getOutputStream();
        output.write(buffer.toString().getBytes());
        IOUtils.copy(input, output);
        output.flush();

    }

    public void sendMultipartFinish() {
        String endstr = "\r\n--" + BOUNDARY + "--\r\n";
        byte[] endbytes = endstr.getBytes();
        try {
            OutputStream output = getOutputStream();
            output.write(endbytes);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void close() {
        try {
            IOUtils.closeQuietly(_outputStream);
            _outputStream = null;
        } catch (Exception e) {

        }
        try {
            IOUtils.closeQuietly(_inputStream);
            _inputStream = null;
        } catch (Exception e) {

        }
        if (conn != null) {
            conn.disconnect();
        }
    }

}
