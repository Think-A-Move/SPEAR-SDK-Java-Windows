package com.tamrd.spearsdkexample.commandList;

//import com.tamrd.speardemo.helper.ConfigPropertyReader;
import com.thinkamove.spearnative.speechType.commands.api.CommandList;

public class CommandListFactory {
    private static final String COMMAND_LIST_ZEBRA = "ZEBRA";
    private static final String COMMAND_LIST_GENERIC = "GENERIC";

    public static CommandList build() {
    	//ConfigPropertyReader configPropertyReader = new ConfigPropertyReader();
        CommandList commandList;
        commandList = new GenericCommandList();

        return commandList;
    }
}

