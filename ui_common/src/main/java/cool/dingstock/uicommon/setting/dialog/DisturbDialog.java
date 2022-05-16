package cool.dingstock.uicommon.setting.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;

import cool.dingstock.appbase.widget.dialog.BaseDialog;
import cool.dingstock.appbase.helper.SettingHelper;
import cool.dingstock.uicommon.R;

public class DisturbDialog extends BaseDialog {
	
	private NumberPicker startPicker;
	private NumberPicker centerPicker;
	private NumberPicker endPicker;
	private TextView cancelView;
	private TextView openText;
	private ActionListener mListener;
	private int mStartTime;
	private int mEndTime;
	
	public DisturbDialog(@NonNull Context context) {
		super(context);
	}
	
	public void setMListener(ActionListener mListener) {
		this.mListener = mListener;
	}
	
	@Override
	public int getLayoutId() {
		return R.layout.setting_dialog_layout;
	}
	
	@Override
	public void initViews() {
		startPicker = mRootView.findViewById(R.id.setting_dialog_start_picker);
		centerPicker = mRootView.findViewById(R.id.setting_dialog_center_picker);
		endPicker = mRootView.findViewById(R.id.setting_dialog_end_picker);
		cancelView = mRootView.findViewById(R.id.setting_dialog_cancel_txt);
		openText = mRootView.findViewById(R.id.setting_dialog_open_txt);

		centerPicker.setDisplayedValues(new String[]{"至"});
		String[] displayedValues = buildValues();
		startPicker.setDisplayedValues(displayedValues);
		startPicker.setMaxValue(displayedValues.length - 1);
		endPicker.setDisplayedValues(displayedValues);
		endPicker.setMaxValue(displayedValues.length - 1);
		
		startPicker.setValue(mStartTime);
		endPicker.setValue(mEndTime);
		
		initListener();

//        setNumberPickerDividerColor(startPicker);
//        setNumberPickerDividerColor(centerPicker);
//        setNumberPickerDividerColor(endPicker);
		setDividerColor(startPicker);
		setDividerColor(centerPicker);
		setDividerColor(endPicker);
	}
	
	private void initListener() {
		cancelView.setOnClickListener(v -> dismiss());
		startPicker.setOnValueChangedListener((picker, oldVal, newVal) -> mStartTime = newVal);
		endPicker.setOnValueChangedListener((picker, oldVal, newVal) -> mEndTime = newVal);
		openText.setOnClickListener(v -> {
			SettingHelper.getInstance().saveDisturbStatus(true);
			SettingHelper.getInstance().saveDisturbTime(mStartTime, mEndTime);
			if (null != mListener) {
				mListener.onAction(true, mStartTime, mEndTime);
			}
			dismiss();
		});
	}
	
	@Override
	public void setDialogConfig(Window window) {
		window.setGravity(Gravity.BOTTOM);
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	private String[] buildValues() {
		String[] timeStrArray = new String[24];
		for (int i = 0; i < 24; i++) {
			timeStrArray[i] = i + "点";
		}
		return timeStrArray;
		
	}
	
	public void setStartTime(int mStartTime) {
		this.mStartTime = mStartTime;
		startPicker.setValue(mStartTime);
	}
	
	public void setEndTime(int mEndTime) {
		this.mEndTime = mEndTime;
		endPicker.setValue(mEndTime);
	}
	
	public interface ActionListener {
		void onAction(boolean status, int start, int end);
	}

	private void setDividerColor(NumberPicker picker) {
		Field field = null;
		try {
			field = NumberPicker.class.getDeclaredField("mSelectionDivider");
			if (field != null) {
				field.setAccessible(true);
				field.set(picker, new ColorDrawable(Color.TRANSPARENT));
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

    public void setNumberPickerDividerColor(NumberPicker numberPicker) {
        NumberPicker picker = numberPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    pf.set(picker, new ColorDrawable(Color.WHITE));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
