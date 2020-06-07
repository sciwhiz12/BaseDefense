package sciwhiz12.basedefense.init;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.api.capablities.ICodeHolder;
import sciwhiz12.basedefense.api.capablities.IContainsCode;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.api.capablities.ILock;
import sciwhiz12.basedefense.capabilities.CodeHolder;
import sciwhiz12.basedefense.capabilities.CodedKey;
import sciwhiz12.basedefense.capabilities.CodedLock;

@EventBusSubscriber(bus = Bus.MOD, modid = BaseDefense.MODID)
public class ModCapabilities {

    @CapabilityInject(ILock.class)
    public static final Capability<ILock> LOCK = null;
    @CapabilityInject(IKey.class)
    public static final Capability<IKey> KEY = null;
    @CapabilityInject(IContainsCode.class)
    public static final Capability<IContainsCode> CONTAINS_CODE = null;
    @CapabilityInject(ICodeHolder.class)
    public static final Capability<ICodeHolder> CODE_HOLDER = null;

    @SubscribeEvent
    static void onCommonSetup(FMLCommonSetupEvent event) {
        BaseDefense.LOG.debug("Registering capabilities");
        CapabilityManager.INSTANCE.register(ILock.class, new Storage<>(), CodedLock::new);
        CapabilityManager.INSTANCE.register(IKey.class, new Storage<>(), CodedKey::new);
        CapabilityManager.INSTANCE.register(IContainsCode.class, new Storage<>(), CodeHolder::new);
        CapabilityManager.INSTANCE.register(ICodeHolder.class, new Storage<>(), CodeHolder::new);
    }

    static class Storage<T> implements Capability.IStorage<T> {
        @SuppressWarnings("unchecked")
        @Override
        public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
            if (instance instanceof INBTSerializable) {
                return ((INBTSerializable<INBT>) instance).serializeNBT();
            } else {
                return null;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
            if (instance instanceof INBTSerializable) { ((INBTSerializable<INBT>) instance).deserializeNBT(nbt); }
        }
    }
}
