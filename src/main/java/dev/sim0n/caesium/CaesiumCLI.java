package dev.sim0n.caesium;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.sim0n.caesium.config.ObfuscationConfig;
import dev.sim0n.caesium.manager.MutatorManager;
import dev.sim0n.caesium.mutator.impl.*;
import dev.sim0n.caesium.mutator.impl.crasher.BadAnnotationMutator;
import dev.sim0n.caesium.mutator.impl.crasher.ImageCrashMutator;
import dev.sim0n.caesium.util.Dictionary;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "caesium", 
         description = "Caesium Java Bytecode Obfuscator",
         version = "1.0.9",
         mixinStandardHelpOptions = true)
public class CaesiumCLI implements Callable<Integer> {
    
    @Option(names = {"-c", "--config"}, description = "YAML configuration file")
    private File configFile;
    
    @Option(names = {"-i", "--input"}, description = "Input JAR file", required = true)
    private File input;
    
    @Option(names = {"-o", "--output"}, description = "Output JAR file")
    private File output;
    
    @Option(names = {"-d", "--dictionary"}, description = "Dictionary type: ABC_LOWERCASE, ABC, III, NUMBERS, WACK")
    private ObfuscationConfig.DictionaryType dictionary;
    
    // String mutator options
    @Option(names = {"--string"}, description = "Enable string literal encryption")
    private boolean stringEnabled;
    
    @Option(names = {"--string-exclude"}, description = "Strings to exclude from encryption")
    private String[] stringExclusions;
    
    // Control flow
    @Option(names = {"--control-flow"}, description = "Enable control flow obfuscation")
    private boolean controlFlowEnabled;
    
    // Number mutator
    @Option(names = {"--numbers"}, description = "Enable number obfuscation")
    private boolean numbersEnabled;
    
    // Reference mutator
    @Option(names = {"--reference"}, description = "Enable reference hiding (invokedynamic)")
    private boolean referenceEnabled;
    @Option(names = {"--reference-type"}, description = "Reference type: OFF, LIGHT, NORMAL")
    private ObfuscationConfig.ReferenceMutatorConfig.ReferenceType referenceType;
    
    // Local variable
    @Option(names = {"--local-vars"}, description = "Local variable action: OFF, REMOVE, RENAME")
    private ObfuscationConfig.LocalVariableConfig.LocalVariableAction localVarAction;
    
    // Line numbers
    @Option(names = {"--line-numbers"}, description = "Line number action: OFF, REMOVE, SCRAMBLE")
    private ObfuscationConfig.LineNumberConfig.LineNumberAction lineNumberAction;
    
    // Other mutators
    @Option(names = {"--polymorph"}, description = "Enable polymorph (useless instructions)")
    private boolean polymorphEnabled;
    
    @Option(names = {"--trim"}, description = "Enable math function trimming")
    private boolean trimEnabled;
    
    @Option(names = {"--shuffle"}, description = "Enable member shuffling")
    private boolean shuffleEnabled;
    
    @Option(names = {"--crasher"}, description = "Enable GUI crasher")
    private boolean crasherEnabled;
    
    @Option(names = {"--class-folder"}, description = "Enable class folder trick")
    private boolean classFolderEnabled;
    
    @Option(names = {"--library"}, description = "Add library/dependency JAR")
    private String[] libraries;
    
    @Override
    public Integer call() throws Exception {
        ObfuscationConfig config = loadConfig();
        
        // Apply command line overrides
        applyOverrides(config);
        
        // Validate config
        if (!config.getInput().exists()) {
            System.err.println("Input file not found: " + config.getInput());
            return 1;
        }
        
        if (config.getOutput() == null) {
            String outputName = config.getInput().getName().replace(".jar", "-obfuscated.jar");
            config.setOutput(new File(config.getInput().getParent(), outputName));
        }
        
        // Load libraries
        for (String lib : config.getLibraries()) {
            PreRuntime.libraries.addElement(lib);
        }
        PreRuntime.loadClassPath();
        
        // Initialize Caesium
        Caesium caesium = new Caesium();
        caesium.setDictionary(mapDictionary(config.getDictionary()));
        
        // Configure mutators
        configureMutators(caesium.getMutatorManager(), config);
        
        // Run obfuscation
        return caesium.run(config.getInput(), config.getOutput());
    }
    
    private ObfuscationConfig loadConfig() throws Exception {
        ObfuscationConfig config = new ObfuscationConfig();
        config.setInput(input);
        config.setOutput(output);
        
        if (configFile != null && configFile.exists()) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            config = mapper.readValue(configFile, ObfuscationConfig.class);
            
            // CLI input/output override config file
            if (this.input != null) config.setInput(this.input);
            if (this.output != null) config.setOutput(this.output);
        }
        
