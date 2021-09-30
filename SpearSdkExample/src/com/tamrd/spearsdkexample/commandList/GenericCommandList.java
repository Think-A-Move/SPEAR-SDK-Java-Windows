package com.tamrd.spearsdkexample.commandList;

import com.thinkamove.spearnative.speechType.commands.api.CommandList;
import com.thinkamove.spearnative.speechType.commands.model.CommandsFileData;

import java.util.HashMap;
import java.util.Map;

public class GenericCommandList extends CommandList {
    private static final String[] GENERIC_COMMANDS = new String[] {
            "HEY SPEAR",
            "SET HEADING",
            "SET COURSE",
            "TUNE COM",
            "SET COM",
            "SELECT COM",
            "TUNE CHANNEL",
            "SET CHANNEL",
            "SELECT CHANNEL",
            "TUNE V H F",
            "SELECT V H F",
            "SET ALTITUDE",
            "FLIGHT LEVEL",
            "PROCEED DIRECT",
            "SELECT DIRECT",
            "CENTER MAP",
            "SET RANGE",
            "RANGE CLICK",
            "MAP ZOOM IN",
            "MAP ZOOM OUT"
    };

    @Override
    public String[] getCommandList() {
        return GENERIC_COMMANDS;
    }

    @Override
    public HashMap<String, String> mapCommands() {
        return new HashMap<>();
    }

    @Override
    public CommandsFileData getCommandsFileData() {
        return null;
    }

	@Override
	public Map<String, String[]> getGrammarLabels() {
		// TODO Auto-generated method stub
		return null;
	}
}