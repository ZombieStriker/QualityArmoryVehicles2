package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.perms.PermissionHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class RemoveBuggedCommand extends SubCommand {

    public RemoveBuggedCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "removeBugged";
    }

    @Override
    public @Nullable String getDescription(@NotNull CommandSender sender) {
        if (sender.hasPermission(PermissionHandler.PERM_REMOVEDBUGGED)) {
            return " : Removes all bugged vehicles";
        }

        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission(PermissionHandler.PERM_REMOVEDBUGGED)) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_NO_PERM);
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_ONLY_PLAYERs);
            return;
        }

        Player player = (Player) sender;
        final Collection<ArmorStand> entities = player.getWorld().getEntitiesByClass(ArmorStand.class);
        entities.stream()
                .filter(QualityArmoryVehicles::isVehicle)
                .filter(entity -> QualityArmoryVehicles.getVehicleEntityByEntity(entity) == null)
                .forEach(Entity::remove);

        sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_REMOVE_BUGGED);
    }
}
