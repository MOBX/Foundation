package lamfire;

import com.lamfire.code.Base64;
import com.lamfire.code.DES;

/**
 * @author zxc Jul 18, 2015 1:17:23 PM
 */
public class DESTest {

    public static void main(String[] args) throws Exception {
        String inputStr = "lin12345";
        String key = DES.genkey("lin12345");
        System.err.println("原文:\t" + inputStr);

        System.err.println("密钥:\t" + key);

        byte[] inputData = inputStr.getBytes();
        inputData = DES.encode(inputData, key);

        System.err.println("加密后:\t" + Base64.encode(inputData));

        byte[] outputData = DES.decode(inputData, key);
        String outputStr = new String(outputData);

        System.err.println("解密后:\t" + outputStr);

        System.out.println(inputStr.equals(outputStr));
    }
}
