package hungteen.opentd.api.interfaces;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.ISimpleEntry;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:26
 **/
public interface ITowerComponentType<P extends ITowerComponent> extends ISimpleEntry {

    /**
     * Get the method to codec tower.
     * @return Codec method.
     */
    Codec<P> codec();

    Codec<P> networkCodec();
}
