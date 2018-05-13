package com.heytz.baiduvoicesdk;


import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.os.RemoteException;
import android.util.Log;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;
import com.heytz.optimus.customize.ScenarioDefinitions;
import com.heytz.optimus.util.VoiceUIManagerUtil;
import com.heytz.optimus.util.VoiceUIVariableUtil;
import jp.co.sharp.android.voiceui.VoiceUIManager;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private VoiceUIManager vm = null;
    private SpeechSynthesizer mSpeechSynthesizer = null;
    protected String appId = "11194650";

    protected String appKey = "vtPSfelas3ycuEtW3uGmGl1B";

    protected String secretKey = "34f378211d11588f85576618ab22efd3";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        context = cordova.getActivity().getApplicationContext();

    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        resultCallbackContext = callbackContext;
        if (action.equals("start")) {
            try {

                HashMap map = new HashMap();
                map.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin"); //唤醒词文件请去http://yuyin.baidu.com/wake下载
                String json = "{\"kws-file\":\"assets:///WakeUp.bin\"}";
                wp.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);
            } catch (Exception e) {
            }


            return true;
        }
        if (action.equals("stop")) {
            try {
                mSpeechSynthesizer.speak("我是小夏");
//                ArrayList<VoiceUIVariable> listVariables = new ArrayList<VoiceUIVariable>();
//                VoiceUIVariable variable = new VoiceUIVariable(ScenarioDefinitions.TAG_ACCOST, VoiceUIVariable.VariableType.STRING);
//                variable.setStringValue(ScenarioDefinitions.ACC_ACCOST);
//                listVariables.add(variable);
//                sleep(8000);
//                VoiceUIManagerUtil.updateAppInfo(vm, listVariables, true);
//                sleep(1000);
            } catch (Exception e) {

            }
//            wp.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
            return true;
        }
        if (action.equals("announce")) {
            try {
                String word = args.getString(0);
                if (word != null && word != "") {
                    mSpeechSynthesizer.speak(word);
//                ArrayList<VoiceUIVariable> listVariables = new ArrayList<VoiceUIVariable>();
//                VoiceUIVariable variable = new VoiceUIVariable(ScenarioDefinitions.TAG_ACCOST, VoiceUIVariable.VariableType.STRING);
//                variable.setStringValue(ScenarioDefinitions.ACC_ACCOST);
//                listVariables.add(variable);
//                sleep(8000);
//                VoiceUIManagerUtil.updateAppInfo(vm, listVariables, true);
//                sleep(1000);
                }
            } catch (Exception e) {

            }
//            wp.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
            return true;
        }
        if (action.equals("init")) {
            try {
                vm = VoiceUIManager.getService(context);
                sleep(500);
                vm.notifyDisableMic();
                wp = EventManagerFactory.create(context, "wp");
                wp.registerListener(wakeupListener);
                asr = EventManagerFactory.create(context, "asr");
                asr.registerListener(voiceListener);
                if (mSpeechSynthesizer == null) {
                    mSpeechSynthesizer = SpeechSynthesizer.getInstance();
                    mSpeechSynthesizer.setContext(context);
                    int result = mSpeechSynthesizer.setAppId(appId);
                    result = mSpeechSynthesizer.setApiKey(appKey, secretKey);
                    mSpeechSynthesizer.auth(TtsMode.ONLINE);
                    mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "4");
                    // 设置合成的音量，0-9 ，默认 5
                    mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
                    // 设置合成的语速，0-9 ，默认 5
                    mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
                    // 设置合成的语调，0-9 ，默认 5
                    mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");

                    mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
                    // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
                    // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
                    // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
                    // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
                    // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

                    mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL);
                    mSpeechSynthesizer.initTts(TtsMode.MIX);
                }
//                vm.notifyEnableMic();
                registerScenario("hvml", false);
                sleep(500);
                VoiceUIManagerUtil.enableScene(vm, ScenarioDefinitions.SCENE_COMMON);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
