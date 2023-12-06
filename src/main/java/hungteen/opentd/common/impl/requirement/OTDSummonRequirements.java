package hungteen.opentd.common.impl.requirement;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ISummonRequirementType;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:28
 **/
public interface OTDSummonRequirements {

    HTCodecRegistry<ISummonRequirement> REQUIREMENTS = HTRegistryManager.create(Util.prefix("summon_requirements"), OTDSummonRequirements::getCodec);

    static void register(BootstapContext<ISummonRequirement> context){

    }

    static ResourceKey<ISummonRequirement> create(String name) {
        return registry().createKey(Util.prefix(name));
    }

    static Codec<ISummonRequirement> getDirectCodec(){
        return OTDRequirementTypes.registry().byNameCodec().dispatch(ISummonRequirement::getType, ISummonRequirementType::codec);
    }

    static Codec<Holder<ISummonRequirement>> getCodec(){
        return registry().getHolderCodec(getDirectCodec());
    }

    static IHTCodecRegistry<ISummonRequirement> registry() {
        return REQUIREMENTS;
    }

}
