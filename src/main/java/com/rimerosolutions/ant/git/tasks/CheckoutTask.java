package com.rimerosolutions.ant.git.tasks;

import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;

public class CheckoutTask extends AbstractGitRepoAwareTask {

        private String branchName;
        private boolean createBranch = false;
        private boolean trackBranchOnCreate = false;

        public void setTrackBranchOnCreate(boolean trackBranchOnCreate) {
                this.trackBranchOnCreate = trackBranchOnCreate;
        }

        public void setCreateBranch(boolean createBranch) {
                this.createBranch = createBranch;
        }

        public void setBranchName(String branchName) {
                this.branchName = branchName;
        }

        @Override
        protected void doExecute() throws BuildException {
                try {
                        CheckoutCommand cmd = Git.wrap(repo).checkout();

                        if (createBranch) {
                                cmd.setCreateBranch(true);
                        }
                        cmd.setName(branchName);

                        if (trackBranchOnCreate) {
                                cmd.setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).setStartPoint("origin/" + branchName);
                        }

                        cmd.call();
                } catch (RefAlreadyExistsException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (RefNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (InvalidRefNameException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (CheckoutConflictException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (GitAPIException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }

        }

}
