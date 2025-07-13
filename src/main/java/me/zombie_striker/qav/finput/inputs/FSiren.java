package me.zombie_striker.qav.finput.inputs;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.finput.FInput;
import me.zombie_striker.qav.finput.FInputManager;

import java.util.HashMap;

public class FSiren implements FInput {

    HashMap<VehicleEntity, WrappedTask> storedSirens = new HashMap<>();

    public FSiren() {
        FInputManager.add(this);
    }

    @Override
    public void onInput(VehicleEntity ve) {
        if (storedSirens.containsKey(ve)) {
            storedSirens.remove(ve).cancel();
        } else {
            storedSirens.put(ve, Main.foliaLib.getScheduler().runAtEntityTimer(ve.getModelEntity(), () -> {
                if (ve.isInvalid()) {
                    WrappedTask task = storedSirens.remove(ve);
                    if (task != null) task.cancel();
                    return;
                }

                try {
                    ve.getDriverSeat().getWorld().playSound(ve.getDriverSeat().getLocation(),
                            me.zombie_striker.qg.guns.utils.WeaponSounds.SIREN.getSoundName(), 2, (float) 1);
                } catch (Error | Exception e4) {
                    ve.getDriverSeat().getWorld().playSound(ve.getDriverSeat().getLocation(),
                            "siren", 2, (float) 1);

                }
            }, 0, 20));
        }
    }

    @Override
    public String getName() {
        return FInputManager.POLICE_SIREN;
    }

}
