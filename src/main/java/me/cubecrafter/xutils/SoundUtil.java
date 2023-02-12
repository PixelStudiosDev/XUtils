package me.cubecrafter.xutils;

import com.cryptomorin.xseries.XSound;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

import java.util.List;

@UtilityClass
public class SoundUtil {

    public static void play(Player player, String sound) {
        play(player, sound, 1, 1);
    }

    public static void play(List<Player> players, String sound) {
        players.forEach(player -> play(player, sound));
    }

    public static void play(Player player, String sound, float volume, float pitch) {
        XSound.matchXSound(sound).ifPresent(xSound -> xSound.play(player, volume, pitch));
    }

    public static void play(List<Player> players, String sound, float volume, float pitch) {
        players.forEach(player -> play(player, sound, volume, pitch));
    }

}
