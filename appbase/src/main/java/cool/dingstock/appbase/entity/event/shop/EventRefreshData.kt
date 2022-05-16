package cool.dingstock.appbase.entity.event.shop

import cool.dingstock.appbase.entity.bean.shop.AddressEntity
import cool.dingstock.appbase.entity.bean.shop.UserAddressEntity

/**
 * @author wangjiang
 *  CreateAt Time 2021/6/10  11:56
 */
class EventRefreshData {
    var entity: AddressEntity? = null
}

class EventRefreshAddressListData {
    var entity: UserAddressEntity? = null
}

class EventRefreshBoxData {
}