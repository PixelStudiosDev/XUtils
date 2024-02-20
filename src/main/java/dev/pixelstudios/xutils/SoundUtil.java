package dev.pixelstudios.xutils;

import com.cryptomorin.xseries.XSound;
import lombok.experimental.UtilityClass;
import org.bukkit.Sound;
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
        if (sound == null || sound.isEmpty()) {
            return;
        }
        XSound.matchXSound(sound).ifPresent(xSound -> xSound.play(player, volume, pitch));
    }

    public static void play(List<Player> players, String sound, float volume, float pitch) {
        players.forEach(player -> play(player, sound, volume, pitch));
    }

    public static void play(Player player, XSound sound, float volume, float pitch) {
        sound.play(player, volume, pitch);
    }

    public static void play(List<Player> players, XSound sound, float volume, float pitch) {
        players.forEach(player -> play(player, sound, volume, pitch));
    }

    public static void play(Player player, XSound sound) {
        play(player, sound, 1, 1);
    }

    public static void play(List<Player> players, XSound sound) {
        players.forEach(player -> play(player, sound));
    }

    public static void play(Player player, Sound sound) {
        player.playSound(player, sound, 1, 1);
    }

    public static void play(List<Player> players, Sound sound) {
        players.forEach(player -> play(player, sound));
    }

}
