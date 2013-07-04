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

        public void setName(String name) {
                this.name = name;
        }

        @Override
        protected void doExecute() {
                String message = String.format("Creating tag '%s'", name);
                try {
                        // TODO log result?
                        Git.wrap(repo).commit().setAll(true).setMessage("commit all pending changes before tagging " + name);
                        Git.wrap(repo).tag().setName(name).setMessage(message).call();
                        Git.wrap(repo).commit().setAll(true).setMessage(message);
                } catch (GitAPIException ex) {
                         throw new BuildException(String.format("Could not create tag %s", name), ex);
                } 
        }
}
