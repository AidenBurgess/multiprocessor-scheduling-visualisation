package main.java.dataretriever;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

/**
 * Gets the system performance statistics at runtime. Currently used to extract the CPU and RAM from the computer.
 */
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
     * @return double representing the RAM usage in gigabytes
     */
    public double getRAMUsageGigaBytes() {
        return ((totalRAM - statsRetriever.getFreePhysicalMemorySize()) / (double)1.074e9);
    }

    /**
     * @return long representing the CPU time in nanoseconds
     */
    public long getTimeElapsed() {
        return statsRetriever.getProcessCpuTime();
    }

    /**
     * @return double representing the total RAM available in gigabytes
     */
    public double getTotalRAMGigaBytes() {
        return (totalRAM  / (double)1.074e9);
    }

}
