package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReloadCommand extends SubCommand {

    public ReloadCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public @Nullable String getDescription(@NotNull CommandSender sender) {
        return sender.hasPermission("qualityarmoryvehicles.reload") ? " : Reloads the plugin" : null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("qualityarmoryvehicles.reload")) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_NO_PERM);
            return;
        }

        long time = System.currentTimeMillis();
        QualityArmoryVehicles.getPlugin().initVals();
        Main.loadVehicles(true);
        sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_RELOAD.replace("%time%", String.valueOf(System.currentTimeMillis() - time)));
    }
}
