package hungteen.opentd.impl.requirement;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ISummonRequirementType;

import java.util.Arrays;
import java.util.List;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:28
 **/
public class HTSummonRequirements {

    public static final HTSimpleRegistry<ISummonRequirementType<?>> REQUIREMENT_TYPES = HTRegistryManager.create(HTLib.prefix("requirement_type"));
    public static final HTCodecRegistry<ISummonRequirement> REQUIREMENTS = HTRegistryManager.create(ISummonRequirement.class, "tower_defence/requirements", HTSummonRequirements::getCodec);

    /* Tower types */

    public static final ISummonRequirementType<ExperienceRequirement> EXPERIENCE_REQUIREMENT = new DefaultRequirement<>("experience_requirement",  ExperienceRequirement.CODEC);

    /* Towers */

//    public static final HTRegistryHolder<ISummonRequirement> DEFAULT = TowerMENTS.innerRegister(
//            HTLib.prefix("default"), new CenterAreaTower(
//                    Vec3.ZERO, 0, 1, true, 0, true
//            )
//    );

    /**
     * {@link OpenTD#OpenTD()}
     */
    public static void registerStuffs(){
        Arrays.asList(EXPERIENCE_REQUIREMENT).forEach(HTSummonRequirements::registerTowerType);
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
