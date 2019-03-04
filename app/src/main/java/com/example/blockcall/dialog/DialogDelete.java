package com.example.blockcall.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.example.blockcall.R;
import com.example.blockcall.model.ContactObj;

public class DialogDelete extends Dialog {

    private TextView tvOK, tvCancel;
    private DialogListener dialogListener;


    public DialogDelete(Context context) {
        super(context);
        setCancelable(false);
    }

    public interface DialogListener {
        void onClickDone();
        void onClickCancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_delete);
        tvOK = (TextView) findViewById(R.id.tv_ok);
        tvCancel = (TextView)findViewById(R.id.tv_cancel);

        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogListener.onClickDone();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogListener.onClickCancel();
            }
        });
    }

    public void action(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }
}
