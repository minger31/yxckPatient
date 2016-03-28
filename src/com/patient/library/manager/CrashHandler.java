package com.patient.library.manager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;

import com.patient.util.LogUtil;

/**
 * 
 * 
 * UncaughtExceptionHandler：线程未捕获异常控制器是用来处理未捕获异常的。 如果程序出现了未捕获异常默认情况下则会出现强行关闭对话框
 * 实现该接口并注册为程序中的默认未捕获异常处理 这样当未捕获异常发生时，就可以做些异常处理操作 例如：收集异常信息，发送错误报告 等。
 * 
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String CLIENT_VERSION = "clientVersion";

    /** CrashHandler实例 */
    private static CrashHandler INSTANCE;
    /** 程序的Context对象 */
    private Context mContext;
    /** 系统默认的UncaughtException处理类 */
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /** 使用Properties来保存设备的信息和错误堆栈信息 */
    private Properties mDeviceCrashInfo = new Properties();

    /** 保证只有一个CrashHandler实例 */
    private CrashHandler() {
    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CrashHandler();
        return INSTANCE;
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     * 
     * @param ctx
     */
    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        // 收集设备信息
        collectCrashDeviceInfo(mContext);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LogUtil.d2File("CrashHandler.uncaughtException start.");

        // 未捕获异常写文件
        writeCrashInfoToFile(ex);

        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            System.exit(0);
        }

//        if (!handleException(ex) && mDefaultHandler != null) {
//            // 如果用户没有处理则让系统默认的异常处理器来处理
//            mDefaultHandler.uncaughtException(thread, ex);
//        } else {
//            // Sleep一会后结束程序
//            // 来让线程停止一会是为了显示Toast信息给用户，然后Kill程序
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                LogUtil.e("CrashHandler.uncaughtException 线程睡眠出错", e);
//            }
//            // 退出整个应用
//            int count = dao.queryOnlineConunt();
//            if (count > 0) {
//                dao.batchUpdateOnlineStatus();
//            }
//            Intent intent0 = new Intent();
//            intent0.setClass(mContext, CallManageService.class);
//            mContext.stopService(intent0);
//
//            Intent broadcast_intent = new Intent(
//                    BizConstant.ACTION_KILL_BIZSERVICE);
//            mContext.sendBroadcast(broadcast_intent);
//
//            CallAudioPlayer.getInstance().unbindPlayAudioSvc(
//                    NetPhoneApplication.getContext());
//            System.exit(0);
//        }
        LogUtil.d2File("CrashHandler.uncaughtException end.");
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     * 
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {

        if (ex == null) {
            return true;
        }

        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                // TODO:Toast 显示需要出现在一个线程的消息队列中
                Looper.prepare();
                // Toast.makeText(mContext, R.string.zcb_crash_msg,
                // Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        // 未捕获异常写文件
        writeCrashInfoToFile(ex);
        return true;
    }

    /**
     * 收集程序崩溃的设备信息
     * 
     * @param ctx
     */
    private void collectCrashDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();

            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                // public String versionName The version name of this package,
                // as specified by the <manifest> tag's versionName attribute.
                mDeviceCrashInfo.setProperty(CLIENT_VERSION,
                        pi.versionName == null ? "not set" : pi.versionName);
            }
        } catch (NameNotFoundException e) {
            LogUtil.e("CrashHandler.collectCrashDeviceInfo exception", e);
        }
    }

    /**
     * 将异常信息写入日志文件
     * 
     * @param ex
     * @return
     */
    private String writeCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        // printStackTrace(PrintWriter s)
        // 将此 throwable 及其追踪输出到指定的 PrintWriter
        ex.printStackTrace(printWriter);

        // getCause() 返回此 throwable 的 cause；如果 cause 不存在或未知，则返回 null。
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        // toString() 以字符串的形式返回该缓冲区的当前值。
        String result = info.toString();
        printWriter.close();

        String errorLog = String
                .format("FINGERPRINT=%s|"
                        + "STACK_TRACE=%s|versionName=%s|MODEL=%s|MANUFACTURER=%s|BRAND=%s|RELEASE=%s",
                // 硬件名称
                        Build.FINGERPRINT,
                        // 异常信息
                        result,
                        // 应用版本名称
                        mDeviceCrashInfo.getProperty(CLIENT_VERSION),
                        // 设备名称
                        Build.MODEL,
                        // 硬件制造商
                        Build.MANUFACTURER,
                        // android系统定制商
                        Build.BRAND,
                        // android SDK版本
                        Build.VERSION.RELEASE);
        
        LogUtil.d2File("CrashHandler errorLog=" + errorLog);

        return null;
    }
}
