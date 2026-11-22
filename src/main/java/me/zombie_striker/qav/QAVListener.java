package me.zombie_striker.qav;

import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.api.events.VehicleDamageEvent;
import me.zombie_striker.qav.api.events.VehicleDestroyEvent;
import me.zombie_striker.qav.api.events.VehicleRepairEvent;
import me.zombie_striker.qav.hitbox.HitResult;
import me.zombie_striker.qav.hitbox.HitboxEntities;
import me.zombie_striker.qav.hitbox.HitboxRaycast;
import me.zombie_striker.qav.hitbox.HitboxVisualizer;
import me.zombie_striker.qav.hitbox.SeatSelection;
import me.zombie_striker.qav.hitbox.VehicleFrame;
import me.zombie_striker.qav.hitbox.VehicleHitbox;
import me.zombie_striker.qav.hitch.HitchManager;
import me.zombie_striker.qav.hooks.model.Animation;
import me.zombie_striker.qav.menu.OverviewMenu;
import me.zombie_striker.qav.perms.PermissionHandler;
import me.zombie_striker.qav.qamini.ParticleHandlers;
import me.zombie_striker.qav.util.ForksUtil;
import me.zombie_striker.qav.util.SchedulerUtil;
import me.zombie_striker.qav.util.VehicleUtils;
import me.zombie_striker.qav.vehicles.*;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class QAVListener implements Listener {

    private Main main;

    public QAVListener(Main main) {
        this.main = main;
    }

    /** Clicks on a vehicle within this time are one interaction (both hands, entity + item use packets) */
    private static final long INTERACTION_COOLDOWN_NANOS = 150_000_000L;
    private final Map<UUID, Long> lastInteraction = new ConcurrentHashMap<>();

    /**
     * Clicks in the air or on a block: finds the vehicle the player is looking at with the precise hitbox raycast.
     * Needed on versions without hitbox entities, and when the player clicks a vehicle part not covered by them.
     */
    @EventHandler
    public void onClickVehicle(PlayerInteractEvent e) {
        if (e.getPlayer().getVehicle() != null) {
            return;
        }

        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        HitResult hit = QualityArmoryVehicles.getVehicleHitPlayerLookingAt(e.getPlayer());
        if (hit == null) {
            return;
        }

        Main.DEBUG("Detected hitbox interaction.");
        e.setCancelled(true);

        if (!startInteraction(e.getPlayer())) {
            return;
        }

        handleInteraction(e.getPlayer(), getHand(e), hit.getVehicle(), hit.getLocalPoint());
    }

    /**
     * Handles a right click on a vehicle by a player that is not riding one.
     *
     * @param localPoint the clicked point in the vehicle's local space, used to choose the seat. Nullable
     */
    private void handleInteraction(Player player, EquipmentSlot hand, VehicleEntity ve, @Nullable Vector localPoint) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (player.hasPermission("qualityarmoryvehicles.repair") && Main.repairItem.isItem(item)) {
            if (ve.getHealth() >= ve.getType().getMaxHealth()) {
                return;
            }

            VehicleRepairEvent event = new VehicleRepairEvent(player, item, ve);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) return;

            ve.setHealth(ve.getType().getMaxHealth());
            player.sendMessage(Main.prefix + MessagesConfig.MESSAGE_REPAIR);
            return;
        }

        if (tryHitch(player, hand, ve)) {
            return;
        }

        UUID uuid = player.getUniqueId();
        boolean override = player.hasPermission(PermissionHandler.PERM_OVERRIDE_WHITELIST) || VehicleUtils.isOverrideWhitelisted(uuid);
        boolean canDrive = ve.allowUserDriver(uuid) || override;
        boolean canRide = ve.allowUserPassager(uuid) || override;

        if (player.isSneaking() && ve.allowUserDriver(uuid)) {
            if (player.hasPermission(PermissionHandler.PERM_OPEN_VEHICLE_GUI)) {
                new OverviewMenu(player, ve).open();
            } else {
                player.sendMessage(ChatColor.RED + " You do not have permission to use this vehicle.");
            }
            return;
        }

        if (!canRide) {
            return;
        }

        if (!player.hasPermission("qualityarmoryvehicles.use")) {
            player.sendMessage(ChatColor.RED + " You do not have permission to use this vehicle.");
            return;
        }

        int seat = VehicleHitbox.seatAt(ve.getType(), localPoint);
        if (ve.isTrackAutomatic() && ve.getType() instanceof AbstractTrain) {
            // Automatic trains have no driver
            QualityArmoryVehicles.addPlayerToCar(ve, player, false, seat == SeatSelection.DRIVER ? SeatSelection.NONE : seat);
            return;
        }

        QualityArmoryVehicles.addPlayerToCar(ve, player, canDrive, seat);
    }

    /**
     * @return false if the player already interacted with a vehicle in the last few ticks
     */
    private boolean startInteraction(Player player) {
        long now = System.nanoTime();
        Long last = lastInteraction.put(player.getUniqueId(), now);
        return last == null || now - last > INTERACTION_COOLDOWN_NANOS;
    }

    private static EquipmentSlot getHand(PlayerInteractEvent e) {
        try {
            return e.getHand() == null ? EquipmentSlot.HAND : e.getHand();
        } catch (Throwable ignored) {
            return EquipmentSlot.HAND; // 1.8
        }
    }

    private static EquipmentSlot getHand(PlayerInteractEntityEvent e) {
        try {
            return e.getHand();
        } catch (Throwable ignored) {
            return EquipmentSlot.HAND; // 1.8
        }
    }

    /**
     * @return the clicked point in the vehicle's local space, or null if unknown
     */
    private static @Nullable Vector getClickedPoint(PlayerInteractEntityEvent e, VehicleEntity ve) {
        HitResult hit = HitboxRaycast.intersect(ve, e.getPlayer());
        if (hit != null) return hit.getLocalPoint();

        if (e instanceof PlayerInteractAtEntityEvent) {
            VehicleFrame frame = VehicleFrame.of(ve);
            if (frame == null) return null;
            Vector clicked = e.getRightClicked().getLocation().toVector()
                    .add(((PlayerInteractAtEntityEvent) e).getClickedPosition());
            return frame.toLocal(clicked);
        }
        return null;
    }

    @EventHandler
    public void onPlace(PlayerInteractEvent e) {
        if (getHand(e) == EquipmentSlot.OFF_HAND)
            return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            AbstractVehicle vehicle = QualityArmoryVehicles.getVehicleByItem(e.getItem());

            if (vehicle != null) {
                e.setCancelled(true);
                if (e.getPlayer().getVehicle() == null && QualityArmoryVehicles.getVehicleHitPlayerLookingAt(e.getPlayer()) != null) {
                    // Clicked a vehicle, not the block behind it
                    return;
                }

                if (!startInteraction(e.getPlayer())) {
                    return;
                }
                Location spawnLocation = e.getClickedBlock().getRelative(BlockFace.UP).getLocation();

                if (vehicle instanceof AbstractTrain) {
                    // Train placement should use rail level; spawn routine handles its own offset.
                    spawnLocation = e.getClickedBlock().getLocation();
                }

                VehicleEntity ve = QualityArmoryVehicles.spawnVehicle(vehicle, spawnLocation, e.getPlayer());

                if (ve == null) {
                    return;
                }

                if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                }
            }
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if (!ForksUtil.isFlyKick(event)) return;
        if (event.getPlayer().getVehicle() == null) return;
        if (!QualityArmoryVehicles.isVehicle(event.getPlayer().getVehicle())) return;

        event.setCancelled(true);
        Main.DEBUG("Cancelled kick event for flying because player is on plane.");
    }

    @EventHandler
    public void oninteractEntity(PlayerInteractEntityEvent e) {
        Entity clicked = e.getRightClicked();
        boolean hitboxEntity = HitboxEntities.isHitboxEntity(clicked);

        VehicleEntity ve = QualityArmoryVehicles.getVehicleEntityByEntity(clicked);
        if (ve == null && clicked instanceof Player && clicked.getVehicle() != null) {
            ve = QualityArmoryVehicles.getVehicleEntityByEntity(clicked.getVehicle());
        }

        if (ve == null) {
            if (hitboxEntity) {
                // Leftover hitbox of a removed vehicle
                e.setCancelled(true);
                SchedulerUtil.runAtEntity(clicked, clicked::remove);
            }
            return;
        }

        if (e.getPlayer().getVehicle() != null) {
            // Riding players are handled by onManipulate
            if (hitboxEntity) e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
        if (!startInteraction(e.getPlayer())) {
            return;
        }

        handleInteraction(e.getPlayer(), getHand(e), ve, getClickedPoint(e, ve));
    }

    @EventHandler
    public void onManipulate(PlayerArmorStandManipulateEvent e) {
        VehicleEntity ve = null;
        if (QualityArmoryVehicles.isPassager(e.getRightClicked())) {

            for (VehicleEntity ve2 : Main.vehicles) {
                if (ve2.getPassagers().containsValue(e.getRightClicked())) {
                    ve = ve2;
                    break;
                }
            }

        } else if (QualityArmoryVehicles.isVehicle(e.getRightClicked())) {
            ve = QualityArmoryVehicles.getVehicleEntityByEntity(e.getRightClicked());
        }

        if (ve == null) {
            return;
        }

        e.setCancelled(true);
        if (tryHitch(e.getPlayer(), e.getHand(), ve)) {
            return;
        }
        boolean trackAuto = ve.isTrackAutomatic() && ve.getType() instanceof AbstractTrain;
        if (ve.allowUserPassager(e.getPlayer().getUniqueId())) {
            if (e.getPlayer().isSneaking() && ve.allowUserDriver(e.getPlayer().getUniqueId())) {
                if (e.getPlayer().hasPermission(PermissionHandler.PERM_OPEN_VEHICLE_GUI)) {
                    new OverviewMenu(e.getPlayer(), ve).open();
                } else {
                    e.getPlayer()
                            .sendMessage(ChatColor.RED + " You do not have permission to use this vehicle.");
                }
            } else {
                if (e.getPlayer().hasPermission("qualityarmoryvehicles.use")) {
                    QualityArmoryVehicles.addPlayerToCar(ve, e.getPlayer(),
                            !trackAuto && ve.allowUserDriver(e.getPlayer().getUniqueId()));
                } else {
                    e.getPlayer()
                            .sendMessage(ChatColor.RED + " You do not have permission to use this vehicle.");
                }
            }
        } else if (e.getPlayer().hasPermission(PermissionHandler.PERM_OVERRIDE_WHITELIST) || VehicleUtils.isOverrideWhitelisted(e.getPlayer().getUniqueId())) {
            if (!trackAuto && ve.getDriverSeat().getPassenger() == null) {
                if (e.getPlayer().hasPermission("qualityarmoryvehicles.use")) {
                    ve.getDriverSeat().setPassenger(e.getPlayer());
                } else {
                    e.getPlayer().sendMessage(ChatColor.RED + " You do not have permission to use this vehicle.");
                }

                return;
            }

            QualityArmoryVehicles.addPlayerToCar(ve, e.getPlayer(),
                    !trackAuto && ve.allowUserDriver(e.getPlayer().getUniqueId()));
        }
    }

    /**
     * Lets players hitch/unhitch animals by right-clicking a carriage (not sneaking, so the overview menu still opens).
     */
    private boolean tryHitch(Player player, EquipmentSlot hand, VehicleEntity ve) {
        if (!(ve.getType() instanceof AbstractCarriage) || player.isSneaking()) return false;
        if (hand == EquipmentSlot.OFF_HAND) return false;
        return HitchManager.handleInteract(player, ve);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!Main.enableVehiclePlayerCollision) {
            return;
        }

        // Riders move with their vehicle and lag a tick behind its hitbox: cancelling their move teleports them back,
        // which dismounts them
        if (e.getPlayer().getVehicle() != null) {
            return;
        }

        for (Entity ent : e.getPlayer().getNearbyEntities(10, 10, 10)) {
            if (QualityArmoryVehicles.isVehicle(ent)) {
                VehicleEntity ve = QualityArmoryVehicles.getVehicleEntityByEntity(ent);
                if (ve == null) {
                    return;
                }

                if (QualityArmoryVehicles.isWithinVehicle(e.getTo(), ve)
                        && !QualityArmoryVehicles.isWithinVehicle(e.getFrom(), ve)) {
                    if (e.getTo().getX() != e.getFrom().getX() || e.getTo().getZ() != e.getFrom().getZ()) {
                        if (e.getPlayer().getVelocity().getY() < -0.05) {
                            e.getPlayer().setVelocity(e.getPlayer().getVelocity().setY(0.3));
                        }
                    }
                    e.setCancelled(true);
                    break;

                } else {
                    Location to = e.getTo().clone().add(0, 1, 0);
                    Location from = e.getFrom().clone().add(0, 1, 0);
                    if (QualityArmoryVehicles.isWithinVehicle(to, ve)
                            && !QualityArmoryVehicles.isWithinVehicle(from, ve)) {
                        if (e.getTo().getX() != e.getFrom().getX() || e.getTo().getZ() != e.getFrom().getZ()) {
                            if (e.getPlayer().getVelocity().getY() < -0.05) {
                                e.getPlayer().setVelocity(e.getPlayer().getVelocity().setY(0.3));
                            }
                        }
                        e.setCancelled(true);
                        break;

                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDamage(EntityDamageEvent e) {
        handleDamage(e, e.getEntity(), e.getDamage(), e.getCause(), e::setDamage);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        lastInteraction.remove(event.getPlayer().getUniqueId());
        HitboxEntities.onQuit(event.getPlayer().getUniqueId());
        HitboxVisualizer.remove(event.getPlayer().getUniqueId());

        if (Main.removeVehicleONLEAVE || Main.destroyVehicleONLEAVE) {
            List<VehicleEntity> vehicles = QualityArmoryVehicles.getOwnedVehicles(event.getPlayer().getUniqueId());
            for (VehicleEntity vehicle : vehicles) {
                if (Main.destroyVehicleONLEAVE)
                    vehicle.deconstruct(event.getPlayer(), "Quit");
                else
                    VehicleUtils.callback(vehicle, event.getPlayer(), "Quit");
            }
        }

        Entity vehicle = event.getPlayer().getVehicle();
        if (QualityArmoryVehicles.isVehicle(vehicle) || QualityArmoryVehicles.isPassager(vehicle)) {
            VehicleEntity entity = QualityArmoryVehicles.getVehicleEntityByEntity(vehicle);
            if (entity != null) {
                if (vehicle.equals(entity.getDriverSeat())) {
                    entity.getDriverSeat().eject();
                }

                if (entity.getPassagerSeats().contains(vehicle)) {
                    vehicle.eject();
                    vehicle.remove();
                }
            }
        }
    }

    public static void handleDamage(Cancellable e, Entity entity, double damage, EntityDamageEvent.DamageCause cause, Consumer<Double> setDamage) {
        if (HitboxEntities.isHitboxEntity(entity)) {
            e.setCancelled(true);
            VehicleEntity owner = HitboxEntities.getOwner(entity);
            // Punching the hitbox does nothing (like the invisible model), weapons damage the vehicle
            if (owner == null || owner.getDriverSeat() == null || cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                    || cause.name().equals("ENTITY_SWEEP_ATTACK")) {
                return;
            }
            entity = owner.getDriverSeat();
        }

        if (entity.getVehicle() != null && QualityArmoryVehicles.isVehicle(entity.getVehicle()) || QualityArmoryVehicles.isPassager(entity.getVehicle())) {
            VehicleEntity ve = QualityArmoryVehicles.getVehicleEntityByEntity(entity.getVehicle());
            if (ve != null && ve.getType() != null)
                if ((ve.getType() instanceof AbstractHelicopter || ve.getType() instanceof AbstractPlane
                        || ve.getType() instanceof AbstractCar) && cause == EntityDamageEvent.DamageCause.FALL) {
                    e.setCancelled(true);
                    return;
                }
        }

        VehicleEntity ve = null;
        // Get the vehicle
        if (QualityArmoryVehicles.isVehicle(entity)) {
            ve = QualityArmoryVehicles.getVehicleEntityByEntity(entity);
        } else if (entity.getVehicle() != null && QualityArmoryVehicles.isVehicle(entity.getVehicle())) {
            ve = QualityArmoryVehicles.getVehicleEntityByEntity(entity.getVehicle());
        }

        if (ve == null) {
            return;
        }

        if (!Main.enableVehicleDamage) {
            e.setCancelled(true);
            return;
        }

        if (cause == EntityDamageEvent.DamageCause.SUFFOCATION || cause == EntityDamageEvent.DamageCause.DROWNING
                || cause == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
            return;
        }

        if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK && e instanceof EntityDamageByEntityEvent) {
            if (((EntityDamageByEntityEvent) e).getDamager().getType() == EntityType.ENDERMITE) {
                e.setCancelled(true);
                return;
            }
        }

        VehicleDamageEvent vde = new VehicleDamageEvent(ve, damage);
        Bukkit.getPluginManager().callEvent(vde);

        if (vde.isCanceled()) {
            e.setCancelled(true);
            return;
        }

        Main.DEBUG("Damaged vehicle: " + vde.getDamage() + " || Health= " + ve.getHealth() + " || Cause= "
                + cause.name());

        setDamage.accept(vde.getDamage());
        ve.setHealth((float) (ve.getHealth() - vde.getDamage()));
        e.setCancelled(true);

        try {
            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1);
        } catch (Error | Exception e4) {
            try {
                entity.getWorld().playSound(entity.getLocation(), Sound.valueOf("HURT"), 1f, 1);
            } catch (Error | Exception ignored) {
            }
        }

        if (ve.getHealth() <= 0) {

            VehicleDestroyEvent vehicleDestroyEvent = new VehicleDestroyEvent(ve);
            Bukkit.getPluginManager().callEvent(vehicleDestroyEvent);

            if (vehicleDestroyEvent.isCanceled()) {
                e.setCancelled(true);
                return;
            }

            if (!Main.freezeOnDestroy) {
                ve.deconstruct(null, "Destroy");
            }

            ve.getType().playAnimation(ve, Animation.AnimationType.BREAK);

            try {
                ParticleHandlers.spawnMushroomCloud(entity.getLocation());
            } catch (Error | Exception ignored) {
            }

            try {
                entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.5f,
                        1);
            } catch (Error | Exception e4) {
                try {
                    entity.getWorld().playSound(entity.getLocation(), Sound.valueOf("EXPLODE"), 2.5f,
                            1);
                } catch (Error | Exception ignored) {
                }
            }
        }
    }
}
