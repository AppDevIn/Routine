package com.mad.p03.np2020.routine.Class;

import java.util.List;

class Section {

    private String mName;
    private List<Task> mTaskList;

    public Section(String name,List<Task> taskList) {
        this.mName = name;
        this.mTaskList = taskList;
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
