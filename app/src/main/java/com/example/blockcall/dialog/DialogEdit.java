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

public class DialogEdit extends Dialog {

    private TextView tvOK, tvCancel;
    public EditText edtName, edtPhone;
    private DialogListener dialogListener;
    private ContactObj contactObj;


    public DialogEdit(Context context, ContactObj contactObj) {
        super(context);
        setCancelable(false);
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
        setContentView(R.layout.dialog_edit);
        tvOK = (TextView) findViewById(R.id.tv_ok);
        tvCancel = (TextView)findViewById(R.id.tv_cancel);
        edtPhone = (EditText)findViewById(R.id.edt_edit_phone);
        edtName = (EditText)findViewById(R.id.edt_edit_name);
        edtPhone.setText(contactObj.getPhoneNum());
        edtName.setText(contactObj.getUserName());
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
