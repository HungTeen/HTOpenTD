package hungteen.opentd.common.impl.requirement;

import com.mojang.serialization.Codec;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ISummonRequirementType;

import java.util.Arrays;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:28
 **/
public class HTSummonRequirements {

    public static final HTSimpleRegistry<ISummonRequirementType<?>> REQUIREMENT_TYPES = HTRegistryManager.create(OpenTD.prefix("requirement_type"));
    public static final HTCodecRegistry<ISummonRequirement> REQUIREMENTS = HTRegistryManager.create(ISummonRequirement.class, "tower_defence/requirements", HTSummonRequirements::getCodec, OpenTD.MOD_ID);

    /* Requirement types */

    public static final ISummonRequirementType<NoRequirement> NO_REQUIREMENT = new DefaultRequirement<>("no",  NoRequirement.CODEC);
    public static final ISummonRequirementType<OrRequirement> OR_REQUIREMENT = new DefaultRequirement<>("or",  OrRequirement.CODEC);
    public static final ISummonRequirementType<AndRequirement> AND_REQUIREMENT = new DefaultRequirement<>("and",  AndRequirement.CODEC);
    public static final ISummonRequirementType<NotRequirement> NOT_REQUIREMENT = new DefaultRequirement<>("not",  NotRequirement.CODEC);
    public static final ISummonRequirementType<ExperienceRequirement> EXPERIENCE_REQUIREMENT = new DefaultRequirement<>("experience",  ExperienceRequirement.CODEC);
    public static final ISummonRequirementType<BlockRequirement> BLOCK_REQUIREMENT = new DefaultRequirement<>("block",  BlockRequirement.CODEC);
    public static final ISummonRequirementType<EntityRequirement> ENTITY_REQUIREMENT = new DefaultRequirement<>("entity",  EntityRequirement.CODEC);
    public static final ISummonRequirementType<AroundEntityRequirement> AROUND_ENTITY_REQUIREMENT = new DefaultRequirement<>("around_entity",  AroundEntityRequirement.CODEC);
    public static final ISummonRequirementType<InventoryRequirement> INVENTORY_REQUIREMENT = new DefaultRequirement<>("inventory",  InventoryRequirement.CODEC);

    /* Requirement */

    /**
     * {@link OpenTD#OpenTD()}
     */
    public static void registerStuffs(){
        Arrays.asList(NO_REQUIREMENT, OR_REQUIREMENT, AND_REQUIREMENT, NOT_REQUIREMENT, EXPERIENCE_REQUIREMENT, BLOCK_REQUIREMENT, ENTITY_REQUIREMENT, AROUND_ENTITY_REQUIREMENT, INVENTORY_REQUIREMENT).forEach(HTSummonRequirements::registerTowerType);
    }

    public static void registerTowerType(ISummonRequirementType<?> type){
        REQUIREMENT_TYPES.register(type);
    }

    public static Codec<ISummonRequirement> getCodec(){
        return REQUIREMENT_TYPES.byNameCodec().dispatch(ISummonRequirement::getType, ISummonRequirementType::codec);
    }

    protected record DefaultRequirement<P extends ISummonRequirement>(String name, Codec<P> codec) implements ISummonRequirementType<P> {

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
