package me.cubecrafter.xutils;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class Events<T extends Event> implements EventExecutor, Listener {

    private final Consumer<? super T> handler;

    public static <T extends Event> void subscribe(Class<T> eventClass, Consumer<? super T> handler) {
        subscribe(eventClass, handler, EventPriority.NORMAL, false);
    }

    public static <T extends Event> void subscribe(Class<T> eventClass, Consumer<? super T> handler, EventPriority priority) {
        subscribe(eventClass, handler, priority, false);
    }

    public static <T extends Event> void subscribe(Class<T> eventClass, Consumer<? super T> handler, boolean ignoreCancelled) {
        subscribe(eventClass, handler, EventPriority.NORMAL, ignoreCancelled);
    }

    public static <T extends Event> void subscribe(Class<T> eventClass, Consumer<? super T> handler, EventPriority priority, boolean ignoreCancelled) {
        Events<T> executor = new Events<>(handler);
        Bukkit.getPluginManager().registerEvent(eventClass, executor, priority, executor, XUtils.getPlugin(), ignoreCancelled);
    }

    public static boolean call(Event event) {
        Bukkit.getPluginManager().callEvent(event);
        return event instanceof Cancellable && ((Cancellable) event).isCancelled();
    }

    public static void register(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, XUtils.getPlugin());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(Listener listener, Event event) {
        handler.accept((T) event);
    }

}
