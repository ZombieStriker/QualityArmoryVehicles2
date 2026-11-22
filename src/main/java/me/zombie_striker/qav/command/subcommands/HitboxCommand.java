package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.hitbox.HitResult;
import me.zombie_striker.qav.hitbox.HitboxEntities;
import me.zombie_striker.qav.hitbox.HitboxVisualizer;
import me.zombie_striker.qav.hitbox.SeatSelection;
import me.zombie_striker.qav.perms.PermissionHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * /qav hitbox: shows the vehicle hitboxes and seats with particles, and prints the hitbox of the vehicle the player is looking at.
 */
public class HitboxCommand extends SubCommand {

    public HitboxCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "hitbox";
    }

    @Override
    public @Nullable String getDescription(@NotNull CommandSender sender) {
        return sender.hasPermission(PermissionHandler.PERM_DEBUG) ? " : Shows the vehicle hitboxes and seats" : null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission(PermissionHandler.PERM_DEBUG)) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_NO_PERM);
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_ONLY_PLAYERs);
            return;
        }

        Player player = (Player) sender;
        boolean shown = HitboxVisualizer.toggle(player);
        player.sendMessage(Main.prefix + (shown ? " Showing vehicle hitboxes" : " Hiding vehicle hitboxes")
                + ChatColor.GRAY + " (hitbox entities: " + (HitboxEntities.isEnabled() ? "on" : "off") + ")");

        HitResult hit = QualityArmoryVehicles.getVehicleHitPlayerLookingAt(player);
        if (!shown || hit == null) return;

        VehicleEntity ve = hit.getVehicle();
        Vector local = hit.getLocalPoint();
        int seat = hit.getSeat();
        player.sendMessage(ChatColor.GRAY + ve.getType().getName() + (ve.getType().getHitbox().isGenerated() ? " (generated)" : "")
                + ": " + ve.getType().getHitbox().getBoxes());
        player.sendMessage(ChatColor.GRAY + String.format("Looking at x=%.2f y=%.2f z=%.2f, seat: %s", local.getX(), local.getY(), local.getZ(),
                seat == SeatSelection.DRIVER ? "driver" : seat == SeatSelection.NONE ? "none" : "passenger " + seat));
    }
}
