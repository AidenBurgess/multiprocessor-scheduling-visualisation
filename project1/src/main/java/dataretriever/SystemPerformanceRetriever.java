package main.java.dataretriever;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class SystemPerformanceRetriever {
    private final OperatingSystemMXBean statsRetriever = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    private final long totalRAM = statsRetriever.getTotalPhysicalMemorySize();

    /**
     * @return double representing the CPU percentage use
     */
    public double getCPUUsagePercent() {
        return statsRetriever.getProcessCpuLoad() * 100;
    }

    /**
     * @return long representing the RAM usage in bytes
     */
    public long getRAMUsageBytes() {
        return totalRAM - statsRetriever.getFreePhysicalMemorySize();
    }

    /**
     * @return long representing the CPU time in nanoseconds
     */
    public long getTimeElapsed() {
        return statsRetriever.getProcessCpuTime();
    }

}
