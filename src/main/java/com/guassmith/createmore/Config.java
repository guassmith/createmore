package com.guassmith.createmore;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ElectricMotorConfig ELECTRIC_MOTOR = new ElectricMotorConfig(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();

    public static class ElectricMotorConfig {
        public final ForgeConfigSpec.ConfigValue<Double> stressCapacity;
        public final ForgeConfigSpec.ConfigValue<Integer> energyCapacity;
        public final ForgeConfigSpec.ConfigValue<Integer> energyUsage;
        public final ForgeConfigSpec.ConfigValue<Integer> defaultSpeed;
        public final ForgeConfigSpec.ConfigValue<Integer> minSpeed;
        public final ForgeConfigSpec.ConfigValue<Integer> maxSpeed;

        public ElectricMotorConfig(ForgeConfigSpec.Builder builder) {
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
