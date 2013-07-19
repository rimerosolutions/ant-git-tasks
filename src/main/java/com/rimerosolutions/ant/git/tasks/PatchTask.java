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
import org.eclipse.jgit.api.errors.GitAPIException;
import java.util.Collection;
import org.eclipse.jgit.api.ApplyResult;
import org.eclipse.jgit.api.errors.PatchFormatException;
import org.eclipse.jgit.api.errors.PatchApplyException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitUtils;
import com.rimerosolutions.ant.git.GitBuildException;

/**
 * Apply a patch
 *
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-apply.html">Git documentation about apply</a></p>
 *
 * <p><a href="http://download.eclipse.org/jgit/docs/latet/apidocs/org/eclipse/jgit/api/ApplyCommand.html">JGit ApplyCommand</a></p>
 *
 * @author Yves Zoundi
 */
public class PatchTask extends AbstractGitRepoAwareTask {

        private static final String TASK_NAME = "git-patch";
        private File patchFile;
        private String updatedCountProperty;

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * Set the updated count property
         *
         * @param updatedCountProperty Sets the property holding the count of affected files
         */
        public void setUpdatedCountProperty(String updatedCountProperty) {
                this.updatedCountProperty = updatedCountProperty;
        }

        /**
         * Sets the location of the patch file
         *
         * @param file Path to the patch file
         */
        public void setPatchFile(String file) {
                patchFile = new File(file);
        }

        @Override
        protected void doExecute() {
                InputStream in = null;

                try {
                        in = new FileInputStream(patchFile);
                        ApplyResult result = git.apply().setPatch(in).call();
                        Collection<File> updatedFiles = result.getUpdatedFiles();

                        log("Updated files:" + updatedFiles.size());

                        if (updatedCountProperty != null) {
                                getProject().setProperty(updatedCountProperty, String.valueOf(updatedFiles.size()));
                        }
                }
                catch (PatchFormatException pfe) {
                        throw new GitBuildException("Invalid patch format.", pfe);
                }
                catch (PatchApplyException pae) {
                        throw new GitBuildException("Failed to apply patch.", pae);
                }
                catch (GitAPIException gae) {
                        throw new GitBuildException("Unexpected runtime error.", gae);
                }
                catch (IOException ioe) {
                        throw new GitBuildException("Unexpected IO/Error.", ioe);
                }
                finally {
                        if (in != null) {
                                try {
                                        in.close();
                                }
                                catch(IOException ioe) {
                                        throw new GitBuildException("Cannot close patch file IO stream.", ioe);
                                }
                        }
                }
        }

}
