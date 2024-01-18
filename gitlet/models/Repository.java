package me.gitlet.models;

import java.util.Map;

/**
 * Repository
 */
public class Repository {

    Commit head;
    Map<String, Commit> branchMap;

    public void init() {
        head = new Commit();
    }

    public boolean isStageEmpty() {
        return true;
    }

    public void showLog() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showLog'");
    }
}