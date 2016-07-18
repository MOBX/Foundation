package com.lamfire.utils;

import java.io.*;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import com.lamfire.code.Hex;

public class MACAddressUtils {

    private static String       macAddress       = null;
    private static long         macAddressAsLong = -1;
    private static List<String> hardwareAddresses;

    public static String getMacAddress() {
        return macAddress;
    }

    public static long getMacAddressAsLong() {
        if (macAddressAsLong == -1) {
            macAddressAsLong = asLongAddress(macAddress);
        }
        return macAddressAsLong;
    }

    public static long asLongAddress(String macAddress) {
        return Hex.toLong("0000" + macAddress);
    }

    public static String asStringAddress(long macAddress) {
        return Hex.encode(Bytes.toBytes(macAddress)).substring(4);
    }

    private static String getFirstLineOfCommand(String[] commands) throws IOException {
        Process p = null;
        BufferedReader reader = null;
        try {
            p = Runtime.getRuntime().exec(commands);
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()), 128);

            String str = reader.readLine();
            return str;
        } finally {
            if (p != null) {
                if (reader != null) try {
                    reader.close();
                } catch (IOException ex) {
                }
                try {
                    p.getErrorStream().close();
                } catch (IOException ex) {
                }
                try {
                    p.getOutputStream().close();
                } catch (IOException ex) {
                }
                p.destroy();
            }
        }
    }

    static {
        try {
            Class.forName("java.net.InterfaceAddress");
            macAddress = getHardwareAddress();
        } catch (Exception err) {
        }

        if (macAddress == null) {
            Process p = null;
            BufferedReader in = null;
            try {
                String osname = System.getProperty("os.name", "");

                if (osname.startsWith("Windows")) {
                    p = Runtime.getRuntime().exec(new String[] { "ipconfig", "/all" }, null);
                } else if ((osname.startsWith("Solaris")) || (osname.startsWith("SunOS"))) {
                    String hostName = getFirstLineOfCommand(new String[] { "uname", "-n" });

                    if (hostName != null) {
                        p = Runtime.getRuntime().exec(new String[] { "/usr/sbin/arp", hostName }, null);
                    }

                } else if (new File("/usr/sbin/lanscan").exists()) {
                    p = Runtime.getRuntime().exec(new String[] { "/usr/sbin/lanscan" }, null);
                } else if (new File("/sbin/ifconfig").exists()) {
                    p = Runtime.getRuntime().exec(new String[] { "/sbin/ifconfig", "-a" }, null);
                }

                if (p != null) {
                    in = new BufferedReader(new InputStreamReader(p.getInputStream()), 128);

                    String l = null;
                    while ((l = in.readLine()) != null) {
                        macAddress = parse(l);
                        if ((macAddress == null) || (Hex.toShort(macAddress) == 255)) {
                            continue;
                        }
                    }
                }
            } catch (SecurityException ex) {
            } catch (IOException ex) {
            } finally {
                if (p != null) {
                    if (in != null) try {
                        in.close();
                    } catch (IOException ex) {
                    }
                    try {
                        p.getErrorStream().close();
                    } catch (IOException ex) {
                    }
                    try {
                        p.getOutputStream().close();
                    } catch (IOException ex) {
                    }
                    p.destroy();
                }
            }

        }

    }

    static String parse(String in) {
        String out = in;

        int hexStart = out.indexOf("0x");
        if ((hexStart != -1) && (out.indexOf("ETHER") != -1)) {
            int hexEnd = out.indexOf(' ', hexStart);
            if (hexEnd > hexStart + 2) {
                out = out.substring(hexStart, hexEnd);
            }

        } else {
            int octets = 0;

            if (out.indexOf('-') > -1) {
                out = out.replace('-', ':');
            }

            int lastIndex = out.lastIndexOf(':');

            if (lastIndex > out.length() - 2) {
                out = null;
            } else {
                int end = Math.min(out.length(), lastIndex + 3);

                octets++;
                int old = lastIndex;
                while ((octets != 5) && (lastIndex != -1) && (lastIndex > 1)) {
                    lastIndex--;
                    lastIndex = out.lastIndexOf(':', lastIndex);
                    if ((old - lastIndex == 3) || (old - lastIndex == 2)) {
                        octets++;
                        old = lastIndex;
                    }
                }

                if ((octets == 5) && (lastIndex > 1)) {
                    out = out.substring(lastIndex - 2, end).trim();
                } else {
                    out = null;
                }

            }

        }

        if ((out != null) && (out.startsWith("0x"))) {
            out = out.substring(2);
        }

        return out;
    }

    public static String getHardwareAddress() {
        return getHardwareAddress(0);
    }

    public static String getHardwareAddress(int iface) {
        List<String> list = getHardwareAddresses();
        if (list == null || list.isEmpty()) {
            return null;
        }
        try {
            return list.get(iface);
        } catch (Exception e) {

        }
        return null;
    }

    public static List<String> getHardwareAddresses() {
        if (hardwareAddresses != null) {
            return hardwareAddresses;
        }
        hardwareAddresses = Lists.newArrayList();
        try {
            Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
            if (ifs != null) {

                while (ifs.hasMoreElements()) {
                    NetworkInterface iface = (NetworkInterface) ifs.nextElement();
                    byte[] hardware = iface.getHardwareAddress();
                    if ((hardware != null) && (hardware.length == 6) && (hardware[1] != -1)) {
                        String hardwareAddr = Hex.append(new StringBuilder(36), hardware).toString();
                        hardwareAddresses.add(hardwareAddr);
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return hardwareAddresses;
    }
}
