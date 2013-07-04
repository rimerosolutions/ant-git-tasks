package smartrics.ant.git;

import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 *
 * @author yves
 */
public class PushTask extends AbstractGitRepoAwareTask {

        private String pushFailedProperty;
        private boolean includeTags = true;
        private String username;
        private String password;

        public void setPassword(String password) {
                this.password = password;
        }

        public void setUsername(String username) {
                this.username = username;
        }        

        public void setIncludeTags(boolean includeTags) {
                this.includeTags = includeTags;
        }

        public void setPushFailedProperty(String pushFailedProperty) {
                this.pushFailedProperty = pushFailedProperty;
        }

        @Override
        protected void doExecute() {

                try {
                        List<RemoteConfig> remoteConfigs = RemoteConfig.getAllRemoteConfigs(repo.getConfig());

                        if (!remoteConfigs.isEmpty()) {
                                CredentialsProvider cp = new UsernamePasswordCredentialsProvider(username, password);
 
                                log("Pushing tags");
                                
                                if (includeTags) {
                                        Git.wrap(repo).push().setCredentialsProvider(cp).setForce(true).setPushTags().call();
                                } else {
                                        Git.wrap(repo).push().setCredentialsProvider(cp).setForce(true).call();
                                }
                        }

                } catch (Exception e) {
                        if (pushFailedProperty != null) {
                                getProject().setProperty(pushFailedProperty, e.getMessage());
                        }

                        throw new GitBuildException("Git push failed", e);
                }
        }
}
