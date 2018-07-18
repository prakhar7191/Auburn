package personalassistant;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
//import javax.speech.synthesis.Voice;

public class FemaleVoice{
    VoiceManager freettsVM;
    Voice freettsVoice;
    public FemaleVoice()
    {
        System.setProperty("mbrola.base","C:\\Users\\Shreyas\\Documents\\NetBeansProjects\\PersonalAssistant\\mbrola\\mbrola");
        freettsVM=VoiceManager.getInstance();
        freettsVoice = freettsVM.getVoice("mbrola_us1");
        freettsVoice.allocate();
        //sayWords(words);
    }
    
    public void sayWords(String words)
    {
        freettsVoice.speak(words);
    }
    public static void main(String[] args)
    {
       FemaleVoice fv = new FemaleVoice();
       fv.sayWords("Hi all");
    }
}