package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.premium.PremiumHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LicenseCommand extends SubCommand {

    public LicenseCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "license";
    }

    @Override
    public @Nullable String getDescription(@NotNull CommandSender sender) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        PremiumHandler.sendMessage(sender);
    }
}
