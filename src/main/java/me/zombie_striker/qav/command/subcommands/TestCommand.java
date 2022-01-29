package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.hooks.ModelEngineHook;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestCommand extends SubCommand {

    public TestCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public @Nullable String getDescription(@NotNull CommandSender sender) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        sender.sendMessage(QualityArmoryVehicles.getVehicleEntityByEntity(((Player) sender).getVehicle()).toString());
    }
}
