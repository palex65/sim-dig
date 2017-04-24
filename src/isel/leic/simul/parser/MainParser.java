package isel.leic.simul.parser;

import isel.leic.simul.Simul;
import isel.leic.simul.module.Module;

import java.io.IOException;

public class MainParser extends Parser {
    public MainParser(String initFileName, Simul.App app) throws IOException {
        super(initFileName, app);
    }

    @Override
    protected void parseLine(String line) throws SyntaxError {
        if (line.startsWith("package "))
            packages.addLast(line.substring(line.indexOf(' ')+1).trim());
        else if (line.contains("->"))
            parseLink(line);
        else if (line.contains("="))
            parseModule(line);
        else
            throw new SyntaxError();
    }

    @Override
    protected Module getModule(String name, Object obj) {
        return createModule(name, obj);
    }
}
