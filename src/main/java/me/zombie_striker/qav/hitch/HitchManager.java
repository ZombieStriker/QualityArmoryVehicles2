package me.zombie_striker.qav.hitch;

import com.cryptomorin.xseries.XAttribute;
import com.cryptomorin.xseries.XMaterial;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.api.events.VehicleHitchEvent;
import me.zombie_striker.qav.api.events.VehicleUnhitchEvent;
import me.zombie_striker.qav.perms.PermissionHandler;
import me.zombie_striker.qav.util.VehicleUtils;
import me.zombie_striker.qav.vehicles.AbstractCarriage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Handles hitching animals (horses, donkeys, mules...) to {@link AbstractCarriage carriages}.
 * <p>
 * While hitched, an animal has its AI disabled and is teleported every tick to its hitch point.
 * Its UUID is stored in the {@link VehicleEntity} so the link survives restarts.
 */
public final class HitchManager {
    public static final String HITCHED_TAG = "qav_hitched";

    private static final int MAX_MISSING_TICKS = 20 * 10;
    private static final double LEASH_RANGE = 10;
    private static final double DEFAULT_SPEED = 0.225;
    private static final double DEFAULT_JUMP = 0.7;
    private static final boolean SUPPORTED = isClassPresent("org.bukkit.entity.AbstractHorse");

    private static final long INTERACT_COOLDOWN = 250;
    private static final Map<UUID, Long> lastInteract = new HashMap<>();

    private HitchManager() {
    }

    public static boolean isSupported() {
        return SUPPORTED;
    }

    /**
     * Handles a player right-clicking a vehicle.
     *
     * @return true if the interaction has been consumed by the hitching system
     */
    public static boolean handleInteract(@NotNull Player player, @NotNull VehicleEntity ve) {
        if (!(ve.getType() instanceof AbstractCarriage)) return false;
        AbstractCarriage carriage = (AbstractCarriage) ve.getType();

        List<LivingEntity> leashed = SUPPORTED ? getLeashedAnimals(player, ve, carriage) : Collections.emptyList();
        boolean holdingLead = isLead(player.getInventory().getItemInMainHand());
        if (leashed.isEmpty() && !holdingLead) return false;

        // A single click can fire several interact events: only handle the first one
        long now = System.currentTimeMillis();
        Long last = lastInteract.put(player.getUniqueId(), now);
        if (last != null && now - last < INTERACT_COOLDOWN) return true;

        if (!SUPPORTED) {
            send(player, MessagesConfig.MESSAGE_HITCH_UNSUPPORTED);
            return true;
        }

        if (!player.hasPermission(PermissionHandler.PERM_HITCH)) {
            send(player, MessagesConfig.MESSAGE_HITCH_NO_PERMISSION);
            return true;
        }

        boolean override = player.hasPermission(PermissionHandler.PERM_OVERRIDE_WHITELIST)
                || VehicleUtils.isOverrideWhitelisted(player.getUniqueId());
        if (!override && !ve.allowUserDriver(player.getUniqueId())) {
            send(player, MessagesConfig.MESSAGE_HITCH_NOT_ALLOWED);
            return true;
        }

        if (!leashed.isEmpty()) {
            hitchAll(player, ve, carriage, leashed, override);
        } else if (!ve.getHitchedAnimals().isEmpty()) {
            unhitchAll(ve, player);
        } else {
            send(player, MessagesConfig.MESSAGE_HITCH_NONE_NEARBY);
        }
        return true;
    }

    private static void hitchAll(Player player, VehicleEntity ve, AbstractCarriage carriage, List<LivingEntity> animals, boolean override) {
        int count = 0;
        for (LivingEntity animal : animals) {
            if (animal instanceof Tameable) {
                Tameable tameable = (Tameable) animal;
                if (!tameable.isTamed()) {
                    send(player, MessagesConfig.MESSAGE_HITCH_NOT_TAMED);
                    continue;
                }
                AnimalTamer owner = tameable.getOwner();
                if (!override && (owner == null || !player.getUniqueId().equals(owner.getUniqueId()))) {
                    send(player, MessagesConfig.MESSAGE_HITCH_NOT_OWNER);
                    continue;
                }
            }

            Integer slot = getFreeHitchPoint(ve, carriage);
            if (slot == null) {
                send(player, MessagesConfig.MESSAGE_HITCH_FULL);
                break;
            }

            VehicleHitchEvent event = new VehicleHitchEvent(ve, animal, player, slot);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) continue;

            animal.setLeashHolder(null);
            ve.getHitchedAnimals().put(slot, animal.getUniqueId());
            ve.getHitchedAnimalCache().put(slot, animal);
            ve.getHitchedMissingTicks().remove(slot);
            applyHitchedState(animal);
            count++;
        }

