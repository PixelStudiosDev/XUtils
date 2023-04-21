package me.cubecrafter.xutils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@UtilityClass
public class Tasks {

    public static BukkitTask sync(Runnable task) {
        return Bukkit.getScheduler().runTask(XUtils.getPlugin(), task);
    }

    public static void sync(Consumer<BukkitTask> task) {
        Bukkit.getScheduler().runTask(XUtils.getPlugin(), task);
    }

    public static BukkitTask async(Runnable task) {
        return Bukkit.getScheduler().runTaskAsynchronously(XUtils.getPlugin(), task);
    }

    public static void async(Consumer<BukkitTask> task) {
        Bukkit.getScheduler().runTaskAsynchronously(XUtils.getPlugin(), task);
    }

    public static BukkitTask later(Runnable task, long delay) {
        return Bukkit.getScheduler().runTaskLater(XUtils.getPlugin(), task, delay);
    }

    public static void later(Consumer<BukkitTask> task, long delay) {
        Bukkit.getScheduler().runTaskLater(XUtils.getPlugin(), task, delay);
    }

    public static BukkitTask laterAsync(Runnable task, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(XUtils.getPlugin(), task, delay);
    }

    public static void laterAsync(Consumer<BukkitTask> task, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(XUtils.getPlugin(), task, delay);
    }

    public static BukkitTask repeat(Runnable task, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(XUtils.getPlugin(), task, delay, period);
    }

    public static void repeat(Consumer<BukkitTask> task, long delay, long period) {
        Bukkit.getScheduler().runTaskTimer(XUtils.getPlugin(), task, delay, period);
    }

    public static void repeatTimes(Runnable task, long delay, long period, int times) {
        AtomicInteger timer = new AtomicInteger(times);
        repeat(bukkitTask -> {
            task.run();
            if (timer.decrementAndGet() == 0) {
                bukkitTask.cancel();
            }
        }, delay, period);
    }

    public static BukkitTask repeatAsync(Runnable task, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(XUtils.getPlugin(), task, delay, period);
    }

    public static void repeatAsync(Consumer<BukkitTask> task, long delay, long period) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(XUtils.getPlugin(), task, delay, period);
    }

    public static void repeatAsyncTimes(Runnable task, long delay, long period, int times) {
        AtomicInteger timer = new AtomicInteger(times);
        repeatAsync(bukkitTask -> {
            task.run();
            if (timer.decrementAndGet() == 0) {
                bukkitTask.cancel();
            }
        }, delay, period);
    }

    public static void cancelAll() {
        Bukkit.getScheduler().cancelTasks(XUtils.getPlugin());
    }

}
