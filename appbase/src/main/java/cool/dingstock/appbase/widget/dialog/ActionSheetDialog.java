package cool.dingstock.appbase.widget.dialog;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;



public class ActionSheetDialog {
    public static void show(AppCompatActivity activity,
                            String title,
                            List<CharSequence> menuText,
                            ActionSheetItemClickListener actionSheetItemClickListener,
                            boolean isShowCancelButton) {
        ArrayList<Action> actions = new ArrayList<>();
        for(int i=0;i<menuText.size();i++){
            CharSequence text = menuText.get(i);
            actions.add(new Action(text.toString(),i));
        }
        DcBottomMenu.Companion.builder()
                .title(title)
                .showCancel(isShowCancelButton)
                .show(activity.getSupportFragmentManager(), actions, new OnMenuClickListener() {
                    @Override
                    public void onItemClick(int index,@NotNull Action action, @NotNull DcBottomMenu bottomMenu) {
                        actionSheetItemClickListener.onClick(action.getName(), index);
                    }
                });
    }

    public static void show(AppCompatActivity activity,
                            List<String> menuText,
                            ActionSheetItemClickListener actionSheetItemClickListener,
                            boolean isShowCancelButton) {

        ArrayList<Action> actions = new ArrayList<>();
        for(int i=0;i<menuText.size();i++){
            CharSequence text = menuText.get(i);
            actions.add(new Action(text.toString(),i));
        }
        DcBottomMenu.Companion.builder()
                .showCancel(isShowCancelButton)
                .show(activity.getSupportFragmentManager(), actions, new OnMenuClickListener() {
                    @Override
                    public void onItemClick(int index,@NotNull Action action, @NotNull DcBottomMenu bottomMenu) {
                        actionSheetItemClickListener.onClick(action.getName(), index);
                        bottomMenu.dismissAllowingStateLoss();
                    }
                });


    }
}
