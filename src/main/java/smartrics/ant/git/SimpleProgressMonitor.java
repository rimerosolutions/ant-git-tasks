package smartrics.ant.git;

import org.eclipse.jgit.lib.ProgressMonitor;

public class SimpleProgressMonitor implements ProgressMonitor {

    private GitTask owner;
    private String name;

    public SimpleProgressMonitor(GitTask t) {
        this.owner = t;
    }

    @Override
    public void update(int sz) {
    }

    @Override
    public void start(int sz) {
        owner.log("[start] " + sz);
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void endTask() {
        owner.log("[end] " + name);
    }

    @Override
    public void beginTask(String what, int sz) {
        name = what;
        owner.log("[begin] " + what + " (" + sz + ")");
    }

}
