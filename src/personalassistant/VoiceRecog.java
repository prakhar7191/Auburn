/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package personalassistant;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.lang.*;
import javax.speech.Central;
import javax.speech.recognition.*;
import java.io.FileReader;
import java.util.Locale;

 
public class VoiceRecog extends ResultAdapter {
    static Recognizer recognizer;
    boolean recognized=false;
    public void resultAccepted(ResultEvent resultEvent) {
        Result result = (Result)(resultEvent.getSource());
        ResultToken resultToken[] = result.getBestTokens();
        for (int nIndex = 0; nIndex < resultToken.length; nIndex++){
            System.out.print(resultToken[nIndex].getSpokenText() + " ");
        }
        try {      
            // Deallocate the recognizer
            recognizer.forceFinalize(true);          
            recognizer.deallocate();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        System.exit(0);
    }
    public void interpret() {
        try {
            Central.registerEngineCentral 
            ("com.cloudgarden.speech.CGEngineCentral");
            RecognizerModeDesc desc = new RecognizerModeDesc(Locale.US,Boolean.TRUE);
            // Create a recognizer that supports US English.
            recognizer = Central.createRecognizer(desc);
            // Start up the recognizer
            recognizer.allocate();
            // Load the grammar from a file, and enable it
            FileReader fileReader = new FileReader("C:\\Users\\Shreyas\\Documents\\NetBeansProjects\\PersonalAssistant\\my_grammar.grammar");
            RuleGrammar grammar = recognizer.loadJSGF(fileReader);
            grammar.setEnabled(true);
            // Add the listener to get results
            recognizer.addResultListener(new VoiceRecog());
            // Commit the grammar
            recognizer.commitChanges();
            recognizer.waitEngineState(Recognizer.LISTENING);
             // Request focus and start listening
            recognizer.requestFocus();
            recognizer.resume();
            recognizer.waitEngineState(Recognizer.FOCUS_ON);
            System.out.println("started");
            recognizer.forceFinalize(true);            
            System.out.println("end");
            recognized=false;
            recognizer.waitEngineState(Recognizer.DEALLOCATED);
            System.out.println("end1");
            System.out.println(recognizer.toString());
            recognized=true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String args[]) {
        VoiceRecog vreg = new VoiceRecog();
        vreg.interpret();
    }
}