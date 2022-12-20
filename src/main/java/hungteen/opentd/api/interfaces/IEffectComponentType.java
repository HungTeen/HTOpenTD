package hungteen.opentd.api.interfaces;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.ISimpleEntry;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-20 19:15
 **/
public interface IEffectComponentType<P extends IEffectComponent> extends ISimpleEntry {

    /**
     * Get the method to codec effect.
     * @return Codec method.
     */
    Codec<P> codec();
}
