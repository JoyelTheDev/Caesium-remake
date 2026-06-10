// src/main/java/dev/sim0n/caesium/ObfuscationConfig.java
package dev.sim0n.caesium;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ObfuscationConfig {
    private File input;
    private File output;
    private boolean stringEncryption = false;
    private List<String> stringExclusions = new ArrayList<>();
    private boolean controlFlow = false;
    private boolean numberObfuscation = false;
    private boolean polymorph = false;
    private boolean trimMath = false;
    private boolean shuffleMembers = false;
    private String localVariableAction = "OFF";
    private String lineNumberAction = "OFF";
    private List<String> libraries = new ArrayList<>();

    // Getters and Setters
    public File getInput() { return input; }
    public void setInput(File input) { this.input = input; }
    
    public File getOutput() { return output; }
    public void setOutput(File output) { this.output = output; }
    
    public boolean isStringEncryption() { return stringEncryption; }
    public void setStringEncryption(boolean stringEncryption) { this.stringEncryption = stringEncryption; }
    
    public List<String> getStringExclusions() { return stringExclusions; }
    public void setStringExclusions(List<String> stringExclusions) { this.stringExclusions = stringExclusions; }
    
    public boolean isControlFlow() { return controlFlow; }
    public void setControlFlow(boolean controlFlow) { this.controlFlow = controlFlow; }
    
    public boolean isNumberObfuscation() { return numberObfuscation; }
    public void setNumberObfuscation(boolean numberObfuscation) { this.numberObfuscation = numberObfuscation; }
    
    public boolean isPolymorph() { return polymorph; }
    public void setPolymorph(boolean polymorph) { this.polymorph = polymorph; }
    
    public boolean isTrimMath() { return trimMath; }
    public void setTrimMath(boolean trimMath) { this.trimMath = trimMath; }
    
    public boolean isShuffleMembers() { return shuffleMembers; }
    public void setShuffleMembers(boolean shuffleMembers) { this.shuffleMembers = shuffleMembers; }
    
    public String getLocalVariableAction() { return localVariableAction; }
    public void setLocalVariableAction(String localVariableAction) { this.localVariableAction = localVariableAction; }
    
    public String getLineNumberAction() { return lineNumberAction; }
    public void setLineNumberAction(String lineNumberAction) { this.lineNumberAction = lineNumberAction; }
    
    public List<String> getLibraries() { return libraries; }
    public void setLibraries(List<String> libraries) { this.libraries = libraries; }
}
