package cool.dingstock.appbase.annotations;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({ActivityAnim.NORMAL, ActivityAnim.PRESENT})
@Retention(RetentionPolicy.SOURCE)
public @interface ActivityAnim {
   String NORMAL = "normal";
   String PRESENT = "present";
}