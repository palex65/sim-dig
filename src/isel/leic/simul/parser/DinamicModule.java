package isel.leic.simul.parser;

import isel.leic.simul.elem.Elem;
import isel.leic.simul.module.Module;

import java.util.HashMap;
import java.util.Map;

public class DinamicModule extends Module {
    public DinamicModule(String name) {
        super(name);
    }
    Map<String,Elem> elems = new HashMap<>();

    void addInput(String name) { new InPin(name); }
    void addOutput(String name) { new OutPin(name); }
    void addInBlock(String name, int len) { new InBlock(name,len); }
    void addOutBlock(String name, int len) { new OutBlock(name,len); }

    @Override
    protected boolean canShowStatus() { return true; }

    @Override
    public String getStatus() {
        String res = "";
        if (elems!=null) {
            for(Map.Entry<String,Elem> e : elems.entrySet())
                res += "<tr><td colspan=2 style='background-color:#C0C0C0;'><b><center>"+
                        e.getKey()+"="+typeName(e.getValue().getClass())+
                        "</center></b></td></tr>"+e.getValue().getStatus();
        }
        return res;
    }
}
