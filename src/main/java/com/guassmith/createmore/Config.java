package com.guassmith.createmore;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;

public class Config {

    private static final Builder BUILDER = new Builder();
    public static final ElectricMotorConfig ELECTRIC_MOTOR = new ElectricMotorConfig(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();

    public static class ElectricMotorConfig {
        public final ConfigValue<Double> stressCapacity;
        public final ConfigValue<Integer> energyCapacity;
        public final ConfigValue<Integer> energyUsage;
        public final ConfigValue<Integer> defaultSpeed;
        public final ConfigValue<Integer> minSpeed;
        public final ConfigValue<Integer> maxSpeed;

        public ElectricMotorConfig(Builder builder) {
            builder.push("Electric Motor");
            stressCapacity = builder
                .comment("Stress capacity of the electric motor (this is multiplied by the rpm)")
                .define("electric_motor_stressCapacity", 1024.d);
            energyCapacity = builder
                .comment("Sets the Reach of the Torcher [0..50|default:20]")
                .define("electric_motor_energyCapacity", 10000);
            energyUsage = builder
                .comment("How much energy the electric motor uses per tick (multiplied by rpm)")
                .define("electric_motor_energyUsage", 10);
            defaultSpeed = builder
                .comment("The speed of an electric motor when it's first placed down")
                .define("electric_motor_defaultSpeed",  16);
            minSpeed = builder
                .comment("The minimum rotation speed of an electric motor")
                .define("electric_motor_minSpeed", -256);
            maxSpeed = builder
                .comment("The maximum rotation speed of an electric motor")
                .define("electric_motor_maxSpeed", 256);
            builder.pop();

        }
    }
}
