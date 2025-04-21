package me.zombie_striker.qav.input;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import org.bukkit.entity.Player;

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

        Main.foliaLib.getScheduler().runNextTick(task -> {
            handleInput(ve, player, forward, backward, left, right, space, shift);
        });
    }

    public void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }
}
