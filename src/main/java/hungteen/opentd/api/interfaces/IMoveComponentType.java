package hungteen.opentd.api.interfaces;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.ISimpleEntry;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/12 10:57
 */
public interface IMoveComponentType<P extends IMoveComponent> extends ISimpleEntry {

    /**
     * Get the method to codec move component.
     * @return Codec method.
     */
    Codec<P> codec();

}