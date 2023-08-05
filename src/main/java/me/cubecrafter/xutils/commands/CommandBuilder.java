package me.cubecrafter.xutils.commands;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@Setter
@Accessors(chain = true, fluent = true)
@RequiredArgsConstructor
public final class CommandBuilder {

    private final String name;

    private final List<CommandWrapper> subCommands = new ArrayList<>();
    private final List<String> aliases = new ArrayList<>();

    private String permission;
    private String description;

    private BiConsumer<CommandSender, String[]> executes;
    private BiConsumer<ConsoleCommandSender, String[]> executesConsole;
    private BiConsumer<Player, String[]> executesPlayer;
    private BiFunction<CommandSender, String[], List<String>> tabComplete;

    public CommandBuilder aliases(String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }

    public CommandBuilder registerSub(CommandWrapper... subCommands) {
        this.subCommands.addAll(Arrays.asList(subCommands));
        return this;
    }

    public CommandWrapper build() {
        return new CommandWrapper(
                name,
                permission,
                description,
                aliases,
                executes,
                executesConsole,
                executesPlayer,
                tabComplete,
                subCommands
        );
    }

    public CommandWrapper register() {
        return CommandManager.get().register(build());
    }

}
