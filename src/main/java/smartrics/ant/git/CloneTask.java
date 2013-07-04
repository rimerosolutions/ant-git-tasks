package smartrics.ant.git;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.api.CloneCommand;

public class CloneTask extends AbstractGitTask {

    @Override
    public void execute() {
        try {
            CloneCommand clone = new CloneCommand();
            clone.setURI(getUri());
            clone.setDirectory(new File(getDirectory().getAbsolutePath()));
            clone.setProgressMonitor(getProgressMonitor());
            clone.call();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BuildException("Unexpected exception: " + e.getMessage(), e);
        }
    }
}
