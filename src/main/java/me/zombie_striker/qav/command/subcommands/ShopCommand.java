package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.menu.ShopMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShopCommand extends SubCommand {

    public ShopCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "shop";
    }

    @Override
    public @Nullable String getDescription(@NotNull CommandSender sender) {
        return MessagesConfig.subcommand_Shop;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessagesConfig.COMMANDMESSAGES_ONLY_PLAYERs);
            return;
        }

        new ShopMenu((Player) sender).open();
    }
}
