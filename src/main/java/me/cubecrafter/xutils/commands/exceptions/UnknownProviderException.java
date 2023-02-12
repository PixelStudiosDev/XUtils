package me.cubecrafter.xutils.commands.exceptions;

public class UnknownProviderException extends Exception {

    public UnknownProviderException(Class<?> clazz) {
        super("Could not find a valid provider for " + clazz.getName());
    }

}
