/*
 * Copyright 2013 Fabrizio Cannizzo (https://github.com/smartrics/jgit-ant), Rimero Solutions
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
package com.rimerosolutions.ant.git;

import java.io.File;

import org.eclipse.jgit.lib.ProgressMonitor;

/**
 * Git task interface.
 *
 * @author Fabrizio Cannizzo
 * @author Yves Zoundi
 */
public interface GitTask {
        /**
         * Sets the progress monitor for the Git command
         *
         * @param pm The progress monitor
         * See {@link org.eclipse.jgit.lib.ProgressMonitor}
         */
        void useProgressMonitor(ProgressMonitor pm);

        /**
         * Sets a reference to Git settings
         *
         * @param settingsRef A reference to Git settings
         * See {@link com.rimerosolutions.ant.git.GitSettings}
         */
        void setSettingsRef(String settingsRef);

        /**
         * Sets the Git repo directory or initial folder
         *
         * @param dir The repo or local base directory
         */
        void setDirectory(File dir);

        /**
         * Logs a message
         *
         * @param message The message to log
         */
        void log(String message);

        /**
         * Condition to meet prior to execution
         *
         * @return the condition property to meet prior execution
         */
        String getIf();

        /**
         * Condition to veto execution
         *
         * @return The condition to veto the task execution
         */
        String getUnless();

        /**
         * Executes a task
         */
        void execute();

        /**
         * Returns the task name
         *
         * @return The Ant task name
         */
        String getName();
}