        if (count > 0) {
            send(player, MessagesConfig.MESSAGE_HITCH_HITCHED
                    .replace("%amount%", String.valueOf(count))
                    .replace("%total%", String.valueOf(ve.getHitchedAnimals().size()))
                    .replace("%max%", String.valueOf(carriage.getHitchPoints().size())));
        }
    }

    /**
     * Unhitches every animal from the vehicle, leashing them to the player when possible.
     */
    public static void unhitchAll(@NotNull VehicleEntity ve, @Nullable Player player) {
        int count = 0;
        for (Map.Entry<Integer, UUID> entry : new ArrayList<>(ve.getHitchedAnimals().entrySet())) {
            int slot = entry.getKey();
            Entity entity = resolve(ve, slot);

            if (entity instanceof LivingEntity && entity.isValid()) {
                LivingEntity animal = (LivingEntity) entity;
                VehicleUnhitchEvent event = new VehicleUnhitchEvent(ve, animal, player, slot);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled() && player != null) continue;

                release(animal);
                if (player != null && player.getWorld().equals(animal.getWorld())
                        && player.getLocation().distance(animal.getLocation()) <= LEASH_RANGE) {
                    animal.setLeashHolder(player);
                } else {
                    dropLead(animal.getLocation());
                }
                count++;
            }

            removeSlot(ve, slot);
        }

        if (player != null) {
            send(player, MessagesConfig.MESSAGE_HITCH_UNHITCHED.replace("%amount%", String.valueOf(count)));
        }
    }

    /**
     * Releases every hitched animal. Called when the vehicle is despawned.
     *
     * @param disabling when true (plugin shutdown) the link is kept so it can be restored on the next start
     */
    public static void releaseAll(@NotNull VehicleEntity ve, boolean disabling) {
        if (ve.getHitchedAnimals().isEmpty()) return;

        for (Map.Entry<Integer, UUID> entry : new ArrayList<>(ve.getHitchedAnimals().entrySet())) {
            Entity entity = ve.getHitchedAnimalCache().get(entry.getKey());
            if (entity == null && SUPPORTED) entity = findEntity(entry.getValue(), null);
            if (!(entity instanceof LivingEntity) || !entity.isValid()) continue;

            LivingEntity animal = (LivingEntity) entity;
            if (!disabling) {
                Bukkit.getPluginManager().callEvent(new VehicleUnhitchEvent(ve, animal, null, entry.getKey()));
                dropLead(animal.getLocation());
            }
            release(animal);
        }

        ve.getHitchedAnimalCache().clear();
        ve.getHitchedMissingTicks().clear();
        if (!disabling) ve.getHitchedAnimals().clear();
    }

    /**
     * Moves the hitched animals to their hitch points. Called every tick by the carriage.
     */
    public static void tick(@NotNull VehicleEntity ve) {
        if (!SUPPORTED || ve.getHitchedAnimals().isEmpty()) return;
        if (!(ve.getType() instanceof AbstractCarriage)) return;

        AbstractCarriage carriage = (AbstractCarriage) ve.getType();
        Entity seat = ve.getDriverSeat();
        if (seat == null) return;

        for (Map.Entry<Integer, UUID> entry : new ArrayList<>(ve.getHitchedAnimals().entrySet())) {
            int slot = entry.getKey();

            if (slot >= carriage.getHitchPoints().size()) {
                // The hitch point has been removed from the config
                Entity entity = resolve(ve, slot);
                if (entity instanceof LivingEntity && entity.isValid()) {
                    release((LivingEntity) entity);
                    dropLead(entity.getLocation());
                }
                removeSlot(ve, slot);
                continue;
            }

            Entity entity = resolve(ve, slot);
            if (entity == null) {
                int missing = ve.getHitchedMissingTicks().merge(slot, 1, Integer::sum);
                if (missing > MAX_MISSING_TICKS) {
                    Main.DEBUG("Hitched animal " + entry.getValue() + " not found, unhitching it.");
                    removeSlot(ve, slot);
                }
                continue;
            }
            ve.getHitchedMissingTicks().remove(slot);

            if (entity.isDead()) {
                handleLost(ve, slot);
                continue;
            }

            if (!entity.isValid()) {
                // Unloaded, try again later
                ve.getHitchedAnimalCache().remove(slot);
                continue;
            }

            Location target = seat.getLocation()
                    .add(QualityArmoryVehicles.rotateRelToCar(ve, seat, carriage.getHitchPoints().get(slot), false))
                    .add(seat.getVelocity());
            Vector direction = ve.getDirection().clone().setY(0);
            if (direction.lengthSquared() > 0) target.setDirection(direction);
            target.setPitch(0);
            entity.teleport(target);
        }
    }

    /**
     * Called when a hitched animal dies.
     */
    public static void handleDeath(@NotNull Entity entity) {
        VehicleEntity ve = getVehicleByAnimal(entity);
        if (ve == null) return;

        for (Map.Entry<Integer, UUID> entry : new ArrayList<>(ve.getHitchedAnimals().entrySet())) {
            if (entry.getValue().equals(entity.getUniqueId())) {
                handleLost(ve, entry.getKey());
            }
        }
    }

    private static void handleLost(VehicleEntity ve, int slot) {
        removeSlot(ve, slot);

        Entity driver = ve.getDriverSeat() == null ? null : ve.getDriverSeat().getPassenger();
        if (driver instanceof Player) {
            send((Player) driver, MessagesConfig.MESSAGE_HITCH_ANIMAL_LOST);
        }
    }

    /**
     * @return the vehicle the given animal is hitched to, or null
     */
    public static @Nullable VehicleEntity getVehicleByAnimal(@NotNull Entity entity) {
        UUID uuid = entity.getUniqueId();
        for (VehicleEntity ve : Main.vehicles) {
            if (ve != null && ve.getHitchedAnimals().containsValue(uuid)) return ve;
        }
        return null;
    }

    public static boolean isHitched(@NotNull Entity entity) {
        return getVehicleByAnimal(entity) != null;
    }

    /**
     * @return the hitched animals that are currently loaded and alive
     */
    public static @NotNull List<LivingEntity> getHitchedAnimals(@NotNull VehicleEntity ve) {
        List<LivingEntity> animals = new ArrayList<>();
        if (!SUPPORTED) return animals;

        for (Integer slot : ve.getHitchedAnimals().keySet()) {
            Entity entity = ve.getHitchedAnimalCache().get(slot);
            if (entity instanceof LivingEntity && entity.isValid()) {
                animals.add((LivingEntity) entity);
            }
        }
        return animals;
    }

    public static boolean hasHitchedAnimals(@NotNull VehicleEntity ve) {
        return !getHitchedAnimals(ve).isEmpty();
    }

    /**
     * @return the average movement speed attribute of the hitched animals, 0 if none are hitched
     */
    public static double getAverageSpeed(@NotNull VehicleEntity ve) {
        List<LivingEntity> animals = getHitchedAnimals(ve);
        if (animals.isEmpty()) return 0;

        double total = 0;
        for (LivingEntity animal : animals) {
            total += getAttribute(animal, XAttribute.MOVEMENT_SPEED, DEFAULT_SPEED);
        }
        return total / animals.size();
    }

    /**
     * @return the average jump strength of the hitched animals, 0 if none are hitched
     */
    public static double getAverageJumpStrength(@NotNull VehicleEntity ve) {
        List<LivingEntity> animals = getHitchedAnimals(ve);
        if (animals.isEmpty()) return 0;

        double total = 0;
        for (LivingEntity animal : animals) {
            if (animal instanceof AbstractHorse) {
                total += ((AbstractHorse) animal).getJumpStrength();
            } else {
                total += getAttribute(animal, XAttribute.JUMP_STRENGTH, DEFAULT_JUMP);
            }
        }
        return total / animals.size();
    }

    /**
     * Restores an animal that still carries the hitched tag but is no longer linked to any vehicle
     * (e.g. the vehicle data was lost).
     *
     * @return true if the animal has been restored
     */
    public static boolean cleanupOrphan(@NotNull Entity entity) {
        if (!SUPPORTED || !(entity instanceof LivingEntity)) return false;
        if (!entity.getScoreboardTags().contains(HITCHED_TAG)) return false;
        if (isHitched(entity)) return false;

        release((LivingEntity) entity);
        return true;
    }

    private static double getAttribute(LivingEntity entity, XAttribute attribute, double fallback) {
        try {
            Attribute bukkit = attribute.get();
            if (bukkit == null) return fallback;
            AttributeInstance instance = entity.getAttribute(bukkit);
            return instance == null ? fallback : instance.getValue();
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static List<LivingEntity> getLeashedAnimals(Player player, VehicleEntity ve, AbstractCarriage carriage) {
        List<LivingEntity> result = new ArrayList<>();
        Location center = ve.getCenter();
        double radius = carriage.getHitchRadius();

        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (!(entity instanceof LivingEntity)) continue;
            LivingEntity living = (LivingEntity) entity;
            if (!living.isLeashed()) continue;
            if (!carriage.getAllowedAnimals().contains(entity.getType().name())) continue;

            try {
                if (!player.equals(living.getLeashHolder())) continue;
            } catch (IllegalStateException ignored) {
                continue;
            }

            if (isHitched(living)) continue;
            result.add(living);
        }
        return result;
    }

    private static @Nullable Integer getFreeHitchPoint(VehicleEntity ve, AbstractCarriage carriage) {
        for (int i = 0; i < carriage.getHitchPoints().size(); i++) {
            if (!ve.getHitchedAnimals().containsKey(i)) return i;
        }
        return null;
    }

    private static @Nullable Entity resolve(VehicleEntity ve, int slot) {
        UUID uuid = ve.getHitchedAnimals().get(slot);
        if (uuid == null) return null;

        Entity cached = ve.getHitchedAnimalCache().get(slot);
        if (cached != null && uuid.equals(cached.getUniqueId()) && (cached.isValid() || cached.isDead())) {
            return cached;
        }

        Entity found = findEntity(uuid, ve.getDriverSeat() == null ? null : ve.getCenter());
        if (found != null) {
            ve.getHitchedAnimalCache().put(slot, found);
            if (found instanceof LivingEntity && found.isValid()) {
                applyHitchedState((LivingEntity) found);
            }
        }
        return found;
    }

    private static @Nullable Entity findEntity(UUID uuid, @Nullable Location near) {
        try {
            return Bukkit.getEntity(uuid);
        } catch (NoSuchMethodError ignored) {
            // Bukkit#getEntity is not available on very old versions
        }

        if (near == null || near.getWorld() == null) return null;
        for (Entity entity : near.getWorld().getNearbyEntities(near, 16, 16, 16)) {
            if (entity.getUniqueId().equals(uuid)) return entity;
        }
        return null;
    }

    private static void removeSlot(VehicleEntity ve, int slot) {
        ve.getHitchedAnimals().remove(slot);
        ve.getHitchedAnimalCache().remove(slot);
        ve.getHitchedMissingTicks().remove(slot);
    }

    private static void applyHitchedState(LivingEntity animal) {
        animal.eject();
        animal.setAI(false);
        try {
            animal.setCollidable(false);
        } catch (Throwable ignored) {
        }
        animal.addScoreboardTag(HITCHED_TAG);
    }

    private static void release(LivingEntity animal) {
        animal.setAI(true);
        try {
            animal.setCollidable(true);
        } catch (Throwable ignored) {
        }
        animal.removeScoreboardTag(HITCHED_TAG);
    }

    private static void dropLead(Location location) {
        ItemStack lead = XMaterial.LEAD.parseItem();
        if (lead != null && location.getWorld() != null) {
            location.getWorld().dropItemNaturally(location, lead);
        }
    }

    private static boolean isLead(@Nullable ItemStack item) {
        return item != null && XMaterial.LEAD.isSimilar(item);
    }

    private static void send(Player player, String message) {
        player.sendMessage(Main.prefix + message);
    }

    private static boolean isClassPresent(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
