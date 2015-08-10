package com.soagrowers.cqrs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Ben on 10/08/2015.
 */
public class MaterialView {

    private static MaterialView ourInstance = new MaterialView();
    private static Map<String, String> toDoItems = new HashMap<String, String>();
    private static Map<String, String> doneItems = new HashMap<String, String>();

    public static MaterialView getInstance() {
        return ourInstance;
    }

    private MaterialView() {
    }

    public void addToDo(String id, String desc){
        toDoItems.put(id, desc);
    }

    public void changeToDone(String id){
        doneItems.put(id, toDoItems.get(id));
        toDoItems.remove(id);
    }

    public void dumpView(){
        StringBuffer buf = new StringBuffer();

        buf.append("--- Start Material View ---\n");
        buf.append("ToDoItems: \n");
        for (Iterator iterator = toDoItems.keySet().iterator(); iterator.hasNext(); ) {
            String id =  (String)iterator.next();
            String desc = toDoItems.get(id);
            buf.append("Id: " + id + ", Desc: " + desc);
        }

        buf.append("\nDoneItems: \n");
        for (Iterator iterator = doneItems.keySet().iterator(); iterator.hasNext(); ) {
            String id =  (String)iterator.next();
            String desc = doneItems.get(id);
            buf.append("Id: " + id + ", Desc: " + desc);
        }

        buf.append("\n--- End Material View ---");

        System.out.println(buf.toString());
    }

}
