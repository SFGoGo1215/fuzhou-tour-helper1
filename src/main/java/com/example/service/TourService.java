package com.fuzhoutour.service;

import org.springframework.stereotype.Service;
import javax.speech.synthesis.*;
import java.util.Locale;

@Service
public class TTSService {
    public String convertToSpeech(String text) {
        try {
            System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
            Synthesizer synth = Central.createSynthesizer(null);
            synth.allocate();
            synth.speakPlainText(text, null);
            synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
            synth.deallocate();
            return "语音播放成功";
        } catch (Exception e) {
            return "播放失败: " + e.getMessage();
        }
    }
}