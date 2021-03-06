package cool.dingstock.lib_base.thread.scheduler;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.FlowableTransformer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * author：Vinsmoke
 * e-mail：vinsmokeleigh@gmail.com
 */
public class RxSchedulers {


    @Deprecated
    public static FlowableTransformer flowableTransformer = upstream -> upstream.subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

    public static <T> FlowableTransformer<T, T> io() {
        return upstream -> upstream.subscribeOn(NetThreadManager.INSTANCE.getIo_ThreadPool())
                .unsubscribeOn(NetThreadManager.INSTANCE.getIo_ThreadPool())
                .observeOn(NetThreadManager.INSTANCE.getIo_ThreadPool());
    }

    public static <T> FlowableTransformer<T, T> netio_main() {
        return upstream -> upstream.subscribeOn(NetThreadManager.INSTANCE.getIo_ThreadPool())
                .unsubscribeOn(NetThreadManager.INSTANCE.getIo_ThreadPool())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> FlowableTransformer<T, T> io_main() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> FlowableTransformer<T, T> new_main() {
        return upstream -> upstream.subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> FlowableTransformer<T, T> new_new() {
        return upstream -> upstream.subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread());
    }

    public static <T> FlowableTransformer<T, T> main_main() {
        return upstream -> upstream.subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> FlowableTransformer<T, T> main_io() {
        return upstream -> upstream.subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io());
    }

    public static <T> FlowableTransformer<T, T> cpu_main() {


        return upstream -> upstream.subscribeOn(Schedulers.computation())
                .unsubscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
