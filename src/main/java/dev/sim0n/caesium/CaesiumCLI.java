// src/main/java/dev/sim0n/caesium/CaesiumCLI.java
package dev.sim0n.caesium;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "caesium", 
         description = "Caesium Java Bytecode Obfuscator for Java 21",
         version = "1.0.9",
         mixinStandardHelpOptions = true)
public class CaesiumCLI implements Callable<Integer> {
    
    @Option(names = {"-c", "--config"}, description = "YAML configuration file")
    private File configFile;
    
    @Option(names = {"-i", "--input"}, description = "Input JAR file", required = true)
    private File input;
    
    @Option(names = {"-o", "--output"}, description = "Output JAR file")
    private File output;
    
    @Option(names = {"--string"}, description = "Enable string literal encryption")
    private boolean stringEnabled;
    
    @Option(names = {"--control-flow"}, description = "Enable control flow obfuscation")
    private boolean controlFlowEnabled;
    
    @Option(names = {"--numbers"}, description = "Enable number obfuscation")
    private boolean numbersEnabled;
    
    @Option(names = {"--polymorph"}, description = "Enable polymorph (useless instructions)")
    private boolean polymorphEnabled;
    
    @Option(names = {"--trim"}, description = "Enable math function trimming")
    private boolean trimEnabled;
    
    @Option(names = {"--shuffle"}, description = "Enable member shuffling")
    private boolean shuffleEnabled;
    
    @Option(names = {"--local-vars"}, description = "Local variable action: OFF, REMOVE, RENAME")
    private String localVarAction = "OFF";
    
    @Option(names = {"--line-numbers"}, description = "Line number action: OFF, REMOVE, SCRAMBLE")
    private String lineNumberAction = "OFF";
    
    @Option(names = {"--verbose", "-v"}, description = "Enable verbose output")
    private boolean verbose = false;
    
    @Override
    public Integer call() throws Exception {
        // Load configuration
        ObfuscationConfig config = loadConfig();
        
        // Validate input
        if (!config.getInput().exists()) {
            System.err.println("Error: Input file not found: " + config.getInput());
            return 1;
        }
        
        // Set default output if not specified
        if (config.getOutput() == null) {
            String outputName = config.getInput().getName().replace(".jar", "-obfuscated.jar");
            config.setOutput(new File(config.getInput().getParent(), outputName));
        }
        
        // Print banner
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║     Caesium Bytecode Obfuscator      ║");
        System.out.println("║         Java 21 Edition v1.0.9       ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println();
        System.out.println("Input:  " + config.getInput());
        System.out.println("Output: " + config.getOutput());
        System.out.println();
        
        // Run obfuscation
        Obfuscator obfuscator = new Obfuscator(config, verbose);
        boolean success = obfuscator.obfuscate();
        
        if (success) {
            System.out.println("\n Obfuscation completed successfully!");
            return 0;
        } else {
            System.err.println("\n Obfuscation failed!");
            return 1;
        }
    }
    
    private ObfuscationConfig loadConfig() throws Exception {
        ObfuscationConfig config = new ObfuscationConfig();
        config.setInput(input);
        config.setOutput(output);
        config.setStringEncryption(stringEnabled);
        config.setControlFlow(controlFlowEnabled);
        config.setNumberObfuscation(numbersEnabled);
        config.setPolymorph(polymorphEnabled);
        config.setTrimMath(trimEnabled);
        config.setShuffleMembers(shuffleEnabled);
        config.setLocalVariableAction(localVarAction);
        config.setLineNumberAction(lineNumberAction);
        
        // Load YAML config if provided
        if (configFile != null && configFile.exists()) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            ObfuscationConfig yamlConfig = mapper.readValue(configFile, ObfuscationConfig.class);
            
            // Merge configurations (CLI takes precedence)
            if (!stringEnabled && yamlConfig.isStringEncryption()) 
                config.setStringEncryption(true);
            if (!controlFlowEnabled && yamlConfig.isControlFlow()) 
                config.setControlFlow(true);
            if (!numbersEnabled && yamlConfig.isNumberObfuscation()) 
                config.setNumberObfuscation(true);
            if (!polymorphEnabled && yamlConfig.isPolymorph()) 
                config.setPolymorph(true);
            if (!trimEnabled && yamlConfig.isTrimMath()) 
                config.setTrimMath(true);
            if (!shuffleEnabled && yamlConfig.isShuffleMembers()) 
                config.setShuffleMembers(true);
            if ("OFF".equals(localVarAction) && !"OFF".equals(yamlConfig.getLocalVariableAction()))
                config.setLocalVariableAction(yamlConfig.getLocalVariableAction());
            if ("OFF".equals(lineNumberAction) && !"OFF".equals(yamlConfig.getLineNumberAction()))
                config.setLineNumberAction(yamlConfig.getLineNumberAction());
            
            if (config.getInput() == null && yamlConfig.getInput() != null)
                config.setInput(yamlConfig.getInput());
            if (config.getOutput() == null && yamlConfig.getOutput() != null)
                config.setOutput(yamlConfig.getOutput());
        }
        
        return config;
    }
    
    public static void main(String[] args) {
        int exitCode = new CommandLine(new CaesiumCLI()).execute(args);
        System.exit(exitCode);
    }
}
