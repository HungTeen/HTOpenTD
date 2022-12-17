package hungteen.opentd.api.interfaces;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.ISimpleEntry;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 11:42
 **/
public interface ISummonRequirementType<P extends ISummonRequirement> extends ISimpleEntry {

    /**
     * Get the method to codec summon requirement.
     * @return Codec method.
     */
    Codec<P> codec();

}
