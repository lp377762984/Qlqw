package com.ascba.rebate.net;

import android.app.ProgressDialog;
import com.ascba.rebate.R;
import com.ascba.rebate.base.activity.BaseNetActivity;
import com.ascba.rebate.base.activity.BaseUIActivity;
import com.ascba.rebate.bean.Result;
import com.ascba.rebate.manager.ToastManager;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.NotFoundCacheError;
import com.yanzhenjie.nohttp.error.ServerError;
import com.yanzhenjie.nohttp.error.StorageReadWriteError;
import com.yanzhenjie.nohttp.error.StorageSpaceNotEnoughError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.error.URLError;
import com.yanzhenjie.nohttp.error.UnKnownHostError;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Response;

import java.lang.ref.WeakReference;

/**
 * Created by 李平 on 2017/8/11.
 * 网络请求监听器
 */

public abstract class HttpResponseListener<T> implements OnResponseListener<Result<T>> {

    private ProgressDialog dialog;
    protected WeakReference<BaseNetActivity> context;

    public HttpResponseListener(BaseNetActivity context, String message) {
        this.context =new WeakReference<>(context);
        if(message!=null){
            this.dialog = new ProgressDialog(this.context.get(), R.style.dialog);
            dialog.setCanceledOnTouchOutside(false);//点击外部不可取消
            dialog.setCancelable(true);//返还键可取消
            dialog.setMessage(message);
        }
    }


    @Override
    public void onStart(int what) {
        if(dialog!=null && !dialog.isShowing()) {
            dialog.show();
        }
        onHttpStart(what);
    }

    @Override
    public void onSucceed(int what, Response<Result<T>> response) {
        onHttpSucceed(what,response);
    }

    @Override
    public void onFailed(int what, Response<Result<T>> response) {

        Exception e = response.getException();
        if (e instanceof NetworkError) {
            //context.showToast(e.getMessage());
        }else if(e instanceof ServerError){
            ToastManager.show(e.getMessage());
        }else if(e instanceof StorageReadWriteError){
            ToastManager.show(e.getMessage());
        }else if(e instanceof StorageSpaceNotEnoughError){
            ToastManager.show(e.getMessage());
        }else if(e instanceof TimeoutError){
            ToastManager.show(e.getMessage());
        }else if(e instanceof UnKnownHostError){
            ToastManager.show(e.getMessage());
        }else if(e instanceof NotFoundCacheError){
            ToastManager.show(e.getMessage());
        }else if(e instanceof URLError){
            ToastManager.show(e.getMessage());
        }else {
            ToastManager.show(e.getMessage());
        }
        onHttpFailed(what,response);

    }

    @Override
    public void onFinish(int what) {
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        onHttpFinish(what);
    }

    public abstract void onHttpSucceed(int what, Response<Result<T>> response);
    public abstract void onHttpFailed(int what, Response<Result<T>> response);
    public abstract void onHttpFinish(int what);
    public abstract void onHttpStart(int what);
}
