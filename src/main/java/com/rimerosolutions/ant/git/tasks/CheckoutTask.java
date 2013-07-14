/*
 * Copyright 2013 Rimero Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rimerosolutions.ant.git.tasks;

import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;

import com.rimerosolutions.ant.git.GitBuildException;

/**
 * Perform a checkout
 *
 * <p><a href="https://www.kernel.org/pub/software/scm/git/docs/git-checkout.html">Git documentation about checkout</a></p>
 *
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/CheckoutCommand.html">JGit CheckoutCommand</a></p>
 *
 * @author Yves Zoundi
 */
public class CheckoutTask extends AbstractGitRepoAwareTask {

        private String branchName;
        private boolean createBranch = false;
        private boolean trackBranchOnCreate = true;
        private static final String TASK_NAME = "git-checkout";

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * Whether or not to track the branch automatically when created
         * 
         * @param trackBranchOnCreate Track branch after creation(Default true)
         *
         */
        public void setTrackBranchOnCreate(boolean trackBranchOnCreate) {
                this.trackBranchOnCreate = trackBranchOnCreate;
        }

        /**
         * Whether or not to create the branch if it doesn't exist
         *
         * @param createBranch Whether or not to create the branch (Default false)
         */
        public void setCreateBranch(boolean createBranch) {
                this.createBranch = createBranch;
        }

        /**
         * The branch to checkout
         *
         * @param branchName The branch name
         */
        public void setBranchName(String branchName) {
                this.branchName = branchName;
        }

        @Override
        protected void doExecute() throws BuildException {
                try {
                        CheckoutCommand checkoutCommand = git.checkout();

                        if (createBranch) {
                                checkoutCommand.setCreateBranch(true);
                        }
                        checkoutCommand.setName(branchName);

                        if (trackBranchOnCreate) {
                                checkoutCommand.setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).
                                        setStartPoint("origin/" + branchName);
                        }

                        checkoutCommand.call();
                } catch (RefAlreadyExistsException e) {
                        throw new GitBuildException(String.format("Cannot create branch '%s', as it already exists!", branchName), e);
                } catch (RefNotFoundException e) {
                        throw new GitBuildException(String.format("The branch '%s' was not found", branchName), e);
                } catch (InvalidRefNameException e) {
                        throw new GitBuildException("An invalid branch name was specified", e);
                } catch (CheckoutConflictException e) {
                        throw new GitBuildException("Some checkout conflicts were found", e);
                } catch (GitAPIException e) {
                        throw new GitBuildException(String.format("Could not checkout branch '%s'", branchName), e);
                }

        }

}
