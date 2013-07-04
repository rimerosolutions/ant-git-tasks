package smartrics.ant.git;

import java.io.File;

import org.eclipse.jgit.lib.ProgressMonitor;

public interface GitTask {
    void setProgressMonitor(ProgressMonitor pm);

    void setDirectory(File dir);

    void log(String message);

    void execute();
}
