package com.guassmith.createmore;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;

public class Config {

    private static final Builder BUILDER;
    public static ForgeConfigSpec commonConf;

    static {
        BUILDER = new Builder();
        ELECTRIC_MOTOR.make();
        DYNAMO.make();
        commonConf = BUILDER.build();
    }

    public static class ELECTRIC_MOTOR {
        public static ConfigValue<Double> stressCapacity;
        public static ConfigValue<Integer> energyCapacity;
        public static ConfigValue<Integer> energyUsage;
        public static ConfigValue<Integer> defaultSpeed;
        public static ConfigValue<Integer> minSpeed;
        public static ConfigValue<Integer> maxSpeed;

        private static void make() {
            BUILDER.push("Electric Motor");
            stressCapacity = BUILDER
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
                .defineInRange("Maximum Speed", 256, Integer.MIN_VALUE, Integer.MAX_VALUE);
            defaultSpeed = BUILDER
                .comment("The speed of an electric motor when it's first placed down")
                .defineInRange("Default Speed",  16, Integer.MIN_VALUE, Integer.MAX_VALUE);
            BUILDER.pop();
        }
    }

    public static class DYNAMO {
        public static ConfigValue<Double> stressImpact;
        public static ConfigValue<Integer> energyCapacity;
        public static ConfigValue<Integer> energyProduction;
        public static ConfigValue<Integer> energyOutput;

        private static void make() {
            BUILDER.push("Dynamo");
            stressImpact = BUILDER
                .comment("Stress impact of the dynamo (this is multiplied by the rpm)")
                .define("Stress Impact", 100.d);
            energyCapacity = BUILDER
                .comment("How much energy the dynamo can store")
                .define("Energy Capacity", 100000);
            energyProduction = BUILDER
                .comment("How much energy the dynamo produces per tick (multiplied by rpm)")
                .define("Energy Production", 50);
            energyOutput = BUILDER
                .comment("How much energy the dynamo can output per tick")
                .define("Energy Output",  1000);
            BUILDER.pop();
        }
    }
}
