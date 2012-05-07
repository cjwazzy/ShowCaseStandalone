package com.miykeal.showCaseStandalone.Utilities;

import com.miykeal.showCaseStandalone.ShowCaseStandalone;

/**
 *
 * @author Sorklin <sorklin at gmail.com>
 */
public class BenchMark {
    
    private long globalStartTime;
    private long startTime;
    private long endTime;
    private String markMessage;
    private String section;
    
    public BenchMark(String section){
        this.section = section;
        globalStartTime = System.nanoTime();
        startTime = globalStartTime;
        endTime = globalStartTime;
        markMessage = "General";
    }
    
    public void start(){
        start("Generic mark");
    }
    
    public void start(String message){
        startTime = System.nanoTime();
        markMessage = message;
        ShowCaseStandalone.dlog("Benchmark Start (" + section + ": " + markMessage + "): " + startTime);
    }
    
    public void mark(String message){
        markMessage = message;
        mark();
    }
    
    public void mark(){
        endTime = System.nanoTime();
        long elapsed = endTime - startTime;
        double msElapsed = (double)elapsed / 1000000.0;
        ShowCaseStandalone.dlog("Benchmark mark (" + section + ": " + markMessage + "): " + endTime);
        ShowCaseStandalone.dlog("Elapsed time: " + elapsed + " = " + msElapsed + "millis.");
        
        startTime = System.nanoTime();
    }
    
    public void end(){
        endTime = System.nanoTime();
        long elapsed = endTime - globalStartTime;
        double msElapsed = (double)elapsed / 1000000.0;
        ShowCaseStandalone.dlog("Benchmark End (" + section + "): " + endTime);
        ShowCaseStandalone.dlog("Total elapsed time: " + elapsed + " = " + msElapsed + "millis.");
    }
}
