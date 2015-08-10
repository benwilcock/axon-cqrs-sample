package com.soagrowers.cqrs.views;

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
        StringBuilder buf = new StringBuilder();

        buf.append("--- Start Material View ---\n");
        buf.append("ToDoItems: \n");
        for (String id : toDoItems.keySet()) {
            String desc = toDoItems.get(id);
            buf.append("Id: " + id + ", Desc: " + desc);
        }

        buf.append("\nDoneItems: \n");
        for (String id : doneItems.keySet()) {
            String desc = doneItems.get(id);
            buf.append("Id: " + id + ", Desc: " + desc);
        }

        buf.append("\n--- End Material View ---");

        System.out.println(buf.toString());
    }

}
