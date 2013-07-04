package smartrics.ant.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.api.RebaseResult.Status;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.transport.FetchResult;

public class PullTask extends AbstractGitTask {
    private String pullFailedProperty;

    private String modificationExistProperty;

    public void setModificationExistProperty(String p) {
        this.modificationExistProperty = p;
    }

    public void setPullFailedProperty(String p) {
        this.pullFailedProperty = p;
    }

    @Override
    public void execute() {
        try {
            RepositoryBuilder builder = new RepositoryBuilder();
            Repository repo = builder.findGitDir(getDirectory()).build();
            Git g = new Git(repo);
            PullCommand pull = g.pull();
            PullResult result = pull.call();
            FetchResult fRes = result.getFetchResult();
            log("Fetch result: " + fRes);
            log("Fetch result message: " + fRes.getMessages());
            MergeResult mRes = result.getMergeResult();
            RebaseResult rRes = result.getRebaseResult();
            Status rStatus = null;
            if (rRes != null) {
                rStatus = rRes.getStatus();
            }
            MergeStatus mStatus = mRes.getMergeStatus();
            boolean alreadyUpToDate = mStatus != null && MergeStatus.ALREADY_UP_TO_DATE.equals(mStatus);
            if (!alreadyUpToDate && modificationExistProperty != null) {
                getProject().setProperty(modificationExistProperty, "true");
                log("Setting '" + modificationExistProperty + "' to 'true'", 2);
            }
            boolean mergeFailed = mStatus != null && MergeStatus.FAILED.equals(mStatus) || MergeStatus.CONFLICTING.equals(mStatus);
            boolean rebaseFailed = rStatus != null && Status.FAILED.equals(rStatus);
            if (mergeFailed || rebaseFailed) {
                if (pullFailedProperty != null) {
                    String m = mRes.toString();
                    if (rRes != null) {
                        m = m + "\nrebase: " + rRes.getStatus();
                    }
                    getProject().setProperty(pullFailedProperty, m);
                    log("Setting '" + pullFailedProperty + "' to '" + m + "'", 2);
                }
            }
            if ((mergeFailed || rebaseFailed) && pullFailedProperty != null) {
                getProject().setProperty(pullFailedProperty, mRes.toString());
                log("Setting '" + pullFailedProperty + "' to 'failed'", 2);
            }
        } catch (Exception e) {
            if (pullFailedProperty != null) {
                getProject().setProperty(pullFailedProperty, e.getMessage());
                log("Setting '" + pullFailedProperty + "' to '" + e.getMessage() + "'", 2);
            }
        }

    }
}
