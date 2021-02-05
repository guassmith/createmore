package com.guassmith.createmore;

import net.minecraftforge.energy.EnergyStorage;

public class NewEnergyStorage extends EnergyStorage {
    public NewEnergyStorage(int capacity) {
        super(capacity);
    }

    public void setEnergy(int e) {
        this.energy = e;
    }
}
