package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.customitemmanager.CustomItemManager;
import me.zombie_striker.qav.util.ForksUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResourcePackCommand extends SubCommand {

    public ResourcePackCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "getresourcepack";
    }

    @Override
    public @Nullable String getDescription(@NotNull CommandSender sender) {
        return "Returns the resourcepack link";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        String resourcepack = CustomItemManager.getResourcepack(sender instanceof Player ? (Player) sender : null);
        ForksUtil.sendComponent(sender, Main.prefix + " " + MessagesConfig.COMMANDMESSAGES_TEXTURE, null, resourcepack == null ? "" : resourcepack);
    }
}
