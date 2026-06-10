package dev.sim0n.caesium.config;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class ObfuscationConfig {
    // Input/Output
    private String input;
    private String output;
    
    // Dictionary settings
    private DictionaryType dictionary = DictionaryType.NUMBERS;
    
    private StringMutatorConfig string = new StringMutatorConfig();
    private ControlFlowConfig controlFlow = new ControlFlowConfig();
    private NumberMutatorConfig number = new NumberMutatorConfig();
    private ReferenceMutatorConfig reference = new ReferenceMutatorConfig();
    private LocalVariableConfig localVariable = new LocalVariableConfig();
    private LineNumberConfig lineNumber = new LineNumberConfig();
    private PolymorphConfig polymorph = new PolymorphConfig();
    private TrimConfig trim = new TrimConfig();
    private ShuffleConfig shuffle = new ShuffleConfig();
    private CrasherConfig crasher = new CrasherConfig();
    private ClassFolderConfig classFolder = new ClassFolderConfig();
    
    // Libraries (classpath dependencies)
    private List<String> libraries = new ArrayList<>();
    
    public enum DictionaryType {
        ABC_LOWERCASE, 
        ABC, 
        III, 
        NUMBERS, 
        WACK
    }
    
    @Data
    public static class StringMutatorConfig {
        private boolean enabled = false;
        private List<String> exclusions = new ArrayList<>();
    }
    
    @Data
    public static class ControlFlowConfig {
        private boolean enabled = false;
    }
    
    @Data
    public static class NumberMutatorConfig {
        private boolean enabled = false;
    }
    
    @Data
    public static class ReferenceMutatorConfig {
        private boolean enabled = false;
        private ReferenceType type = ReferenceType.NORMAL;
        
        public enum ReferenceType {
            OFF, 
            LIGHT, 
            NORMAL
        }
    }
    
    @Data
    public static class LocalVariableConfig {
        private boolean enabled = false;
        private LocalVariableAction action = LocalVariableAction.OFF;
        
        public enum LocalVariableAction {
            OFF, 
            REMOVE, 
            RENAME
        }
    }
    
    @Data
    public static class LineNumberConfig {
        private boolean enabled = false;
        private LineNumberAction action = LineNumberAction.OFF;
        
        public enum LineNumberAction {
            OFF, 
            REMOVE, 
            SCRAMBLE
        }
    }
    
    @Data
    public static class PolymorphConfig {
        private boolean enabled = false;
    }
    
    @Data
    public static class TrimConfig {
        private boolean enabled = false;
    }
    
    @Data
    public static class ShuffleConfig {
        private boolean enabled = false;
    }
    
    @Data
    public static class CrasherConfig {
        private boolean enabled = false;
    }
    
    @Data
    public static class ClassFolderConfig {
        private boolean enabled = false;
    }
}