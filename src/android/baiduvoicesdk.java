package com.heytz.baiduvoicesdk;


import android.content.Context;
import android.util.Log;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import jp.co.sharp.android.voiceui.VoiceUIManager;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static java.lang.Thread.sleep;

/**
 * This class starts transmit to activation
 */
public class baiduvoicesdk extends CordovaPlugin {

    private static String TAG = "=====baiduvoicesdk.class====";
    private CallbackContext resultCallbackContext;
    private Context context;
    private String result = "";
    private EventManager asr = null;
    private EventManager wp = null;


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        context = cordova.getActivity().getApplicationContext();

    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        resultCallbackContext = callbackContext;
        if (action.equals("start")) {
            HashMap map = new HashMap();
            map.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin"); //唤醒词文件请去http://yuyin.baidu.com/wake下载
            String json = "{\"kws-file\":\"assets:///WakeUp.bin\"}";
            wp.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);
            return true;
        }
        if (action.equals("stop")) {
            wp.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
            return true;
        }
        if (action.equals("init")) {
            try {
                VoiceUIManager vm = VoiceUIManager.getService(context);
                sleep(500);
                vm.notifyDisableMic();
                wp = EventManagerFactory.create(context, "wp");
                wp.registerListener(wakeupListener);
                asr = EventManagerFactory.create(context, "asr");
                asr.registerListener(voiceListener);
            } catch (Exception e) {

            }
//            String json = "{\"accept-audio-data\":false,\"disable-punctuation\":false,\"accept-audio-volume\":true,\"pid\":15361}";
//            asr.send(SpeechConstant.ASR_START, json, null, 0, 0);
            return true;
        }
//        if (action.equals("stopPlay")) {
//            asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
//            return true;
//        }
        return false;
    }

    //      public static void HideKeyboard(View v)
//      {
//          InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );
//        if ( imm.isActive( ) ) {
//            imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );
//
//        }
//      }
    EventListener voiceListener = new EventListener() {
        @Override
        public void onEvent(String name, String params, byte[] data, int offset, int length) {
            if (!name.equals(SpeechConstant.CALLBACK_EVENT_ASR_VOLUME)) {
            } else {
            }
            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
                // 引擎就绪，可以说话，一般在收到此事件后通过UI通知用户可以说话了
            }
            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
                // 识别结束
                PluginResult pr = new PluginResult(PluginResult.Status.OK, result);
                pr.setKeepCallback(true);
                resultCallbackContext.sendPluginResult(pr);
            }
            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_EXIT)) {
                // 识别结束
                String json = "{\"kws-file\":\"assets:///WakeUp.bin\"}";
                wp.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);
            }
            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_ERROR)) {
                // 识别结束
            }
            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
                // 识别结束
                try {

                    JSONObject json = new JSONObject(params);
                    if (!json.isNull("result_type") && json.getString("result_type").equals("final_result")) {
                        result = json.getString("results_recognition");
                        if (asr != null) {
                            asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
                        }
                    }
                } catch (Exception e) {

                }


            }

            // ... 支持的输出事件和事件支持的事件参数见“输入和输出参数”一节
        }
    };

    EventListener wakeupListener = new EventListener() {
        @Override
        public void onEvent(String name, String params, byte[] data, int offset, int length) {
            Log.d(TAG, String.format("event: name=%s, params=%s", name, params));
            //唤醒成功
            if (name.equals("wp.data")) {
                try {
                    JSONObject json = new JSONObject(params);
                    int errorCode = json.getInt("errorCode");
                    if (errorCode == 0) {
                        //唤醒成功
                        result = "";
                        if (wp != null) {
                            wp.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
                        }
                        String settingJson = "{\"accept-audio-data\":false,\"disable-punctuation\":false,\"accept-audio-volume\":true,\"pid\":15361}";
                        asr.send(SpeechConstant.ASR_START, settingJson, null, 0, 0);
                    } else {
                        //唤醒失败
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if ("wp.exit".equals(name)) {
                //唤醒已停止
//                asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
            }
        }
    };
}
