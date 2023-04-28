package com.example.service;

import com.iflytek.cloud.speech.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName: VoiceTool
 * @Description:
 * @Author: 刘苏义
 * @Date: 2023年04月26日14:10
 * @Version: 1.0
 **/
@Slf4j
public class VoiceTool {

    private StringBuilder sb;
    private SpeechRecognizer speechRecognizer;
    private Object lock = new Object();

    public VoiceTool(String appId){
        SpeechUtility.createUtility(SpeechConstant.APPID + "=" + appId);
    }

    public String RecognizePcmfileByte(MultipartFile audioFile) {
        sb = new StringBuilder();
        try {
            if (speechRecognizer == null) {//参数可以在配置文件设置
                speechRecognizer = SpeechRecognizer.createRecognizer();
                speechRecognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
                speechRecognizer.setParameter( SpeechConstant.RESULT_TYPE, "plain" );
                speechRecognizer.setParameter(SpeechConstant.VAD_BOS,"5000");//前端点超时，
                speechRecognizer.setParameter(SpeechConstant.VAD_EOS,"10000");//后端点超时要与运行SDK时配置的一样
            }
            speechRecognizer.startListening(recListener);
            byte[] buffer = audioFile.getBytes();
            if (buffer == null || buffer.length == 0) {
                log.error("no audio avaible!");
                speechRecognizer.cancel();
            } else {
                int lenRead = buffer.length;
                log.info("文件长度"+buffer.length);
                speechRecognizer.writeAudio( buffer, 0, lenRead );
                speechRecognizer.stopListening();
                synchronized (lock) {
                    lock.wait();//主线程等待
                }
                log.info("输出语音内容：" + sb.toString());
                return sb.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private RecognizerListener recListener = new RecognizerListener() {
        @Override
        public void onBeginOfSpeech() {
            log.info("准备语音");
        }
        @Override
        public void onEndOfSpeech() {
            log.info("结束语音");
        }
        /**
         * 获取听写结果
         */
        @Override
        public void onResult(RecognizerResult results, boolean islast) {
            //用json解析器解析为json格式
            String text = results.getResultString();
            sb.append(text);
            //log.info("解析结果："+curRet.toString());
            if( islast ) {
                synchronized (lock) {
                    lock.notify();//子线程唤醒
                }
            }
        }
        @Override
        public void onVolumeChanged(int volume) {

        }

        @Override
        public void onError(SpeechError error) {

        }
        @Override
        public void onEvent(int eventType, int arg1, int agr2, String msg) {

        }
    };
}