package dev.pixelstudios.xutils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Supplier;

@UtilityClass
public class Tasks {

    public static BukkitTask sync(Runnable task) {
        return Bukkit.getScheduler().runTask(XUtils.getPlugin(), task);
    }

    public static BukkitTask async(Runnable task) {
        return Bukkit.getScheduler().runTaskAsynchronously(XUtils.getPlugin(), task);
    }

    public static BukkitTask later(Runnable task, long delay) {
        return Bukkit.getScheduler().runTaskLater(XUtils.getPlugin(), task, delay);
    }

    public static BukkitTask laterAsync(Runnable task, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(XUtils.getPlugin(), task, delay);
    }

    public static BukkitTask repeat(Runnable task, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(XUtils.getPlugin(), task, delay, period);
    }

    public static void repeatTimes(Runnable task, long delay, long period, int times) {
        new BukkitRunnable() {
            int remaining = times;
            @Override
            public void run() {
                task.run();
                if (--remaining == 0) {
                    cancel();
                }
            }
        }.runTaskTimer(XUtils.getPlugin(), delay, period);
    }

    public static BukkitTask repeatAsync(Runnable task, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(XUtils.getPlugin(), task, delay, period);
    }

    public static void repeatAsyncTimes(Runnable task, long delay, long period, int times) {
        new BukkitRunnable() {
            int remaining = times;
            @Override
            public void run() {
                task.run();
                if (--remaining == 0) {
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(XUtils.getPlugin(), delay, period);
    }

    public static void repeat(Runnable task, long delay, long period, Supplier<Boolean> condition) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!condition.get()) {
                    cancel();
                    return;
                }
                task.run();
            }
        }.runTaskTimer(XUtils.getPlugin(), delay, period);
    }

    public static void repeatAsync(Runnable task, long delay, long period, Supplier<Boolean> condition) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!condition.get()) {
                    cancel();
                    return;
                }
                task.run();
            }
        }.runTaskTimerAsynchronously(XUtils.getPlugin(), delay, period);
    }

    public static void cancelAll() {
        Bukkit.getScheduler().cancelTasks(XUtils.getPlugin());
    }

}
