package dev.pixelstudios.xutils;

import com.cryptomorin.xseries.XSound;
import lombok.experimental.UtilityClass;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

@UtilityClass
public class SoundUtil {

    public static void play(List<Player> players, String sound) {
        XSound.play(sound, x -> x.forPlayers(players));
    }

    public static void play(Player player, String sound) {
        XSound.play(sound, x -> x.forPlayers(player));
    }

    public static void play(Player player, Sound sound) {
        player.playSound(player, sound, 1, 1);
    }

    public static void play(List<Player> players, Sound sound) {
        players.forEach(player -> play(player, sound));
    }

}
