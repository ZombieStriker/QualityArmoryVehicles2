package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.menu.MenuHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GarageCommand extends SubCommand {

    public GarageCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "garage";
    }

    @Override
    public String getDescription(@NotNull CommandSender sender) {
        if (!Main.enableGarage) return null;

        return MessagesConfig.subcommand_garage;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessagesConfig.COMMANDMESSAGES_ONLY_PLAYERs);
            return;
        }

        MenuHandler.openGarage((Player) sender);
    }
}
