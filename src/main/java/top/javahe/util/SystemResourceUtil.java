package top.javahe.util;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.text.DecimalFormat;

public class SystemResourceUtil {
    private static final DecimalFormat df = new DecimalFormat("#.##");

    public static String getSystemInfo() {
        StringBuilder info = new StringBuilder();
        
        // 获取内存使用情况
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        long heapMemoryUsage = memoryMXBean.getHeapMemoryUsage().getUsed();
        long nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage().getUsed();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        
        // 获取磁盘使用情况
        File[] roots = File.listRoots();
        StringBuilder diskInfo = new StringBuilder();
        for (File root : roots) {
            long totalSpace = root.getTotalSpace();
            long freeSpace = root.getFreeSpace();
            long usedSpace = totalSpace - freeSpace;
            
            diskInfo.append(String.format("\n磁盘 %s: 总空间=%s, 已用=%s, 可用=%s",
                root.getPath(),
                formatSize(totalSpace),
                formatSize(usedSpace),
                formatSize(freeSpace)));
        }

        info.append("系统资源信息：\n");
        info.append(String.format("堆内存使用: %s\n", formatSize(heapMemoryUsage)));
        info.append(String.format("非堆内存使用: %s\n", formatSize(nonHeapMemoryUsage)));
        info.append(String.format("总内存: %s\n", formatSize(totalMemory)));
        info.append(String.format("空闲内存: %s\n", formatSize(freeMemory)));
        info.append(diskInfo);

        return info.toString();
    }

    private static String formatSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return df.format(size / 1024.0) + " KB";
        } else if (size < 1024 * 1024 * 1024) {
            return df.format(size / (1024.0 * 1024)) + " MB";
        } else {
            return df.format(size / (1024.0 * 1024 * 1024)) + " GB";
        }
    }
}