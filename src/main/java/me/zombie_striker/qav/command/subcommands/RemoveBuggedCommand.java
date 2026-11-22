package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.perms.PermissionHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import me.zombie_striker.qav.util.SchedulerUtil;
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

    private static final double FOLIA_RADIUS = 128;

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
        final Collection<? extends Entity> entities = SchedulerUtil.isFolia()
                // Folia: only the entities of the current region can be accessed
                ? player.getNearbyEntities(FOLIA_RADIUS, FOLIA_RADIUS, FOLIA_RADIUS)
                : player.getWorld().getEntitiesByClass(ArmorStand.class);
        entities.stream()
                .filter(entity -> entity instanceof ArmorStand)
                .filter(QualityArmoryVehicles::isVehicle)
                .filter(entity -> QualityArmoryVehicles.getVehicleEntityByEntity(entity) == null)
                .forEach(entity -> SchedulerUtil.runAtEntity(entity, entity::remove)); // Folia: remove on the owning region

        sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_REMOVE_BUGGED);
    }
}
