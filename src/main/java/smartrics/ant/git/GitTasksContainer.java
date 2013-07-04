package smartrics.ant.git;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class GitTasksContainer extends Task {

    private boolean verbose = false;
    private File localDirectory;

    private List<GitTask> tasks = new ArrayList<GitTask>();

    public void setVerbose(boolean v) {
        this.verbose = v;
    }
    
    public void setLocalDirectory(File dir) {
        this.localDirectory = dir;
    }

    public CloneTask createClone() {
        CloneTask c = new CloneTask();
        tasks.add(c);
        return c;
    }
    
    public CommitTask createCommit() {
        CommitTask c = new CommitTask();
        tasks.add(c);
        return c;
    }
    
     public UpToDateTask createUpToDate() {
        UpToDateTask c = new UpToDateTask();
        tasks.add(c);
        return c;
    }
    
    public PushTask createPush() {
        PushTask c = new PushTask();
        tasks.add(c);
        return c;
    }
    
      public TagTask createTag() {
        TagTask c = new TagTask();
        tasks.add(c);
        return c;
    }
    
    public PullTask createPull() {
        PullTask p = new PullTask();
        tasks.add(p);
        return p;
    }

    @Override
    public void execute() throws BuildException {
        if (localDirectory == null) {
            throw new BuildException("Please specify local repository directory");
        }
        int size = tasks.size();
        while(size>0) {
            GitTask t = tasks.remove(0);
            size = tasks.size();
            if (verbose) {
                t.setProgressMonitor(new SimpleProgressMonitor(t));
            }
            t.setDirectory(localDirectory);
            try {
                t.execute();
            } catch (Exception e) {
                throw new BuildException("Unexpected exception occurred!", e);
            }
        }
    }
}
