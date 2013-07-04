package smartrics.ant.git;

import java.io.File;
import java.net.URISyntaxException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.transport.URIish;

public abstract class AbstractGitTask extends Task implements GitTask {

    private String uri;
    private ProgressMonitor progressMonitor;
    private File directory;

    public void setUri(String uri) {
        if (uri == null) {
            throw new BuildException("Can;t set null URI attribute");
        }
        try {
            new URIish(uri);
            this.uri = uri;
        } catch (URISyntaxException e) {
            throw new BuildException("Invalid URI: " + uri, e);
        }
    }

    @Override
    public void setDirectory(File dir) {
        if (dir == null) {
            throw new BuildException("Can;t set null directory attribute");
        }
        this.directory = new File(dir.getAbsolutePath());
    }

    @Override
    public void setProgressMonitor(ProgressMonitor pm) {
        this.progressMonitor = pm;
    }

    protected String getUri() {
        return this.uri;
    }

    protected File getDirectory() {
        return this.directory;
    }

    protected ProgressMonitor getProgressMonitor() {
        return this.progressMonitor;
    }

    abstract public void execute();
}
