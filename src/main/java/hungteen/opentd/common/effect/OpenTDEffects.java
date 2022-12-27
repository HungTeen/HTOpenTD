package hungteen.opentd.common.effect;

import hungteen.htlib.util.helper.ColorHelper;
import hungteen.opentd.OpenTD;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-23 15:02
 **/
public class OpenTDEffects {

    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, OpenTD.MOD_ID);

    public static final RegistryObject<MobEffect> ENERGETIC_EFFECT = EFFECTS.register("energetic", () -> {
        return new HTMobEffect(MobEffectCategory.BENEFICIAL, ColorHelper.CREEPER_GREEN);
    });

    public static void register(IEventBus event){
        EFFECTS.register(event);
    }

}
