package hungteen.opentd.common;

import hungteen.opentd.OpenTD;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-02 22:00
 **/
public class OpenTDSounds {

    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, OpenTD.MOD_ID);

    public static RegistryObject<SoundEvent> ZOMBOSS = registerSound("zomboss");

    public static void register(IEventBus event){
        SOUNDS.register(event);
    }

    private static RegistryObject<SoundEvent> registerSound(String name){
        return SOUNDS.register(name, ()->{
            return new SoundEvent(OpenTD.prefix(name));
        });
    }

}
