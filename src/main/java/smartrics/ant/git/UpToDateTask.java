
package smartrics.ant.git;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.treewalk.FileTreeIterator;

/**
 *
 * @author yves
 */
public class UpToDateTask extends AbstractGitRepoAwareTask {

        private String modificationExistProperty;

        public void setModificationExistProperty(String p) {
                this.modificationExistProperty = p;
        }

        @Override
        protected void doExecute() throws BuildException {
                FileTreeIterator workingTreeIterator = new FileTreeIterator(repo);

                try {
                        IndexDiff diff = new IndexDiff(repo, Constants.HEAD, workingTreeIterator);
                        diff.diff();

                        Status status = new Status(diff);

                        if (!status.isClean()) {
                                if (modificationExistProperty != null) {
                                        getProject().setProperty(modificationExistProperty, "true");
                                }

                                if (isFailOnError()) {
                                        StringBuilder msg = new StringBuilder();
                                        msg.append("The Git tree was modified.");
                                        msg.append("\n").append("Changed:").append(status.getChanged());
                                        msg.append("\n").append("Added:").append(status.getAdded());
                                        msg.append("\n").append("Modified:").append(status.getModified());
                                        msg.append("\n").append("Missing:").append(status.getMissing());

                                        throw new GitBuildException("Status is not clean:" + msg.toString());
                                }
                        } else {
                                log("The Git tree is up to date!");
                        }
                } catch (IOException ioe) {
                        throw new GitBuildException("IO Error when checking repository status", ioe);
                }

        }
}
