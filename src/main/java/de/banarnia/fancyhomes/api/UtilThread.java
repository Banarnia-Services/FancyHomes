package de.banarnia.fancyhomes.api;

import de.banarnia.fancyhomes.FancyHomes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class UtilThread {

    public static <T> Future<T> runSync(JavaPlugin plugin, Callable<T> callable) {
        if (Bukkit.isPrimaryThread()) {
            try {
                return CompletableFuture.completedFuture(callable.call());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return Bukkit.getScheduler().callSyncMethod(plugin, callable);
    }

    public static Future sendMessageSync(JavaPlugin plugin, Player target, String message) {
        Callable c = () -> {
            target.sendMessage(message);
            return true;
        };
        return runSync(plugin, c);
    }
}
