package me.zombie_striker.qav.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand {

    protected QAVCommand command;

    public SubCommand(QAVCommand command) {
        this.command = command;
    }

    public abstract String getName();
    public abstract @Nullable String getDescription(@NotNull CommandSender sender);

    public QAVCommand getCommand() {
        return command;
    }

    public abstract void perform(CommandSender sender, String[] args);
    public List<String> complete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    public void help(CommandSender sender) {
        if (this.getDescription(sender) == null) return;

        sender.sendMessage("/QAV " + this.getName() + ChatColor.GRAY + this.getDescription(sender));
    }

    @Override
    public String toString() {
        return getName();
    }
}
