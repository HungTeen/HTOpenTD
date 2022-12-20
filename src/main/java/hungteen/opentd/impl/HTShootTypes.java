package hungteen.opentd.impl;

import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.IShootType;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-20 15:35
 **/
public class HTShootTypes {

    public static final HTSimpleRegistry<IShootType> WORK_TYPES = HTRegistryManager.create(OpenTD.prefix("shoot_type"));


}