//            String json = "{\"accept-audio-data\":false,\"disable-punctuation\":false,\"accept-audio-volume\":true,\"pid\":15361}";
//            asr.send(SpeechConstant.ASR_START, json, null, 0, 0);
            return true;
        }
        if (action.equals("voiceSpeech")) {
            try {
                if (vm != null) {
                    VoiceUIVariableUtil.VoiceUIVariableListHelper helper = new VoiceUIVariableUtil.VoiceUIVariableListHelper().addAccost(ScenarioDefinitions.ACC_ACCOST);
                    VoiceUIManagerUtil.updateAppInfo(vm, helper.getVariableList(), true);
                }
//               vm.
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
                        JSONArray jsonArray = json.getJSONArray("results_recognition");
                        result = jsonArray.get(0).toString();
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
//                        if (vm != null) {
                        try {
                            if (wp != null) {
                                wp.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
                            }
//                                VoiceUIVariableUtil.VoiceUIVariableListHelper helper = new VoiceUIVariableUtil.VoiceUIVariableListHelper().addAccost(ScenarioDefinitions.ACC_ACCOST);
//                                ArrayList<VoiceUIVariable> listVariables = new ArrayList<VoiceUIVariable>();
//                                VoiceUIVariable variable = new VoiceUIVariable(ScenarioDefinitions.TAG_ACCOST, VoiceUIVariable.VariableType.STRING);
//                                variable.setStringValue(ScenarioDefinitions.ACC_ACCOST);
//                                listVariables.add(variable);
//                                sleep(1000);
//                                VoiceUIManagerUtil.updateAppInfo(vm, listVariables, true);
                            sleep(1000);
                            mSpeechSynthesizer.speak("我在");
                            sleep(1000);

//                                VoiceUIManagerUtil.updateAppInfo(vm, helper.getVariableList(), true);
                        } catch (Exception e) {

                        }
//                        }
                        result = "";
//                            wp.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
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

    private void registerScenario(String locale, Boolean home) {
        Log.d(TAG, "registerScenario-S: " + locale + " : " + home.toString());

        //ローカルフォルダー作成.
        File localFolder = null;
        if (home) {
            localFolder = this.createLocalFolder("home");
        } else {
            localFolder = this.createLocalFolder("other");
        }
        if (localFolder == null) {
            Log.e(TAG, "can not make local folder");
            return;
        }

        //assetsフォルダー名取得.
        String assetsFolderName = this.getAssetsScenarioFolderName(locale, home);

        //assetsフォルダー内のファイル名リストを取得.
        final AssetManager assetManager = context.getResources().getAssets();
        String[] fileList = null;
        try {
            fileList = assetManager.list(assetsFolderName);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        final String hvmlPrefix = context.getPackageName().replace(".", "_");

        //assetsからローカルへhvmlファイルをコピー.ただしHVMLファイル命名規則違反のファイルはコピーしない
        for (String fileName : fileList) {
            if (fileName.endsWith(".hvml")
                    && fileName.startsWith(hvmlPrefix)
                    ) {
                Log.d(TAG, "hvml files = " + fileName);
                this.copyFileFromAssetsToLocal(assetsFolderName, localFolder.getPath(), fileName);
            }
        }

        //ローカルフォルダーのファイル名リストを取得.
        File[] files = localFolder.listFiles();

        //ローカルフォルダーのhvmlファイルのシナリオを登録する.
        for (File file : files) {
            Log.d(TAG, "registerScenario file=" + file.getAbsolutePath());
            int result = VoiceUIManager.VOICEUI_ERROR;
            try {
                if (home) {
                    //home用.
                    result = vm.registerHomeScenario(file.getAbsolutePath());
                    if (result == VoiceUIManager.VOICEUI_ERROR)
                        Log.e(TAG, "registerScenario:Error");
                } else {
                    //other.
                    result = vm.registerScenario(file.getAbsolutePath());
                    if (result == VoiceUIManager.VOICEUI_ERROR)
                        Log.e(TAG, "registerScenario:Error");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "registerScenario-E:" + home.toString());
    }

    private File createLocalFolder(String childPath) {
        File folder = null;
        try {
            folder = new File(context.getFilesDir(), childPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            folder.setReadable(true, false);
            folder.setWritable(true, false);
            folder.setExecutable(true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return folder;
    }

    /**
     * assetsからローカルへファイルをコピー.
     */
    private void copyFileFromAssetsToLocal(String assetsFolderName, String localFolderName, String fileName) {
        File assetsFile = null;
        InputStream inputStream = null;
        File localFile = null;
        FileOutputStream fileOutputStream = null;
        byte[] buffer = null;
        byte[] outPutBuffer = null;
        try {
            //   AssetsフォルダのファイルOpen
            assetsFile = new File(assetsFolderName, fileName);
            inputStream = context.getResources().getAssets().open(assetsFile.getPath());

            //   ローカルフォルダーにファイル作成
            localFile = new File(localFolderName, fileName);
            if (localFile.exists()) {
                localFile.delete();
            }
            fileOutputStream = new FileOutputStream(localFile.getPath());
            localFile.setReadable(true, false);
            localFile.setWritable(true, false);
            localFile.setExecutable(true, false);
            buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) >= 0) {
                fileOutputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                }
            }
            fileOutputStream = null;

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                }
            }
            inputStream = null;
            buffer = null;
            assetsFile = null;
            localFile = null;
            assetsFile = null;
        }
    }

    private String getAssetsScenarioFolderName(String locale, Boolean home) {
        String result = "";
        //locale指定が無い場合はhvmlフォルダを利用する
        if (locale == null || "".equals(locale)) {
            if (home) {
                result = "hvml/home";
            } else {
                result = "hvml/other";
            }
        } else {
            //hvml_<locale>の形式.
            result = "hvml" + "_" + locale;
            final AssetManager assetManager = context.getResources().getAssets();
            String[] fileList = null;
            try {
                fileList = assetManager.list(result);
            } catch (IOException e) {
                e.printStackTrace();
                result = "hvml";
            }
            //locale指定があるが、該当フォルダが存在しない場合はhvmlフォルダを利用する
            if (fileList == null || fileList.length == 0) {
                Log.w(TAG, "not exist assets folder");
                result = "hvml";
            }
            if (home) {
                result = result + "/" + "home";
            } else {
                result = result + "/" + "other";
            }
        }
        return result;
    }

}
