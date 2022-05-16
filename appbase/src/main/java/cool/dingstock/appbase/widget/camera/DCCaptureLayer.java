package cool.dingstock.appbase.widget.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cool.dingstock.appbase.R;
import cool.dingstock.appbase.widget.IconTextView;

public class DCCaptureLayer extends FrameLayout {

    private LinearLayout doneLayer;
    private ImageView actonView;
    private IconTextView doneTxt;
    private IconTextView backTxt;

    private CaptureListener listener;


    public interface CaptureListener {
        void onCaptureClick();

        void onBackClick();

        void onDoneClick();
    }


    public DCCaptureLayer(@NonNull Context context) {
        this(context, null);
    }

    public DCCaptureLayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DCCaptureLayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View rootView = View.inflate(getContext(), R.layout.common_capture_layer, null);
        actonView = rootView.findViewById(R.id.common_capture_action_iv);
        doneLayer = rootView.findViewById(R.id.common_capture_done_layer);
        doneTxt = rootView.findViewById(R.id.common_capture_done_txt);
        backTxt = rootView.findViewById(R.id.common_capture_back_txt);

        actonView.setOnClickListener(v -> {
            if (null != listener) {
                listener.onCaptureClick();
            }
        });
        doneTxt.setOnClickListener(v -> {
            if (null != listener) {
                listener.onDoneClick();
            }
        });
        backTxt.setOnClickListener(v -> {
            if (null != listener) {
                listener.onBackClick();
            }
        });

        addView(rootView);
        normal();
    }


    public void normal() {
        if (null != actonView) {
            actonView.setVisibility(VISIBLE);
        }
        if (null != doneLayer) {
            doneLayer.setVisibility(GONE);
        }
    }

    public void done(){
        if (null != actonView) {
            actonView.setVisibility(GONE);
        }
        if (null != doneLayer) {
            doneLayer.setVisibility(VISIBLE);
        }
    }

    public void setListener(CaptureListener listener) {
        this.listener = listener;
    }
}
