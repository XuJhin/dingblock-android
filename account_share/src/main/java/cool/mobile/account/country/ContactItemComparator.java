package cool.mobile.account.country;

import java.util.Comparator;

import cool.dingstock.appbase.entity.bean.account.CountryBean;

public class ContactItemComparator implements Comparator<CountryBean> {

    @Override
    public int compare(CountryBean lhs, CountryBean rhs) {
        if (lhs.getSpell() == null || rhs.getSpell() == null)
            return -1;
        return lhs.getSpell().compareTo(rhs.getSpell());

    }

}
