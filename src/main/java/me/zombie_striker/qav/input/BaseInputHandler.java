package me.zombie_striker.qav.input;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.api.events.PlayerExitQAVehicleEvent;
import me.zombie_striker.qav.util.VehicleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class BaseInputHandler extends PacketAdapter {

    public BaseInputHandler() {
        super(QualityArmoryVehicles.getPlugin(), ListenerPriority.NORMAL, PacketType.Play.Client.STEER_VEHICLE);
    }
    
    // Cleanup method to prevent memory leaks
    public static void cleanupStoppedVehicle(UUID vehicleUUID) {
        if (vehicleStateMap != null && vehicleUUID != null) {
            vehicleStateMap.remove(vehicleUUID);
        }
    }

    @Override
    public void onPacketReceiving(final PacketEvent event) {
        final Player player = event.getPlayer();
        try {
            event.getPlayer().getVehicle();
        } catch (UnsupportedOperationException e) {
            Main.DEBUG("The method getVehicle is not supported for temporary players.");
            return;
        }

        if (player.getVehicle() == null) return;

        VehicleEntity ve = QualityArmoryVehicles.getVehicleEntityByEntity(player.getVehicle());
        if (ve == null)
            return;

        if (!ve.getDriverSeat().equals(player.getVehicle())) return;

        PacketContainer packet = event.getPacket();
        onInputReceived(player, packet);
    }

    public abstract void onInputReceived(Player player, PacketContainer packet);

    // Enum to track the vehicle's movement state for better control
    private enum VehicleState {
        MOVING_FORWARD,    // Vehicle is moving forward (speed > 0)
        STOPPED,           // Vehicle is fully stopped (speed = 0)
        STOPPING,          // Vehicle is slowing down to stop (S pressed while moving forward)
        MOVING_BACKWARD    // Vehicle is moving backward (speed < 0)
    }
    
    // Map to track the state of each vehicle
    private static final Map<UUID, VehicleState> vehicleStateMap = new HashMap<>();
    
    public void handleInput(VehicleEntity ve, Player player, boolean forward, boolean backward, boolean left, boolean right, boolean space, boolean shift) {
        // A and D should turn left and right (fixed direction)
        if (left)
            ve.getType().handleTurnRight(ve, player);
        if (right)
            ve.getType().handleTurnLeft(ve, player);
        
        UUID vehicleId = ve.getVehicleUUID();
        double currentSpeed = ve.getSpeed();
        
        // Initialize vehicle state if not present
        if (!vehicleStateMap.containsKey(vehicleId)) {
            if (currentSpeed > 0) {
                vehicleStateMap.put(vehicleId, VehicleState.MOVING_FORWARD);
            } else if (currentSpeed < 0) {
                vehicleStateMap.put(vehicleId, VehicleState.MOVING_BACKWARD);
            } else {
                vehicleStateMap.put(vehicleId, VehicleState.STOPPED);
            }
        }
        
        VehicleState currentState = vehicleStateMap.get(vehicleId);
        
        // W and S controls with QOL improvement for non-planes
        if (backward) {
            if (ve.getType() instanceof me.zombie_striker.qav.vehicles.AbstractPlane) {
                // For planes, S just decreases speed as usual
                ve.setBackwardMovement(true);
                ve.getType().handleSpeedDecrease(ve, player);
            } else {
                // For non-planes, improved backward behavior based on state
                switch (currentState) {
                    case MOVING_FORWARD:
                        // Vehicle is moving forward, start slowing down
                        ve.setBackwardMovement(false);
                        ve.getType().handleSpeedDecrease(ve, player);
                        
                        // If speed reaches zero, transition to STOPPED
                        if (ve.getSpeed() <= 0) {
                            ve.setSpeed(0); // Force exactly zero
                            vehicleStateMap.put(vehicleId, VehicleState.STOPPED);
                        } else {
                            vehicleStateMap.put(vehicleId, VehicleState.STOPPING);
                        }
                        break;
                        
                    case STOPPING:
                        // Continue slowing down
                        ve.setBackwardMovement(false);
                        ve.getType().handleSpeedDecrease(ve, player);
                        
                        // If speed reaches zero, transition to STOPPED
                        if (ve.getSpeed() <= 0) {
                            ve.setSpeed(0); // Force exactly zero
                            vehicleStateMap.put(vehicleId, VehicleState.STOPPED);
                        }
                        break;
                        
                    case STOPPED:
                        // Simplify reverse trigger - if we're fully stopped and S is pressed, go into reverse
                        // This makes it easier to trigger reverse but still prevents accidental reverse
                        ve.setBackwardMovement(true);
                        ve.getType().handleSpeedDecrease(ve, player);
                        vehicleStateMap.put(vehicleId, VehicleState.MOVING_BACKWARD);
                        break;
                        
                    case MOVING_BACKWARD:
                        // Already moving backward, continue as normal
                        ve.setBackwardMovement(true);
                        ve.getType().handleSpeedDecrease(ve, player);
                        break;
                }
            }
        } else {
            // Player is not pressing S
            ve.setBackwardMovement(false);
            
            // Check for state transitions when S is released
            if (currentState == VehicleState.STOPPING && ve.getSpeed() <= 0) {
                // If we were stopping and now speed is zero
                ve.setSpeed(0);
                vehicleStateMap.put(vehicleId, VehicleState.STOPPED);
            }
        }
        
        if (forward) {
            // Pressing W should always set vehicle to forward movement mode
            if (ve.getSpeed() >= 0 && !ve.getType().getName().contains("plane")) {
                vehicleStateMap.put(vehicleId, VehicleState.MOVING_FORWARD);
            }
            ve.getType().handleSpeedIncrease(ve, player);
        }
        
        // Space bar should do nothing for planes
        if (space && !(ve.getType() instanceof me.zombie_striker.qav.vehicles.AbstractPlane))
            ve.getType().handleSpace(ve, player);
        
        // The client will always dismount on shift, so we can't override it
    }
}
