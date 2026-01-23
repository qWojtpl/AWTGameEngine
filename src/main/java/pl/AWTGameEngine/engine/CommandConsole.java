package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.annotations.Command;
import pl.AWTGameEngine.engine.helpers.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CommandConsole {

    private static final List<ParentCommand> commands = new ArrayList<>();

    public static void execute(String text) {
        String[] split = text.split(" ");
        String typedParentCommand = split[0];
        ParentCommand parentCommand = null;
        for (ParentCommand cmd : commands) {
            if (cmd.commandName.equals(typedParentCommand)) {
                parentCommand = cmd;
                break;
            }
        }
        if(parentCommand == null) {
            Logger.error("Command " + typedParentCommand + " not found.");
            return;
        }
        Object result = executeParent(parentCommand, split);
        if(parentCommand.equals(result)) {
            displayHelp(parentCommand);
            return;
        } else if(result != null) {
            displayObject(result);
        }
    }

    public static Object executeParent(ParentCommand parentCommand, String[] split) {
        if(split.length == 1) {
            return parentCommand;
        }
        String typedCommand = split[1];
        ExecutableCommand executableCommand = null;
        for(ExecutableCommand cmd : parentCommand.executableCommands) {
            if(cmd.command.equals(typedCommand)) {
                executableCommand = cmd;
                break;
            }
        }
        if(executableCommand == null) {
            Logger.error("Subcommand " + typedCommand + " not found.");
            return parentCommand;
        }
        String[] args = new String[executableCommand.arguments.size()];
        for(int i = 2; i < split.length; i++) {
            if(split[i].startsWith("-")) {
                String typedArg = split[i].replace("-", "");
                if(executableCommand.arguments.contains(typedArg)) {
                    int j = 0;
                    for(String arg : executableCommand.arguments) {
                        if(arg.equals(typedArg)) {
                            args[j] = split[i + 1];
                            break;
                        }
                        j++;
                    }
                } else {
                    Logger.error("Argument " + typedArg + " not found.");
                }
            }
        }
        try {
            return executableCommand.method.invoke(parentCommand, (Object[]) args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Logger.exception("Cannot execute command", e);
            return null;
        }
    }

    public static void displayHelp(ParentCommand parentCommand) {
        Logger.info(
                parentCommand.commandName + "'s help page: " + "\n"
                + "\t" + "\n"
        );
    }

    public static void displayObject(Object object) {
        if(object instanceof String || object instanceof Number) {
            Logger.info(object.toString());
            return;
        }
        StringBuilder bobTheBuilder = new StringBuilder();
        bobTheBuilder.append("\n");
        bobTheBuilder.append(object.getClass().getCanonicalName());
        bobTheBuilder.append("\n");
        for(Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                bobTheBuilder.append("\t");
                bobTheBuilder.append(field.getName());
                bobTheBuilder.append(TextUtils.getSpaces(field.getName(), 30));
                bobTheBuilder.append(field.get(object));
                bobTheBuilder.append("\n");
            } catch (IllegalAccessException e) {
                Logger.exception("Cannot get field", e);
            }
        }
        Logger.info(bobTheBuilder.toString());
    }

    public static void register(String parentCommandName, ParentCommand instance) {
        instance.commandName = parentCommandName;
        for(Method method : instance.getClass().getMethods()) {
            if(!method.isAnnotationPresent(Command.class)) {
                continue;
            }
            Command command = method.getAnnotation(Command.class);
            ExecutableCommand executableCommand = new ExecutableCommand();
            executableCommand.command = command.value();
            executableCommand.method = method;
            executableCommand.addArgument(command);
            instance.executableCommands.add(executableCommand);
        }
        commands.add(instance);
    }

    public static void runScanner() {
        new Thread(() ->  {
            while(true) {
                Scanner scanner = new Scanner(System.in);
                execute(scanner.nextLine());
            }
        }).start();
    }

    public static void pass() {
        System.out.print("> ");
    }

    private static class ExecutableCommand {

        String command;
        Method method;
        List<String> arguments = new ArrayList<>();

        void addArgument(Command command) {
            arguments.addAll(Arrays.asList(command.argumentNames()));
        }

    }

    public static abstract class ParentCommand {

        String commandName;
        List<ExecutableCommand> executableCommands = new ArrayList<>();

        public ParentCommand() {
            if(!getClass().isAnnotationPresent(Command.class)) {
                return;
            }
            CommandConsole.register(getClass().getAnnotation(Command.class).value(), this);
        }

    }

}
