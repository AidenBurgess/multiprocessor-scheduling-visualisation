package main.java.dataretriever;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class SystemPerformanceRetriever {
    // Get OS management object
    private final OperatingSystemMXBean statsRetriever = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    // Get total RAM to calculate used RAM later
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
    public double getRAMUsageBytes() {
        return ((totalRAM - statsRetriever.getFreePhysicalMemorySize()) / (double)1e9);
    }

    /**
     * @return long representing the CPU time in nanoseconds
     */
    public long getTimeElapsed() {
        return statsRetriever.getProcessCpuTime();
    }

    /**
     * @return long representing the total RAM available in bytes
     */
    public long getTotalRAMBytes() {
        return totalRAM;
    }

}
