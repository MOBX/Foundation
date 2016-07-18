package com.lamfire.utils;

import java.io.Serializable;
import java.lang.management.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JvmInfo {

    private RuntimeMXBean         runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    private MemoryMXBean          memoryMXBean  = ManagementFactory.getMemoryMXBean();
    private OperatingSystemMXBean osMXBean      = ManagementFactory.getOperatingSystemMXBean();
    private ThreadMXBean          ThreadBean    = ManagementFactory.getThreadMXBean();
    private static JvmInfo        INSTANCE;

    public synchronized static JvmInfo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JvmInfo();
        }
        return INSTANCE;
    }

    private long                pid;
    private String              hostname;
    private String              jdkVersion;
    private String              vmName;
    private String              vmVersion;
    private String              vmVendor;
    private long                startTime;
    private Mem                 mem;
    private String[]            inputArguments;
    private String              bootClassPath;
    private String              classPath;
    private Map<String, String> systemProperties;
    private Method              getMaxFileDescriptorCountField;
    private Method              getOpenFileDescriptorCountField;

    private JvmInfo() {
        try {
            String xname = runtimeMXBean.getName();
            String[] processInfos = xname.split("@");
            this.pid = Long.parseLong(processInfos[0]);
            this.hostname = processInfos[1];
        } catch (Exception e) {

        }

        if (!ThreadBean.isThreadCpuTimeEnabled()) {
            ThreadBean.setThreadCpuTimeEnabled(true);
        }

        this.startTime = runtimeMXBean.getStartTime();
        this.jdkVersion = ((String) runtimeMXBean.getSystemProperties().get("java.version"));
        this.vmName = runtimeMXBean.getVmName();
        this.vmVendor = runtimeMXBean.getVmVendor();
        this.vmVersion = runtimeMXBean.getVmVersion();
        this.mem = new Mem();
        this.mem.heapInit = memoryMXBean.getHeapMemoryUsage().getInit();
        this.mem.heapMax = memoryMXBean.getHeapMemoryUsage().getMax();
        this.mem.nonHeapInit = memoryMXBean.getNonHeapMemoryUsage().getInit();
        this.mem.nonHeapMax = memoryMXBean.getNonHeapMemoryUsage().getMax();
        try {
            Class<?> vmClass = Class.forName("sun.misc.VM");
            this.mem.directMemoryMax = ((Long) vmClass.getMethod("maxDirectMemory", new Class[0]).invoke(null,
                                                                                                         new Object[0])).longValue();
        } catch (Throwable t) {
        }
        this.inputArguments = ((String[]) runtimeMXBean.getInputArguments().toArray(new String[runtimeMXBean.getInputArguments().size()]));
        this.bootClassPath = runtimeMXBean.getBootClassPath();
        this.classPath = runtimeMXBean.getClassPath();
        this.systemProperties = runtimeMXBean.getSystemProperties();

        try {
            this.getMaxFileDescriptorCountField = osMXBean.getClass().getDeclaredMethod("getMaxFileDescriptorCount",
                                                                                        new Class[0]);
            this.getMaxFileDescriptorCountField.setAccessible(true);
        } catch (Exception e) {
        }

        try {
            this.getOpenFileDescriptorCountField = osMXBean.getClass().getDeclaredMethod("getOpenFileDescriptorCount",
                                                                                         new Class[0]);
            this.getOpenFileDescriptorCountField.setAccessible(true);
        } catch (Exception e) {
        }
    }

    public long getPid() {
        return pid;
    }

    public String getJdkVersion() {
        return jdkVersion;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getVmName() {
        return vmName;
    }

    public String getVmVersion() {
        return vmVersion;
    }

    public String getVmVendor() {
        return vmVendor;
    }

    public long getStartTime() {
        return startTime;
    }

    public Mem getMem() {
        this.mem.heapUsed = (memoryMXBean.getHeapMemoryUsage().getUsed() < 0L ? 0L : memoryMXBean.getHeapMemoryUsage().getUsed());
        this.mem.nonHeapUsed = (memoryMXBean.getNonHeapMemoryUsage().getUsed() < 0L ? 0L : memoryMXBean.getHeapMemoryUsage().getUsed());
        return mem;
    }

    public String[] getInputArguments() {
        return inputArguments;
    }

    public String getBootClassPath() {
        return bootClassPath;
    }

    public String getClassPath() {
        return classPath;
    }

    public Map<String, String> getSystemProperties() {
        return systemProperties;
    }

    public List<ThreadInfo> getThreads() {
        return getHotThreadInfos();
    }

    public List<ThreadInfo> getAllThreadInfos() {
        List<ThreadInfo> result = new ArrayList<ThreadInfo>();
        if (!ThreadBean.isThreadCpuTimeSupported()) {
            throw new IllegalStateException("MBean doesn't support thread CPU Time");
        }

        for (long threadId : ThreadBean.getAllThreadIds()) {
            ThreadInfo info = ThreadBean.getThreadInfo(threadId, 0);
            result.add(info);
        }
        return result;
    }

    public List<ThreadInfo> getHotThreadInfos() {
        List<ThreadInfo> result = new ArrayList<ThreadInfo>();
        if (!ThreadBean.isThreadCpuTimeSupported()) {
            throw new IllegalStateException("MBean doesn't support thread CPU Time");
        }

        for (long threadId : ThreadBean.getAllThreadIds()) {
            if (ThreadBean.getThreadCpuTime(threadId) > 0) {
                ThreadInfo info = ThreadBean.getThreadInfo(threadId, 0);
                result.add(info);
            }
        }
        return result;
    }

    public int getThreadCount() {
        return ThreadBean.getThreadCount();
    }

    public long getTotalStartedThreadCount() {
        return ThreadBean.getTotalStartedThreadCount();
    }

    public long getThreadCpuTime(long id) {
        return ThreadBean.getThreadCpuTime(id);
    }

    public long getThreadUserTime(long id) {
        return ThreadBean.getThreadUserTime(id);
    }

    public long getCurrentThreadCpuTime() {
        return ThreadBean.getCurrentThreadCpuTime();
    }

    public long getCurrentThreadUserTime() {
        return ThreadBean.getCurrentThreadUserTime();
    }

    public int getDaemonThreadCount() {
        return ThreadBean.getDaemonThreadCount();
    }

    public int getPeakThreadCount() {
        return ThreadBean.getPeakThreadCount();
    }

    public long[] getAllThreadIds() {
        return ThreadBean.getAllThreadIds();
    }

    public ThreadInfo getThreadInfo(long id) {
        return ThreadBean.getThreadInfo(id);
    }

    public ThreadInfo[] getThreadInfo(long[] ids) {
        return ThreadBean.getThreadInfo(ids);
    }

    public List<GarbageCollector> getGarbageCollectors() {
        List<GarbageCollector> result = new ArrayList<GarbageCollector>();
        List<GarbageCollectorMXBean> gcMxBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcMxBean : gcMxBeans) {
            GarbageCollector gc = new GarbageCollector();
            gc.name = gcMxBean.getName();
            gc.collectionCount = gcMxBean.getCollectionCount();
            gc.collectionTime = gcMxBean.getCollectionTime();
            result.add(gc);
        }
        return result;
    }

    public long getUptime() {
        return runtimeMXBean.getUptime();
    }

    public String getOperatingSystemName() {
        return osMXBean.getName();
    }

    public String getOperatingSystemArch() {
        return osMXBean.getArch();
    }

    public int getAvailableProcessors() {
        return osMXBean.getAvailableProcessors();
    }

    public String getOperatingSystemVersion() {
        return osMXBean.getVersion();
    }

    public double getSystemLoadAverage() {
        return osMXBean.getSystemLoadAverage();
    }

    public long getMaxFileDescriptorCount() {
        if (getMaxFileDescriptorCountField == null) return -1L;
        try {
            return ((Long) getMaxFileDescriptorCountField.invoke(osMXBean, new Object[0])).longValue();
        } catch (Exception e) {
        }
        return -1L;
    }

    public long getOpenFileDescriptorCount() {
        if (getOpenFileDescriptorCountField == null) return -1L;
        try {
            return ((Long) getOpenFileDescriptorCountField.invoke(osMXBean, new Object[0])).longValue();
        } catch (Exception e) {
        }
        return -1L;
    }

    public static class Mem implements Serializable {

        private static final long serialVersionUID = 8056846086993649041L;
        long                      heapInit         = 0L;
        long                      heapMax          = 0L;
        long                      heapUsed         = 0L;
        long                      nonHeapInit      = 0L;
        long                      nonHeapMax       = 0L;
        long                      nonHeapUsed      = 0L;
        long                      directMemoryMax  = 0L;

        public long heapInit() {
            return (this.heapInit);
        }

        public long getHeapInit() {
            return heapInit();
        }

        public long heapMax() {
            return (this.heapMax);
        }

        public long getHeapMax() {
            return heapMax();
        }

        public long nonHeapInit() {
            return (this.nonHeapInit);
        }

        public long getNonHeapInit() {
            return nonHeapInit();
        }

        public long nonHeapMax() {
            return (this.nonHeapMax);
        }

        public long getNonHeapMax() {
            return nonHeapMax();
        }

        public long directMemoryMax() {
            return (this.directMemoryMax);
        }

        public long getDirectMemoryMax() {
            return directMemoryMax();
        }

        public long getHeapUsed() {
            return heapUsed;
        }

        public long getNonHeapUsed() {
            return nonHeapUsed;
        }

    }

    public static class GarbageCollector {

        String name;
        long   collectionCount;
        long   collectionTime;

        public String getName() {
            return name;
        }

        public long getCollectionCount() {
            return collectionCount;
        }

        public long getCollectionTime() {
            return collectionTime;
        }
    }
}
