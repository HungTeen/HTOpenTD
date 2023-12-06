package hungteen.opentd.common.impl.requirement;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTSimpleRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ISummonRequirementType;
import hungteen.opentd.util.Util;

public interface OTDRequirementTypes {

    HTSimpleRegistry<ISummonRequirementType<?>> REQUIREMENT_TYPES = HTRegistryManager.createSimple(Util.prefix("requirement_type"));

    ISummonRequirementType<NoRequirement> NO_REQUIREMENT = register(new DefaultRequirement<>("no",  NoRequirement.CODEC));
    ISummonRequirementType<OrRequirement> OR_REQUIREMENT = register(new DefaultRequirement<>("or",  OrRequirement.CODEC));
    ISummonRequirementType<AndRequirement> AND_REQUIREMENT = register(new DefaultRequirement<>("and",  AndRequirement.CODEC));
    ISummonRequirementType<NotRequirement> NOT_REQUIREMENT = register(new DefaultRequirement<>("not",  NotRequirement.CODEC));
    ISummonRequirementType<ExperienceRequirement> EXPERIENCE_REQUIREMENT = register(new DefaultRequirement<>("experience",  ExperienceRequirement.CODEC));
    ISummonRequirementType<BlockRequirement> BLOCK_REQUIREMENT = register(new DefaultRequirement<>("block",  BlockRequirement.CODEC));
    ISummonRequirementType<EntityRequirement> ENTITY_REQUIREMENT = register(new DefaultRequirement<>("entity",  EntityRequirement.CODEC));
    ISummonRequirementType<AroundEntityRequirement> AROUND_ENTITY_REQUIREMENT = register(new DefaultRequirement<>("around_entity",  AroundEntityRequirement.CODEC));
    ISummonRequirementType<InventoryRequirement> INVENTORY_REQUIREMENT = register(new DefaultRequirement<>("inventory",  InventoryRequirement.CODEC));

    static IHTSimpleRegistry<ISummonRequirementType<?>> registry(){
        return REQUIREMENT_TYPES;
    }

    static <T extends ISummonRequirement> ISummonRequirementType<T> register(ISummonRequirementType<T> type){
        return registry().register(type);
    }

    record DefaultRequirement<P extends ISummonRequirement>(String name, Codec<P> codec) implements ISummonRequirementType<P> {

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getModID() {
            return OpenTD.MOD_ID;
        }
    }
}
