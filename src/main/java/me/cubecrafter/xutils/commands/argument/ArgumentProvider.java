package me.cubecrafter.xutils.commands.argument;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.xutils.commands.exceptions.CommandArgumentException;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public abstract class ArgumentProvider<T> {

    private final Class<T> type;
    private final Set<Class<? extends Annotation>> annotations = new HashSet<>();

    @SafeVarargs
    public ArgumentProvider(Class<T> type, Class<? extends Annotation>... annotations) {
        this.type = type;
        this.annotations.addAll(Arrays.asList(annotations));
    }

    public abstract boolean isArgumentRequired();

    public abstract T provide(CommandArgs args, List<? extends Annotation> annotations) throws CommandArgumentException;

}
