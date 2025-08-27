package lolens.cases.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;

@Modmenu(modId = "cases")
@Config(name = "cases-config", wrapperName = "CasesConfig")
public class CasesConfigModel {

    public LoggerLevel LOGGER_LEVEL = LoggerLevel.WARN;


    public enum LoggerLevel {
        ERROR, WARN, INFO
    }

}
