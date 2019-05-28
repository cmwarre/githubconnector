package com.tamakicontrol;
import java.io.Serializable;
import java.util.List;

public class GitException extends Exception implements Serializable {

    private List<String> conflictingPaths = null;

    public GitException(Exception e){
        super(e);
    }

    public GitException(Exception e, List<String> conflictingPaths){
        super(e);
        this.conflictingPaths = conflictingPaths;
    }

    public List<String> getConflictingPaths(){
        return conflictingPaths;
    }

}
