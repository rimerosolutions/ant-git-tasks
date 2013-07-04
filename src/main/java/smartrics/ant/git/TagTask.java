package smartrics.ant.git;

import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 *
 * @author yves
 */
public class TagTask extends AbstractGitRepoAwareTask {

        private String name;
        private String message;

        public void setMessage(String message) {
                this.message = message;
        }

        public void setName(String name) {
                this.name = name;
        }

        @Override
        protected void doExecute() {
                log(String.format("Creating tag '%s'", name));
                try {
                        // TODO log result?
                        Git.wrap(repo).tag().setName(name).setMessage(message).call();
                } catch (GitAPIException ex) {
                         throw new BuildException(String.format("Could not create tag %s", name), ex);
                } 
        }
}
