package dev.creoii.greatbigworld.stackableplants.neoforge;

import dev.creoii.greatbigworld.stackableplants.StackablePlants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("stackable_plants")
public final class StackablePlantsNeoForge {
    public StackablePlantsNeoForge(IEventBus modBus) {
        StackablePlants.init();
    }
}
