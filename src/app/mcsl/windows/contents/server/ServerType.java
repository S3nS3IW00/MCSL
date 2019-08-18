package app.mcsl.windows.contents.server;

import app.mcsl.managers.Language;

public enum ServerType {
    LOCAL("local"), EXTERNAL("external");

    String typeName;
    ServerType(String typeName){
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public static String[] displayValues(){
        String[] displayNames = new String[ServerType.values().length];
        for(int i = 0; i < ServerType.values().length; i++){
            displayNames[i] = Language.getText(ServerType.values()[i].typeName);
        }
        return displayNames;
    }

    public static ServerType getFromDisplayName(String s){
        for(ServerType type : ServerType.values()){
            if(Language.getText(type.typeName).equalsIgnoreCase(s)) return type;
        }
        return null;
    }
}
