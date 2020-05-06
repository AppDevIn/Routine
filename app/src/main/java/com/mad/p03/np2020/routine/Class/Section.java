package com.mad.p03.np2020.routine.Class;

import java.util.List;

public class Section {

    private String mName;
    private List<Task> mTaskList;
    private String mBackgroundColor;

    public Section(String name,List<Task> taskList, String backgroundColor) {
        this.mName = name;
        this.mTaskList = taskList;
        this.mBackgroundColor = backgroundColor;
    }


    public List<Task> getTaskList() {
        return mTaskList;
    }

    public String getName() {
        return mName;
    }

    public String getBackgroundColor() {
        return mBackgroundColor;
    }

    public void addTask(Task task){
        // TODO: Please upload any chnages to this class to the main branch`
        mTaskList.add(task);

    }
    
    public void rmTask(Task task){
        // TODO: Please upload any chnages to this class to the main branch`
        mTaskList.remove(task);
    }
    
}
