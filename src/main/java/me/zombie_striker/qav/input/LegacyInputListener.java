package me.zombie_striker.qav.input;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LegacyInputListener extends BaseInputHandler {

    @Override
    public void onInputReceived(Player player, PacketContainer packet) {
        VehicleEntity ve = QualityArmoryVehicles.getVehicleEntityByEntity(player.getVehicle());
        boolean left, right, forward, backward, space, shift;

        left = packet.getFloat().read(0) > 0;
        right = packet.getFloat().read(0) < 0;
        forward = packet.getFloat().read(1) > 0;
        backward = packet.getFloat().read(1) < 0;
        space = packet.getBooleans().read(0);
        shift = packet.getBooleans().read(1);

        new BukkitRunnable() {
            public void run() {
                handleInput(ve, player, forward, backward, left, right, space, shift);
            }
        }.runTaskLater(QualityArmoryVehicles.getPlugin(), 0);
    }

    public void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }
}
