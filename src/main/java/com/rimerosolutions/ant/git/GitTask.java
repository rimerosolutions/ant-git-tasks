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
package com.rimerosolutions.ant.git;

import java.io.File;

import org.eclipse.jgit.lib.ProgressMonitor;

public interface GitTask {
        void setProgressMonitor(ProgressMonitor pm);
        
        void setSettingsRef(String settingsRef);

        void setDirectory(File dir);

        void log(String message);
        
        String getIf();
        
        String getUnless();

        void execute();
        
        String getName();
}
