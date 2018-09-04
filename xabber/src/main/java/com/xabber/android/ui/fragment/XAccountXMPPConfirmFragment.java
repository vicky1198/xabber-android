package com.xabber.android.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xabber.android.R;
import com.xabber.android.data.xaccount.XMPPAuthManager;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class XAccountXMPPConfirmFragment extends Fragment implements View.OnClickListener {

    private String jid;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    private EditText edtCode;
    private Button btnConfirm;
    private Button btnResend;
    private TextView tvTitle;

    private Listener listener;

    public interface Listener {
        void onConfirmXMPPClick(String jid, String code);
        void onResendClick(String jid);
    }

    public static XAccountXMPPConfirmFragment newInstance(Listener listener) {
        XAccountXMPPConfirmFragment fragment = new XAccountXMPPConfirmFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_xaccount_xmpp_confirm, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtCode = view.findViewById(R.id.edtCode);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        btnResend = view.findViewById(R.id.btnResend);
        tvTitle = view.findViewById(R.id.tvTitle);

        btnConfirm.setOnClickListener(this);
        btnResend.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        subscribeForAuthCode();
        tvTitle.setText(getString(R.string.xmpp_confirm_title, jid));
    }

    @Override
    public void onPause() {
        super.onPause();
        unsubscribeAll();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConfirm:
                onConfirmClick();
                break;
            case R.id.btnResend:
                onResendClick();
                break;
        }
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    private void onConfirmClick() {
        String code = edtCode.getText().toString();

        if (code.isEmpty()) {
            edtCode.setError(getString(R.string.empty_field));
            return;
        }

        listener.onConfirmXMPPClick(jid, code);
    }

    private void onResendClick() {
        listener.onResendClick(jid);
    }

    private void subscribeForAuthCode() {
        subscriptions.add(XMPPAuthManager.getInstance().subscribeForAuthCode()
            .doOnNext(new Action1<String>() {
                @Override
                public void call(String authCode) {
                    if (edtCode != null) edtCode.setText(authCode);
                }
            }).subscribe());
    }

    private void unsubscribeAll() {
        subscriptions.clear();
    }
}