package com.example.blockcall.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.example.blockcall.R;
import com.example.blockcall.model.ContactObj;

public class DialogUtil extends Dialog {

    private TextView tvOK, tvCancel;
    public EditText edtName, edtPhone;
    private DialogListener dialogListener;
    private int idLayout;
    private ContactObj contactObj;


    public DialogUtil(Context context, int idLayout, ContactObj contactObj) {
        super(context);
        setCancelable(false);
        this.idLayout = idLayout;
        this.contactObj = contactObj;
    }

    public interface DialogListener {
        void onClickDone();
        void onClickCancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(idLayout);
        tvOK = (TextView) findViewById(R.id.tv_ok);
        tvCancel = (TextView)findViewById(R.id.tv_cancel);
        if(idLayout == R.layout.dialog_edit) {
            edtPhone = (EditText)findViewById(R.id.edt_edit_phone);
            edtName = (EditText)findViewById(R.id.edt_edit_name);

            edtPhone.setText(contactObj.getPhoneNum());
            edtName.setText(contactObj.getUserName());
        }
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
