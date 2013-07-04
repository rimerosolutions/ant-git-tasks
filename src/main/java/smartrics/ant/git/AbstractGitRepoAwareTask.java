
package smartrics.ant.git;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

/**
 *
 * @author yves
 */
public abstract class AbstractGitRepoAwareTask extends AbstractGitTask {

        protected Repository repo;
        private boolean failOnError = false;

        public void setFailOnError(boolean failOnError) {
                this.failOnError = failOnError;
        }

        public boolean isFailOnError() {
                return failOnError;
        } 
        
        protected abstract void doExecute() throws BuildException;
        
        @Override
        public final void execute() {
                RepositoryBuilder builder = new RepositoryBuilder();

                try {                       
                         try {
                                 repo = builder.findGitDir(getDirectory()).build();
                         }
                         catch (IOException ioe) {
                                 String errorMsg = "Specified path (%s) doesn't seem to be a git repository.";
                                 
                                 throw new BuildException(String.format(errorMsg, getDirectory().getAbsolutePath()), ioe);
                         }
                         
                         doExecute();
                         
                }  
                catch (GitBuildException e) {                        
                        log(e, Project.MSG_ERR);
                        
                        if (failOnError) {
                                throw new BuildException(e);
                        }                        
                }
                finally {
                        if (repo != null) {
                                repo.close();
                        }
                }
        }
        
}
