package dev.creoii.greatbigworld.stackableplants.fabric;

import dev.creoii.greatbigworld.stackableplants.StackablePlants;
import net.fabricmc.api.ModInitializer;

public final class StackablePlantsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        StackablePlants.init();
    }
}
