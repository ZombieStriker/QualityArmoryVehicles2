package me.zombie_striker.qav.command;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.command.subcommands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class QAVCommand implements TabExecutor {
    private final Map<String,SubCommand> subcommands = new HashMap<>();

    public QAVCommand() {
        List<SubCommand> subcommands = new ArrayList<>();

        subcommands.add(new CallbackAllCommand(this));
        subcommands.add(new CallbackCommand(this));
        subcommands.add(new DebugCommand(this));
        subcommands.add(new GarageCommand(this));
        subcommands.add(new GiveCommand(this));
        subcommands.add(new LicenseCommand(this));
        subcommands.add(new ListCommand(this));
        subcommands.add(new ReloadCommand(this));
        subcommands.add(new RemoveBuggedCommand(this));
        subcommands.add(new RemoveCommand(this));
        subcommands.add(new RemoveNearbyCommand(this));
        subcommands.add(new ShopCommand(this));
        subcommands.add(new SpawnCommand(this));

        for (SubCommand subCommand : subcommands) {
            this.subcommands.put(subCommand.getName().toLowerCase(), subCommand);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length > 0) {
            if (subcommands.containsKey(args[0].toLowerCase())) {
                subcommands.get(args[0].toLowerCase()).perform(sender, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }

        sendHelp(sender);
        return true;
    }
    
    public void sendHelp(@NotNull CommandSender player) {
        player.sendMessage(Main.prefix + " Commands");

        for (SubCommand subCommand : subcommands.values()) {
            String desc = subCommand.getDescription(player);
            if (desc != null) {
                player.sendMessage("/QAV " + subCommand.getName() + ChatColor.GRAY + desc);
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final List<String> strings = new ArrayList<>();

        if (args.length == 0) {
            return new ArrayList<>(subcommands.keySet());
        }

        if (args.length == 1) {
            for (SubCommand subCommand : subcommands.values()) {
                if (subCommand.getName().toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT))) strings.add(subCommand.getName());
            }
        }

        if (args.length > 1) {
            if (subcommands.containsKey(args[0].toLowerCase())) {
                strings.addAll(subcommands.get(args[0].toLowerCase()).complete(sender, Arrays.copyOfRange(args, 1, args.length)));
            }
        }

        return strings;
    }
}
