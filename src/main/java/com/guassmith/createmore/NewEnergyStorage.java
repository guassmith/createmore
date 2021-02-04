package com.guassmith.createmore;

import net.minecraftforge.energy.EnergyStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NewEnergyStorage extends EnergyStorage {
    public NewEnergyStorage(int capacity) {
        super(capacity);
    }

    public void setEnergy(int e) {
        this.energy = e;
    }
}
