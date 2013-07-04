package smartrics.ant.git;

import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 *
 * @author yves
 */
public class CommitTask extends AbstractGitRepoAwareTask {

        private String message = "Commit message";

        public void setMessage(String message) {
                this.message = message;
        } 
        
        @Override
        protected void doExecute() throws BuildException {
                try {
                        setFailOnError(true); 
                        Git.wrap(repo).commit().setAll(true).setMessage(message).call();
                } catch (GitAPIException ex) {
                        throw new GitBuildException("Cannot commit to Git repository", ex);
                } 
        }
        
}