        return config;
    }
    
    private void applyOverrides(ObfuscationConfig config) {
        if (dictionary != null) config.setDictionary(dictionary);
        if (stringEnabled) config.getString().setEnabled(true);
        if (stringExclusions != null) {
            for (String excl : stringExclusions) {
                config.getString().getExclusions().add(excl);
            }
        }
        if (controlFlowEnabled) config.getControlFlow().setEnabled(true);
        if (numbersEnabled) config.getNumber().setEnabled(true);
        if (referenceEnabled) config.getReference().setEnabled(true);
        if (referenceType != null) config.getReference().setType(referenceType);
        if (localVarAction != null) {
            config.getLocalVariable().setEnabled(localVarAction != ObfuscationConfig.LocalVariableConfig.LocalVariableAction.OFF);
            config.getLocalVariable().setAction(localVarAction);
        }
        if (lineNumberAction != null) {
            config.getLineNumber().setEnabled(lineNumberAction != ObfuscationConfig.LineNumberConfig.LineNumberAction.OFF);
            config.getLineNumber().setAction(lineNumberAction);
        }
        if (polymorphEnabled) config.getPolymorph().setEnabled(true);
        if (trimEnabled) config.getTrim().setEnabled(true);
        if (shuffleEnabled) config.getShuffle().setEnabled(true);
        if (crasherEnabled) config.getCrasher().setEnabled(true);
        if (classFolderEnabled) config.getClassFolder().setEnabled(true);
        if (libraries != null) {
            for (String lib : libraries) {
                config.getLibraries().add(lib);
            }
        }
    }
    
    private Dictionary mapDictionary(ObfuscationConfig.DictionaryType type) {
        if (type == null) return Dictionary.NUMBERS;
        switch (type) {
            case ABC_LOWERCASE: return Dictionary.ABC_LOWERCASE;
            case ABC: return Dictionary.ABC;
            case III: return Dictionary.III;
            case WACK: return Dictionary.WACK;
            default: return Dictionary.NUMBERS;
        }
    }
    
    private void configureMutators(MutatorManager manager, ObfuscationConfig config) {
        // String mutator
        if (config.getString().isEnabled()) {
            StringMutator mutator = manager.getMutator(StringMutator.class);
            mutator.setEnabled(true);
            mutator.getExclusions().addAll(config.getString().getExclusions());
        }
        
        // Control flow
        if (config.getControlFlow().isEnabled()) {
            manager.getMutator(ControlFlowMutator.class).setEnabled(true);
        }
        
        // Number mutator
        if (config.getNumber().isEnabled()) {
            manager.getMutator(NumberMutator.class).setEnabled(true);
        }
        
        // Reference mutator
        if (config.getReference().isEnabled() && config.getReference().getType() != ObfuscationConfig.ReferenceMutatorConfig.ReferenceType.OFF) {
            manager.getMutator(ReferenceMutator.class).setEnabled(true);
        }
        
        // Local variable
        if (config.getLocalVariable().isEnabled()) {
            LocalVariableMutator mutator = manager.getMutator(LocalVariableMutator.class);
            mutator.setEnabled(true);
            mutator.setType(config.getLocalVariable().getAction().ordinal());
        }
        
        // Line number
        if (config.getLineNumber().isEnabled()) {
            LineNumberMutator mutator = manager.getMutator(LineNumberMutator.class);
            mutator.setEnabled(true);
            mutator.setType(config.getLineNumber().getAction().ordinal());
        }
        
        // Other mutators
        if (config.getPolymorph().isEnabled()) {
            manager.getMutator(PolymorphMutator.class).setEnabled(true);
        }
        if (config.getTrim().isEnabled()) {
            manager.getMutator(TrimMutator.class).setEnabled(true);
        }
        if (config.getShuffle().isEnabled()) {
            manager.getMutator(ShuffleMutator.class).setEnabled(true);
        }
        if (config.getCrasher().isEnabled()) {
            manager.getMutator(BadAnnotationMutator.class).setEnabled(true);
            manager.getMutator(ImageCrashMutator.class).setEnabled(true);
        }
        if (config.getClassFolder().isEnabled()) {
            manager.getMutator(ClassFolderMutator.class).setEnabled(true);
        }
    }
    
    public static void main(String[] args) {
        int exitCode = new CommandLine(new CaesiumCLI()).execute(args);
        System.exit(exitCode);
    }
}