package com.sjl.scanner.test.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.sjl.scanner.UsbKeyboardAutoScan;
import com.sjl.scanner.listener.OnScanListener;
import com.sjl.scanner.test.R;
import com.sjl.scanner.test.ScannerUsbKeyboardActivity;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyDialogFragment
 * @time 2021/8/14 10:21
 * @copyright(C) 2021 song
 */
public class MyDialogFragment extends DialogFragment {
    EditText et_fragment_barcode;

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        //DialogFragment宽度
        getDialog().getWindow().setLayout((int) (displayMetrics.widthPixels * 0.9), (int) (displayMetrics.heightPixels * 0.3));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.my_dialog_fragment, container, false);
        et_fragment_barcode = inflate.findViewById(R.id.et_fragment_barcode);
        return inflate;

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (myDialogFragmentListener != null) {
            myDialogFragmentListener.onDismiss();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ScannerUsbKeyboardActivity activity = (ScannerUsbKeyboardActivity) getActivity();
        //或者你把UsbKeyboardAutoScan封装成单例，这样就不用通过activity取
        UsbKeyboardAutoScan usbKeyboardAutoScan = activity.getUsbKeyboardAutoScan();
        usbKeyboardAutoScan.setOnScanListener(new OnScanListener() {//覆盖了ScannerUsbKeyboardActivity的监听，收不到信息,一般都是一处地方接收扫码结果
            @Override
            public void onScanSuccess(String barcode) {
                et_fragment_barcode.setText(barcode);
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //设置actionbar的隐藏
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private MyDialogFragmentListener myDialogFragmentListener;

    public interface MyDialogFragmentListener {
        void onDismiss();
    }

    public void setMyDialogFragmentListener(MyDialogFragmentListener myDialogFragmentListener) {
        this.myDialogFragmentListener = myDialogFragmentListener;
    }
}
